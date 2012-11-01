from django.db import models
from servercore.util.contents import Contents
# A model class for piston api.

class Media(models.Model):
    # mid = models.IntegerField()
    mood = models.IntegerField()
    content = models.IntegerField()
    
    #TODO Please Implement
    def __unicode__(self):
        return str(self.mood) +  ' ' + str(self.content)

class Pictures(models.Model):
    mid = models.IntegerField()
    url = models.URLField()
    photo_id = models.BigIntegerField() # for filtering
    #TODO Other meta data??
    
    #TODO Please Implement
    def __unicode__(self):
        return str(self.url)
    
class Rank(models.Model):
    mid = models.IntegerField()
    thumbs_up = models.IntegerField()
    thumbs_down = models.IntegerField()
    
    #TODO Please Implement
    def __unicode__(self):
        return str(self.thumbs_up) + ':' + str(self.thumbs_down)
    

'''
TODO: Please polish this
'''
def destory(mid, content):
    assert(content in Contents.all)
    
    # Delete in media table
    try:
        m = Media.objects.get(id=mid)
        m.delete()
    except Exception:
        # no mid found
        return False
    
    # delete in Rank
    Rank.objects.get(mid=mid).delete()
    
    if content == Contents.PICTURE:
        Pictures.objects.get(mid=mid).delete()
        return True
    else:
        return False
        
    
    
    
    
    
    
    
    