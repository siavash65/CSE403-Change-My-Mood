'''
Created on Oct 26, 2012

This is a class that handles API calls.

@author: hunlan
'''
import json
import random
from servercore.util.moods import Moods
from servercore.util.contents import Contents
from servercore.CmmData.models import Media, Pictures

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
            
                return json.dumps({'url': cur_pic.url})
            except Exception:
                return ApiDataProvider.returnError('database currupted')
        elif myContent == Contents.VIDEO:
            return ApiDataProvider.returnError('under construction')
        elif myContent == Contents.TEXT:
            return ApiDataProvider.returnError('under construction')
        elif myContent == Contents.MUSIC:
            return ApiDataProvider.returnError('under construction')
        
        raise Exception('myContent is not in Contents')
    
    @staticmethod
    def returnError(msg):
        assert isinstance(msg, str)
        return json.dumps({'error': msg}, sort_keys=True)