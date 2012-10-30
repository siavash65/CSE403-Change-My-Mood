"""
This file demonstrates writing tests using the unittest module. These will pass
when you run "manage.py test".

Replace this with more appropriate tests for your application.
"""

from django.test import TestCase
from servercore.CmmData.models import Pictures, Media
from servercore.util.moods import Moods
from servercore.util.contents import Contents
from servercore.CmmCore.ApiDataProvider.ApiDataProvider import ApiDataProvider
import json


class SimpleTest(TestCase):
    
    
    def setUp(self):
        self.websites = ['www.google.com', 'www.bing.com', 'www.yahoo.com']
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
        json_str = ApiDataProvider.getContent(Moods.HUMOROUS, Contents.PICTURE)
        self.assertTrue(json_str['url'] in self.websites)
        
    def test_getContent_emptyMood(self):
        json_str = ApiDataProvider.getContent(Moods.ENERVATE, Contents.PICTURE)
        self.assertTrue('error' in json_str)
    
    def test_getContent_emptyContent(self):
        json_str = ApiDataProvider.getContent(Moods.HUMOROUS, Contents.VIDEO)
        self.assertTrue('error' in json_str)
        
    def test_getContent_emptyBoth(self):
        json_str = ApiDataProvider.getContent(Moods.ENERVATE, Contents.VIDEO)
        self.assertTrue('error' in json_str)
        
    def test_getContent_baddatabase_nopictures(self):
        Pictures.objects.all().delete()
        json_str = ApiDataProvider.getContent(Moods.HUMOROUS, Contents.PICTURE)
        self.assertTrue('error' in json_str)
        
    def test_getContent_outofboundsinput(self):
        with self.assertRaises(AssertionError) as err:
            ApiDataProvider.getContent(4, Contents.PICTURE)
        with self.assertRaises(AssertionError) as err:  
            ApiDataProvider.getContent(Moods.HUMOROUS, 4)
        