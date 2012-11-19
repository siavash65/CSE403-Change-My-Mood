'''
Created on Nov 12, 2012

@author: hunlan
'''
#!/usr/bin/env python
import os
import sys

if __name__ == "__main__":
    os.environ.setdefault("DJANGO_SETTINGS_MODULE", "servercore.settings")

    from servercore.CmmData.models import Mood, Media  
    from servercore.CmmCore.ContentDataOrganizer.ContentDataOrganizer import ContentDataOrganizer  
    ContentDataOrganizer.brokenURLFilterCronJob(Mood.HAPPY, Media.PICTURE)
    ContentDataOrganizer.brokenURLFilterCronJob(Mood.HAPPY, Media.VIDEO)
    ContentDataOrganizer.brokenURLFilterCronJob(Mood.ROMANTIC, Media.PICTURE)
    ContentDataOrganizer.brokenURLFilterCronJob(Mood.ROMANTIC, Media.VIDEO)
    