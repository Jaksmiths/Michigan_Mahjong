from django.shortcuts import render
from django.http import JsonResponse, HttpResponse
from django.db import connection
from django.views.decorators.csrf import csrf_exempt
import json
import os, time
from django.conf import settings
from django.core.files.storage import FileSystemStorage
from .logic import cal_result
from .inference import get_tiles


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
    # REMOVE tiles = ["4z", "2z", "3s", "2s", "3p", "4s", "8p", "2p", "1m", "5m", "8p", "6m", "5z", "3m"]
    tiles = get_tiles("/home/ubuntu/Michigan_Mahjong/server/media/" + filename)
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
    
    # discard pile and open tiles are optional
    discard_pile, open_tiles = None, None
    for location in tile_list.keys():
        if invalidlocation(location):
            return HttpResponse(status=500)
        elif location == "discard":
            discard_pile = tile_list["discard"]
        elif location == "open":
            open_tiles = tile_list["open"]

    # replace tmp value with call to GameLogic
    #discard_pile = tile_list["discard"] if "discard" in tile_list.keys() else None
    #open_tiles = tile_list["open"] if "open" in tile_list.keys() else None
    text = "THIS IS A TEMP PLACEHOLDER GOOD MOVE! :^)"
    tile = cal_result(tile_list["hand"], discard_pile, open_tiles)

    return JsonResponse({"tile": tile, "text": text})
