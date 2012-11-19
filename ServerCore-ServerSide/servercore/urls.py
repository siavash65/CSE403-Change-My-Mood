# import servercore.django_cron
# django_cron.autodiscover()
from django.conf.urls import patterns, include, url
from servercore.CmmSite.views import main_site

# Uncomment the next two lines to enable the admin:
# from django.contrib import admin
# admin.autodiscover()

urlpatterns = patterns('',
    # Linking to API urls
    (r'^api/', include('servercore.CmmBridge.urls')),
    (r'^$', main_site),
)
