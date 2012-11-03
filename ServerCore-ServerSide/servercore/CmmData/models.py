from django.db import models
# A model class for piston api.

class Media(models.Model):
    mood = models.IntegerField()
    content = models.IntegerField()
    
    #TODO Please Implement
    def __unicode__(self):
        return u"%s" % self.id

class Pictures(models.Model):
    url = models.URLField()
    #TODO Other meta data??
    
    #TODO Please Implement
    def __unicode__(self):
        return u"%s" % self.id
    
class Rank(models.Model):
    thumbs_up = models.IntegerField()
    thumbs_down = models.IntegerField()

    def __unicode__(self):
       return u"%s" % self.id
