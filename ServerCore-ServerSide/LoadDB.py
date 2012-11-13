'''
Created on Nov 12, 2012

@author: hunlan
'''
#!/usr/bin/env python
import os
import sys

if __name__ == "__main__":
    os.environ.setdefault("DJANGO_SETTINGS_MODULE", "servercore.settings")

    from servercore.CmmData.models import Mood    
    happy = Mood(mood_type=Mood.HAPPY)
    inspired = Mood(mood_type=Mood.INSPIRED)
    romantic = Mood(mood_type=Mood.ROMANTIC)
    excited = Mood(mood_type=Mood.EXCITED)
    
    happy.save()
    inspired.save()
    romantic.save()
    excited.save()