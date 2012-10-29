'''
Created on Oct 26, 2012

This is a class that handles API calls.

@author: hunlan
'''
import json
import random
from servercore.util.moods import Moods
from servercore.util.contents import Contents
from servercore.CmmData.models import Media, Pictures, Rank
from piston.utils import rc
from servercore.util.ranks import Ranks

class ApiDataProvider():
    @staticmethod
    def helloworld():
        return json.dumps({'hello': 'world'}, sort_keys=True)
    
    @staticmethod
    def getContent(myMood, myContent):
        # check inputs
        assert myMood in Moods.all
        assert myContent in Contents.all
        
        # get list of content from database according to mood and content
        content_subset = Media.objects.filter(mood = myMood, content = myContent)
        
        # check if content is empty or not
        content_subset_len = len(content_subset)
        if content_subset_len == 0:
            return ApiDataProvider.returnError('empty database')
        
        # create random index and get single element
        random_index = int(random.random() * content_subset_len)
        cur_content = content_subset[random_index]
        
        # switch on content
        if myContent == Contents.PICTURE:
            # get picture
            try:
                cur_pic = Pictures.objects.get(mid = cur_content.mid)
                assert isinstance(cur_pic, Pictures)
            
                return {'url': cur_pic.url} #json.dumps({'url': cur_pic.url})
            except Exception:
                return ApiDataProvider.returnError('database corrupted')
        elif myContent == Contents.VIDEO:
            return ApiDataProvider.returnError('under construction')
        elif myContent == Contents.TEXT:
            return ApiDataProvider.returnError('under construction')
        elif myContent == Contents.MUSIC:
            return ApiDataProvider.returnError('under construction')
        
        raise Exception('myContent is not in Contents')
    
    @staticmethod
    def rateContent(myMid, isThumbsUp):
        # check inputs
        assert isinstance(myMid, int)
        assert isThumbsUp in Ranks.all
        
        # get from rank table
        media_arr = Rank.objects.filter(mid=myMid)
        
        # check if invalid mid
        if len(media_arr) == 0:
            return ApiDataProvider.returnError('incorrect mid')
        
        # check if database is bad, meaning more than 1 obj for a mid
        if len(media_arr != 1):
            return ApiDataProvider.returnError('database corrupted')
        
        # update rank
        media = media_arr[0]
        if isThumbsUp == Ranks.THUMBS_UP:
            media.thumbs_up = media.thumbs_up + 1
        elif isThumbsUp == Ranks.THUMBS_DOWN:
            media.thumbs_down = media.thumbs_down + 1
        else:
            return ApiDataProvider.returnError('unexpected error')
        
        #return success message
        return ApiDataProvider.returnSuccess('updated rank')
    
    @staticmethod
    def returnError(msg):
        assert isinstance(msg, str)
        return {'error': msg} # json.dumps({'error': msg}, sort_keys=True)
    
    @staticmethod
    def returnSuccess(msg):
        assert isinstance(msg, str)
        return {'success': msg} # json.dumps({'success': msg}, sort_keys=True)
        
    
    
    