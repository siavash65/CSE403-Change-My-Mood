"""
This file demonstrates writing tests using the unittest module. These will pass
when you run "manage.py test".

Replace this with more appropriate tests for your application.
"""

from django.test import TestCase
from servercore.CmmData.models import Picture, Media, Rank, Mood
from servercore.CmmCore.ApiDataProvider.ApiDataProvider import ApiDataProvider


class SimpleTest(TestCase):
    def setUp(self):
        self.websites = ['www.google.com', 'www.bing.com', 'www.yahoo.com']
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
    Testing Get Content
    '''
    def test_getContent_normalsituation(self):
        json_str = ApiDataProvider.getContent(Mood.HAPPY, Media.PICTURE)
        self.assertTrue(json_str[ApiDataProvider.PARAM_URL] in self.websites)
        mid_arr = [self.m1.id, self.m2.id, self.m3.id]
        self.assertTrue(json_str[ApiDataProvider.MEDIA_ID] in mid_arr)
        
    def test_getContent_emptyMood(self):
        json_str = ApiDataProvider.getContent(Mood.EXCITED, Media.PICTURE)
        self.assertTrue(ApiDataProvider.STATUS_ERROR in json_str)
    
    def test_getContent_emptyContent(self):
        json_str = ApiDataProvider.getContent(Mood.HAPPY, Media.TEXT)
        self.assertTrue(ApiDataProvider.STATUS_ERROR in json_str)
        
    def test_getContent_emptyBoth(self):
        json_str = ApiDataProvider.getContent(Mood.HAPPY, Media.TEXT)
        self.assertTrue(ApiDataProvider.STATUS_ERROR in json_str)
        
    def test_getContent_baddatabase_nopictures(self):
        Picture.objects.all().delete()
        json_str = ApiDataProvider.getContent(Mood.HAPPY, Media.PICTURE)
        self.assertTrue(ApiDataProvider.STATUS_ERROR in json_str)
        
    def test_getContent_outofboundsinput(self):
        with self.assertRaises(AssertionError) as err:
            ApiDataProvider.getContent(4, Media.PICTURE)
        with self.assertRaises(AssertionError) as err:  
            ApiDataProvider.getContent(Mood.HAPPY, 4)
        
    '''
    Testing Rate Content
    '''
    def test_rateContent_normalsituation_up(self):
        json_str = ApiDataProvider.rateContent(self.m1.id, Rank.THUMBS_UP)
        self.assertTrue(ApiDataProvider.STATUS_SUCCESS in json_str) 
        content0 = Rank.objects.get(media = self.m1)
        self.assertEqual(1, content0.thumbs_up)
        self.assertEqual(0, content0.thumbs_down)
        
    def test_rateContent_normalsituation_up_twice(self):
        json_str = ApiDataProvider.rateContent(self.m1.id, Rank.THUMBS_UP)
        self.assertTrue(ApiDataProvider.STATUS_SUCCESS in json_str)
        json_str = ApiDataProvider.rateContent(self.m1.id, Rank.THUMBS_UP)
        self.assertTrue(ApiDataProvider.STATUS_SUCCESS in json_str)
         
        content0 = Rank.objects.get(media = self.m1)
        self.assertEqual(2, content0.thumbs_up)
        self.assertEqual(0, content0.thumbs_down)
           
    def test_rateContent_normalsituation_down(self):
        json_str = ApiDataProvider.rateContent(self.m1.id, Rank.THUMBS_DOWN)
        self.assertTrue(ApiDataProvider.STATUS_SUCCESS in json_str)
        
        content0 = Rank.objects.get(media=self.m1)
        self.assertEqual(0, content0.thumbs_up)
        self.assertEqual(1, content0.thumbs_down)
             
    def test_rateContent_baddatabase_empty(self):
        json_str = ApiDataProvider.rateContent(-1, Rank.THUMBS_DOWN)
        self.assertTrue(ApiDataProvider.STATUS_ERROR in json_str)
        
    def test_rateContent_illegalInput_wrongmid(self):
        with self.assertRaises(AssertionError) as err:
            ApiDataProvider.rateContent('0', Rank.THUMBS_DOWN)
        with self.assertRaises(AssertionError) as err:  
            ApiDataProvider.rateContent('abc', Rank.THUMBS_UP)
    
    def test_rateContent_illegalInput_wrongthumbing(self):
        with self.assertRaises(AssertionError) as err:
            ApiDataProvider.rateContent(0, 2)
        
            
    
    
    