"""
This file demonstrates writing tests using the unittest module. These will pass
when you run "manage.py test".

Replace this with more appropriate tests for your application.
"""

from django.test import TestCase
from servercore.CmmData.models import Pictures, Media, Rank
from servercore.util.moods import Moods
from servercore.util.contents import Contents
from servercore.CmmCore.ApiDataProvider.ApiDataProvider import ApiDataProvider
import json
from servercore.util.ranks import Ranks
from servercore.util.datanames import DataNames


class SimpleTest(TestCase):
    def setUp(self):
        self.websites = ['www.google.com', 'www.bing.com', 'www.yahoo.com']
        m1 = Media(mood = Moods.HUMOROUS, content = Contents.PICTURE)
        m2 = Media(mood = Moods.HUMOROUS, content = Contents.PICTURE)
        m3 = Media(mood = Moods.HUMOROUS, content = Contents.PICTURE)
        
        m1.save()
        m2.save()
        m3.save()
        
        self.mid1 = m1.id
        self.mid2 = m2.id
        self.mid3 = m3.id
        self.allmid = [m1.id, m2.id, m3.id]
                
        p1 = Pictures(mid = m1.id, url = self.websites[0], photo_id = 7)
        p2 = Pictures(mid = m2.id, url = self.websites[1], photo_id = 8)
        p3 = Pictures(mid = m3.id, url = self.websites[2], photo_id = 11)
        
        r1 = Rank(mid = m1.id, thumbs_up = 0, thumbs_down = 0)
        r2 = Rank(mid = m2.id, thumbs_up = 1, thumbs_down = 20)
        r3 = Rank(mid = m3.id, thumbs_up = 7, thumbs_down = 5)

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
    Testing Get Content
    '''
    def test_getContent_normalsituation(self):
        json_str = ApiDataProvider.getContent(Moods.HUMOROUS, Contents.PICTURE)
        self.assertTrue(json_str[ApiDataProvider.PARAM_URL] in self.websites)
        
        self.assertTrue(json_str[DataNames.MID] in self.allmid)
        
    def test_getContent_emptyMood(self):
        json_str = ApiDataProvider.getContent(Moods.ENERVATE, Contents.PICTURE)
        self.assertTrue(ApiDataProvider.STATUS_ERROR in json_str)
    
    def test_getContent_emptyContent(self):
        json_str = ApiDataProvider.getContent(Moods.HUMOROUS, Contents.VIDEO)
        self.assertTrue(ApiDataProvider.STATUS_ERROR in json_str)
        
    def test_getContent_emptyBoth(self):
        json_str = ApiDataProvider.getContent(Moods.ENERVATE, Contents.VIDEO)
        self.assertTrue(ApiDataProvider.STATUS_ERROR in json_str)
        
    def test_getContent_baddatabase_nopictures(self):
        Pictures.objects.all().delete()
        json_str = ApiDataProvider.getContent(Moods.HUMOROUS, Contents.PICTURE)
        self.assertTrue(ApiDataProvider.STATUS_ERROR in json_str)
        
    def test_getContent_outofboundsinput(self):
        with self.assertRaises(AssertionError) as err:
            ApiDataProvider.getContent(4, Contents.PICTURE)
        with self.assertRaises(AssertionError) as err:  
            ApiDataProvider.getContent(Moods.HUMOROUS, 4)
        
    '''
    Testing Rate Content
    '''
    def test_rateContent_normalsituation_up(self):
        json_str = ApiDataProvider.rateContent(self.mid1, Ranks.THUMBS_UP)
        self.assertTrue(ApiDataProvider.STATUS_SUCCESS in json_str) 
        content0 = Rank.objects.get(mid=self.mid1)
        self.assertEqual(1, content0.thumbs_up)
        self.assertEqual(0, content0.thumbs_down)
        
    def test_rateContent_normalsituation_up_twice(self):
        json_str = ApiDataProvider.rateContent(self.mid1, Ranks.THUMBS_UP)
        self.assertTrue(ApiDataProvider.STATUS_SUCCESS in json_str)
        json_str = ApiDataProvider.rateContent(self.mid1, Ranks.THUMBS_UP)
        self.assertTrue(ApiDataProvider.STATUS_SUCCESS in json_str)
         
        content0 = Rank.objects.get(mid=self.mid1)
        self.assertEqual(2, content0.thumbs_up)
        self.assertEqual(0, content0.thumbs_down)
           
    def test_rateContent_normalsituation_down(self):
        json_str = ApiDataProvider.rateContent(self.mid1, Ranks.THUMBS_DOWN)
        self.assertTrue(ApiDataProvider.STATUS_SUCCESS in json_str)
        
        content0 = Rank.objects.get(mid=self.mid1)
        self.assertEqual(0, content0.thumbs_up)
        self.assertEqual(1, content0.thumbs_down)
             
    def test_rateContent_baddatabase_empty(self):
        json_str = ApiDataProvider.rateContent(-1, Ranks.THUMBS_DOWN)
        self.assertTrue(ApiDataProvider.STATUS_ERROR in json_str)
        
    def test_rateContent_illegalInput_wrongmid(self):
        with self.assertRaises(AssertionError) as err:
            ApiDataProvider.rateContent(str(self.mid1), Ranks.THUMBS_DOWN)
        with self.assertRaises(AssertionError) as err:  
            ApiDataProvider.rateContent('abc', Ranks.THUMBS_UP)
    
    def test_rateContent_illegalInput_wrongthumbing(self):
        with self.assertRaises(AssertionError) as err:
            ApiDataProvider.rateContent(self.mid1, 2)
        
            
    
    
    