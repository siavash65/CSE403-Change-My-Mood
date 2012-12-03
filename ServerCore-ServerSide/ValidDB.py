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
    from servercore import CmmData  
    addData = Media.objects.all()
    count = 0
    i = 0
    for m in addData:
        i += 1
        print str(i) + '/' + str(len(addData))
        if not m.is_valid():
            CmmData.models.destory(m.id, m.content_type)
            count += 1
    print "Deleted " + str(count) + " invalid data"
        
    
    
    