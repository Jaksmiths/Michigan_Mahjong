from django.shortcuts import render
from django.http import JsonResponse, HttpResponse
from django.db import connection
from django.views.decorators.csrf import csrf_exempt
import json
import os, time
from django.conf import settings
from django.core.files.storage import FileSystemStorage

@csrf_exempt
def getcvresult(request):
    if request.method != 'POST':
        return HttpResponse(status=400)

    if request.FILES.get("image"):
        content = request.FILES['image']
        filename = str(time.time())+".jpeg"
        fs = FileSystemStorage()
        filename = fs.save(filename, content)
        imageurl = fs.url(filename)
    else:
        imageurl = No
    

    return JsonResponse({"imagereturn": "Hello world!"})

def getrecmove(request):
    if request.method != 'GET':
        return HttpResponse(status=404)
    return JsonResponse({})
