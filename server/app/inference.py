import torch
from PIL import Image
import numpy as np

class Tile:
    def __init__(self, name, conf, x_coord, y_coord):
        self.name = name
        self.conf = conf
        self.x_coord = x_coord
        self.y_coord = y_coord

    def rotate(self, rads):
        x_rot = self.x_coord * np.cos(rads) - self.y_coord * np.sin(rads)
        y_rot = self.x_coord * np.sin(rads) + self.y_coord * np.cos(rads)
        self.x_coord = x_rot
        self.y_coord = y_rot

"""Returns indices of duplicate tiles, or False if no duplicates are present"""
def find_dupes(tiles, x_tolerance, y_tolerance):
    for i in range(len(tiles)):
        for j in range(i+1, len(tiles)):
            if abs(tiles[i].x_coord - tiles[j].x_coord) <= x_tolerance and abs(tiles[i].y_coord - tiles[j].y_coord) <= y_tolerance:
                return i, j
    return False

"""Removes duplicate tiles within a tolerance"""
def remove_dupes(tiles, x_tolerance, y_tolerance):
    if len(tiles) <= 1:
        return
    tiles.sort(key=lambda t: t.x_coord)
    dupe_indices = find_dupes(tiles, x_tolerance, y_tolerance)
    while dupe_indices:
        i, j = dupe_indices
        if tiles[i].conf > tiles[j].conf:
            tiles.pop(j)
        else:
            tiles.pop(i)
        dupe_indices = find_dupes(tiles, x_tolerance, y_tolerance)

"""Returns angle we must rotate tiles for tiles to be facing up, given direction of input image"""
def direction_to_rads(direction):
    if direction == 'up':
        return 0
    elif direction == 'left' :
        return np.pi / 2
    elif direction == 'down':
        return np.pi
    elif direction == 'right':
        return -np.pi / 2

def rotate_all(tiles, rads):
    for tile in tiles:
        tile.rotate(rads)

"""Returns the slope that the tiles are pointing, assuming that the tiles are in a roughly rectangular formation"""
def get_slope(tiles):
    if len(tiles) <= 1:
        return 0
    x_coords = [tile.x_coord for tile in tiles]
    x_coord_median = get_median(x_coords)
    tiles.sort(key=lambda t: t.y_coord)
    anchor_index = -1
    # find top-left or top-right tile
    for i in range(len(tiles)):
        if tiles[i].x_coord != x_coord_median:
            anchor_index = i
            break
    if anchor_index == -1:
        return 0
    slopes = []
    slope_found = False
    # top-left case
    if tiles[anchor_index].x_coord < x_coord_median:
        for i in range(anchor_index + 1, len(tiles)):
            if tiles[i].x_coord <= tiles[anchor_index].x_coord:
                if not slope_found:
                    anchor_index = i
                else:
                    break
            else:
                slopes.append((tiles[anchor_index].y_coord - tiles[i].y_coord)/(tiles[anchor_index].x_coord - tiles[i].x_coord))
                slope_found = True
    # top-right case
    elif tiles[anchor_index].x_coord > x_coord_median:
        for i in range(anchor_index + 1, len(tiles)):
            if tiles[i].x_coord >= tiles[anchor_index].x_coord:
                if not slope_found:
                    anchor_index = i
                else:
                    break
            else:
                slopes.append((tiles[anchor_index].y_coord-tiles[i].y_coord)/(tiles[anchor_index].x_coord-tiles[i].x_coord))
                slope_found = True
    if len(slopes) == 0:
        return 0
    return get_median(slopes)

"""Sorts tiles from left to right and top to bottom"""
def sort_tiles_from_top_left_to_bottom_right(tiles, y_tolerance):
    if len(tiles) <= 1:
        return tiles
    tiles.sort(key=lambda t: t.y_coord)
    rows = []
    row = []
    y_min = -1
    y_max = -1
    for tile in tiles:
        if y_max == -1:
            row = [tile]
            y_min = tile.y_coord
            y_max = tile.y_coord
        else:
            y_min_new = min(y_min, tile.y_coord)
            y_max_new = max(y_max, tile.y_coord)
            if abs(y_max_new-y_min_new) > y_tolerance:
                row.sort(key=lambda t: t.x_coord)
                rows.append(row)
                row = [tile]
                y_min = tile.y_coord
                y_max = tile.y_coord
            else:
                row.append(tile)
                y_min = y_min_new
                y_max = y_max_new
    row.sort(key=lambda t: t.x_coord)
    rows.append(row)
    tiles_new = []
    # Put the tiles in order in a new list
    for row in rows:
        for tile in row:
            tiles_new.append(tile)
    return tiles_new

def get_median(float_list):
    float_list.sort()
    if len(float_list) % 2 == 0:
        return (float_list[len(float_list) // 2] + float_list[(len(float_list) // 2) - 1]) / 2
    else:
        return float_list[len(float_list) // 2]

"""Returns list of tiles found in inputted image, ordered from left to right and top to bottom, given direction that the image is facing"""
def get_tiles(image_name, direction='up'):
    # Model
    model = torch.hub.load('ultralytics/yolov5', 'custom', path='/home/ubuntu/Michigan_Mahjong/server/app/oldModel.pt', _verbose=True)

    # Images
    im = Image.open(image_name)  # PIL image

    # Inference
    results = model(im)

    # Results
    # results.print()
    # results.save()  # or .show()

    results_dict = results.pandas().xyxy[0].to_dict()
    if results_dict.get('name') is None:
        return []
    num_results = len(results_dict['name'])
    tiles = []
    x_lengths = []
    y_lengths = []
    # Transfers the raw bounding box results to our own Tile structure
    for i in range(num_results):
        name = results_dict['name'][i]
        conf = results_dict['confidence'][i]
        x_min = results_dict['xmin'][i]
        x_max = results_dict['xmax'][i]
        y_min = results_dict['ymin'][i]
        y_max = results_dict['ymax'][i]
        x_avg = (x_min + x_max)/2
        y_avg = (y_min + y_max)/2
        tiles.append(Tile(name, conf, x_avg, y_avg))
        x_lengths.append(x_max - x_min)
        y_lengths.append(y_max - y_min)
    if len(tiles) == 0:
        return []
    x_length_median = get_median(x_lengths)
    y_length_median = get_median(y_lengths)
    # swap x and y lengths if image is rotated left or right
    if direction == 'left' or direction == 'right':
        x_length_median, y_length_median = y_length_median, x_length_median

    # remove any duplicate classes found on the same tile based on highest confidence
    x_tolerance = x_length_median * 0.2
    y_tolerance = y_length_median * 0.2
    remove_dupes(tiles, x_tolerance, y_tolerance)

    # rotate tiles based on given direction so that they are facing up
    rotate_all(tiles, direction_to_rads(direction))

    # correct slope of tiles to be flat
    slope = get_slope(tiles)
    angle = -np.arctan(slope)
    rotate_all(tiles, angle)

    # sort tiles in the order they were placed
    y_tolerance = y_length_median * 0.45
    tiles = sort_tiles_from_top_left_to_bottom_right(tiles, y_tolerance)
    tile_names = [tile.name for tile in tiles]
    return tile_names

if __name__ == "__main__":
    get_tiles('tiles.png')
