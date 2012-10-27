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
    url(r'^helloworld/', hello_handler),
    url(r'^getContent/', content_handler),
    url(r'^rateContent/', hello_handler),
    url(r'^updateUserInfo/', hello_handler),
    url(r'^getAbouts/', hello_handler),
    url(r'^sendMessage/', hello_handler),
)