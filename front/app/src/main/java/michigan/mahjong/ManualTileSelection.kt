package michigan.mahjong

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import michigan.mahjong.TileStore.discard
import michigan.mahjong.TileStore.setTile

var tileSelected by mutableStateOf("")

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ManualTileCorrection(
    context: Context,
    navController: NavHostController,
    tileIndex: Int,
    tileGroup: TileGroup
) {

    val SuitM = listOf<String>("1m", "1p", "1s", "1z")

    MTCBackground()

    var currentTiles = SuitM[0]
    ComposableA(currentTiles, navController, tileIndex, tileGroup)
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
fun ComposableA(
    tile: String,
    navController: NavHostController,
    tileIndex: Int,
    tileGroup: TileGroup
) {
    var currentSuit by remember { mutableStateOf(tile[1]) }

    Row {
        ComposableB(
            suit = currentSuit,
            navController = navController,
            tileIndex = tileIndex,
            tileGroup = tileGroup
        )
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
                        color = Color.White,
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
                            "", tint = Color.White)
                    }
                    MTCTileButtonType3("1" + nextSuit(currentSuit))
                }
            }
        }
    }
}

@Composable
fun ComposableB(
    suit: Char,
    navController: NavHostController,
    tileIndex: Int,
    tileGroup: TileGroup
) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(.65f)
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
                        WhiteOutLinedButton(
                            navController = navController,
                            path = "back",
                            text = "Confirm",
                            onClick = {
                                setTile(tileSelected, tileGroup, tileIndex)
                                discard.value = null
                            }
                        )
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
            }
}

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

@Composable
fun WhiteOutLinedButton(navController: NavHostController?, path: String, text: String, onClick: () -> Unit = {}) {
    Button(
        onClick = {
            onClick()
            navController?.popBackStack()
        },
        border = BorderStroke(0.8.dp, Color.White),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White, backgroundColor = Color.Transparent),
        shape = RoundedCornerShape(20.dp),
        elevation =  ButtonDefaults.elevation(
            defaultElevation = 10.dp,
            pressedElevation = 15.dp,
            disabledElevation = 0.dp
        ),
        contentPadding = PaddingValues(),
        modifier = Modifier
            .height(30.dp)
            .width(150.dp)
            .defaultMinSize(minWidth = 1.dp, minHeight = 1.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text, fontSize = 15.sp)
        }
    }
}