'''
Created on Nov 12, 2012

@author: hunlan
'''
#!/usr/bin/env python
import os
import sys


if __name__ == "__main__":
    os.environ.setdefault("DJANGO_SETTINGS_MODULE", "servercore.settings")

    from servercore.CmmCore.ContentDataOrganizer.Retrievers import PictureRetriever, VideoRetriever
    from servercore.CmmCore.ContentDataOrganizer.ContentDataOrganizer import ContentDataOrganizer
    from servercore.CmmData.models import Mood, Media
    
    moods = [Mood.HAPPY, Mood.ROMANTIC, Mood.INSPIRED, Mood.EXCITED]
    fill_up_to = 100
    # num_data_needed = 100
    
    for mood in moods:
        pic_num = fill_up_to - len(Media.objects.filter(moods = mood, content_type="PI"))
        pic_num = 0 if pic_num < 0 else pic_num
        vid_num = fill_up_to - len(Media.objects.filter(moods = mood, content_type="VI"))
        vid_num = 0 if vid_num < 0 else vid_num
        
        PictureRetriever.pullAndFilter(mood, ContentDataOrganizer._getRandomTerms(mood), pic_num, 200)
        VideoRetriever.pullAndFilter(mood, ContentDataOrganizer._getRandomTerms(mood), vid_num, 100)