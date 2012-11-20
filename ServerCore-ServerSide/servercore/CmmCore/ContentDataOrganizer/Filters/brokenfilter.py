'''
Created on Nov 17, 2012

This class filters out broken urls

@author: hunlan
'''
import gdata.youtube.service
from servercore.CmmCore.ContentDataOrganizer.Filters.filterinterface import FilterInterface
from servercore.CmmData.models import Media, Mood
import flickrapi
from servercore.util.datanames import ApiKeys
from servercore import CmmData

class BrokenFilter(FilterInterface):
    FILTER_AT_A_TIME = 20
    
    @staticmethod
    def filter(mood, content, delete_ratio=None):
        # check param
        assert(content in Media.CONTENT_TYPES)
        assert(mood in Mood.MOOD_TYPES)
        
        # get content to filter
        medias = Media.objects.filter(moods=mood, \
                                      content_type=content, \
                                      filtercheck__checked=False)
        
        # short circuit on media == 0
        if len(medias) == 0:
            m = Media.objects.filter(moods=mood, content_type=content)
            BrokenFilter._falsifyAllMedia(m)
        
        # Do filter
        if content == Media.PICTURE:
            return BrokenFilter._checkPicture(medias, mood)
        elif content == Media.VIDEO:
            return BrokenFilter._checkVideo(medias, mood)
            
        return True
        
    
    @staticmethod
    def _checkVideo(medias, mood):
        if len(medias) == 0:
            return True
        
        # check with yt object
        yt = gdata.youtube.service.YouTubeService() 
        
        # tracking variable
        count = BrokenFilter.FILTER_AT_A_TIME
        deleted = 0
        for m in medias:
            count -= 1
            
            #do a try and see if succeed or not
            yid = m.video.youtube_id
            try:
                yt.GetYouTubeVideoEntry(video_id=yid)
                m.filtercheck.checked = True
                m.filtercheck.save()
            except Exception:
                CmmData.models.destory(m.id, Media.VIDEO)
                deleted += 1
                
            if count <= 0:
                break
        
        #print to console for logging
        print 'deleted ' + str(deleted) + ' contents'
        
        # update DB
        yetToFilter = Media.objects.filter(moods=mood, content_type=Media.VIDEO, filtercheck__checked=False)
        if len(yetToFilter) == 0:
            m = Media.objects.filter(moods=mood, content_type=Media.VIDEO)
            BrokenFilter._falsifyAllMedia(m)
            print 'reseted'
            return True
        
        return False
        
    @staticmethod
    def _checkPicture(medias, mood):
        if len(medias) == 0:
            return True
        
        # flickr object
        flickr = flickrapi.FlickrAPI(ApiKeys.FLICKR_API_KEY)
        
        # counters
        count = BrokenFilter.FILTER_AT_A_TIME
        deleted = 0
        for m in medias:
            count -= 1
            
            # try fetching data to see if fail or not
            fid = m.picture.flickr_id
            try:
                flickr.photos_getInfo(photo_id=fid)
                m.filtercheck.checked = True
                m.filtercheck.save()
            except Exception:
                deleted += 1
                CmmData.models.destory(m.id, Media.PICTURE)
            
            if count <= 0:
                break
        
        #print to console
        print 'deleted ' + str(deleted) + ' contents'
        
        # update DB
        yetToFilter = Media.objects.filter(moods=mood, content_type=Media.PICTURE, filtercheck__checked=False)
        if len(yetToFilter) == 0:
            m = Media.objects.filter(moods=mood, content_type=Media.PICTURE)
            BrokenFilter._falsifyAllMedia(m)
            print 'reseted'
            return True
        
        return False
            
    # Update DB by setting false to checked to medias
    @staticmethod
    def _falsifyAllMedia(medias):
        for m in medias:
            m.filtercheck.checked = False
            m.filtercheck.save()
            