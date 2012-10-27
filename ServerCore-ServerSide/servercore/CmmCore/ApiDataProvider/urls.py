'''
Created on Oct 26, 2012

@author: hunlan
'''
from django.conf.urls import patterns, url
from servercore.CmmCore.ApiDataProvider.ApiDataProvider import HelloHandler
from piston.resource import Resource

hello_handler = Resource(HelloHandler)

urlpatterns = patterns('',
    # returns a HTTP Get hello world.
    url(r'^helloworld/', hello_handler), 
)