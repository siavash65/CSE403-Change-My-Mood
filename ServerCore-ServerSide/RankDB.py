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
    
    medias = Media.objects.all()
    uprank = 0
    downrank = 0
    for m in medias:
        try:
            uprank += m.rank.thumbs_up
            downrank += m.rank.thumbs_down
        except Exception:
            print "exception"
            
    print 'up ratings: ' + str(uprank)
    print 'dw ratings: ' + str(downrank)