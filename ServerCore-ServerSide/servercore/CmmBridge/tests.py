"""
This file demonstrates writing tests using the unittest module. These will pass
when you run "manage.py test".

Replace this with more appropriate tests for your application.
"""

from django.test import TestCase
from servercore.CmmData.models import Pictures, Media, Rank
from servercore.util.moods import Moods
from servercore.util.contents import Contents
from django.http import HttpRequest
from servercore.CmmBridge.handler import ContentHandler, RankHandler
from servercore.util.ranks import Ranks
from servercore.util.datanames import DataNames
from servercore.CmmCore.ApiDataProvider.ApiDataProvider import ApiDataProvider


class SimpleTest(TestCase):
    
    
    def setUp(self):
        self.websites = ['www.google.com', 'www.bing.com', 'www.yahoo.com']
        self.content = ContentHandler()
        self.rank = RankHandler()
        
        m1 = Media(id = 0, mood = Moods.HUMOROUS, content = Contents.PICTURE)
        m2 = Media(id = 1, mood = Moods.HUMOROUS, content = Contents.PICTURE)
        m3 = Media(id = 2, mood = Moods.HUMOROUS, content = Contents.PICTURE)
        
        p1 = Pictures(id = 0, mid = 0, url = self.websites[0])
        p2 = Pictures(id = 1, mid = 1, url = self.websites[1])
        p3 = Pictures(id = 2, mid = 2, url = self.websites[2])
        
        r1 = Rank(id = 0, mid = 0, thumbs_up = 0, thumbs_down = 0)
        r2 = Rank(id = 1, mid = 1, thumbs_up = 1, thumbs_down = 20)
        r3 = Rank(id = 2, mid = 2, thumbs_up = 7, thumbs_down = 5)
        
        m1.save()
        m2.save()
        m3.save()
        p1.save()
        p2.save()
        p3.save()
        r1.save()
        r2.save()
        r3.save()
        
        pass
    
    def tearDown(self):
        Media.objects.all().delete()
        Pictures.objects.all().delete()
        Rank.objects.all().delete()
        pass
    
    '''
    Get Content Test
    '''
    def test_getContent_normalsituation(self):
        mocker = HttpRequest()
        mocker.GET[Contents.name()] = '0'
        mocker.GET[Moods.name()] = '0'
        
        json_str = self.content.read(mocker)
        self.assertTrue(json_str[ApiDataProvider.PARAM_URL] in self.websites)
        
    def test_getContent_normalsituation_with_integer(self):
        mocker = HttpRequest()
        mocker.GET[Contents.name()] = 0
        mocker.GET[Moods.name()] = 0
        
        json_str = self.content.read(mocker)
        self.assertTrue(json_str[ApiDataProvider.PARAM_URL] in self.websites)

    def test_getContent_noparameters(self):
        mocker = HttpRequest()
        
        json_str = self.content.read(mocker)
        self.assertTrue(ApiDataProvider.STATUS_ERROR in json_str)
    
    def test_getContent_oneparameter(self):
        mocker1 = HttpRequest()
        mocker2 = HttpRequest()
        mocker1.GET[Contents.name()] = '0'
        mocker2.GET[Moods.name()] = '0'
        
        json_str1 = self.content.read(mocker1)
        self.assertTrue(ApiDataProvider.STATUS_ERROR in json_str1)
        
        json_str2 = self.content.read(mocker2)
        self.assertTrue(ApiDataProvider.STATUS_ERROR in json_str2)
        
    
    def test_getContent_paramoutofbounds(self):
        mocker = HttpRequest()
        mocker.GET[Contents.name()] = '5'
        mocker.GET[Moods.name()] = '0'
        
        json_str = self.content.read(mocker)
        self.assertTrue(ApiDataProvider.STATUS_ERROR in json_str)
        
    def test_getContent_emptyDatabase(self):
        mocker = HttpRequest()
        mocker.GET[Contents.name()] = '1'
        mocker.GET[Moods.name()] = '0'
        
        json_str = self.content.read(mocker)
        self.assertTrue(ApiDataProvider.STATUS_ERROR in json_str)
        
    
    '''
    Rate Content Test
    '''
    def test_rateContent_normalCase_thumbup(self):
        mocker = HttpRequest()
        mocker.POST[Ranks.name()] = Ranks.THUMBS_UP
        mocker.POST[DataNames.MID] = '0'
        
        json_str = self.rank.create(mocker)
        
        self.assertTrue(ApiDataProvider.STATUS_SUCCESS in json_str)
        
        thumbedup_object = Rank.objects.get(mid=0)
        self.assertEqual(1, thumbedup_object.thumbs_up)
        self.assertEqual(0, thumbedup_object.thumbs_down)
        
    def test_rateContent_normalCase_thumbdown(self):
        mocker = HttpRequest()
        mocker.POST[Ranks.name()] = Ranks.THUMBS_DOWN
        mocker.POST[DataNames.MID] = '0'
        
        json_str = self.rank.create(mocker)
        
        self.assertTrue(ApiDataProvider.STATUS_SUCCESS in json_str)
        
        thumbeddown_object = Rank.objects.get(mid=0)
        self.assertEqual(0, thumbeddown_object.thumbs_up)
        self.assertEqual(1, thumbeddown_object.thumbs_down)
        
    def test_rateContent_emptyParameter(self):
        mocker = HttpRequest()
        
        json_str = self.rank.create(mocker)
        
        self.assertTrue(ApiDataProvider.STATUS_ERROR in json_str)   
        
    def test_rateContent_missingparam(self):
        mocker1 = HttpRequest()
        mocker1.POST[DataNames.MID] = '0'
        
        json_str1 = self.rank.create(mocker1)
        
        self.assertTrue(ApiDataProvider.STATUS_ERROR in json_str1)    
    
        mocker2 = HttpRequest()
        mocker2.POST[Ranks.name()] = Ranks.THUMBS_UP
        
        json_str2 = self.rank.create(mocker2)
        
        self.assertTrue(ApiDataProvider.STATUS_ERROR in json_str2)    
    
        
    def test_rateContent_paramOutOfBounds(self):
        mocker = HttpRequest()
        mocker.POST[Ranks.name()] = '2'
        mocker.POST[DataNames.MID] = '0'
        
        json_str = self.rank.create(mocker)
        
        self.assertTrue(ApiDataProvider.STATUS_ERROR in json_str)
        
    def test_rateContent_databaseEmpty(self):
        mocker = HttpRequest()
        mocker.POST[Ranks.name()] = Ranks.THUMBS_DOWN
        mocker.POST[DataNames.MID] = '4'
        
        json_str = self.rank.create(mocker)
        
        self.assertTrue(ApiDataProvider.STATUS_ERROR in json_str)