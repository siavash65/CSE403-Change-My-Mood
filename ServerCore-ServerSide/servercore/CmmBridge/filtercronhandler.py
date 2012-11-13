'''
Created on Nov 1, 2012

@author: hunlan
'''
from piston.handler import BaseHandler
from servercore.CmmBridge.models.filtercron_model.models import FilterCronModel
from servercore.CmmCore.ContentDataOrganizer.ContentDataOrganizer import ContentDataOrganizer
from servercore.CmmCore.ApiDataProvider.ApiDataProvider import ApiDataProvider
from servercore.util.datanames import DataNames, ApiKeys
from servercore.CmmData.models import Mood

'''
This class handles the cron job call and do the filtering of our database.
The create method returns true if successful
'''
class FilterCronHandler(BaseHandler):
    _TAG = 'FilterCronHandler-'
    
    allowed_methods = ('POST',)
    model = FilterCronModel
    
    def create(self, request):
        # check if post has secret parameter
        print request.POST
        if not(DataNames.SECRET in request.POST) :
            return ApiDataProvider.returnError(FilterCronHandler._TAG + 'no ' + DataNames.SECRET)
        
        # check if secret parameter is correct
        the_secret = request.POST[DataNames.SECRET]
        if the_secret != ApiKeys.GAE_PRIVATE_KEY:
            return ApiDataProvider.returnError(FilterCronHandler._TAG + 'incorrect private key')
        
        if not (Mood.URL_TAG in request.POST):
            return ApiDataProvider.returnError(FilterCronHandler._TAG + 'no ' + Mood.URL_TAG)
        
        mood = request.POST[Mood.URL_TAG]
        if not (mood in Mood.MOOD_TYPES) :
            return ApiDataProvider.returnError('mood input outofbounds')
        
        # perform cron job
        if ContentDataOrganizer.filterContentCronJob(mood):
            return ApiDataProvider.returnSuccess('success filtered data')
        else:
            return ApiDataProvider.returnError(FilterCronHandler._TAG + 'error filtering data')
        
        
        
        