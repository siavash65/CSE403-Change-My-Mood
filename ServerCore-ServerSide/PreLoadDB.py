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
    from servercore.CmmData.models import Mood
    
    moods = [Mood.INSPIRED]
    num_data_needed = 100
    
    for mood in moods:
        PictureRetriever.pullAndFilter(mood, ContentDataOrganizer._getRandomTerms(mood), num_data_needed, 200)
        VideoRetriever.pullAndFilter(mood, ContentDataOrganizer._getRandomTerms(mood), num_data_needed, 100)