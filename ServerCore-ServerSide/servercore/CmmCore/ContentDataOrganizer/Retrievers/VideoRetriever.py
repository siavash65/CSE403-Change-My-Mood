'''
Created on Nov 14, 2012

@author: hunlan
'''
import gdata.youtube.service
from servercore.CmmData.models import Video
import math
import random

# Number to search results
_SEARCH_NUM = 250

# Number of search result per page, Max = 50 (youtube spec)
_MAX_RESULTS = 50

'''
Method to pull videos to database

@param mood Mood
@param terms List of serach terms
@param add_num (default = 0) number of videos to add
'''
def pullVideos(mood, terms, add_num = 0):
    # youtube object
    yt = gdata.youtube.service.YouTubeService()
    
    # get _SEARCH_NUM of video from Youtube
    entries = _getEntries(yt, terms, _SEARCH_NUM)
    
    # calculate how many video to do 
    length = len(entries)  
    myLen = length if add_num > length else add_num
    
    # keep track of which video we are looking at
    video_map = range(0, length)
    random.shuffle(video_map)

    for i in range(0, myLen):
        vid_id = None
        while len(video_map) != 0:
            idx = video_map.pop()
            entry = entries[idx]
            entryid = _parseId(entry)
            try:
                Video.objects.get(youtube_id=entryid)
                if len(video_map) == 0:
                    # fail gracefully
                    print 'Max out video search results'
                    
            except Exception:
                vid_id = entryid 
                break
        
            
        url = _getURL(vid_id)
        assert Video.add(vid_id, url, mood)

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
        query.racy = 'include'
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


