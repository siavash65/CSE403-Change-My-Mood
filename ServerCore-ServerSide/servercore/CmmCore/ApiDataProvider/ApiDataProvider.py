'''
Created on Oct 26, 2012

This is a class that handles API calls.

@author: hunlan
'''
from piston.handler import BaseHandler
from servercore.cmmdata.hellodata.models import Hello
import json

class HelloHandler(BaseHandler):
    allowed_methods = ('GET',)
    model = Hello
    
    def read(self, request):
        return json.dumps({'hello': 'world'}, sort_keys=True)