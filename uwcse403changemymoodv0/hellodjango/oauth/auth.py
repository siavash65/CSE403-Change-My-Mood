'''
Created on Oct 14, 2012
http://stackoverflow.com/questions/4307677/django-google-federated-login
@author: hunlan
'''
from django.shortcuts import render_to_response
from oauth2client.client import OAuth2WebServerFlow
from django.http import HttpResponseRedirect, HttpResponse
import httplib2

CLIENT_ID = '514542194443.apps.googleusercontent.com'
CLIENT_SECRET = 'QuaWnt3THPMYo7C7leY2EeT1'

def oauthpage(request):
    return render_to_response('oauth.html')

def getReqToken(request):
#    https://developers.google.com/api-client-library/python/guide/aaa_oauth  
#    http://googlecodesamples.com/oauth_playground/ 
    flow = OAuth2WebServerFlow(client_id=CLIENT_ID,
                               client_secret=CLIENT_SECRET,
                               scope='https://www.googleapis.com/auth/calendar',
                               redirect_uri='https://localhost:8000/oauth/step1')
    
    auth_uri = flow.step1_get_authorize_url()
    
    return HttpResponseRedirect(auth_uri)

def exchange(request):
    if 'error' in request.GET:
        return HttpResponse('Fail to OAuth... too bad')
    
    if not ('code' in request.GET):
        return HttpResponse('Missing OAuth Code')
    
    flow = OAuth2WebServerFlow(client_id=CLIENT_ID,
                               client_secret=CLIENT_SECRET,
                               scope='https://www.googleapis.com/auth/calendar',
                               redirect_uri='https://localhost:8000/oauth/step2')
    
    code = request.GET['code']
    credentials = flow.step2_exchange(code)
    
    #Authorize
    http = httplib2.Http()
    http = credentials.authorize(http)
    
    return HttpResponse(http)
    
    
    
    
    