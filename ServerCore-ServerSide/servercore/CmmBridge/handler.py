'''
Created on Oct 26, 2012

@author: hunlan
'''
from piston.handler import BaseHandler
from servercore.CmmCore.ApiDataProvider.ApiDataProvider import ApiDataProvider
from servercore.CmmBridge.models.hellodata.models import Hello

class HelloHandler(BaseHandler):
    allowed_methods = ('GET',)
    model = Hello
    
    def read(self, request):
        return ApiDataProvider.helloworld();
            
            