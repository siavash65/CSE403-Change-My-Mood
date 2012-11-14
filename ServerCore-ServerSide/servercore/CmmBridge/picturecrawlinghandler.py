'''
Created on Oct 31, 2012

TODO: THIS IS A HACK CLASS, PLEASE CLEAN IT UP

@author: hunlan
'''
from piston.handler import BaseHandler
from servercore.CmmBridge.models.picturecrawling_model.models import \
    PictureCrawlingModel
from servercore.CmmCore.ApiDataProvider.ApiDataProvider import ApiDataProvider
from servercore.CmmCore.ContentDataOrganizer.ContentDataOrganizer import \
    ContentDataOrganizer
from servercore.util.datanames import DataNames, ApiKeys
from servercore.CmmData.models import Mood, Media

class PictureCrawlingHandler(BaseHandler):
    _TAG = 'PictureCrawlingHandler-'
    
    allowed_method = ('POST',)
    model = PictureCrawlingModel
    
    def create(self, request):
        print request.POST
        # check if post has secret parameter
        if not(DataNames.SECRET in request.POST) :
            return ApiDataProvider.returnError(PictureCrawlingHandler._TAG + \
                                               'no ' + DataNames.SECRET)
        
        # check if secret parameter is correct
        the_secret = request.POST[DataNames.SECRET]
        if the_secret != ApiKeys.GAE_PRIVATE_KEY:
            return ApiDataProvider.returnError(PictureCrawlingHandler._TAG + \
                                               'incorrect private key')
        
        # Parameter short circuits
        if not(Mood.URL_TAG in request.POST):
            return ApiDataProvider.returnError('no mood param')
        
        # Parameter short circuits
        if not(Media.URL_TAG in request.POST):
            return ApiDataProvider.returnError('no media param')
        
        # get data
        mood = request.POST[Mood.URL_TAG]
        content = request.POST[Media.URL_TAG]
        
        # out of bounds short circuit
        if not(mood in Mood.MOOD_TYPES and content in Media.CONTENT_TYPES):
            return ApiDataProvider.returnError('parameter out of range')
        
        ContentDataOrganizer.collectContentCronJob(mood, content)
        return ApiDataProvider.returnSuccess('added some photo')
        
        
        
        
        
        