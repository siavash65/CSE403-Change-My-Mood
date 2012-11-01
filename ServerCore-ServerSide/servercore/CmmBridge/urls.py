'''
Created on Oct 26, 2012

@author: hunlan
'''
from django.conf.urls import patterns, url
from servercore.CmmBridge.handler import HelloHandler
from piston.resource import Resource
from servercore.CmmBridge.contenthandler import ContentHandler
from servercore.CmmBridge.rankhandler import RankHandler
from servercore.CmmBridge.picturecrawlinghandler import PictureCrawlingHandler
from servercore.CmmBridge.deletedbhandler import DeleteDBHandler
from servercore.CmmBridge.filtercronhandler import FilterCronHandler

# standard public interface
hello_handler = Resource(HelloHandler)
content_handler = Resource(ContentHandler)
rank_handler = Resource(RankHandler)

# private cron interface
filtercron_handler = Resource(FilterCronHandler)

# hacking around
picturecrawling_handler = Resource(PictureCrawlingHandler)
deletedb_handler = Resource(DeleteDBHandler)

urlpatterns = patterns('',
    # returns a HTTP Get hello world.
    url(r'^(?i)helloworld/', hello_handler),
    url(r'^(?i)getContent/', content_handler, { 'emitter_format': 'json' }),
    url(r'^(?i)rateContent/', rank_handler),
    url(r'^(?i)updateUserInfo/', hello_handler),
    url(r'^(?i)getAbouts/', hello_handler),
    url(r'^(?i)sendMessage/', hello_handler),
    
    # cron calls
    url(r'^(?i)filter/', filtercron_handler),
    
    #hunlan hacking around
    url(r'^(?i)importsomePic/', picturecrawling_handler),
    url(r'^(?i)deleteall/', deletedb_handler),
)