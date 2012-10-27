'''
Created on Oct 26, 2012

This is a class that handles API calls.

@author: hunlan
'''
import json

class ApiDataProvider():
    @staticmethod
    def helloworld():
        return json.dumps({'hello': 'world'}, sort_keys=True)