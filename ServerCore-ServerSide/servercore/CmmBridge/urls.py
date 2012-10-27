'''
Created on Oct 26, 2012

@author: hunlan
'''
from django.conf.urls import patterns, url
from servercore.CmmBridge.handler import HelloHandler, ContentHandler
from piston.resource import Resource

hello_handler = Resource(HelloHandler)
content_handler = Resource(ContentHandler)

urlpatterns = patterns('',
    # returns a HTTP Get hello world.
    url(r'^(?i)helloworld/', hello_handler),
    url(r'^(?i)getContent/', content_handler),
    url(r'^(?i)rateContent/', hello_handler),
    url(r'^(?i)updateUserInfo/', hello_handler),
    url(r'^(?i)getAbouts/', hello_handler),
    url(r'^(?i)sendMessage/', hello_handler),
)