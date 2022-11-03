from django.shortcuts import render
from django.http import JsonResponse, HttpResponse
from django.db import connection
from django.views.decorators.csrf import csrf_exempt
import json
import os, time
from django.conf import settings
from django.core.files.storage import FileSystemStorage

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
    
    location  = request.POST.get("location")
    if invalidlocation(location):
        print("wrong location")
        return HttpResponse(status=500)

    # replace tmp value with a call to CV
    tiles = ["Green", "North", "Sou3", "Sou2", "Pin3", "Sou4", "Pin8", "Pin2", "Man1", "Man5", "Pin8", "Man6", "South", "Man3:"]
    fs.delete(filename)

    return JsonResponse({"tile_list": {location: tiles}})

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
    result = "Green Dragon"

    return JsonResponse({"tile": result})
