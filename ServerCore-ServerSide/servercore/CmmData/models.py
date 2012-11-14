from django.db import models
# A model class for piston api.
    
class Mood(models.Model):
    URL_TAG = 'mood'
    
    HAPPY = 'HA'
    INSPIRED = 'IN'
    ROMANTIC = 'RO'
    EXCITED = 'EX'
    MOOD_TYPE_CHOICES = (
        (INSPIRED, 'Inspired'),
        (HAPPY, 'Happy'),
        (ROMANTIC, 'Romantic'),
        (EXCITED, 'Excited'),
    )
    MOOD_TYPES = (HAPPY, INSPIRED, ROMANTIC, EXCITED)
    mood_type = models.CharField(max_length=2, choices=MOOD_TYPE_CHOICES, default=HAPPY, primary_key=True)       
    
    def __unicode__(self):
        return str(self.mood_type)

class Media(models.Model):
    URL_TAG = 'content'
    
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
    URL_TAG = 'rank'
    
    THUMBS_UP = 0
    THUMBS_DOWN = 1
    RANK_TYPE_CHOICES = (
        (THUMBS_UP, "ThumbsUp"),
        (THUMBS_DOWN, "ThumbsDown"),
    )
    RANK_TYPES = (THUMBS_UP, THUMBS_DOWN)
    
    media = models.OneToOneField(Media, primary_key = True) # , parent_link=True)
    thumbs_up = models.IntegerField()
    thumbs_down = models.IntegerField()
    
    #TODO Please Implement
    def __unicode__(self):
        return str(self.thumbs_up) + ':' + str(self.thumbs_down)

class Video(models.Model):
    youtube_id = models.CharField(unique=True, max_length=20)
    url = models.URLField(unique=True)
    media = models.OneToOneField(Media, primary_key=True)
    
    def __unicode__(self):
        return str(self.url)   
    
    @staticmethod
    def add(youtube_id, url, mood):
        assert mood in Mood.MOOD_TYPES
        
        res = Video.objects.filter(youtube_id = youtube_id)
        if len(res) == 0:
            m = Media(content_type = Media.VIDEO)
            m.save()
            m.moods.add(mood)
            
            v = Video(media = m, youtube_id = youtube_id, url = url)
            v.save()
            
            r = Rank(media = m, thumbs_up=0, thumbs_down=0)
            r.save()
            return True
        elif len(res) == 1:
            #Depreciated
            vid = res[0]
            med = vid.media
            if mood in med.moods.all():
                return False #already exist
            else:
                med.moods.add(mood)
                med.save()
                return True #TODO: RANK????!!!!!!
        else:
            raise Exception('corrupted database')

class Picture(models.Model):
    flickr_id = models.BigIntegerField(unique=True) # for filtering
    url = models.URLField(unique=True)
    media = models.OneToOneField(Media, primary_key=True)#, parent_link=True)
    #TODO Other meta data??

    @staticmethod
    def add(flickr_id, url, mood):
        assert mood in Mood.MOOD_TYPES
        
        res = Picture.objects.filter(flickr_id = flickr_id)
        if len(res) == 0:
            m = Media(content_type = Media.PICTURE)
            m.save()
            m.moods.add(mood)
            
            p = Picture(media = m, flickr_id = flickr_id, url = url)
            p.save()
            
            r = Rank(media = m, thumbs_up=0, thumbs_down=0)
            r.save()
            return True
        elif len(res) == 1:
            #Depreciated
            pic = res[0]
            med = pic.media
            if mood in med.moods.all():
                return False #already exist
            else:
                med.moods.add(mood)
                med.save()
                return True #TODO: RANK????!!!!!!
        else:
            raise Exception('corrupted database')
    
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
    assert(content in Media.CONTENT_TYPES)
    
    # Delete in media table
    m = None
    try:
        m = Media.objects.get(id=mid)
    except Exception:
        # no mid found
        return False
    
    # delete in Rank
    Rank.objects.get(media=mid).delete()
    
    # delete in Content
    if content == Media.PICTURE:
        Picture.objects.get(media=mid).delete()
    else:
        return False
    
    m.delete()
    return True
        
    
    
    
    
    
    
    
    