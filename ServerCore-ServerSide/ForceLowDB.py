'''
Created on Nov 12, 2012

@author: hunlan
'''
#!/usr/bin/env python
import os
import sys


if __name__ == "__main__":
    os.environ.setdefault("DJANGO_SETTINGS_MODULE", "servercore.settings")

    from servercore.CmmData.models import Media
    
    medias = Media.objects.all()
    for m in medias:
        try:
            if m.rank.thumbs_down > 2:
                for i in range(0,30):
                    m.thumbs_down()
        except Exception:
            print 'exception'