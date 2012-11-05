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
class ContentHandler(BaseHandler):
    allowed_methods = ('GET',)
    model = ContentModel
    
    def read(self, request):
        print request.GET
        if (Contents.name() in request.GET) and (Moods.name() in request.GET):
            myMood = None
            myContent = None
            
            # check if input is integer
            try:
                myMood = int(request.GET[Moods.name()])
                myContent = int(request.GET[Contents.name()])
            except Exception:
                return ApiDataProvider.returnError('bad input')
            
            # check if input is within bounds
            if not((myMood in Moods.all) and (myContent in Contents.all)):
                return ApiDataProvider.returnError('input out of bounds')
            
            # save guard
            try:
                return ApiDataProvider.getContent(myMood, myContent)
            except Exception:
                return ApiDataProvider.returnError('unexpected error')
        else :
            return ApiDataProvider.returnError('bad request')