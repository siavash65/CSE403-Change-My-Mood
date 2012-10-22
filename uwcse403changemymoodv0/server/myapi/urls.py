'''
Created on Oct 6, 2012

@author: hunlan
'''
from django.conf.urls import patterns, url
from piston.resource import Resource
from handlers import DatabaseHandler
from handlers import FlickrHandler

db_handler = Resource(DatabaseHandler)
flickr_handler = Resource(FlickrHandler)

urlpatterns = patterns('',
    url(r'^characters/', db_handler, {'emitter_format':'json'}),
    url(r'^flickr/', flickr_handler),
)
