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
        
        #try:
        ContentDataOrganizer.putSomeData()
        return ApiDataProvider.returnSuccess('added some photo')
        #except Exception:
        #    return ApiDataProvider.returnError(PictureCrawlingHandler._TAG + \
        #                                       'unexpected error')