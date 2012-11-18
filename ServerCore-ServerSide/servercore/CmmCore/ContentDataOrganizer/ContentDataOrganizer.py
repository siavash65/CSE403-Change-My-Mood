'''
Created on Oct 31, 2012

@author: hunlan
'''

import random
import flickrapi
from servercore.CmmData.models import Picture, Rank, Media, User, Mood
from servercore.CmmCore.ContentDataOrganizer.Filters.basicfilter import BasicFilter
from servercore.util.datanames import ApiKeys
from servercore.CmmCore.ContentDataOrganizer.Retrievers import PictureRetriever,\
    VideoRetriever
from servercore.CmmCore.ContentDataOrganizer.Filters.scorefilter import ScoreFilter

class ContentDataOrganizer():
    # Number of data to filter
    DELETE_RATIO = 0.1    
    
    # Data parameters
    HAPPY_TERMS = ['funny']
    ROMANTIC_TERMS = ['love']
    EXCITED_TERMS = ['excited']
    INSPIRED_TERMS = ['inspired']
    
    NUM_DATA = 50
    
    @staticmethod
    def collectContentCronJob(mood, content):
        datas = Media.objects.filter(moods=mood, content_type = content)
        num_data_needed = ContentDataOrganizer.NUM_DATA - len(datas)
        
        if num_data_needed <= 0:
            print 'already have enough data'
        
        if content == Media.PICTURE:
            PictureRetriever.pullPictures(mood, \
                                          ContentDataOrganizer._getTerms(mood),\
                                          num_data_needed)
        elif content == Media.VIDEO:
            VideoRetriever.pullVideos(mood, \
                                      ContentDataOrganizer._getTerms(mood), \
                                      num_data_needed)
            
        return None
    
    @staticmethod
    def _getTerms(mood):
        if mood == Mood.HAPPY:
            return ContentDataOrganizer.HAPPY_TERMS
        elif mood == Mood.ROMANTIC:
            return ContentDataOrganizer.ROMANTIC_TERMS
        elif mood == Mood.EXCITED:
            return ContentDataOrganizer.EXCITED_TERMS
        elif mood == Mood.INSPIRED:
            return ContentDataOrganizer.INSPIRED_TERMS
        
        raise Exception('invalid mood input in getTerms')
        
    
    '''
    TODO: currently only support pictures
    
    This method filters the media data in our database and returns
    true on success
    '''
    @staticmethod
    def filterContentCronJob(mood = Mood.HAPPY):
        return BasicFilter.filter(mood, \
                                  Media.PICTURE, \
                                  ContentDataOrganizer.DELETE_RATIO)
        
    @staticmethod
    def scoreThresholdFilterCronJob(mood = Mood.HAPPY, content = Media.PICTURE):
        return ScoreFilter.filter(mood, content)

    
#-----------------------------------------------------------------------------#
#                            Depricated                                       #
#-----------------------------------------------------------------------------#

    
    '''
    TODO: A hack method, Garrett, please purify this, do all error checking
    and make it so that it puts N data to database instead of 20.
    '''
    @staticmethod
    def putSomeData(mood=Mood.HAPPY, content=Media.PICTURE):
        # grab term
        #whichTerm = int(random.random() * len(ContentDataOrganizer.FUNNY_TERMS))
        #term = ContentDataOrganizer.FUNNY_TERMS[whichTerm]
        
        # our database
        moodPics = Media.objects.filter(moods=mood, content_type = content)
        num_moodPics = ContentDataOrganizer.NUM_DATA - len(moodPics)
        
        PictureRetriever.pullPictures(mood, \
                                      ContentDataOrganizer.HAPPY_TERMS,\
                                      num_moodPics)
    
    
    '''
    TODO: Hunlan Hack: PLEASE DO NOT DO THIS IN PRODUCTION!!!!!
    '''    
    @staticmethod
    def clearPhotoDatabase():
        Media.objects.all().delete()
        Picture.objects.all().delete()
        Rank.objects.all().delete()
        User.objects.all().delete()
        # Mood.objects.all().delete()
        
        return None
    
    @staticmethod
    def fetch_flickr_photos(apikey, mood_type, num_photos, tags):
        # grab term
        
        # our database
        current_pics = Picture.objects.all()
        amount_needed = num_photos - len(current_pics)
        
        # grab from flickr

        flickr = flickrapi.FlickrAPI(apikey)
        photos =  flickr.walk(tag_mode = 'all', tags = 'happy', extras='url_q') 
        for i in range(amount_needed):
            photo = photos.next()
            mood = Mood(mood_type=mood_type)
            mood.save()
            media = Media.objects.create(content_type = Media.PICTURE)
            media.save()
            media.moods.add(mood)
            media.save()
            rank = Rank.objects.create(media=media, thumbs_up=0, thumbs_down=0)
            rank.save()
            m = Picture.objects.create(url=photo.get('url_q'), flickr_id=photo.get('id'), media=media)
            print m.url
            m.save()
