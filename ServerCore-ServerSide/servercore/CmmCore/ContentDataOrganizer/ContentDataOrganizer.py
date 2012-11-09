'''
Created on Oct 31, 2012

@author: hunlan
'''

import random
import flickrapi
from servercore.CmmData.models import Picture, Rank, Media, User, Mood
from servercore.util.moods import Moods
from servercore.util.contents import Contents
from servercore.CmmCore.ContentDataOrganizer.Filters.basicfilter import BasicFilter

class ContentDataOrganizer():
    # Number of data to filter
    DELETE_NUM = 2
    
    @staticmethod
    def collectContentCronJob():
        return None
    
    '''
    TODO: currently only support humorous picture
    
    This method filters the media data in our database and returns
    true on success
    '''
    @staticmethod
    def filterContentCronJob():
        return BasicFilter.filter(Moods.HUMOROUS, \
                                  Contents.PICTURE, \
                                  ContentDataOrganizer.DELETE_NUM)








    '''
    ----------------------------------------------------------------------
    TODO: Everything below this point is considered HACK, please clean it up.
    '''
    
    FUNNY_TERMS = ['funny']
    
    '''
    TODO: A hack method, Garrett, please purify this, do all error checking
    and make it so that it puts N data to database instead of 20.
    '''
    @staticmethod
    def putSomeData():
        # grab term
        whichTerm = int(random.random() * len(ContentDataOrganizer.FUNNY_TERMS))
        term = ContentDataOrganizer.FUNNY_TERMS[whichTerm]
        
        # our database
        allPics = Pictures.objects.filter()
        amount_needed = 50 - len(allPics)
        
        # grab from flickr
        flickr = flickrapi.FlickrAPI(ApiKeys.FLICKR_API_KEY)
        pics = flickr.photos_search(api_key=ApiKeys.FLICKR_API_KEY,\
                                    tags=term,\
                                    safe_search=1,\
                                    per_page=500)
        
        length = len(pics[0])
        myLen = length if amount_needed > length else amount_needed
        
        pic_count = 0
        for i in range(0, myLen):
            first_attrib = None
            photo_id = 0
            for j in range(pic_count, 500):
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
                    Pictures.objects.get(photo_id=photo_id)
                    # photo id already exist in database
                    if j == 499:
                        raise Exception('I FAILED!!')
                except Exception:
                    break
        
            # make media
            m = Media(mood=Moods.HUMOROUS, content=Contents.PICTURE)
            m.save()
        
            # put photo data. TODO: Use flickr's object's method to generate url
            p = Pictures(mid = m.id, \
                          url = 'http://static.flickr.com/' + first_attrib['server'] \
                          + '/' + first_attrib['id'] + "_" + first_attrib['secret'] + ".jpg",\
                          photo_id = photo_id)
            p.save()
        
            # rank
            r = Rank(mid = m.id, thumbs_up=0, thumbs_down=0)
            r.save()
    
    '''
    TODO: Hunlan Hack: PLEASE DO NOT DO THIS IN PRODUCTION!!!!!
    '''    
    @staticmethod
    def clearPhotoDatabase():
        Media.objects.all().delete()
        Picture.objects.all().delete()
        Rank.objects.all().delete()
        User.objects.all().delete()
        Mood.objects.all().delete()
        
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
