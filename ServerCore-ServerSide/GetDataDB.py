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
    
    moods = [Mood.HAPPY, Mood.ROMANTIC, Mood.INSPIRED, Mood.EXCITED]
    for mood in moods:
        
        pictures = Media.objects.filter(moods = mood, content_type = "PI")
        print 'Pictures:'
        for p in pictures:
            try:
                url = p.picture.url
                up = p.rank.thumbs_up
                dw = p.rank.thumbs_down
                print 'U: ' + str(up) + 'D: ' + str(dw) + ', URL:' + url
            except Exception:
                print 'exception'
        
        videos = Media.objects.filter(moods = mood, content_type = "VI")
        print 'Videos:'
        for v in videos:
            try:
                url = v.picture.url
                up = v.rank.thumbs_up
                dw = v.rank.thumbs_down
                print 'U: ' + str(up) + 'D: ' + str(dw) + ', URL:' + url
            except Exception:
                print 'exception'
