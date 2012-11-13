'''
Created on Oct 31, 2012

@author: hunlan
'''

import random
import flickrapi
from servercore.CmmData.models import Picture, Rank, Media, User, Mood
from servercore.CmmCore.ContentDataOrganizer.Filters.basicfilter import BasicFilter
from servercore.util.datanames import ApiKeys

class ContentDataOrganizer():
    # Number of data to filter
    DELETE_RATIO = 0.1
    
    @staticmethod
    def collectContentCronJob():
        return None
    
    '''
    TODO: currently only support humorous picture
    
    This method filters the media data in our database and returns
    true on success
    '''
    @staticmethod
    def filterContentCronJob(mood = Mood.HAPPY):
        return BasicFilter.filter(mood, \
                                  Media.PICTURE, \
                                  ContentDataOrganizer.DELETE_RATIO)








    '''
    ----------------------------------------------------------------------
    TODO: Everything below this point is considered HACK, please clean it up.
    '''
    
    FUNNY_TERMS = ['funny']
    ROMANTIC_TERMS = ['love']
    NUM_PICS = 50
    
    '''
    TODO: A hack method, Garrett, please purify this, do all error checking
    and make it so that it puts N data to database instead of 20.
    '''
    @staticmethod
    def putSomeData(mood=Mood.HAPPY):
        # grab term
        #whichTerm = int(random.random() * len(ContentDataOrganizer.FUNNY_TERMS))
        #term = ContentDataOrganizer.FUNNY_TERMS[whichTerm]
        
        # our database
        moodPics = Media.objects.filter(moods=mood, content_type = Media.PICTURE)
        num_moodPics = ContentDataOrganizer.NUM_PICS - len(moodPics)
        
        ContentDataOrganizer.pullPictures(mood, \
                                          ContentDataOrganizer.FUNNY_TERMS,\
                                          num_moodPics)
        


    @staticmethod
    def pullPictures(mood, terms, add_num = 0):
        pic_per_page = 500
        
        flickr = flickrapi.FlickrAPI(ApiKeys.FLICKR_API_KEY)
        pics = flickr.photos_search(tags=terms,\
                                    safe_search=1,\
                                    per_page=pic_per_page)
        
        length = len(pics[0])
        myLen = length if add_num > length else add_num
        
        pic_count = 0
        for i in range(0, myLen):
            first_attrib = None
            photo_id = 0
            pic = None
            for j in range(pic_count, pic_per_page):
                pic_count = pic_count + 1
                first_attrib = pics[0][j].attrib
                photo_id = int(first_attrib['id'])
                
                try:
                    # throw error if bad things happen
                    flickr.photos_getInfo(photo_id=photo_id)
                except Exception:
                    continue
                
                try:                    
                    # throw error 
                    Picture.objects.get(flickr_id=photo_id)
                    # photo id already exist in database
                    if j == 499:
                        raise Exception('I FAILED!!')
                except Exception:
                    pic = pics[0][j]
                    break
        
            url ='http://static.flickr.com/' + first_attrib['server'] + \
                    '/' + first_attrib['id'] + "_" + first_attrib['secret'] + ".jpg"
            
            assert Picture.add(photo_id, url, mood)
    
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
