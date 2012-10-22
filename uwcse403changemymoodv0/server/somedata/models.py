from django.db import models

# Create your models here.
class Characters(models.Model):
    chars = models.CharField(max_length=80)
    
    def __unicode__(self):
        return self.chars