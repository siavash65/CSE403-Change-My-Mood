'''
Created on Oct 26, 2012

@author: hunlan
'''
from django.conf.urls import patterns, url
from servercore.CmmBridge.handler import HelloHandler
from piston.resource import Resource
from servercore.CmmBridge.contenthandler import ContentHandler
from servercore.CmmBridge.rankhandler import RankHandler

hello_handler = Resource(HelloHandler)
content_handler = Resource(ContentHandler)
rank_handler = Resource(RankHandler)

urlpatterns = patterns('',
    # returns a HTTP Get hello world.
    url(r'^(?i)helloworld/', hello_handler),
    url(r'^(?i)getContent/', content_handler, { 'emitter_format': 'json' }),
    url(r'^(?i)rateContent/', rank_handler),
    url(r'^(?i)updateUserInfo/', hello_handler),
    url(r'^(?i)getAbouts/', hello_handler),
    url(r'^(?i)sendMessage/', hello_handler),
)