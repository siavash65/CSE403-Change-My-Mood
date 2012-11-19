'''
Created on Nov 17, 2012

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
        
        medias = Media.objects.filter(moods=mood, \
                                      content_type=content, \
                                      filtercheck__checked=False)
        
        if len(medias) == 0:
            m = Media.objects.filter(moods=mood, content_type=content)
            BrokenFilter._falsifyAllMedia(m)
        
        if content == Media.PICTURE:
            return BrokenFilter._checkPicture(medias, mood)
        elif content == Media.VIDEO:
            return BrokenFilter._checkVideo(medias, mood)
            
        return True
        
    
    @staticmethod
    def _checkVideo(medias, mood):
        if len(medias) == 0:
            return True
        
        yt = gdata.youtube.service.YouTubeService() 
        
        count = BrokenFilter.FILTER_AT_A_TIME
        deleted = 0
        for m in medias:
            count -= 1
            
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
        
        print 'deleted ' + str(deleted) + ' contents'
        
        if count > 0:
            m = Media.objects.filter(moods=mood, content_type=Media.VIDEO)
            BrokenFilter._falsifyAllMedia(m)
            return True
        
        return False
        
    @staticmethod
    def _checkPicture(medias, mood):
        if len(medias) == 0:
            return True
        
        # flickr object
        flickr = flickrapi.FlickrAPI(ApiKeys.FLICKR_API_KEY)
        
        count = BrokenFilter.FILTER_AT_A_TIME
        for m in medias:
            count -= 1
            
            fid = m.picture.flickr_id
            try:
                flickr.photos_getInfo(photo_id=fid)
                m.filtercheck.checked = True
                m.filtercheck.save()
            except Exception:
                CmmData.models.destory(m.id, Media.PICTURE)
            
            if count <= 0:
                break
        
        print 'deleted ' + str(deleted) + ' contents'
        
        if count > 0:
            m = Media.objects.filter(moods=mood, content_type=Media.PICTURE)
            BrokenFilter._falsifyAllMedia(m)
            return True
        
        return False
            
    
    @staticmethod
    def _falsifyAllMedia(medias):
        for m in medias:
            m.filtercheck.checked = False
            m.filtercheck.save()
            