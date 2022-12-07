
/**
 * Calculates the minimum shanten of the hand, considering a standard hand, seven pairs, or thirteen orphans.
 * @param {TileCounts} handToCheck The hand to calculate the shanten of.
 */
function calculateMinimumShanten(handToCheck, minimumShanten = -2) {
    let chiitoiShanten = calculateChiitoitsuShanten(handToCheck);

    // If it's complete with chiitoi, no need to keep checking.
    if (chiitoiShanten < 0) {
        return chiitoiShanten;
    }

    let kokushiShanten = calculateKokushiShanten(handToCheck);

    // If a hand has a kokushi shanten of 3 or less, it cannot possibly be closer to a standard hand
    // Example: 1239m1239s199p123z is 3-shanten for both kokushi and a standard hand
    // But 1239m1239s199p112z is 4-shanten for kokushi and 2-shanten for standard
    if (kokushiShanten < 3) {
        return kokushiShanten;
    }

    let standardShanten = calculateStandardShanten(handToCheck, minimumShanten);

    return Math.min(standardShanten, chiitoiShanten, kokushiShanten);
}

let hand = new Array(38);
let completeSets;
let pair;
let partialSets;
let bestShanten;
let minimumShanten;
let hasGivenMinimum;

/**
 * Calculates how many tiles away from a complete knitted straight hand the current hand is. (WIP...)
 * @param {TileCounts} handToCheck The hand to calculate the shanten of.
 */
function calculateKnittedShanten(handToCheck) {
    let honorsCount = 0;

    for (let i = 31; i < handToCheck.length; i++) {
        if (handToCheck[i] >= 1) {
            honorsCount++;
        }
    }

    let bestKnittedStraight = findMostViableKnittedStraight(handToCheck);
    let knittedAndHonorsShanten = 13 - bestKnittedStraight.length - honorsCount;

    let hand = handToCheck.slice();
    for (let i = 0; i < bestKnittedStraight.length; i++) {
        hand[bestKnittedStraight[i]]--;
    }

    let knittedStraightShanten = 9 - bestKnittedStraight.length;
    let standardShanten = calculateStandardShanten(hand);
    let combinedShanten = standardShanten - Math.floor(bestKnittedStraight / 3) * 2;

    return Math.min(combinedShanten, knittedAndHonorsShanten);
}

/**
 * Finds which knitted straight the hand is closest to.
 * @param {TileCounts} handToCheck The hand to check.
 */
function findMostViableKnittedStraight(handToCheck) {
    let possibilites = [
        [1, 4, 7, 12, 15, 18, 23, 26, 29],
        [1, 4, 7, 22, 25, 28, 13, 16, 19],
        [11, 14, 17, 22, 25, 28, 3, 6, 9],
        [11, 14, 17, 2, 5, 8, 23, 26, 29],
        [21, 24, 27, 2, 5, 8, 13, 16, 19],
        [21, 24, 27, 12, 15, 18, 3, 6, 9]
    ];

    let best = [];

    for (let i = 0; i < possibilites.length; i++) {
        let current = [];

        for (let j = 0; j < possibilites[i].length; j++) {
            if (handToCheck[possibilites[i][j]] >= 1) {
                current.push(possibilites[i][j]);
            }
        }

        if (current.length > best.length) {
            best = current.slice();

            // 9 is the best case scenario
            if (best.length === 9) return best;
        }
    }

    return best;
}

/**
 * Calculates how many tiles away from chiitoitsu/seven pairs the hand is.
 * @param {TileCounts} handToCheck The hand to calculate the shanten of.
 */
function calculateChiitoitsuShanten(handToCheck) {
    hand = convertRedFives(handToCheck);
    let pairCount = 0, uniqueTiles = 0;

    for (let i = 1; i < hand.length; i++) {
        if (hand[i] === 0) continue;

        uniqueTiles++;

        if (hand[i] >= 2) {
            pairCount++;
        }
    }

    let shanten = 6 - pairCount;

    if (uniqueTiles < 7) {
        shanten += 7 - uniqueTiles;
    }

    return shanten;
}

/**
 * Calculates how many tiles away from kokushi/thirteen orphans the hand is.
 * @param {TileCounts} handToCheck The hand to calculate the shanten of.
 */
function calculateKokushiShanten(handToCheck) {
    let uniqueTiles = 0;
    let hasPair = 0;

    for (let i = 1; i < handToCheck.length; i++) {
        if (i % 10 === 1 || i % 10 === 9 || i > 30) {
            if (handToCheck[i] !== 0) {
                uniqueTiles++;

                if (handToCheck[i] >= 2) {
                    hasPair = 1;
                }
            }
        }
    }

    return 13 - uniqueTiles - hasPair;
}

/**
 * Calculates how many tiles away from a complete standard hand the given hand is.
 * @param {TileCounts} handToCheck The hand to calculate the shanten of.
 */
function calculateStandardShanten(handToCheck, minimumShanten_ = -2) {
    hand = convertRedFives(handToCheck);

    // Initialize variables
    hasGivenMinimum = true;
    minimumShanten = minimumShanten_;
    completeSets = 0;
    pair = 0;
    partialSets = 0;
    bestShanten = 8;

    if (minimumShanten_ == -2) {
        hasGivenMinimum = false;
        minimumShanten = -1;
    }

    // Loop through hand, removing all pair candidates and checking their shanten
    for (let i = 1; i < hand.length; i++) {
        if (hand[i] >= 2) {
            pair++;
            hand[i] -= 2;
            removeCompletedSets(1);
            hand[i] += 2;
            pair--;
        }
    }

    // Check shanten when there's nothing used as a pair
    removeCompletedSets(1);

    return bestShanten;
}

/**
 * Removes all possible combinations of complete sets from the hand and recursively checks the shanten of each.
 * @param {TileIndex} i The current tile index to check from.
 */
function removeCompletedSets(i) {
    if (bestShanten <= minimumShanten) return;
    // Skip to the next tile that exists in the hand.
    for (; i < hand.length && hand[i] === 0; i++) { }

    if (i >= hand.length) {
        // We've gone through the whole hand, now check for partial sets.
        removePotentialSets(1);
        return;
    }

    // Pung
    if (hand[i] >= 3) {
        completeSets++;
        hand[i] -= 3;
        removeCompletedSets(i);
        hand[i] += 3;
        completeSets--;
    }

    // Chow
    if (i < 30 && hand[i + 1] !== 0 && hand[i + 2] !== 0) {
        completeSets++;
        hand[i]--; hand[i + 1]--; hand[i + 2]--;
        removeCompletedSets(i);
        hand[i]++; hand[i + 1]++; hand[i + 2]++;
        completeSets--;
    }

    // Check all alternative hand configurations
    removeCompletedSets(i + 1);
}

/**
 * Removes all possible combinations of pseudo sets from the hand and recursively checks the shanten of each.
 * @param {TileIndex} i The current tile index to check from.
 */
function removePotentialSets(i) {
    if (bestShanten <= minimumShanten) return;

    // If we've given a minimum shanten, we can break off early in some cases
    // For example, if we know the hand wants to be tenpai, we know the hand must have 3 complete sets
    if (hasGivenMinimum && completeSets < 3 - minimumShanten) return;

    // Skip to the next tile that exists in the hand
    for (; i < hand.length && hand[i] === 0; i++) { }

    if (i >= hand.length) {
        // We've checked everything. See if this shanten is better than the current best.
        let currentShanten = 8 - (completeSets * 2) - partialSets - pair;
        if (currentShanten < bestShanten) {
            bestShanten = currentShanten;
        }
        return;
    }

    // A standard hand will only ever have four groups plus a pair.
    if (completeSets + partialSets < 4) {
        // Pair
        if (hand[i] === 2) {
            partialSets++;
            hand[i] -= 2;
            removePotentialSets(i);
            hand[i] += 2;
            partialSets--;
        }

        // Edge or Side wait protorun
        if (i < 30 && hand[i + 1] !== 0) {
            partialSets++;
            hand[i]--; hand[i + 1]--;
            removePotentialSets(i);
            hand[i]++; hand[i + 1]++;
            partialSets--;
        }

        // Closed wait protorun
        if (i < 30 && i % 10 <= 8 && hand[i + 2] !== 0) {
            partialSets++;
            hand[i]--; hand[i + 2]--;
            removePotentialSets(i);
            hand[i]++; hand[i + 2]++;
            partialSets--;
        }
    }

    // Check all alternative hand configurations
    removePotentialSets(i + 1);
}

/**
 * Gets a random item in an array.
 * @param {any[]} array An array of items.
 * @returns {any} A random item from the array.
 */
function getRandomItem(array) {
    return array[randomInt(array.length)];
}

function evaluateBestDiscard(ukeireObjects) {
    let ukeire = ukeireObjects.map(o => o.value);
    let bestUkeire = Math.max(...ukeire);
    let bests = [];

    for (let i = 0; i < ukeire.length; i++) {
        if (ukeire[i] === bestUkeire) {
            bests.push(i);
        }
    }

    if (!bests.length) return -1;
    if (bests.length === 1) return bests[0];

    // Suggest discarding winds first, then dragons, if any are present.
    if (bests.indexOf(32) > -1) return 32;
    if (bests.indexOf(33) > -1) return 33;
    if (bests.indexOf(34) > -1) return 34;
    if (bests.indexOf(31) > -1) return 31;
    if (bests.indexOf(35) > -1) return 35;
    if (bests.indexOf(36) > -1) return 36;
    if (bests.indexOf(37) > -1) return 37;

    // Suggest discarding terminals.
    for (let i = 1; i < 10; i += 8) {
        for (let j = 0; j < 3; j++) {
            let tile = j + i;

            if (bests.indexOf(tile) > -1) return tile;
        }
    }

    // Suggest discarding twos and eights.
    for (let i = 2; i < 10; i += 6) {
        for (let j = 0; j < 3; j++) {
            let tile = j + i;

            if (bests.indexOf(tile) > -1) return tile;
        }
    }

    // Suggest a random remaining tile (all are between 3 and 7)
    return getRandomItem(bests);
}

function calculateDiscardUkeire(handArray, remainingTiles, baseShanten = -2) {
    let results = Array(handArray.length).fill(0);
    let convertedHand = convertRedFives(handArray);
    if (baseShanten === -2) {
        baseShanten = calculateMinimumShanten(convertedHand);
    }
    // Check the ukeire of each hand that results from each discard
    for (let handIndex = 0; handIndex < convertedHand.length; handIndex++) {
        if (convertedHand[handIndex] === 0) {
            results[handIndex] = { value: 0, tiles: [] };
            continue;
        }

        convertedHand[handIndex]--;
        let ukeire = calculateUkeire(convertedHand, remainingTiles, calculateMinimumShanten, baseShanten);
        convertedHand[handIndex]++;

        results[handIndex] = ukeire;
    }

    return results;
}

/**
 * Calculates the ukeire of a hand.
 * @param {TileCounts} hand The number of each tile in the player's hand.
 * @param {TileCounts} remainingTiles The number of each tile the player cannot see.
 * @param {Function} shantenFunction The function to use to calculate shanten
 * @param {number} baseShanten The hand's current shanten, if precalculated.
 * @param {number} shantenOffset The hand's current shanten offset, if precalculated.
 * @returns {UkeireObject} The ukeire data.
 */
function calculateUkeire(hand, remainingTiles, shantenFunction, baseShanten = -2) {
    let convertedHand = convertRedFives(hand);
    let convertedTiles = convertRedFives(remainingTiles);

    if (baseShanten === -2) {
        baseShanten = shantenFunction(convertedHand);
    }

    let value = 0;
    let tiles = [];

    // Check adding every tile to see if it improves the shanten
    for (let addedTile = 1; addedTile < convertedHand.length; addedTile++) {
        if (remainingTiles[addedTile] === 0) continue;
        if (addedTile % 10 === 0) continue;

        convertedHand[addedTile]++;

        if (shantenFunction(convertedHand, baseShanten - 1) < baseShanten) {
            // Improves shanten. Add the number of remaining tiles to the ukeire count
            value += convertedTiles[addedTile];
            tiles.push(addedTile);
        }

        convertedHand[addedTile]--;
    }

    return {
        value,
        tiles
    };
}

/**
 * Converts red fives to normal fives.
 * @param {TileIndex|TileIndex[]} tiles The tile index to convert, or an array of tile indexes.
 * @returns {TileIndex|TileIndex[]} The converted tile(s).
 */
function convertRedFives(tiles) {
    if (typeof tiles === 'number') {
        if (tiles % 10 === 0) {
            return tiles + 5;
        }
    }

    if (typeof tiles === 'object' && tiles.length) {
        let result = tiles.slice();

        for (let i = 0; i < 30; i += 10) {
            result[i + 5] += result[i];
            result[i] = 0;
        }

        return result;
    }

    return tiles;
}

/** Array of localization keys for each number tile value. */
const valueKeys = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9];
/** Array of localization keys for each suit. */
// const suitKeys = ["suits.characters", "suits.circles", "suits.bamboo"];
const suitKeys = ["m", "p", "s"];
/** Array of localization keys for each honor tile value. */
const honorKeys = ["hidden", "east", "south", "west", "north", "white", "green", "red"];



function getTileAsText(index) {
    if (index >= 30) {
        // return honorKeys[index - 30];
        return `${index - 30}z`;
    }
    const value = valueKeys[index % 10];
    const suit = suitKeys[Math.floor(index / 10)];

    return `${value}${suit}`;
}

function sortHand(hand) {
    var sortHand = {'m':[], 'p':[], 's':[], 'z':[]}
    for(let tile of hand){
        sortHand[tile[1]].push(tile[0])
    }
    var handText = "";
    for(var key in sortHand){
        if(sortHand[key].length > 0){
            handText += sortHand[key].join('') + key
        }
    }
    return handText;
}

function arrayFromHand(originhand) {
    var handArray = new Array(38).fill(0)
    for(var i = 0; i < originhand.length; i++){
        var tile = originhand[i];
        var suit = tile.charAt(1);
        var by = 0;
        if(suitKeys.indexOf(suit) < 0){
            by = 3;
        } else {
            by = suitKeys.indexOf(suit);
        }
        var index = by * 10 + parseInt(tile.charAt(0))
        handArray[index] = handArray[index] + 1;
    }
    return handArray;
}


function leftTiles(handArray) {
    var remainingTilesArray = new Array(38).fill(4)
    remainingTilesArray[0] = 1
    remainingTilesArray[10] = 1
    remainingTilesArray[20] = 1
    remainingTilesArray[30] = 0
    for(let index in handArray){
        remainingTilesArray[index] = remainingTilesArray[index] - handArray[index];
    }
    return remainingTilesArray;
}
function handFromSorted(sorted) {
    var hand = [];
    var index = 0;
    for(var i = 0; i < sorted.length; i++){
        if(sorted.charAt(i) <= '9' && sorted.charAt(i) >= '0'){
            hand.push(sorted.charAt(i))
        } else {
            for(var j = index; j < hand.length; j++){
                hand[j] = hand[j] + sorted.charAt(i)
            }
            index = hand.length;
        }
    }
    return hand;
}

function checkWin(originhand){
    originhand = originhand.slice();
    var sum = 0;
    for(var count of originhand){
        sum += count;
    }
    if(sum != 14){
        return false;
    }
    // seven pairs
    var pairCount = 0;
    for(var k of originhand){
       if(k % 2 == 0){
           pairCount += k / 2;
       }
    }
    if(pairCount === 7){
        return true;
    }
    // thirteen orphans
    var flag = true;
    for(var index of [1,9,11,19,21,29,31,32,33,34,35,36,37,38]){
        if(originhand[index] !== 1){
            flag = false;
            break;
        }
    }
    if(flag){
        return true;
    }
    // normal
    var shun_num = 0;
    var ke_num = 0;
    var i = 0;

    while (i <= 7){
        if(originhand[i] >= 1 && originhand[i+1] >= 1 && originhand[i+2] >= 1){
            originhand[i] -=  1;
            originhand[i+1] -= 1;
            originhand[i+2] -= 1;
            shun_num += 1
        }
        i = i + 1;
    }
    i = 11;
    while (i <= 17){
        if(originhand[i] >= 1 && originhand[i+1] >= 1 && originhand[i+2] >= 1){
            originhand[i] -=  1;
            originhand[i+1] -= 1;
            originhand[i+2] -= 1;
            shun_num += 1
        }
        i = i + 1;
    }
    i = 21;
    while (i <= 27){
        if(originhand[i] >= 1 && originhand[i+1] >= 1 && originhand[i+2] >= 1){
            originhand[i] -=  1;
            originhand[i+1] -= 1;
            originhand[i+2] -= 1;
            shun_num += 1
        }
        i = i + 1;
    }
    for(var i = 0; i <= 37; i++){
        if(originhand[i] == 3){
            originhand[i] -= 3;
            ke_num += 1;
        }
    }
    if(shun_num + ke_num == 4){
        sum = 0
        for(var count of originhand){
            sum += count;
        }
        if(sum == 2){
            return true;
        }
    }
    return false;
}

function calculateBestDiscard(originhand, discard, open){
    var handArray = arrayFromHand(originhand);
    if(checkWin(handArray)){
        return  {
            'best': '',
            'number': 0,
            'tiles': 'nothing'
        };
    }
    var seenTiles = discard.concat(originhand).concat(open);
    var remainingTilesArray = leftTiles(arrayFromHand(seenTiles))
    var ukeire = calculateDiscardUkeire(handArray, remainingTilesArray);
    var bestTile = evaluateBestDiscard(ukeire);
    var text = getTileAsText(bestTile);
    if(ukeire[bestTile].tiles.length == 0){
        var listenTiles = "nothing";
    } else {
        var listenTiles = ukeire[bestTile].tiles.map(function (tile){
            return getTileAsText(tile);
        }).reduce(function(prev, cur) {
            return prev + ", " + cur;
        })
    }
    return {
        'best': text,
        'number': ukeire[bestTile].value,
        'tiles': listenTiles
    };
}


// The number of each tile in the player's hand.
var originhand = ["1p", "2p", "4s", "5s", "6s", "1s", "1z", "1z", "6z", "6z", "6z", "6z", "5z", "5z"]

var discard = ['3p']
var open = ['5p']
var result = calculateBestDiscard(tileList.hand, tileList.discard, tileList.open)
var bestTile = result.best;
var listenTiles = result.tiles;
var listenNumber = result.number;
var sortedHand = sortHand(originhand)
console.log('Sort hand:', sortedHand)
console.log('Best discard:', bestTile)
console.log(`And it can result in ${listenNumber} tile of ${listenTiles} that can improve the hand.`)
