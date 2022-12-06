package michigan.mahjong

import android.content.Context
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.key.Key.Companion.Back
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

var tileSelected by mutableStateOf("")

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ManualTileCorrection(context: Context, navController: NavHostController, currentTile: String) {

    var isLaunching by rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        if (isLaunching) {
            isLaunching = false
        }
    }

    var isRefreshing by remember { mutableStateOf(false) }

    val SuitM = listOf<String>("1m", "1p", "1s", "1z")

    MTCBackground()

    /*
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxHeight(1f)
            .fillMaxWidth(1f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .fillMaxHeight(0.5f)
                .background(Color.White)
        ){}
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth(1f)
                .fillMaxHeight(1f)
                .background(Color.Yellow)
                //.horizontalScroll(rememberScrollState())
        ) {

            for (tile in SuitM) {
                MTCTileButton(tile, navController)
            }
            */

    var currentTiles = SuitM[0]
    ComposableA(currentTiles, navController)

            /*
            var cardFace by remember {
                mutableStateOf(CardFace.Front)
            }

            FlipCard(
                cardFace = cardFace,
                onClick = { cardFace = cardFace.next },
                modifier = Modifier
                    .fillMaxWidth(.5f)
                    .aspectRatio(1f),
                axis = RotationAxis.AxisY,
                front = {
                    Box(
                        modifier = Modifier.fillMaxSize().background(Color.Red),
                        contentAlignment = Alignment.Center,
                    ) {
                        MTCTileButton(SuitM[0], navController)
                    }
                },
                middle = {
                    Box(modifier = Modifier.fillMaxSize().background(Color.Blue),
                        contentAlignment = Alignment.Center,
                    ){
                        MTCTileButton(SuitM[1], navController)
                    }
                },
                back = {
                    Box(
                        modifier = Modifier.fillMaxSize().background(Color.Blue),
                        contentAlignment = Alignment.Center,
                    ) {
                        MTCTileButton(SuitM[2], navController)
                    }
                },
            )*/
        //}
    //}
    //https://developer.android.com/jetpack/compose/lists
    //https://developer.android.com/jetpack/compose/gestures
}

fun nextSuit(s: Char): Char {
    if (s == 'm') return 'p'
    else if (s == 'p') return 's'
    else if (s == 's') return 'z'
    else return 'm'
}

fun prevSuit(s: Char): Char {
    if (s == 'm') return 'z'
    else if (s == 'p') return 'm'
    else if (s == 's') return 'p'
    else return 's'
}

@Composable
fun ComposableA(tile: String, navController: NavHostController) {
    var currentSuit by remember { mutableStateOf(tile[1]) }

    Row {
        ComposableB(suit = currentSuit, navController = navController){}
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(1f)
                //.background(Color.Magenta)
            ,
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    //.background(Color.Blue)
                ,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                   modifier = Modifier
                       .fillMaxWidth()
                       .fillMaxHeight(0.1f)
                       //.background(Color.Magenta)
                    ,
                    horizontalArrangement = Arrangement.Center
                ){
                    Text(
                        modifier = Modifier
                            .padding(4.dp),
                        text = "Suit Selector",
                        color = Color(0xff9d8eff),
                        //fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(.6f)
                        //.background(Color.Black)
                    ,
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    MTCTileButtonType3("1" + prevSuit(currentSuit))
                    IconButton(
                        modifier = Modifier.fillMaxWidth(.12f)
                            .padding(2.dp),
                        onClick = {
                            currentSuit = prevSuit(currentSuit)
                        })
                    {
                        Icon(Icons.Filled.ArrowBackIosNew, "", tint = Color.White)
                        //chevron_right
                        //chevron_left
                    }
                    MTCTileButtonType2("1" + currentSuit)
                    IconButton(
                        modifier = Modifier.fillMaxWidth(.26f)
                            .padding(2.dp),
                        onClick = {
                            currentSuit = nextSuit(currentSuit)
                        })
                    {
                        Icon(Icons.Filled.ArrowForwardIos,
                            //painter = painterResource(id = R.drawable.ic_baseline_arrow_forward_ios_24),
                            "", tint = Color.White)
                    }
                    MTCTileButtonType3("1" + nextSuit(currentSuit))
                    /*
                Button(onClick = {
                    if (currentSuit.equals('z')) {
                        currentSuit = 'p'
                    } else if (currentSuit == 'p') {
                        currentSuit = 's'
                    } else {
                        currentSuit = 'z'
                    }
                }) {
                    Text(text = "Test")
                }
                */
                }
            }
        }
    }
}

@Composable
fun ComposableB(
    suit: Char,
    navController: NavHostController,
    function: () -> Unit
) {
    //var tileInfo by remember {mutableStateOf(true)}
    //val num = "1"

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(.65f)
                    //.background(Color.Blue)
                ,
                contentAlignment = Alignment.Center,
            ) {
                Row() {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(0.3f)
                            .fillMaxHeight(1f)
                            //.background(Color.White)
                        ,
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        MTCTileButtonType2(tileSelected)
                        Spacer(modifier=Modifier.fillMaxHeight(0.1f))
                        TransparentOutLinedButton(
                            navController = navController,
                            path = "back",
                            text = "Confirm")
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(1f)
                            .fillMaxHeight(1f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(1f)
                                .fillMaxHeight(0.4f)
                                //.background(Color.Cyan)
                            ,
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            if (suit != 'z') {
                                MTCTileButtonType1("1" + suit)
                                MTCTileButtonType1("2" + suit)
                                MTCTileButtonType1("3" + suit)
                                MTCTileButtonType1("4" + suit)
                                MTCTileButtonType1("5" + suit)
                            }
                            else {
                                MTCTileButtonType1("1" + suit)
                                MTCTileButtonType1("2" + suit)
                                MTCTileButtonType1("3" + suit)
                                MTCTileButtonType1("4" + suit)
                            }
                        }
                        Row(Modifier.fillMaxHeight(.15f)){}
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(1f)
                                .fillMaxHeight(1f)
                                //.background(Color.DarkGray)
                            ,
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.Top
                            ) {
                            if (suit != 'z') {
                                Spacer(Modifier.fillMaxHeight().fillMaxWidth(.03f))
                                MTCTileButtonType1("6" + suit)
                                MTCTileButtonType1("7" + suit)
                                MTCTileButtonType1("8" + suit)
                                MTCTileButtonType1("9" + suit)
                                Spacer(Modifier.fillMaxHeight().fillMaxWidth(.03f))
                            }
                            else {
                                Spacer(Modifier.fillMaxHeight().fillMaxWidth(.03f))
                                MTCTileButtonType1("5" + suit)
                                MTCTileButtonType1("6" + suit)
                                MTCTileButtonType1("7" + suit)
                                Spacer(Modifier.fillMaxHeight().fillMaxWidth(.03f))
                            }
                        }
                    }
                }

                //MTCTileButtonType1("1" + suit)
            }
            function()


            /*
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(.65f)
                    .background(Color.Red),
                contentAlignment = Alignment.Center,
            ) {
                Row() {
                    MTCTileButtonType1("1p")
                    MTCTileButtonType2("2p")
                }
            }
            */

}

enum class TileSuit(val s: String){
    Man("m"){

    },
    Pin("p"){

    },
    Sou("s"){

    },
    Honors("z"){

    }

}

/*
enum class CardFace(val angle: Float) {
    Front(0f) {
        override val next: CardFace
            get() = Middle
    },
    Middle(180f){
        override val next: CardFace
        get() = Back
                },
    Back(360f) {
        override val next: CardFace
            get() = Front
    };

    abstract val next: CardFace
}

enum class RotationAxis {
    AxisX,
    AxisY,
}

@ExperimentalMaterialApi
@Composable
fun FlipCard(
    cardFace: CardFace,
    onClick: (CardFace) -> Unit,
    modifier: Modifier = Modifier,
    axis: RotationAxis = RotationAxis.AxisY,
    back: @Composable () -> Unit = {},
    front: @Composable () -> Unit = {},
    middle: @Composable ()-> Unit ={},
) {
    val rotation = animateFloatAsState(
        targetValue = cardFace.angle,
        animationSpec = tween(
            durationMillis = 400,
            easing = FastOutSlowInEasing,
        )
    )
    Row() {
        Card(
            onClick = { onClick(cardFace) },
            modifier = modifier
                .graphicsLayer {
                    rotationY = rotation.value
                    cameraDistance = 12f * density
                },
        ) {
            if (rotation.value <= 90f) {
                Box(
                    Modifier.fillMaxSize()
                ) {
                    front()
                }
            } else if (rotation.value <= 270) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            rotationY = 180f
                        },
                ) {
                    middle()
                }
            } else {
                Box(
                    Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            rotationY = 180f
                        },
                ) {
                    back()
                }
            }
        }
    }
}
*/


@Composable
fun MTCTileButtonType1(
    tileName: String) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { tileSelected = tileName },
            colors = ButtonDefaults.outlinedButtonColors(contentColor =  Color.Black),
            shape = RoundedCornerShape(8.dp),
            elevation =  ButtonDefaults.elevation(
                defaultElevation = 10.dp,
                pressedElevation = 15.dp,
                disabledElevation = 0.dp
            ),
            contentPadding = PaddingValues(),
            modifier = Modifier
                .height(80.dp)
                .width(60.dp)
                .defaultMinSize(minWidth = 1.dp, minHeight = 1.dp)
        )
        {
            Image(
                painter = painterResource(getIcon(tileName)),
                contentDescription = tileName)
        }
    }
}

@Composable
fun MTCTileButtonType2(tileName: String) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { },
            colors = ButtonDefaults.outlinedButtonColors(contentColor =  Color.Black),
            shape = RoundedCornerShape(12.dp),
            elevation =  ButtonDefaults.elevation(
                defaultElevation = 10.dp,
                pressedElevation = 15.dp,
                disabledElevation = 0.dp
            ),
            contentPadding = PaddingValues(),
            modifier = Modifier
                .height(120.dp)
                .width(90.dp)
                .defaultMinSize(minWidth = 1.dp, minHeight = 1.dp)
        )
        {
            Image(
                painter = painterResource(getIcon(tileName)),
                contentDescription = tileName)
        }
    }
}

@Composable
fun MTCTileButtonType3(
    tileName: String) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {  },
            colors = ButtonDefaults.outlinedButtonColors(contentColor =  Color.Black),
            shape = RoundedCornerShape(8.dp),
            elevation =  ButtonDefaults.elevation(
                defaultElevation = 10.dp,
                pressedElevation = 15.dp,
                disabledElevation = 0.dp
            ),
            contentPadding = PaddingValues(),
            modifier = Modifier
                .height(80.dp)
                .width(60.dp)
                .defaultMinSize(minWidth = 1.dp, minHeight = 1.dp)
        )
        {
            Image(
                painter = painterResource(getIcon(tileName)),
                contentDescription = tileName)
        }
    }
}


@Composable
fun MTCBackground(
    @DrawableRes backgroundDrawableResId: Int = R.drawable.manual_tile_background,
    contentDescription: String? = "MTCBackground",
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.BottomStart,
    contentScale: ContentScale = ContentScale.FillHeight,
    alpha: Float = 0.7f,
    colorFilter: ColorFilter? = null
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Image(
            painter = painterResource(backgroundDrawableResId),
            contentDescription = contentDescription,
            alignment = alignment,
            contentScale = contentScale,
            alpha = alpha,
            colorFilter = colorFilter,
            modifier = Modifier
                .matchParentSize()
        )
    }
}
