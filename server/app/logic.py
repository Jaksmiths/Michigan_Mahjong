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

suit_name = {'m': 'Man', 'p': 'Pin', 's': 'Sou'}
honor_name = {
    '1z': 'East',
    '2z': 'South',
    '3z': 'West',
    '4z': 'North',
    '5z': 'White',
    '6z': 'Green',
    '7z': 'Red'
}


def group_hand(hand):
    tile_dict = {}
    for tile in hand:
        if tile[1] not in tile_dict:
            tile_dict[tile[1]] = [int(tile[0])]
        else:
            tile_dict[tile[1]].append(int(tile[0]))
    return tile_dict


def if_win(hand):
    # Check for thirteen orphans
    if 'm' in hand and 1 in hand['m'] and 9 in hand['m']:
        if 's' in hand and 1 in hand['s'] and 9 in hand['s']:
            if 'p' in hand and 1 in hand['p'] and 9 in hand['p']:
                if 'z' in hand and 1 in hand['z'] and 2 in hand['z'] and 3 in hand['z'] and 4 in hand['z']:
                    if 5 in hand['z'] and 6 in hand['z'] and 7 in hand['z']:
                        print("- - thirteen orphans! - - ")
                        return True

    # Check for seven pairs
    for tile_type in hand:
        temp = dict(Counter(hand[tile_type]))
        temp = list(temp.values())
        if 1 in temp or 3 in temp:
            break
    else:
        return True

    # Check for 4*3+2:
    shun_num, ke_num = 0, 0
    for tile_type in hand:
        # find shun zi
        if tile_type in ['m', 's', 'p']:
            type_set = set(hand[tile_type])
            vlist = sorted(list(type_set))
            idx = 0
            while idx < len(vlist):
                if idx + 2 < len(vlist) and vlist[idx + 2] - vlist[idx + 1] == 1 and vlist[idx + 1] - vlist[idx] == 1 \
                        and vlist[idx] in hand[tile_type] and vlist[idx + 1] in hand[tile_type] and vlist[idx + 2] in \
                        hand[tile_type]:
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
    print("Sort2: ", hand)
    return False


def tile_score(tile_type, tiles, discard_tiles):
    lwt_score = 1000000
    para = [10, 2, 1]
    scale = 10
    shun = []
    ke = []
    des = ""
    if tile_type in ['m', 's', 'p']:
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
                shun.append([vlist[idx], vlist[idx + 1], vlist[idx + 2]])
            else:
                idx += 1

        temp = dict(Counter(temp_tiles))
        for k, v in temp.items():
            if v >= 3:
                temp_tiles.remove(k)
                temp_tiles.remove(k)
                temp_tiles.remove(k)
                ke.append(k)

        for c in temp_tiles:
            score = 0
            for cc in temp_tiles:
                gap = abs(cc - c)
                if gap < 3:
                    score += para[gap] * scale

            for cc in discard_tiles:
                gap = abs(cc - c)
                if gap < 3:
                    score -= para[gap]

            if score < lwt_score or (score == lwt_score and (c - 1) * (9 - c) < (discard - 1) * (9 - discard)):
                des = suit_name[tile_type] + " " + str(c)
                lwt_score = score
                discard = c

        return discard, lwt_score, "For %s Suit, %s are harder to ..." % (suit_name[tile_type], des)
    else:
        temp = dict(Counter(tiles))
        for k, v in temp.items():
            score = para[0] * v * scale
            if score < lwt_score:
                lwt_score = score
                discard = k
        return discard, lwt_score, "For Honor tiles, %s are harder to ..." % (honor_name[str(discard) + tile_type])


def cal_result(hand, discard=None, open=None):
    # Calculate best discard from hand.
    print("Input: ", hand)
    group_tiles = group_hand(hand)
    print("Sort : ", group_tiles)

    if if_win(group_hand(hand)):
        print("Reslt: ", "winned")
        return ""
    thirteen_orphans = ['1m', '9m', '1p', '9p', '1s', '9s', '1z', '2z', '3z', '4z', '5z', '6z', '7z']
    count = 0
    copy_hand = copy.deepcopy(hand)
    for o in thirteen_orphans:
        if o in copy_hand:
            count += 1
            copy_hand.remove(o)
    if count >= 10 and len(copy_hand) > 0:
        tile = copy_hand[0]
        print("Reslt: ", tile, 100)
        return tile, "There have been %d available tiles for thirteen orphans! And %s could be discarded." % (
        count, tile)

    copy_hand = copy.deepcopy(hand)
    temp = dict(Counter(copy_hand))
    count = 0
    for k, v in temp.items():
        if v == 2 or v == 4:
            count += v / 2
            for _ in range(v):
                copy_hand.remove(k)
    if count >= 5 and len(copy_hand) > 0:
        tile = copy_hand[0]
        print("Reslt: ", tile, 100)
        return tile, "There have been %d available pairs for seven pairs! And %s could be discarded." % (count, tile)

    score = None
    tile = None
    description = []
    group_discard = group_hand(discard)
    for k, v in group_tiles.items():
        dis = []
        if k in group_discard:
            dis = group_discard[k]
        discard, lwt_score, des = tile_score(k, v, dis)
        description.append(des)
        if score is None or lwt_score < score:
            score = lwt_score
            tile = str(discard) + k

    print("Reslt: ", tile, score)
    print("Description: ", ". ".join(description) + ". \n" + \
          "So the final result is %s, because it has less adjacent tiles or pairs considering the discarded tiles." % tile)
    return tile



if __name__ == '__main__':
    hand = ["1p", "2p", "8p", "5z", "5s", "5s", "1z", "1z", "1z", "6z", "6z", "6z", "5z", "5z"]
    cal_result(hand, ['8p'])
