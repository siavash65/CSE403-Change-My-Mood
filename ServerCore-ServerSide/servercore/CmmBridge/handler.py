'''
Created on Oct 26, 2012

@author: hunlan
'''
from piston.handler import BaseHandler
from servercore.CmmCore.ApiDataProvider.ApiDataProvider import ApiDataProvider
from servercore.CmmBridge.models.hellodata.models import Hello
from servercore.CmmBridge.models.content_model.models import ContentModel
from servercore.util.contents import Contents
from servercore.util.moods import Moods
from servercore.CmmBridge.models.rank_model.models import RankModel
from servercore.util.ranks import Ranks
from servercore.util.datanames import DataNames
from piston.utils import rc

class HelloHandler(BaseHandler):
    allowed_methods = ('GET',)
    model = Hello
    
    def read(self, request):
        return ApiDataProvider.helloworld();

'''
This is a class that handles the api call for getting
content data.
'''
class ContentHandler(BaseHandler):
    allowed_methods = ('GET',)
    model = ContentModel
    
    def read(self, request):
        
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
            
            
            
            