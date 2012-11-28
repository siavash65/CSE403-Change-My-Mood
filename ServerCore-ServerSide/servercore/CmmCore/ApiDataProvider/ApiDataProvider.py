'''
Created on Oct 26, 2012

This is a class that handles API calls.

@author: hunlan
'''
import json
import random
from servercore.CmmData.models import Media, Picture, Mood, User, Rank, Video

class ApiDataProvider():
    _TAG = 'ApiDataProvider-'
    
    STATUS_SUCCESS = 'success'
    STATUS_ERROR = 'error'
    PARAM_URL = 'url'
    MEDIA_ID = 'mid'
    PARAM_THUMBS_UP = 'ups'
    PARAM_THUMBS_DOWN = 'downs'
    
    
    @staticmethod
    def helloworld():
        return json.dumps({'hello': 'world'}, sort_keys=True)
    
    @staticmethod
    def getContent(my_mood, my_content):
        # check inputs
        assert my_mood in Mood.MOOD_TYPES
        assert my_content in Media.CONTENT_TYPES
        
        # get list of content from database according to mood and content
        content_subset = Media.objects.filter(moods = my_mood, content_type = my_content)
        
        # check if content is empty or not
        content_subset_len = len(content_subset)
        if content_subset_len == 0:
            return ApiDataProvider.returnError(ApiDataProvider._TAG + 'empty database')
        
        # create random index and get single element
        random_index = int(random.random() * content_subset_len)
        cur_content = content_subset[random_index]

        # Rank        
        rank = Rank.objects.get(media = cur_content.id)
        tup = rank.thumbs_up
        tdown = rank.thumbs_down
        
        # switch on content
        if my_content == Media.PICTURE:
            # get picture
            try:
                cur_pic = Picture.objects.get(media = cur_content.id)
                assert isinstance(cur_pic, Picture)
                
                return {ApiDataProvider.MEDIA_ID: cur_content.id,\
                        ApiDataProvider.PARAM_URL: cur_pic.url,\
                        ApiDataProvider.PARAM_THUMBS_UP: tup,\
                        ApiDataProvider.PARAM_THUMBS_DOWN: tdown} 
            except Exception:
                return ApiDataProvider.returnError(ApiDataProvider._TAG + 'database corrupted')
        elif my_content == Media.VIDEO:
            try:
                cur_vid = Video.objects.get(media = cur_content.id)
                assert isinstance(cur_vid, Video)
                
                return {ApiDataProvider.MEDIA_ID: cur_content.id,\
                        ApiDataProvider.PARAM_URL: cur_vid.url,\
                        ApiDataProvider.PARAM_THUMBS_UP: tup,\
                        ApiDataProvider.PARAM_THUMBS_DOWN: tdown}
            except Exception:
                return ApiDataProvider.returnError(ApiDataProvider._TAG + 'database corrupted')
        elif my_content == Media.TEXT:
            return ApiDataProvider.returnError(ApiDataProvider._TAG + 'under construction')
        elif my_content == Media.AUDIO:
            return ApiDataProvider.returnError(ApiDataProvider._TAG + 'under construction')
        
        raise Exception('my_content is not in Contents')
    
    @staticmethod
    def rateContent(my_mid, thumb_type):
        # check inputs
        assert isinstance(my_mid, int)
        assert isinstance(thumb_type, int)
        assert thumb_type in Rank.RANK_TYPES
        
        # get from rank table
        media_arr = Media.objects.filter(id=my_mid)
        
        # check if invalid mid
        if len(media_arr) == 0:
            return ApiDataProvider.returnError(ApiDataProvider._TAG + 'incorrect mid')
        
        # check if database is bad, meaning more than 1 obj for a mid
        if len(media_arr) != 1:
            return ApiDataProvider.returnError(ApiDataProvider._TAG + 'database corrupted')
        
        # update rank
        media = media_arr[0]
        if thumb_type == Rank.THUMBS_UP:
            media.thumbs_up()
        elif thumb_type == Rank.THUMBS_DOWN:
            media.thumbs_down()
        else:
            return ApiDataProvider.returnError(ApiDataProvider._TAG + 'unexpected error')
        
        #return success message
        return ApiDataProvider.returnSuccess('updated rank')
    
    @staticmethod
    def getFavorites(user_id):
        user = User.objects.get(id = user_id)
        favorites = user.favorites.all()
        urls = []
        for favorite in favorites: 
            urls.append(favorite.picture.url)
        return {'url': urls}
    
    @staticmethod
    def addFavorites(user_id, media_id):
        user = User.objects.get(media_id)
        user.add_favorite(media_id)
        
    
    @staticmethod
    def returnError(msg):
        assert isinstance(msg, str)
        return {ApiDataProvider.STATUS_ERROR: msg} # json.dumps({'error': msg}, sort_keys=True)
    
    @staticmethod
    def returnSuccess(msg):
        assert isinstance(msg, str)
        return {ApiDataProvider.STATUS_SUCCESS: msg} # json.dumps({'success': msg}, sort_keys=True)
        
    
    
    