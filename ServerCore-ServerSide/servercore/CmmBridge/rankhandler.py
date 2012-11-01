'''
Created on Oct 31, 2012

@author: hunlan
'''
from piston.handler import BaseHandler
from servercore.CmmBridge.models.rank_model.models import RankModel
from servercore.CmmCore.ApiDataProvider.ApiDataProvider import ApiDataProvider
from servercore.util.datanames import DataNames
from servercore.util.ranks import Ranks

            
class RankHandler(BaseHandler):
    allowed_methods = ('POST',)
    model = RankModel
    
    def create(self, request):
        if (Ranks.name() in request.POST) and (DataNames.MID in request.POST):
            myRank = None
            myMid = None
            
            # check input is integer
            try:
                myRank = int(request.POST[Ranks.name()])
                myMid = int(request.POST[DataNames.MID])
            except Exception:
                return ApiDataProvider.returnError('bad input')
            
                        # check if input is within bounds
            if not(myRank in Ranks.all):
                return ApiDataProvider.returnError('input out of bounds')
            
            # save guard
            #try:
            return ApiDataProvider.rateContent(myMid, myRank)
            #except Exception:
                #return ApiDataProvider.returnError('unexpected error')
        else :
            return ApiDataProvider.returnError('bad request')