"""
This file demonstrates writing tests using the unittest module. These will pass
when you run "manage.py test".

Replace this with more appropriate tests for your application.
"""
try:
    from settings.development import *
except ImportError:
    pass

from django.test import TestCase
from servercore.CmmCore.ContentDataOrganizer.ContentDataOrganizer import ContentDataOrganizer
from servercore.CmmData.models import Mood, Media, Picture, Rank
from servercore.CmmCore.ContentDataOrganizer.Filters.basicfilter import BasicFilter


class BasicFilterTest(TestCase):
            
    def setUp(self):
        ContentDataOrganizer.pullPictures(Mood.HAPPY, 'happy', 21)
        pass
    
    def tearDown(self):
        Media.objects.all().delete()
        Picture.objects.all().delete()
        Rank.objects.all().delete()
        pass
    
    def test_normalSituation(self):
        self.assertEqual(21, len(Media.objects.all()))
        medias = Media.objects.filter(moods=Mood.HAPPY, content_type=Media.PICTURE)
        init_num_of_pic = len(medias)
        self.assertEqual(21, init_num_of_pic)
        
        ratio = 0.1
        expected_final_num_of_pic = init_num_of_pic - int(ratio * init_num_of_pic)
        print 'expected: ' + str(expected_final_num_of_pic)
        
        
        p1 = medias[0].picture
        p2 = medias[1].picture
        p3 = medias[2].picture
        
        for i in range(0,10):
            p1.media.thumbs_down()
            p2.media.thumbs_down()
        
        res = BasicFilter.filter(Mood.HAPPY, Media.PICTURE, ratio)
        
        self.assertTrue(res, 'return should return true on successful')
        
        medias_after = Media.objects.filter(moods=Mood.HAPPY, content_type=Media.PICTURE)
        self.assertEqual(expected_final_num_of_pic , len(medias_after))
        self.assertFalse(p1.media in medias_after)
        self.assertFalse(p2.media in medias_after)
        self.assertTrue(p3.media in medias_after)
        
        
        
        
    
    
    
