'''
Created on Nov 14, 2012

@author: hunlan
'''
import gdata.youtube.service
from servercore.CmmData.models import Video, Media, destory, Mood
from servercore.CmmCore.ContentDataOrganizer.Retrievers import ContentRetriever
import math
import random
from servercore.CmmCore.ContentDataOrganizer.Filters.scorefilter import ScoreFilter

# Number to search results
_SEARCH_NUM = 250

# Number of search result per page, Max = 50 (youtube spec)
_MAX_RESULTS = 50

# youtube object
yt = gdata.youtube.service.YouTubeService()

def getPrimeTerm(mood):
    if mood == Mood.HAPPY:
        return ContentRetriever.HAPPY_PRIME_TERM
    elif mood == Mood.ROMANTIC:
        return ContentRetriever.ROMANTIC_PRIME_TERM
    
    raise Exception('mood is not happy or romantic...')

def getSecTerm(mood):
    if mood == Mood.HAPPY:
        return ContentRetriever.HAPPY_SEC_TERMS
    elif mood == Mood.ROMANTIC:
        return ContentRetriever.ROMANTIC_SEC_TERMS
    
    raise Exception('mood is not happy or romantic...')

def computeVideoScore(entry, mood):
    prime_term = getPrimeTerm(mood)
    sec_terms = getSecTerm(mood)
                                            
    entryid = _parseId(entry)
    comment_feed = yt.GetYouTubeVideoCommentFeed(video_id=entryid)
    num_comments = len(comment_feed.entry)
    num_views = entry.statistics.view_count
    num_favs = entry.statistics.favorite_count
    
    title = entry.title.text
    isPrimeMatch = prime_term in title
    num_sec = 0
    for term in sec_terms:
        if term in title:
            num_sec += 1
            
    initial_score = \
        ContentRetriever.computeInitialScore(isPicture=False, \
                                             isPrimeMatch=isPrimeMatch, \
                                             numSecond = num_sec, \
                                             views=num_views, \
                                             comments=num_comments, \
                                             favs=num_favs)
    return initial_score

def pullAndFilter(mood, terms, add_num, partition_num):
    # get _SEARCH_NUM of video from Youtube
    entries = _getEntries(yt, terms, _SEARCH_NUM)
    
    #calculate how many video to do
    length = len(entries)
    myLen = length if partition_num > length else partition_num
    
    #generate a random index
    startIndex = random.randint(0, max(length - myLen, 0))
    
    video_map = range(0, myLen)
    
    added_video_list = []
    
    for i in range(0, myLen):
        vid_id = None
        
        print 'video progress: ' + str(i) + '/' + str(myLen)
        while len(video_map) != 0:
            idx = (video_map.pop() + startIndex) % length
            entry = entries[idx]
            entryid = _parseId(entry)
            
            initial_score = computeVideoScore(entry, mood)
            try:
                Video.objects.get(youtube_id=entryid)
                if len(video_map) == 0:
                    # fail gracefully
                    print 'Max out video search results'
                    
            except Exception:
                vid_id = entryid 
                break
        
            
        url = _getURL(vid_id)
        added_video_list.append((vid_id, url, mood, initial_score))
    
    sorted_video_list = sorted(added_video_list, key=lambda tuple: tuple[3], reverse=True)
    
    preadd = min(add_num, len(sorted_video_list))
    for i in range(0, preadd):
        tuple = sorted_video_list.pop(0)
        assert Video.add(tuple[0], tuple[1], tuple[2], initialScore=tuple[3])
        
    medias = Media.objects.filter(moods=mood, content_type=Media.VIDEO)
    ScoreFilter._calculateFinalScore(medias)
    
    mediaData = Media.objects.filter(moods=mood, score__final_score__gt=-1).order_by('score__final_score')
    if len(mediaData) == 0:
        return preadd
    
    lowestMediaIndex = 0
    while len(sorted_video_list) != 0:
        tuple = sorted_video_list.pop(0)
        score = tuple[3]
        if score > mediaData[lowestMediaIndex].score.final_score:
            destory(mediaData[lowestMediaIndex].id, Media.VIDEO)
            assert Video.add(tuple[0], tuple[1], tuple[2], initialScore=tuple[3])
            
            lowestMediaIndex += 1
            if lowestMediaIndex >= len(mediaData):
                break
        else:
            break
        
    return preadd + lowestMediaIndex

'''
Method to pull videos to database

@param mood Mood
@param terms List of serach terms
@param add_num (default = 0) number of videos to add
'''
def pullVideos(mood, terms, add_num = 0):
    # get _SEARCH_NUM of video from Youtube
    entries = _getEntries(yt, terms, _SEARCH_NUM)
    
    # calculate how many video to do 
    length = len(entries)  
    myLen = length if add_num > length else add_num
    
    # keep track of which video we are looking at
    video_map = range(0, length)
    random.shuffle(video_map)
    
    print "entered pulll videos"

    for i in range(0, myLen):
        vid_id = None
        while len(video_map) != 0:
            idx = video_map.pop()
            entry = entries[idx]
            entryid = _parseId(entry)
            
            initial_score = computeVideoScore(entry, mood)
            try:
                Video.objects.get(youtube_id=entryid)
                if len(video_map) == 0:
                    # fail gracefully
                    print 'Max out video search results'
                    
            except Exception:
                vid_id = entryid 
                break
        
            
        url = _getURL(vid_id)
        assert Video.add(vid_id, url, mood, initialScore=initial_score)

# returns num_entries rounded to nearest 50
def _getEntries(yt, terms, num_entries, offset = 0):
    repeat = int(math.ceil(1.0 * num_entries / _MAX_RESULTS))
    
    entries = []
    for i in range(0, repeat):
        query = None
        feed = None
        query = gdata.youtube.service.YouTubeVideoQuery()
        query.vq = _concatListOfString(terms)
        query.orderby = 'viewCount'
        query.racy = 'exclude'
        query._SetMaxResults(_MAX_RESULTS)
        query.start_index = i * _MAX_RESULTS + 1 + offset
        feed = yt.YouTubeQuery(query)
        entries.extend(feed.entry)
        
    return entries

# convert a list into a string seperated by space
def _concatListOfString(terms):
    string = ''
    for s in terms:
        string += s + ' '
        
    return string

# get youtube id from youtube entry
def _parseId(entry):
    string = entry.id.text
    tokens = string.split('/')
    return tokens[-1]

# get url from youtube id
def _getURL(id):
    return 'https://www.youtube.com/watch?v=' + str(id)


