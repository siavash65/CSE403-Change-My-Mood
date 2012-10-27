from django.db import models
# A model class for ApiDataProviderModel

class CmmBridgeModel(models.Model):
    chars = models.CharField(max_length=80)
    
    def __unicode__(self):
        return self.chars