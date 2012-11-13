'''
Created on Oct 31, 2012

@author: hunlan
'''
from piston.handler import BaseHandler
from servercore.CmmBridge.models.rank_model.models import RankModel
from servercore.CmmCore.ApiDataProvider.ApiDataProvider import ApiDataProvider
from servercore.CmmData.models import Rank

            
class RankHandler(BaseHandler):
    _TAG = 'RankHandler-'
    
    allowed_methods = ('POST',)
    model = RankModel
    
    def create(self, request):
        if (Rank.URL_TAG in request.POST) and (ApiDataProvider.MEDIA_ID in request.POST):
            myRank = None
            myMid = None
            
            # check input is integer
            try:
                myRank = int(request.POST[Rank.URL_TAG ])
                myMid = int(request.POST[ApiDataProvider.MEDIA_ID])
            except Exception:
                return ApiDataProvider.returnError(RankHandler._TAG + 'bad input')
            
                        # check if input is within bounds
            if not(myRank in Rank.RANK_TYPES):
                return ApiDataProvider.returnError(RankHandler._TAG + 'input out of bounds')
            
            # save guard
            #try:
            return ApiDataProvider.rateContent(myMid, myRank)
            #except Exception:
                #return ApiDataProvider.returnError('unexpected error')
        else :
            return ApiDataProvider.returnError(RankHandler._TAG + 'bad request')
        
        