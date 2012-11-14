'''
Created on Nov 14, 2012

@author: hunlan
'''
from servercore.CmmData.models import Picture
from servercore.util.datanames import ApiKeys
import flickrapi
import random

_SEARCH_NUM = 500
 
def pullPictures(mood, terms, add_num = 0):    
    # flickr object
    flickr = flickrapi.FlickrAPI(ApiKeys.FLICKR_API_KEY)
    
    # get pics
    pics = flickr.photos_search(tags=terms,\
                                safe_search=1,\
                                per_page=_SEARCH_NUM)
    
    # calculate how many pictures to do
    length = len(pics[0])
    myLen = length if add_num > length else add_num
    
    # keep track of which picture we are looking at
    picture_map = range(0, length)
    random.shuffle(picture_map)
    
    for i in range(0, myLen):
        first_attrib = None
        photo_id = 0
        pic = None
        
        while len(picture_map) != 0:
            idx = picture_map.pop()
            first_attrib = pics[0][idx].attrib
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
                if len(picture_map) == 0:
                    raise Exception('I FAILED!!')
            except Exception:
                pic = pics[0][idx]
                break
    
        url = _getURL(first_attrib)
        
        assert Picture.add(photo_id, url, mood)
        
def _getURL(first_attrib):
    return 'http://static.flickr.com/' + first_attrib['server'] + \
                '/' + first_attrib['id'] + "_" + first_attrib['secret'] + ".jpg"