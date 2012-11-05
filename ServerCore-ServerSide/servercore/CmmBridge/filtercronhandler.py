'''
Created on Nov 1, 2012

@author: hunlan
'''
from piston.handler import BaseHandler
from servercore.CmmBridge.models.filtercron_model.models import FilterCronModel
from servercore.CmmCore.ContentDataOrganizer.ContentDataOrganizer import ContentDataOrganizer
from servercore.CmmCore.ApiDataProvider.ApiDataProvider import ApiDataProvider
from servercore.util.datanames import DataNames, ApiKeys

'''
This class handles the cron job call and do the filtering of our database.
The create method returns true if successful
'''
class FilterCronHandler(BaseHandler):
    allowed_methods = ('POST',)
    model = FilterCronModel
    
    def create(self, request):
        # check if post has secret parameter
        print request.POST
        if not(DataNames.SECRET in request.POST) :
            return ApiDataProvider.returnError('no ' + DataNames.SECRET)
        
        # check if secret parameter is correct
        the_secret = request.POST[DataNames.SECRET]
        if the_secret != ApiKeys.GAE_PRIVATE_KEY:
            return ApiDataProvider.returnError('incorrect private key')
        
        # perform cron job
        if ContentDataOrganizer.filterContentCronJob():
            return ApiDataProvider.returnSuccess('success filtered data')
        else:
            return ApiDataProvider.returnError('error filtering data')