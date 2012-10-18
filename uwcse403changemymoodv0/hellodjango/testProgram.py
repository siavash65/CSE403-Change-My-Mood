'''
Created on Oct 5, 2012

@author: hunlan
'''
from django.shortcuts import render_to_response
from django.http import HttpResponse
from somedata.models import Characters
import flickrapi
import random


api_key = 'f9691e72075ff63d137fefa44511fbaa'

def storeData_form(request):
    return render_to_response('testing.html');

def storeData(request):    
    # if character is in request
    if 'character' in request.GET:
        user_input = request.GET['character'].lower()
        if user_input.strip() == '':
            return HttpResponse('cannot input empty string')
        
        try:
            Characters.objects.get(chars=user_input)
        except Characters.DoesNotExist:
            Characters.objects.create(chars=user_input)
            return HttpResponse('Added ' + user_input + ' to Database')
        else:
            return HttpResponse('Already in Database')
    else:
        return HttpResponse('You submitted an empty form.')

def displayData(request):
    arr = Characters.objects.all()
    msg = ''
    for ch in arr:
        msg += ch.chars + '<br>'
    return HttpResponse(msg)

def clearData(request):
    Characters.objects.all().delete()
    return HttpResponse('cleared database')

def searchFlickr(request):
#    http://static.flickr.com/{server-id}/{id}_{secret}_[mstb].jpg
    flickr = flickrapi.FlickrAPI(api_key)
    pics = flickr.photos_search(api_key=api_key, text='funny')
    
    length = len(pics[0])
    randompic = int(random.random() * length)
    first_attrib = pics[0][randompic].attrib
    link = 'http://static.flickr.com/' + first_attrib['server'] + '/' + first_attrib['id'] + "_" + first_attrib['secret'] + ".jpg"
    return HttpResponse('<img src=' + link + '>')