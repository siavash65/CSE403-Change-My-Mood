from django.db import models
from servercore.util.contents import Contents
# A model class for piston api.
    
class Mood(models.Model):
    INSPIRED = 'IN'
    HAPPY = 'HA'
    ROMANTIC = 'RO'
    EXCITED = 'EX'
    MOOD_TYPE_CHOICES = (
        (INSPIRED, 'Inspired'),
        (HAPPY, 'Happy'),
        (ROMANTIC, 'Romantic'),
        (EXCITED, 'Excited'),
    )
    MOOD_TYPES = (INSPIRED, HAPPY, ROMANTIC, EXCITED)
    mood_type = models.CharField(max_length=2, choices=MOOD_TYPE_CHOICES, default=HAPPY, primary_key=True)
    
    def __unicode__(self):
        return str(self.mood_type)

class Media(models.Model):
    moods = models.ManyToManyField(Mood)
    PICTURE = "PI"
    VIDEO = "VI"
    TEXT = "TE"
    AUDIO = "AU"
    CONTENT_TYPE_CHOICES = (
        (PICTURE, "Picture"),
        (VIDEO, "Video"),
        (AUDIO, "Audio"),
        (TEXT, "Text"),
    )
    CONTENT_TYPES = (PICTURE, VIDEO, TEXT, AUDIO)
    content_type = models.CharField(max_length=2, choices=CONTENT_TYPE_CHOICES, default=PICTURE)
    
    #TODO Please Implement
    def thumbs_up(self):
        self.rank.thumbs_up = self.rank.thumbs_up + 1
        self.rank.save()
        
    def thumbs_down(self):
        self.rank.thumbs_down = self.rank.thumbs_down + 1
        self.rank.save()
    
    def __unicode__(self):
        return str(self.id)
    
class Rank(models.Model):
    media = models.OneToOneField(Media, primary_key = True, parent_link=True)
    thumbs_up = models.IntegerField()
    thumbs_down = models.IntegerField()
    
    #TODO Please Implement
    def __unicode__(self):
        return str(self.thumbs_up) + ':' + str(self.thumbs_down)

class Picture(models.Model):
    flickr_id = models.BigIntegerField(unique=True) # for filtering
    url = models.URLField(unique=True)
    media = models.OneToOneField(Media, primary_key=True, parent_link=True)
    #TODO Other meta data??
    
    #TODO Please Implement
    def __unicode__(self):
        return str(self.url)   
    
class User(models.Model):
    
    favorites = models.ManyToManyField(Media)
    
    def add_favorite(self, mid):
        media = Media.objects.get(id = mid)
        self.favorites.add(media)
        self.save()
    
    def __unicode__(self):
        return str(self.id)



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
        
    
    
    
    
    
    
    
    