'''
Created on Oct 31, 2012

TODO: THIS IS A HACK CLASS, PLEASE CLEAN IT UP

@author: hunlan
'''
from piston.handler import BaseHandler
from servercore.CmmBridge.models.deletedb_handler.models import DeleteDBModel
from servercore.CmmCore.ApiDataProvider.ApiDataProvider import ApiDataProvider
from servercore.CmmCore.ContentDataOrganizer.ContentDataOrganizer import \
    ContentDataOrganizer
from servercore.util.datanames import DataNames, ApiKeys

class DeleteDBHandler(BaseHandler):
    allowed_methods = ('POST',)
    model = DeleteDBModel
    
    def create(self, request):
        # check if post has secret parameter
        if not(DataNames.SECRET in request.POST) :
            return ApiDataProvider.returnError('no ' + DataNames.SECRET)
        
        # check if secret parameter is correct
        the_secret = request.POST[DataNames.SECRET]
        if the_secret != ApiKeys.GAE_PRIVATE_KEY:
            return ApiDataProvider.returnError('incorrect private key')
        
        try:
            ContentDataOrganizer.clearPhotoDatabase()
            return ApiDataProvider.returnSuccess('cleared database')
        except Exception:
            return ApiDataProvider.returnError('unexpected error')