import json
from collections import Counter
import copy

'''
1-9m = 1-9 man
1-9s = 1-9 Sou
1-9p = 1-9 Pin
1z = East
2z = South
3z = West
4z = North
5z = White
6z = Green
7z = Red
'''

def sort_hand(hand):
    # Sort hand tile in to different list.
    character = []
    bamboo = []
    dots = []
    wind = []
    dragon = []
    for tile in hand:
        if tile[1] == 'm':
            character.append(int(tile[0]))
        elif tile[1] == 's':
            bamboo.append(int(tile[0]))
        elif tile[1] == 'p':
            dots.append(int(tile[0]))
        elif tile[1] == 'z':
            if tile[0] in ['1', '2', '3', '4']:
                wind.append(int(tile[0]))
            elif tile[0] == '5':
                dragon.append(1)
            elif tile[0] == '6':
                dragon.append(2)
            elif tile[0] == '7':
                dragon.append(3)
    thisdict = {
        "character": character,
        "bamboo": bamboo,
        "dots": dots,
        "wind": wind,
        "dragon": dragon
    }
    return thisdict


def if_win(hand):
    # Check for thirteen orphans
    if 1 in hand['character'] and 9 in hand['character']:
        if 1 in hand['bamboo'] and 9 in hand['bamboo']:
            if 1 in hand['dots'] and 9 in hand['dots']:
                if 1 in hand['wind'] and 2 in hand['wind'] and 3 in hand['wind'] and 4 in hand['wind']:
                    if 1 in hand['dragon'] and 2 in hand['dragon'] and 3 in hand['dragon']:
                        print("- - thirteen orphans! - - ")
                        return True

    # Check for seven pairs
    for tile_type in hand:
        temp = dict(Counter(hand[tile_type]))
        temp = list(temp.values())
        if 1 in temp or 3 in temp:
            break
        elif tile_type == "dragon":
            print("- - seven pairs! - - ")
            return True

    # Check for 4*3+2:
    shun_num, ke_num = 0, 0
    for tile_type in hand:
        # find shun zi
        if tile_type in ['character', 'bamboo', 'dots']:
            type_set = set(hand[tile_type])
            vlist = sorted(list(type_set))
            idx = 0
            while idx < len(vlist):
                if idx + 2 < len(vlist) and vlist[idx + 2] - vlist[idx + 1] == 1 and vlist[idx + 1] - vlist[idx] == 1 \
                        and vlist[idx] in hand[tile_type] and vlist[idx + 1] in hand[tile_type] and vlist[idx + 2] in hand[tile_type]:
                    shun_num += 1
                    hand[tile_type].remove(vlist[idx])
                    hand[tile_type].remove(vlist[idx + 1])
                    hand[tile_type].remove(vlist[idx + 2])
                else:
                    idx += 1

        # find ke zi
        temp = dict(Counter(hand[tile_type]))
        for k, v in temp.items():
            if v >= 3:
                ke_num += 1
                hand[tile_type].remove(k)
                hand[tile_type].remove(k)
                hand[tile_type].remove(k)
    if shun_num + ke_num == 4:
        for k, v in hand.items():
            if v and len(v) == 2 and v[0] == v[1]:
                return True
    return False

def tile_score(type, tiles):
    lwt_score = 1000000
    discard = None
    para = [10, 2, 1]
    scale = 10
    if type in ['character', 'bamboo', 'dots']:
        temp_tiles = copy.deepcopy(tiles)
        type_set = set(temp_tiles)
        vlist = sorted(list(type_set))
        idx = 0
        while idx < len(vlist):
            if idx + 2 < len(vlist) and vlist[idx + 2] - vlist[idx + 1] == 1 and vlist[idx + 1] - vlist[idx] == 1 \
                    and vlist[idx] in temp_tiles and vlist[idx + 1] in temp_tiles and vlist[idx + 2] in temp_tiles:
                temp_tiles.remove(vlist[idx])
                temp_tiles.remove(vlist[idx + 1])
                temp_tiles.remove(vlist[idx + 2])
            else:
                idx += 1

        temp = dict(Counter(temp_tiles))
        for k, v in temp.items():
            if v >= 3:
                temp_tiles.remove(k)
                temp_tiles.remove(k)
                temp_tiles.remove(k)

        for index, c in enumerate(temp_tiles):
            score = 0
            for cc in temp_tiles:
                gap = abs(cc - c)
                if gap < 3:
                    score += para[gap] * scale

            if score < lwt_score:
                lwt_score = score
                discard = c
    else:
        temp = dict(Counter(tiles))
        for k, v in temp.items():
            score = para[0] * v * scale
            if score < lwt_score:
                lwt_score = score
                discard = k
    return discard, lwt_score


def cal_result(hand, discard=None, open=None):
    # Calculate best discard from hand.
    print("Input: ", hand)
    hand = sort_hand(hand)
    hand2 = copy.deepcopy(hand)
    print("Sort : ",hand)
    if if_win(hand2):
        return ""
    thirteen_orphans = ['c1', 'c9', 'b1', 'b9', 'd1', 'd9', 'w1', 'w2', 'w3', 'w4', 'r1', 'r2', 'r3']
    mapping = {'dragon': 'r', 'wind': 'w', 'character': 'c', 'bamboo': 'b', 'dots': 'd'}
    total_tiles = []
    for t, v in hand.items():
        total_tiles += [mapping[t] + str(i) for i in v]
    count = 0
    for o in thirteen_orphans:
        if o in total_tiles:
            count += 1
            total_tiles.remove(o)
    if count >= 10 and len(total_tiles) > 0:
        return 100, total_tiles[0]

    total_tiles = []
    for t, v in hand.items():
        total_tiles += [mapping[t] + str(i) for i in v]
    temp = dict(Counter(total_tiles))
    count = 0
    for k, v in temp.items():
        if v == 2 or v == 4:
            count += v / 2
            for _ in range(v):
                total_tiles.remove(k)
    if count >= 5 and len(total_tiles) > 0:
        return 100, total_tiles[0]

    score = None
    tile = None

    for type in ['dragon', 'wind', 'character', 'bamboo', 'dots']:
        if hand[type]:
            discard, lwt_score = tile_score(type, hand[type])
            if score is None or lwt_score < score:
                score = lwt_score
                tile = mapping[type] + str(discard)

    tile = out_format(tile)
    print("Reslt: ", tile)
    return score, tile

def out_format(out_tile):
    if out_tile[0] in ['c', 'b', 'd']:
        out_tile = out_tile.replace('c', 'm')
        out_tile = out_tile.replace('b', 's')
        out_tile = out_tile.replace('d', 'p')
        out_tile = out_tile[1]+out_tile[0]
    else:
        out_tile = out_tile.replace('w1', '1z')
        out_tile = out_tile.replace('w2', '2z')
        out_tile = out_tile.replace('w3', '3z')
        out_tile = out_tile.replace('w4', '4z')
        out_tile = out_tile.replace('r1', 'z5')
        out_tile = out_tile.replace('r2', 'z6')
        out_tile = out_tile.replace('r3', 'z7')
    return out_tile

if __name__ == '__main__':

    hand = ["4z", "2z", "3s", "2s", "3p", "4s", "8p", "2p", "1m", "5m", "8p", "6m", "5z", "3m"]
    score, tile = cal_result(hand)
