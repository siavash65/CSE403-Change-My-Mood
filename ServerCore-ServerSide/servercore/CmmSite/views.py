from django.shortcuts import render_to_response

def main_site(request):
    return render_to_response('index.html')