'''
Created on Oct 6, 2012

@author: hunlan
'''
from server.somedata.models import Characters
from piston.handler import BaseHandler
import json
import flickrapi
import random
from server.flickr.models import FlickModel


api_key = 'f9691e72075ff63d137fefa44511fbaa'

class DatabaseHandler(BaseHandler):
    allowed_methods = ('GET',)
    model = Characters
    
    def read(self, request):
        base = Characters.objects
        
        # if character is in request
        if 'character' in request.GET:
            user_input = request.GET['character'].lower()
            if user_input.strip() == '':
                return 'error'
            
            try:
                ret = base.get(chars=user_input)
            except Characters.DoesNotExist:
                return json.dumps({"error": "no character in database"}, sort_keys=True)
            else:
                return ret 
        else:
            return base.all()
        

class FlickrHandler(BaseHandler):
    allowed_methods = ('GET',)
    model = FlickModel
    
    def read(self, request):
        if 'q' in request.GET:
            user_input = request.GET['q']

        else:
            user_input = 'joke'   
        
        flickr = flickrapi.FlickrAPI(api_key)
        pics = flickr.photos_search(api_key=api_key, text=user_input)

        length = len(pics[0])
        randompic = int(random.random() * length)
        first_attrib = pics[0][randompic].attrib
        link = 'http://static.flickr.com/' + first_attrib['server'] + '/' + first_attrib['id'] + "_" + first_attrib['secret'] + ".jpg"
        return json.dumps({"url": link}, sort_keys=True)