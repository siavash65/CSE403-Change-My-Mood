'''
Created on Oct 6, 2012

@author: hunlan
'''
from django.conf.urls import patterns, url
from hellodjango.oauth.auth import oauthpage, getReqToken, exchange


urlpatterns = patterns('',
    url(r'^$', oauthpage),
    url(r'^login/', getReqToken),
    url(r'^step1/', exchange),
)
