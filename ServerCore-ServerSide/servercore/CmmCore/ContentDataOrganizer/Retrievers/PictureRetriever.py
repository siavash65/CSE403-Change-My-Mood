'''
Created on Nov 14, 2012

This class fetches Picture data

@author: hunlan
'''
from servercore.CmmData.models import Picture, Mood, Media, destory, Deleted
from servercore.util.datanames import ApiKeys
from servercore.CmmCore.ContentDataOrganizer.Retrievers import ContentRetriever
import flickrapi
import random
from servercore.CmmCore.ContentDataOrganizer.Filters.scorefilter import ScoreFilter

_SEARCH_NUM = 500
_NUM_AT_A_TIME = 30

flickr = flickrapi.FlickrAPI(ApiKeys.FLICKR_API_KEY)

# get primary terms
def getPrimeTerm(mood):
    if mood == Mood.HAPPY:
        return ContentRetriever.HAPPY_PRIME_TERM
    elif mood == Mood.ROMANTIC:
        return ContentRetriever.ROMANTIC_PRIME_TERM
    elif mood == Mood.EXCITED:
        return ContentRetriever.EXCITED_PRIME_TERM
    elif mood == Mood.INSPIRED:
        return ContentRetriever.INSPIRED_PRIME_TERM
    
    raise Exception('mood is not happy or romantic...')

# get secondary terms
def getSecTerm(mood):
    if mood == Mood.HAPPY:
        return ContentRetriever.HAPPY_SEC_TERMS
    elif mood == Mood.ROMANTIC:
        return ContentRetriever.ROMANTIC_SEC_TERMS
    elif mood == Mood.EXCITED:
        return ContentRetriever.EXCITED_SEC_TERMS
    elif mood == Mood.INSPIRED:
        return ContentRetriever.INSPIRED_SEC_TERMS
    
    raise Exception('mood is not happy or romantic...')

#Compute Picture Scores
def computePictureScore(pid, mood):
    prime_term = getPrimeTerm(mood)
    sec_terms = getSecTerm(mood)
    
    photo = flickr.photos_getInfo(photo_id=pid)
    attrib = photo[0].attrib
    num_views = attrib['views']
    num_favs = attrib['isfavorite']
    comments = flickr.photos_comments_getList(photo_id=pid)
    com = comments[0]
    num_comments = len(com)
    pic = photo[0]
    num_sec = 0
    isPrimeMatch = False
    
    for tag in pic.find('tags'):
        if tag.attrib['raw'] == prime_term:
            isPrimeMatch = True
        for sec_term in sec_terms:
            if (sec_term == tag.attrib['raw']):
                num_sec = num_sec + 1

    initial_score = \
        ContentRetriever.computeInitialScore(isPrimeMatch=isPrimeMatch,\
                                             numSecond=num_sec, \
                                             views=num_views, \
                                             comments=num_comments, \
                                             favs=num_favs)
    '''print 'score = ' + str(initial_score) + \
            ', pm: ' + str(isPrimeMatch) + \
            ', sec: ' + str(num_sec) + \
            ', view: ' + str(num_views) + \
            ', com: ' + str(num_comments) + \
            ', fav: ' + str(num_favs) '''
            
    return initial_score

# Pull and filter at same time
#@param: add_num = number of content to add
#@param: partition_num = how many data to pull at a time
def pullAndFilter(mood, terms, add_num, partition_num):
    # get pics
    pics = flickr.photos_search(tags=terms,\
                                safe_search=1,\
                                per_page=_SEARCH_NUM)
    
    # calculate how many pictures to do
    length = len(pics[0])
    
    # myLen = min(partition_num, length)
    myLen = length if partition_num > length else partition_num
    myLen = max(myLen, 0)
    
    #generate a random index
    startIndex = random.randint(0, max(length - myLen, 0))
    
    #generate map for keeping track
    picture_map = range(0, myLen)
    random.shuffle(picture_map)
    
    added_picture_list = []
    
    
    #looping myLen times, get out n number of pictures
    for i in range(0, myLen):
        first_attrib = None
        photo_id = 0
        
        print 'picture progress: ' + str(i) + '/' + str(myLen)
        while len(picture_map) != 0:
            idx = (picture_map.pop() + startIndex) % length
            first_attrib = pics[0][idx].attrib
            photo_id = int(first_attrib['id'])
            initial_score = computePictureScore(photo_id, mood)
            
            del_list = Deleted.objects.filter(content_type=Media.PICTURE, content_id=photo_id)
            if len(del_list) != 0:
                continue
            
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
                break
    
        try:
            url = _getURL(first_attrib)
            added_picture_list.append((photo_id, url, mood, initial_score))
        except Exception:
            print 'The None type url error again, die gracefully'
        
    sorted_picture_list = sorted(added_picture_list, key=lambda tuple: tuple[3], reverse=True)
    
    # add till database is full
    preadd = min(add_num, len(sorted_picture_list))
    for i in range(0, preadd):
        tuple = sorted_picture_list.pop(0)
        assert Picture.add(tuple[0], tuple[1], tuple[2], initialScore=tuple[3])

    # get media data    
    medias = Media.objects.filter(moods=mood, content_type=Media.PICTURE)
    ScoreFilter._calculateFinalScore(medias)
    
    mediaData = Media.objects.filter(moods=mood, score__final_score__gt=-1).order_by('score__final_score')
    if len(mediaData) == 0:
        return preadd
    
    #Add to database if score is higher than existing scores
    lowestMediaIndex = 0
    while len(sorted_picture_list) != 0:
        tuple = sorted_picture_list.pop(0)
        score = tuple[3]
        if score > mediaData[lowestMediaIndex].score.final_score:
            destory(mediaData[lowestMediaIndex].id, Media.PICTURE)
            assert Picture.add(tuple[0], tuple[1], tuple[2], initialScore=tuple[3])
            
            lowestMediaIndex += 1
            if lowestMediaIndex >= len(mediaData):
                break
        else:
            break
        
    return preadd + lowestMediaIndex
        

def pullPictures(mood, terms, add_num = 0):        
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
        
        while len(picture_map) != 0:
            idx = picture_map.pop()
            first_attrib = pics[0][idx].attrib
            photo_id = int(first_attrib['id'])
            initial_score = computePictureScore(photo_id, mood)
            
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
                break
    
        url = _getURL(first_attrib)
        assert Picture.add(photo_id, url, mood, initialScore=initial_score)
        
def fetch_flickr_photos(mood_type, terms, num_photos):        
        # grab from flickr
        photos =  flickr.walk(tag_mode = 'all', tags = terms, extras='url_z') 
        for i in range(0, num_photos):
            photo = photos.next()
            url = photo.get('url_z')
            pid = photo.get('id')
            score = computePictureScore(pid, mood_type)
            assert Picture.add(pid, url, mood_type, initialScore = score)
            


def _getURL(first_attrib):
    return 'http://farm' + first_attrib['farm'] + '.staticflickr.com/' + \
            first_attrib['server'] + '/' + first_attrib['id'] + '_' + first_attrib['secret'] + '.jpg'





    '''return 'http://static.flickr.com/' + first_attrib['server'] + \
                '/' + first_attrib['id'] + "_" + first_attrib['secret'] + ".jpg"'''