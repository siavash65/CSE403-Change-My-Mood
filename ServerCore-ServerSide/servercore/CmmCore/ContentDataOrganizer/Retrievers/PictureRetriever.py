'''
Created on Nov 14, 2012

@author: hunlan
'''
from servercore.CmmData.models import Picture, Mood, Media, destory
from servercore.util.datanames import ApiKeys
from servercore.CmmCore.ContentDataOrganizer.Retrievers import ContentRetriever
import flickrapi
import random
from servercore.CmmCore.ContentDataOrganizer.Filters.scorefilter import ScoreFilter

_SEARCH_NUM = 500
_NUM_AT_A_TIME = 30

_HAPPY_SEC_TERMS = ['hilarious', 'comical', 'humorous', 'entertaining']
_ROMANTIC_SEC_TERMS = ['loving', 'romantic', 'affection', 'passionate']

_HAPPY_PRIME_TERM = 'funny'
_ROMANTIC_PRIME_TERM = 'love'

flickr = flickrapi.FlickrAPI(ApiKeys.FLICKR_API_KEY)

def _getPrimeTerm(mood):
    if mood == Mood.HAPPY:
        return _HAPPY_PRIME_TERM
    elif mood == Mood.ROMANTIC:
        return _ROMANTIC_PRIME_TERM
    
    raise Exception('mood is not happy or romantic...')

def _getSecTerm(mood):
    if mood == Mood.HAPPY:
        return _HAPPY_SEC_TERMS
    elif mood == Mood.ROMANTIC:
        return _ROMANTIC_SEC_TERMS
    
    raise Exception('mood is not happy or romantic...')
 
def computePictureScore(pid, mood):
    prime_term = _getPrimeTerm(mood)
    sec_terms = _getSecTerm(mood)
    
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

def pullAndFilter(mood, terms, add_num, partition_num):
    # get pics
    pics = flickr.photos_search(tags=terms,\
                                safe_search=1,\
                                per_page=_SEARCH_NUM)
    
    # calculate how many pictures to do
    length = len(pics[0])
    
    # myLen = min(partition_num, length)
    myLen = length if partition_num > length else partition_num
    
    #generate a random index
    startIndex = random.randint(0, max(length - myLen, 0))
    
    #generate map for keeping track
    picture_map = range(0, myLen)
    
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
        added_picture_list.append((photo_id, url, mood, initial_score))
        
    sorted_picture_list = sorted(added_picture_list, key=lambda added_picture_list: added_picture_list[3], reverse=True)
    
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
        
        

def _getURL(first_attrib):
    return 'http://farm' + first_attrib['farm'] + '.staticflickr.com/' + \
            first_attrib['server'] + '/' + first_attrib['id'] + '_' + first_attrib['secret'] + '.jpg'





    '''return 'http://static.flickr.com/' + first_attrib['server'] + \
                '/' + first_attrib['id'] + "_" + first_attrib['secret'] + ".jpg"'''