'''
Created on Nov 1, 2012

@author: hunlan
'''
from piston.handler import BaseHandler
from servercore.CmmBridge.models.filtercron_model.models import FilterCronModel
from servercore.CmmCore.ContentDataOrganizer.ContentDataOrganizer import ContentDataOrganizer
from servercore.CmmCore.ApiDataProvider.ApiDataProvider import ApiDataProvider
from servercore.util.datanames import DataNames, ApiKeys
from servercore.CmmData.models import Mood, Media
from servercore.CmmCore.ContentDataOrganizer.Filters.scorefilter import ScoreFilter

'''
This class handles the cron job call and do the filtering of our database.
The create method returns true if successful
'''
class FilterCronHandler(BaseHandler):
    _TAG = 'FilterCronHandler-'
    
    FILTER_TYPE = 'filter'
    CONTENT_FILTER = 'content'
    SCORE_FILTER = 'score'
    URL_FILTER = 'broken'
    ALL_FILTER = [CONTENT_FILTER, SCORE_FILTER, URL_FILTER]
    
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
        
        # check if mood in post request
        if not (Mood.URL_TAG in request.POST):
            return ApiDataProvider.returnError(FilterCronHandler._TAG + 'no ' + Mood.URL_TAG)
        
        # check if parameter is correct
        mood = request.POST[Mood.URL_TAG]
        if not (mood in Mood.MOOD_TYPES) :
            return ApiDataProvider.returnError('mood input outofbounds')
        
        # check if content in post request
        if not (Media.URL_TAG in request.POST) :
            return ApiDataProvider.returnError(FilterCronHandler._TAG + 'no ' + Media.URL_TAG)
        
        # check if parameter is correct
        content = request.POST[Media.URL_TAG]
        if not (content in Media.CONTENT_TYPES) :
            return ApiDataProvider.returnError('content input outofbounds')
        
        # check if type is defined
        if not (FilterCronHandler.FILTER_TYPE in request.POST):
            return ApiDataProvider.returnError(FilterCronHandler._TAG + 'no ' + FilterCronHandler.FILTER_TYPE)
        
        # check if parameter is correect
        type = request.POST[FilterCronHandler.FILTER_TYPE]
        if not (type in FilterCronHandler.ALL_FILTER) :
            return ApiDataProvider.returnError('filter type input outofbounds')
        
        # perform cron job
        if type == FilterCronHandler.CONTENT_FILTER:
            #deprecated
            if ContentDataOrganizer.filterContentCronJob(mood):
                return ApiDataProvider.returnSuccess('success filtered data')
            else:
                return ApiDataProvider.returnError(FilterCronHandler._TAG + 'error filtering data')
        elif type == FilterCronHandler.SCORE_FILTER:
            res = ContentDataOrganizer.scoreThresholdFilterCronJob(mood, content)
            if res > 0:
                print 'filtered ' + str(res)
                return ApiDataProvider.returnSuccess('filtered ' + str(res) + ' contents')
            elif res == ScoreFilter.NO_FINAL_SCORE:
                return ApiDataProvider.returnSuccess('no data need to be filtered')
            elif res == ScoreFilter.EMPTY_DB:
                return ApiDataProvider.returnError('nothing in database')
            
        
        
        
        