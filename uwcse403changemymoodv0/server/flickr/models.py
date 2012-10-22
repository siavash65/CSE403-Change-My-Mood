from django.db import models

# Create your models here.
class FlickModel(models.Model):
    server = models.CharField(max_length=20)
    uid = models.CharField(max_length=50)
    secret = models.CharField(max_length=100)
    
    def __unicode__(self):
        return self.server + self.uid + self.secret