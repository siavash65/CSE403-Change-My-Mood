'''
Created on Oct 18, 2012

This is the url connections redirected from the main's url.py.

Since our app only have one page, the url mapping is /

@author: hunlan
'''

from django.conf.urls.defaults import patterns, url

urlpatterns = patterns('sectionproject.uploadAndPrint.views',
    url(r'^$', 'list', name='list'), 
)