from django.shortcuts import render
from django.http import JsonResponse, HttpResponse
from django.db import connection
from django.views.decorators.csrf import csrf_exempt
import json
import os, time
from django.conf import settings
from django.core.files.storage import FileSystemStorage
from .inference import get_tiles
from nodejs import node


def invalidlocation(location):
    return location != "hand" and location != "discard" and location != "open"

@csrf_exempt
def getcvresult(request):
    if request.method != 'POST':
        return HttpResponse(status=404)

    if not request.FILES.get("image"):
        print("no image")
        return HttpResponse(status=400)

    content = request.FILES['image']
    filename = str(time.time())+".jpeg"
    fs = FileSystemStorage()
    filename = fs.save(filename, content)
    imageurl = fs.url(filename)
    
    # default direction is up`
    direction = request.POST.get("direction") if request.POST.get("direction") else "up"
    tiles = get_tiles("/home/ubuntu/Michigan_Mahjong/server/media/" + filename, direction)
    fs.delete(filename)

    return JsonResponse({"tile_list": tiles})

@csrf_exempt
def getrecmove(request):
    if request.method != 'POST':
        return HttpResponse(status=404)
    json_data = json.loads(request.body)
    tile_list = json_data["tile_list"]
    
    if "hand" not in tile_list.keys():
        return HttpResponse(status=400)
    
    # discard pile and open tiles are optional
    discard_pile, open_tiles = [], []
    for location in tile_list.keys():
        if invalidlocation(location):
            return HttpResponse(status=400)
        elif location == "discard":
            discard_pile = tile_list["discard"]
        elif location == "open":
            open_tiles = tile_list["open"]

    # force initialize discard and open keys before passing the tile_list to Game Logic
    tile_list["discard"] = discard_pile
    tile_list["open"] = open_tiles
    result = node.run(["app/logic.js", json.dumps(tile_list)], capture_output=True, text=True)

    if result.returncode != 0:
        return HttpResponse(status=500)
    else:
        print(result.stdout)
        consolelog_list = result.stdout.split("\n")
        # extract recommended best discard tile
        tile = consolelog_list[1].split(":")[1].strip()
        text = consolelog_list[2]

    return JsonResponse({"tile": tile, "text": text})
