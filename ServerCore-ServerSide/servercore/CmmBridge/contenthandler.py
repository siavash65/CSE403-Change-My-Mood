'''
Created on Oct 31, 2012

@author: hunlan
'''
from piston.handler import BaseHandler
from servercore.CmmBridge.models.content_model.models import ContentModel
from servercore.CmmCore.ApiDataProvider.ApiDataProvider import ApiDataProvider
from servercore.CmmData.models import Media, Mood

'''
This is a class that handles the api call for getting
content data.
'''
class ContentHandler(BaseHandler):
    _TAG = 'ContentHandler-'
    
    allowed_methods = ('GET',)
    model = ContentModel
    
    def read(self, request):
        if (Media.URL_TAG in request.GET) and (Mood.URL_TAG in request.GET):            
            # check if input is string
            myMood = request.GET[Mood.URL_TAG]
            myContent = request.GET[Media.URL_TAG]
            
            if not (myMood in Mood.MOOD_TYPES):
                return ApiDataProvider.returnError(ContentHandler._TAG + 'bad mood input')
            
            if not (myContent in Media.CONTENT_TYPES):
                return ApiDataProvider.returnError(ContentHandler._TAG + 'bad content input')
            
            
            # check if input is within bounds
            if not((myMood in Mood.MOOD_TYPES) and (myContent in Media.CONTENT_TYPES)):
                return ApiDataProvider.returnError(ContentHandler._TAG + 'input out of bounds')
            
            # save guard
            try:
                return ApiDataProvider.getContent(myMood, myContent)
            except Exception:
                return ApiDataProvider.returnError(ContentHandler._TAG + 'unexpected error')
        else :
            return ApiDataProvider.returnError(ContentHandler._TAG + 'bad request')