'''
Created on Oct 31, 2012

@author: hunlan
'''
from piston.handler import BaseHandler
from servercore.CmmBridge.models.content_model.models import ContentModel
from servercore.util.contents import Contents
from servercore.util.moods import Moods
from servercore.CmmCore.ApiDataProvider.ApiDataProvider import ApiDataProvider

'''
This is a class that handles the api call for getting
content data.
'''
class FavoritesHandler(BaseHandler):
    allowed_methods = ('GET','POST',)
    model = ContentModel
    
    def read(self, request):
        print request.GET
        if ('uid' in request.GET):
            user_id = NONE;
            
            # check if input is integer
            try:
                user_id = int(request.GET["uid"])
                return ApiDataProvider.getFavorites(user_id)
            except Exception:
                return ApiDataProvider.returnError('bad input')
        else :
            return ApiDataProvider.returnError('bad request')
    
    def update(self, request):
        print request.POST
        if ('uid' in request.POST & 'mid' in request.POST):
            try: 
                user_id = int(request.GET['uid'])
                media_id = int(request.GET['mid'])
                return ApiDataProvider.addFavorite(user_id, media_id)
            except Exception:
                return ApiDataProvider.returnError('bad input')
        else:
            return ApiDataProvider.returnError('bad request')
        