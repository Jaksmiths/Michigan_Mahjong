import json
from collections import Counter
import copy
from turtle import width

'''
c1-9 = character 1-9
b1-9 = bamboo 1-9
d1-9 = dots 1-9
w1 = East, w2 = West, w3 = South, w4 = North
r1 = Red, r2 = green, r3 = white
'''


def read_hand(filename):
    # Read hand from json file.
    with open(filename, "r") as read_file:
        data = json.load(read_file)
    return data['tilelist']


def sort_hand(hand):
    # Sort hand tile in to different list.
    character = []
    bamboo = []
    dots = []
    wind = []
    dragon = []
    for tile in hand:
        if tile[0] == 'c':
            character.append(int(tile[1]))
        elif tile[0] == 'b':
            bamboo.append(int(tile[1]))
        elif tile[0] == 'd':
            dots.append(int(tile[1]))
        elif tile[0] == 'w':
            wind.append(int(tile[1]))
        elif tile[0] == 'r':
            dragon.append(int(tile[1]))
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
        if tile_type == 'character' or tile_type == 'bamboo' or tile_type == 'dots':
            type_set = set(hand[tile_type])
            while len(type_set) >= 3:
                vlist = sorted(list(type_set))
                idx = 0
                while idx < len(vlist):
                    if vlist[idx + 2] - vlist[idx + 1] == 1 and vlist[idx + 1] - vlist[idx] == 1:
                        shun_num += 1
                        hand[tile_type].remove(vlist[idx])
                        hand[tile_type].remove(vlist[idx + 1])
                        hand[tile_type].remove(vlist[idx + 2])
                        idx += 3
                    else:
                        idx += 1
                type_set = set(hand[tile_type])
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


def cardsScore(cards, dark):
    scores = []

    for c in cards:
        score = 0
        for cc in cards:
            gap = abs(cc - c)
            if gap < 3:
                score += para[gap] * scale

        scores.append(score)

    return scores


def tile_score(type, tiles):
    lwt_score = 1000000
    discard_idx = 0
    para = [10, 2, 1]
    scale = 50
    if type in ['character', 'bamboo', 'dots']:
        for index, c in enumerate(tiles):
            score = 0
            for cc in tiles:
                gap = abs(cc - c)
                if gap < 3:
                    score += para[gap] * scale

            if score < lwt_score:
                lwt_score = score
                discard_idx = index

    return discard_idx, lwt_score


def cal_result(hand):
    # Calculate best discard from hand.
    score = None
    card = None
    for type in hand:
        if hand[type]:
            discard_idx, lwt_score = tile_score(type, hand[type])
            if score is None or lwt_score < score:
                score = lwt_score
                card = type[0] + str(hand[type][discard_idx])
    return score, card


if __name__ == '__main__':
    print('- - - Start - - -')

    hand = read_hand('sample_input.json')
    hand = sort_hand(hand)
    hand2 = copy.deepcopy(hand)

    print("win:", if_win(hand))

    print("hand2: ", hand2)

    score, card = cal_result(hand2)
    print("The best discard from hand is %s with score %d." % (card, score))
