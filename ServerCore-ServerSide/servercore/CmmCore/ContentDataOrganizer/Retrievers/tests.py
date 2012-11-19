"""
This file demonstrates writing tests using the unittest module. These will pass
when you run "manage.py test".

Replace this with more appropriate tests for your application.
"""

from django.test import TestCase
from servercore.CmmCore.ContentDataOrganizer.Retrievers import ContentRetriever,\
    PictureRetriever
from servercore.CmmData.models import Mood, Media
from servercore.CmmCore.ContentDataOrganizer.ContentDataOrganizer import ContentDataOrganizer

class PictureRetrieverPullAndFilterTest(TestCase):
    def setUp(self):
        PictureRetriever.pullPictures(Mood.HAPPY, 'happy', 10)
        
    def tearDown(self):
        ContentDataOrganizer.clearPhotoDatabase()
        pass   
    
    def test_addnum(self):
        PictureRetriever.pullAndFilter(Mood.HAPPY, ['happy'], 5, 20)
        ms = Media.objects.all()
        self.assertEqual(15, len(ms))
        
    def test_filteroutlowscore(self):
        ms = Media.objects.all()
        m0 = ms[0]
        m1 = ms[1]
        m2 = ms[2]
        for i in range(0,10):
            m0.thumbs_down()
            
        for i in range(0,9):
            m1.thumbs_down()
            
        for i in range(0,15):
            m2.thumbs_up()         
            
        PictureRetriever.pullAndFilter(Mood.HAPPY, ['happy'], 0, 20)
        ms = Media.objects.all()
        self.assertEqual(10, len(ms))
        self.assertFalse(m0 in ms)# out because more than 10 rating and it sucks
        self.assertTrue(m1 in ms) #in because less than 10 ratings
        self.assertTrue(m2 in ms) # in because its good
        

class ContentRetrieverScoreTest(TestCase):
    def test_primary(self):
        score = ContentRetriever.computeInitialScore(isPrimeMatch=True,\
                                                     numSecond=0, \
                                                     views=0, \
                                                     comments=0, \
                                                     favs=0)
        self.assertEqual(ContentRetriever.PRIME_SCORE, score)
    
    def test_secondary(self):
        score = ContentRetriever.computeInitialScore(isPrimeMatch=False,\
                                                     numSecond=2, \
                                                     views=0, \
                                                     comments=0, \
                                                     favs=0)
        self.assertEqual(2 * ContentRetriever.SECOND_SCORE, score)
        
    def test_view(self):
        score = ContentRetriever.computeInitialScore(isPrimeMatch=False,\
                                                     numSecond=0, \
                                                     views=100, \
                                                     comments=0, \
                                                     favs=0)
        self.assertEqual(ContentRetriever.VIEW_SCORE_ONE, score)
                   
    def test_anothernormalcase(self):
        score = ContentRetriever.computeInitialScore(isPrimeMatch=True,\
                                                     numSecond=2, \
                                                     views=99, \
                                                     comments=10, \
                                                     favs=51)
        expected = ContentRetriever.PRIME_SCORE + \
                    2 * ContentRetriever.SECOND_SCORE + \
                    ContentRetriever.VIEW_SCORE_ONE + \
                    ContentRetriever.FAV_SCORE_ONE
        self.assertEqual(expected, score)
        
    def test_inputcase(self):
        score = ContentRetriever.computeInitialScore(isPrimeMatch=False,\
                                                     numSecond='5', \
                                                     views=99, \
                                                     comments='10', \
                                                     favs=51)
        expected = 4 * ContentRetriever.SECOND_SCORE + \
                    ContentRetriever.VIEW_SCORE_ONE + \
                    ContentRetriever.FAV_SCORE_ONE
        self.assertEqual(expected, score)        
    
    
    