"""
This file demonstrates writing tests using the unittest module. These will pass
when you run "manage.py test".

Replace this with more appropriate tests for your application.
"""
from servercore.CmmCore.ContentDataOrganizer.Retrievers import PictureRetriever
from servercore.CmmCore.ContentDataOrganizer.Filters.scorefilter import ScoreFilter
try:
    from settings.development import *
except ImportError:
    pass

from django.test import TestCase
from servercore.CmmData.models import Mood, Media, Picture, Rank, Score
from servercore.CmmCore.ContentDataOrganizer.Filters.basicfilter import BasicFilter

class ScoreFilterTest(TestCase):
    def setUp(self):
        PictureRetriever.pullPictures(Mood.HAPPY, 'happy', 21)
        
    def tearDown(self):
        Media.objects.all().delete()
        Picture.objects.all().delete()
        Rank.objects.all().delete()
        Score.objects.all().delete()
        pass
    
    def test_noPictureHasFinal(self):
        #medias = Media.objects.filter(moods=Mood.HAPPY, content_type=Media.PICTURE)
        self.assertEqual(ScoreFilter.NO_FINAL_SCORE, ScoreFilter.filter(Mood.HAPPY, Media.PICTURE))
        
    def test_emptyDatabase(self):
        Media.objects.all().delete()
        Picture.objects.all().delete()
        Rank.objects.all().delete()
        Score.objects.all().delete()
        
        self.assertEqual(ScoreFilter.EMPTY_DB, ScoreFilter.filter(Mood.HAPPY, Media.PICTURE))
        
        
    def test_picbelowthreshold(self):
        medias = Media.objects.filter(moods = Mood.HAPPY, content_type=Media.PICTURE)
        m1 = medias[0]
        m2 = medias[len(medias) - 1]
        m3 = medias[1]
        
        s1 = Score.objects.get(media = m1)
        s2 = Score.objects.get(media = m2)
        s3 = Score.objects.get(media = m3)
        
        s1.final_score = ScoreFilter.THRESHOLD
        s2.final_score = ScoreFilter.THRESHOLD - 1
        s3.final_score = ScoreFilter.THRESHOLD + 1
        s1.save()
        s2.save()
        s3.save()
        
        
        self.assertTrue(ScoreFilter.filter(Mood.HAPPY, Media.PICTURE) == 1) # filtered one
        
        m1_arr = Score.objects.filter(media=m1)
        m2_arr = Score.objects.filter(media=m2)
        m3_arr = Score.objects.filter(media=m3)
        
        self.assertEqual(1, len(m1_arr))
        self.assertEqual(0, len(m2_arr))
        self.assertEqual(1, len(m3_arr))

    

class BasicFilterTest(TestCase):
            
    def setUp(self):
        PictureRetriever.pullPictures(Mood.HAPPY, 'happy', 21)
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
        #print 'expected: ' + str(expected_final_num_of_pic)
        
        
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
        
        
        
        
    
    
    
