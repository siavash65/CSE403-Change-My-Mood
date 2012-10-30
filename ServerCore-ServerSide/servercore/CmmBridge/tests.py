"""
This file demonstrates writing tests using the unittest module. These will pass
when you run "manage.py test".

Replace this with more appropriate tests for your application.
"""

from django.test import TestCase
from servercore.CmmData.models import Pictures, Media
from servercore.util.moods import Moods
from servercore.util.contents import Contents
import json
from django.http import HttpRequest
from servercore.CmmBridge.handler import ContentHandler


class SimpleTest(TestCase):
    
    
    def setUp(self):
        self.websites = ['www.google.com', 'www.bing.com', 'www.yahoo.com']
        self.uut = ContentHandler()
        
        m1 = Media(mid = 0, mood = Moods.HUMOROUS, content = Contents.PICTURE)
        m2 = Media(mid = 1, mood = Moods.HUMOROUS, content = Contents.PICTURE)
        m3 = Media(mid = 2, mood = Moods.HUMOROUS, content = Contents.PICTURE)
        
        p1 = Pictures(mid = 0, url = self.websites[0])
        p2 = Pictures(mid = 1, url = self.websites[1])
        p3 = Pictures(mid = 2, url = self.websites[2])
        
        m1.save()
        m2.save()
        m3.save()
        p1.save()
        p2.save()
        p3.save()
        
        pass
    
    def tearDown(self):
        pass
    
    def test_getContent_normalsituation(self):
        mocker = HttpRequest()
        mocker.GET['content'] = '0'
        mocker.GET['mood'] = '0'
        
        json_str = self.uut.read(mocker)
        self.assertTrue(json_str['url'] in self.websites)
        
    def test_getContent_normalsituation_with_integer(self):
        mocker = HttpRequest()
        mocker.GET['content'] = 0
        mocker.GET['mood'] = 0
        
        json_str = self.uut.read(mocker)
        self.assertTrue(json_str['url'] in self.websites)

    def test_getContent_noparameters(self):
        mocker = HttpRequest()
        
        json_str = self.uut.read(mocker)
        self.assertTrue('error' in json_str)
    
    def test_getContent_oneparameter(self):
        mocker1 = HttpRequest()
        mocker2 = HttpRequest()
        mocker1.GET['content'] = '0'
        mocker2.GET['mood'] = '0'
        
        json_str1 = self.uut.read(mocker1)
        self.assertTrue('error' in json_str1)
        
        json_str2 = self.uut.read(mocker2)
        self.assertTrue('error' in json_str2)
        
    
    def test_getContent_paramoutofbounds(self):
        mocker = HttpRequest()
        mocker.GET['content'] = '5'
        mocker.GET['mood'] = '0'
        
        json_str = self.uut.read(mocker)
        self.assertTrue('error' in json_str)
        
    def test_getContent_emptyDatabase(self):
        mocker = HttpRequest()
        mocker.GET['content'] = '1'
        mocker.GET['mood'] = '0'
        
        json_str = self.uut.read(mocker)
        self.assertTrue('error' in json_str)
        