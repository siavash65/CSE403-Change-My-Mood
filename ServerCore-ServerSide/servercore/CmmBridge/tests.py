"""
This file demonstrates writing tests using the unittest module. These will pass
when you run "manage.py test".

Replace this with more appropriate tests for your application.
"""

from django.test import TestCase
from servercore.CmmData.models import Picture, Media, Rank, Mood
from django.http import HttpRequest
from servercore.CmmCore.ApiDataProvider.ApiDataProvider import ApiDataProvider
from servercore.CmmBridge.contenthandler import ContentHandler
from servercore.CmmBridge.rankhandler import RankHandler

class TestContent(TestCase):
    
    def setUp(self):        
        self.websites = ['www.google.com', 'www.bing.com', 'www.yahoo.com']
        self.content = ContentHandler()
        
        self.m1 = Media(id = 0, content_type = Media.PICTURE)
        self.m1.moods.add(Mood.HAPPY)
        self.m2 = Media(id = 1, content_type = Media.PICTURE)
        self.m2.moods.add(Mood.HAPPY)
        self.m3 = Media(id = 2, content_type = Media.PICTURE)
        self.m3.moods.add(Mood.HAPPY)
        
        self.p1 = Picture(media = self.m1, url = self.websites[0], flickr_id = 0)
        self.p2 = Picture(media = self.m2, url = self.websites[1], flickr_id = 1)
        self.p3 = Picture(media = self.m3, url = self.websites[2], flickr_id = 2)
        
        self.r1 = Rank(media = self.m1, thumbs_up = 0, thumbs_down = 0)
        self.r2 = Rank(media = self.m2, thumbs_up = 1, thumbs_down = 20)
        self.r3 = Rank(media = self.m3, thumbs_up = 7, thumbs_down = 5)
        
        self.m1.save()
        self.m2.save()
        self.m3.save()
        self.p1.save()
        self.p2.save()
        self.p3.save()
        self.r1.save()
        self.r2.save()
        self.r3.save()
        
        pass
    
    def tearDown(self):
        Media.objects.all().delete()
        Picture.objects.all().delete()
        Rank.objects.all().delete()
        pass
    
    '''
    Get Content Test
    '''
    def test_getContent_normalsituation(self):
        mocker = HttpRequest()
        mocker.GET[Media.URL_TAG] = Media.PICTURE
        mocker.GET[Mood.URL_TAG] = Mood.HAPPY
        
        json_str = self.content.read(mocker)
        self.assertTrue(json_str[ApiDataProvider.PARAM_URL] in self.websites)
        mid_arr = [self.m1.id, self.m2.id, self.m3.id]
        self.assertTrue(json_str[ApiDataProvider.MEDIA_ID] in mid_arr)

    def test_getContent_noparameters(self):
        mocker = HttpRequest()
        
        json_str = self.content.read(mocker)
        self.assertTrue(ApiDataProvider.STATUS_ERROR in json_str)
    
    def test_getContent_oneparameter(self):
        mocker1 = HttpRequest()
        mocker2 = HttpRequest()
        mocker1.GET[Media.URL_TAG] = Media.PICTURE
        mocker2.GET[Mood.URL_TAG] = Mood.HAPPY
        
        json_str1 = self.content.read(mocker1)
        self.assertTrue(ApiDataProvider.STATUS_ERROR in json_str1)
        
        json_str2 = self.content.read(mocker2)
        self.assertTrue(ApiDataProvider.STATUS_ERROR in json_str2)
        
    
    def test_getContent_paramoutofbounds(self):
        mocker = HttpRequest()
        mocker.GET[Media.URL_TAG] = 'XX'
        mocker.GET[Mood.URL_TAG] = Mood.HAPPY
        
        json_str = self.content.read(mocker)
        self.assertTrue(ApiDataProvider.STATUS_ERROR in json_str)
        
    def test_getContent_emptyDatabase(self):
        mocker = HttpRequest()
        mocker.GET[Media.URL_TAG] = Media.AUDIO
        mocker.GET[Mood.URL_TAG] = Mood.HAPPY
        
        json_str = self.content.read(mocker)
        self.assertTrue(ApiDataProvider.STATUS_ERROR in json_str)
        




class TestRate(TestCase):
    
    def setUp(self):
        self.websites = ['www.google.com', 'www.bing.com', 'www.yahoo.com']
        self.rank = RankHandler()
        
        self.m1 = Media(id = 0, content_type = Media.PICTURE)
        self.m1.moods.add(Mood.HAPPY)
        self.m2 = Media(id = 1, content_type = Media.PICTURE)
        self.m2.moods.add(Mood.HAPPY)
        self.m3 = Media(id = 2, content_type = Media.PICTURE)
        self.m3.moods.add(Mood.HAPPY)
        
        self.p1 = Picture(media = self.m1, url = self.websites[0], flickr_id = 0)
        self.p2 = Picture(media = self.m2, url = self.websites[1], flickr_id = 1)
        self.p3 = Picture(media = self.m3, url = self.websites[2], flickr_id = 2)
        
        self.r1 = Rank(media = self.m1, thumbs_up = 0, thumbs_down = 0)
        self.r2 = Rank(media = self.m2, thumbs_up = 1, thumbs_down = 20)
        self.r3 = Rank(media = self.m3, thumbs_up = 7, thumbs_down = 5)
        
        self.m1.save()
        self.m2.save()
        self.m3.save()
        self.p1.save()
        self.p2.save()
        self.p3.save()
        self.r1.save()
        self.r2.save()
        self.r3.save()
        
        pass
    
    def tearDown(self):
        Media.objects.all().delete()
        Picture.objects.all().delete()
        Rank.objects.all().delete()
        pass
    
    '''
    Rate Content Test
    '''
    def test_rateContent_normalCase_thumbup(self):
        mocker = HttpRequest()
        mocker.POST[Rank.URL_TAG] = Rank.THUMBS_UP
        mocker.POST[ApiDataProvider.MEDIA_ID] = self.m1.id
        
        # previous state
        thumbedup_object = Rank.objects.get(media=self.m1)
        init_thumbup = thumbedup_object.thumbs_up
        init_thumbdown = thumbedup_object.thumbs_down
        
        # do stuff
        json_str = self.rank.create(mocker)
        
        # checks
        self.assertTrue(ApiDataProvider.STATUS_SUCCESS in json_str)
        
        thumbedup_object = Rank.objects.get(media=self.m1)
        self.assertEqual(init_thumbup + 1, thumbedup_object.thumbs_up)
        self.assertEqual(init_thumbdown, thumbedup_object.thumbs_down)
        
    def test_rateContent_normalCase_thumbdown(self):
        mocker = HttpRequest()
        mocker.POST[Rank.URL_TAG] = Rank.THUMBS_DOWN
        mocker.POST[ApiDataProvider.MEDIA_ID] = self.m1.id
        
        # previous state
        thumbeddown_object = Rank.objects.get(media=self.m1)
        init_thumbup = thumbeddown_object.thumbs_up
        init_thumbdown = thumbeddown_object.thumbs_down
        
        # do stuff
        json_str = self.rank.create(mocker)
        
        # checks
        self.assertTrue(ApiDataProvider.STATUS_SUCCESS in json_str)
        
        thumbeddown_object = Rank.objects.get(media=self.m1)
        self.assertEqual(init_thumbup, thumbeddown_object.thumbs_up)
        self.assertEqual(init_thumbdown + 1, thumbeddown_object.thumbs_down)
        
    def test_rateContent_emptyParameter(self):
        mocker = HttpRequest()
        
        json_str = self.rank.create(mocker)
        
        self.assertTrue(ApiDataProvider.STATUS_ERROR in json_str)   
        
    def test_rateContent_missingparam(self):
        mocker1 = HttpRequest()
        mocker1.POST[ApiDataProvider.MEDIA_ID] = self.m1
        
        json_str1 = self.rank.create(mocker1)
        
        self.assertTrue(ApiDataProvider.STATUS_ERROR in json_str1)    
    
        mocker2 = HttpRequest()
        mocker2.POST[Rank.URL_TAG] = Rank.THUMBS_UP
        
        json_str2 = self.rank.create(mocker2)
        
        self.assertTrue(ApiDataProvider.STATUS_ERROR in json_str2)    
    
        
    def test_rateContent_paramOutOfBounds(self):
        mocker = HttpRequest()
        mocker.POST[Rank.URL_TAG] = '2'
        mocker.POST[ApiDataProvider.MEDIA_ID] = '0'
        
        json_str = self.rank.create(mocker)
        
        self.assertTrue(ApiDataProvider.STATUS_ERROR in json_str)
        
    def test_rateContent_databaseEmpty(self):
        mocker = HttpRequest()
        mocker.POST[Rank.URL_TAG] = Rank.THUMBS_DOWN
        mocker.POST[ApiDataProvider.MEDIA_ID] = '-1'
        
        json_str = self.rank.create(mocker)
        
        self.assertTrue(ApiDataProvider.STATUS_ERROR in json_str)
