from django.shortcuts import render
from django.http import JsonResponse, HttpResponse
from django.db import connection
from django.views.decorators.csrf import csrf_exempt
import json
import os, time
from django.conf import settings
from django.core.files.storage import FileSystemStorage
from .logic import cal_result

def invalidlocation(location):
    return location != "hand" and location != "discard" and location != "open"

@csrf_exempt
def getcvresult(request):
    if request.method != 'POST':
        return HttpResponse(status=404)

    if not request.FILES.get("image"):
        print("no image")
        return HttpResponse(status=500)

    content = request.FILES['image']
    filename = str(time.time())+".jpeg"
    fs = FileSystemStorage()
    filename = fs.save(filename, content)
    imageurl = fs.url(filename)
    
    #location  = request.POST.get("location")
    #if invalidlocation(location):
    #    print("wrong location")
    #    return HttpResponse(status=500)

    # replace tmp value with a call to CV
    tiles = ["4z", "2z", "3s", "2s", "3p", "4s", "8p", "2p", "1m", "5m", "8p", "6m", "5z", "3m"]
    fs.delete(filename)

    return JsonResponse({"tile_list": tiles})

@csrf_exempt
def getrecmove(request):
    if request.method != 'POST':
        return HttpResponse(status=404)
    json_data = json.loads(request.body)
    tile_list = json_data["tile_list"]
    
    if "hand" not in tile_list.keys():
        return HttpResponse(status=500)
    
    for location in tile_list.keys():
        if invalidlocation(location):
            return HttpResponse(status=500)

    # replace tmp value with call to GameLogic
    #score, tile = cal_result(tile_list["hand"])

    return JsonResponse({"tile": "4z", "score": 15})
