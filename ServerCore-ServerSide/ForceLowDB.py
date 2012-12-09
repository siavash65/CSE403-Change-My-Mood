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
    
    mids = []
    for mid in mids:
        try:
            m = Media.objects.get(id=mid)
            for i in range(0,10):
                m.thumbs_down()
        except Exception:
            print 'exception'