'''
Created on Oct 31, 2012

@author: hunlan
'''

import random
import flickrapi
from servercore.CmmData.models import Picture, Rank, Media, User, Mood, Score,\
    FilterCheck, Video, Deleted
from servercore.CmmCore.ContentDataOrganizer.Filters.basicfilter import BasicFilter
from servercore.CmmCore.ContentDataOrganizer.Retrievers import PictureRetriever,\
    VideoRetriever
from servercore.CmmCore.ContentDataOrganizer.Filters.scorefilter import ScoreFilter
from servercore.CmmCore.ContentDataOrganizer.Filters.brokenfilter import BrokenFilter

class ContentDataOrganizer():
    # Number of data to filter
    DELETE_RATIO = 0.1    
    
    # Data parameters
    HAPPY_TERMS = ['funny', 'hilarious', 'comical', 'humorous', 'entertaining']
    ROMANTIC_TERMS = ['love', 'loving', 'romantic', 'affection', 'passionate']
    EXCITED_TERMS = ['excited']
    INSPIRED_TERMS = ['inspired']
    
    NUM_DATA = 50
    PARTITION_NUM = 20
    
    @staticmethod
    def collectContentCronJob(mood, content):
        datas = Media.objects.filter(moods=mood, content_type = content)
        num_data_needed = ContentDataOrganizer.NUM_DATA - len(datas)
        
#        if num_data_needed <= 0:
#            print 'already have enough data'
        
        if content == Media.PICTURE:
            print 'Picture filter: ' + mood + ', needed ' + str(num_data_needed) + ' pictures'
            numAdded = PictureRetriever.pullAndFilter(mood, \
                                                      ContentDataOrganizer._getRandomTerms(mood), \
                                                      num_data_needed, \
                                                      ContentDataOrganizer.PARTITION_NUM)
            print '-- added: ' + str(numAdded) + ' new pictures'
#            PictureRetriever.pullPictures(mood, \
#                                          ContentDataOrganizer._getRandomTerms(mood), \
#                                          num_data_needed)
        elif content == Media.VIDEO:
            print 'Video filter: ' + mood + ', needed ' + str(num_data_needed) + ' videos'
            numAdded = VideoRetriever.pullAndFilter(mood, \
                                                    ContentDataOrganizer._getRandomTerms(mood), \
                                                    num_data_needed, \
                                                    ContentDataOrganizer.PARTITION_NUM)
            print '-- added: ' + str(numAdded) + ' new videos'
#            if num_data_needed <= 0:
#                print 'already have enough data'
#            VideoRetriever.pullVideos(mood, \
#                                      ContentDataOrganizer._getRandomTerms(mood), \
#                                      num_data_needed)
            
        return None
    
    @staticmethod
    def _getRandomTerms(mood):
        ret = []
        term = ContentDataOrganizer._getTerms(mood)
        
        # random num of terms
        num_terms = random.randint(1, len(term))
        
        # random terms
        random.shuffle(term)
        for i in range(0, num_terms) :
            ret.append(term.pop())
            
        return ret
    
    @staticmethod
    def _getTerms(mood):
        if mood == Mood.HAPPY:
            return ContentDataOrganizer.HAPPY_TERMS[:]
        elif mood == Mood.ROMANTIC:
            return ContentDataOrganizer.ROMANTIC_TERMS[:]
        elif mood == Mood.EXCITED:
            return ContentDataOrganizer.EXCITED_TERMS[:]
        elif mood == Mood.INSPIRED:
            return ContentDataOrganizer.INSPIRED_TERMS[:]
        
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
    
    @staticmethod
    def brokenURLFilterCronJob(mood = Mood.HAPPY, content = Media.PICTURE):
        return BrokenFilter.filter(mood, content)

    
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
        Video.objects.all().delete()
        Rank.objects.all().delete()
        User.objects.all().delete()
        Score.objects.all().delete()
        FilterCheck.objects.all().delete()
        # Deleted.objects.all().delete()
        # Mood.objects.all().delete()
        
        return None