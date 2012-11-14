'''
Created on Oct 31, 2012

@author: hunlan
'''
from servercore.CmmCore.ContentDataOrganizer.Filters.filterinterface import FilterInterface
from servercore.CmmData.models import Media, Picture, Rank, Mood
import flickrapi
import operator
from servercore import CmmData
from servercore.util.datanames import ApiKeys

class BasicFilter(FilterInterface):
    # Flickr picture filter metric multiplier
    # [ups, downs, fav, views]
    _PIC_METRIC = [100, -15, 10, 1]
    
    '''
    TODO: currently only support humorous picture
    
    This is a basic filter that filter base on thumbs up, thumbs down,
    views, and favorites
    
    '''  
    @staticmethod
    def filter(mood, content, delete_ratio):
        # check param
        assert(content in Media.CONTENT_TYPES)
        assert(mood in Mood.MOOD_TYPES)
        
        # switch case
        if(content == Media.PICTURE):
            BasicFilter._filterPictures(mood, delete_ratio)
            return True
        elif(content == Media.VIDEO):
            return False
        elif(content == Media.AUDIO):
            return False
        elif(content == Media.TEXT):
            return False
        
        raise Exception('this should never happen')
    
    @staticmethod
    def _filterPictures(mood, delete_ratio):   
        # get data from database
        medias = Media.objects.filter(moods=mood, content_type=Media.PICTURE)

        # math to calculate how many to delete
        total_num = len(medias)
        delete_num = int(delete_ratio * total_num)
        
        # clear db
        trashes = BasicFilter._cleanDataBase()
        delete_num = delete_num - trashes
        if delete_num <= 0:
            return
        
        # initiate flickr object     
        flickr = flickrapi.FlickrAPI(ApiKeys.FLICKR_API_KEY)
                
        # a map from mid to score
        score_map = {}
        
        # loop through all media
        for media in medias:
            # get picture mid
            pic = media.picture # Picture.objects.get(media=media.id)
            
            # get rank, then thumbs up and down
            rank = media.rank # Rank.objects.get(media=media)
            thumbsup = rank.thumbs_up
            thumbsdown = rank.thumbs_down
            
            # use pic's mid to get data from flickr
            pic_id = pic.flickr_id
            pic_info = flickr.photos_getInfo(photo_id=pic_id)
            pic_attrib = pic_info[0].attrib
            
            # get favorites and views
            fav = int(pic_attrib['isfavorite'])
            view = int(pic_attrib['views'])
            
            # ups, downs, fav, view
            values = [thumbsup, thumbsdown, fav, view]
            
            # compute score
            score = 0
            for i in range(0, len(BasicFilter._PIC_METRIC)):
                score = score + values[i] * BasicFilter._PIC_METRIC[i]
                score_map[media.id] = score
        
        # sort the score map in to a sorted score list sorted by value
        sorted_score_map = sorted(score_map.iteritems(), \
                                  key=operator.itemgetter(1))
        
        # filter out data "delete_num" of data
        for i in range(0, delete_num) :
            bad_pics = sorted_score_map.pop(0)
            mid = bad_pics[0]
            CmmData.models.destory(mid, Media.PICTURE)
            
            
    @staticmethod
    def _cleanDataBase():
        pics = Picture.objects.all()
        flickr = flickrapi.FlickrAPI(ApiKeys.FLICKR_API_KEY)
        count = 0
        for p in pics:
            try:
                # throw error if bad things happen
                flickr.photos_getInfo(photo_id=p.flickr_id)
            except Exception:
                CmmData.models.destory(p.media.id, Media.PICTURE)
                count = count + 1
        return count
            
            
            
            
            
            