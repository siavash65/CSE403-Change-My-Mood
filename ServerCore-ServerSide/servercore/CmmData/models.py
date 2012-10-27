from django.db import models
# A model class for piston api.

class Media(models.Model):
    mid = models.IntegerField()
    mood = models.IntegerField()
    content = models.IntegerField()
    
    #TODO Please Implement
    def __unicode__(self):
        return None

class Pictures(models.Model):
    mid = models.IntegerField()
    url = models.URLField()
    #TODO Other meta data??
    
    #TODO Please Implement
    def __unicode__(self):
        return None
    
class Rank(models.Model):
    mid = models.IntegerField()
    thumbs_up = models.IntegerField()
    thumbs_down = models.IntegerField()