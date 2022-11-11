package michigan.mahjong

import android.content.Context
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.FontWeight
import michigan.mahjong.TileStore.tiles
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.draw.clip
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import michigan.mahjong.TileStore.discard
import michigan.mahjong.TileStore.recmove
import michigan.mahjong.TileStore.reset
import michigan.mahjong.TileStore.setup

@Composable
fun CurrentHandView(context: Context, navController: NavHostController, apiSent: Boolean = false) {

    var isLaunching by rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        if (isLaunching) {
            isLaunching = false
            setup()
        }
    }

//    val systemUiController: SystemUiController = rememberSystemUiController()
//
//    systemUiController.isStatusBarVisible = false // Status bar
//    systemUiController.isNavigationBarVisible = false // Navigation bar
//    systemUiController.isSystemBarsVisible = false // Status & Navigation bars

    MainBackground()
    MainButtons(navController)


    Column(
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.fillMaxHeight()
    ) {
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column() {
                if (discard.value != null) {
                    ResetButton()
                    Spacer(modifier = Modifier.size(12.dp))
                }
                CalculateButton()
            }
            Spacer(modifier = Modifier.size(12.dp))
        }
        Spacer(modifier = Modifier.size(12.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            for (tile in tiles) {
                TileButton(tile.name, navController)
            }
        }
        Spacer(modifier = Modifier.size(10.dp))
    }

    Row(
        horizontalArrangement = Arrangement.End,
        modifier = Modifier.fillMaxWidth()
    ) {
        GuideButton(navController)
    }
}
//0xFF202325
@Composable
fun DiscardInfo() {
    Box(
        modifier = Modifier
            .size(250.dp, 100.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF2D3135))
    ) {
        Column(verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier=Modifier.fillMaxHeight()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(15.dp, Alignment.CenterHorizontally),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black),
                    shape = RoundedCornerShape(8.dp),
                    elevation = ButtonDefaults.elevation(
                        defaultElevation = 10.dp,
                        pressedElevation = 15.dp,
                        disabledElevation = 0.dp
                    ),
                    contentPadding = PaddingValues(),
                    modifier = Modifier
                        .height(60.dp)
                        .width(43.dp)
                        .defaultMinSize(minWidth = 1.dp, minHeight = 1.dp)
                ) {
                    Image(
                        painter = painterResource(getIcon(discard.value ?: "")),
                        contentDescription = discard.value ?: ""
                    )
                }
                Column {
                    Text("Optimal Discard:", color = Color(0xFFFEFEFE), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(getIconName(discard.value ?: ""), color = Color(0xFF969B9D), fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun GuideButton(navController: NavHostController) {
    IconButton(onClick = {  },
        modifier = Modifier
            .padding(13.dp)
            .border(1.dp, Color(0xff9d8eff), shape = CircleShape)
            .then(Modifier.size(43.dp))
    ) {
        Icon(painterResource(R.drawable.book), contentDescription = "guidebook button", tint = Color.White)
    }
}

//Color(0xffe3e535)

@Composable
fun ResetButton() {

    Button(
        onClick = { reset() },
        border = BorderStroke(1.dp, Color(0xff9d8eff), ),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White, backgroundColor = Color.Transparent),
        shape = RoundedCornerShape(10.dp),
        elevation =  ButtonDefaults.elevation(
            defaultElevation = 10.dp,
            pressedElevation = 15.dp,
            disabledElevation = 0.dp
        ),
        contentPadding = PaddingValues(),
        modifier = Modifier
            .height(33.dp)
            .width(100.dp)
            .defaultMinSize(minWidth = 1.dp, minHeight = 1.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text("Reset All", fontSize = 14.sp)
            Spacer(modifier = Modifier.size(3.dp))
            Icon(
                painter = painterResource(id = R.drawable.reset_alt),
                contentDescription = "reset icon",
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun CalculateButton() {

    Button(
        onClick = { MainScope().launch { recmove() }},
        border = BorderStroke(1.dp, Color(0xff9d8eff), ),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White, backgroundColor = Color.Transparent),
        shape = RoundedCornerShape(10.dp),
        elevation =  ButtonDefaults.elevation(
            defaultElevation = 10.dp,
            pressedElevation = 15.dp,
            disabledElevation = 0.dp
        ),
        contentPadding = PaddingValues(),
        modifier = Modifier
            .height(33.dp)
            .width(165.dp)
            .defaultMinSize(minWidth = 1.dp, minHeight = 1.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text("Calculate Discard", fontSize = 14.sp)
            Spacer(modifier = Modifier.size(3.dp))
            Icon(
                painter = painterResource(id = R.drawable.arrow),
                contentDescription = "right arrow icon",
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

//navController.navigate("ManualTileView?startTile=$tile.name")
//Color(0xffe3e535)
@Composable
fun TileButton(tileName: String, navController: NavHostController) {
    Button(onClick = { },
        colors = ButtonDefaults.outlinedButtonColors(contentColor =  Color.Black),
        shape = RoundedCornerShape(8.dp),
        elevation =  ButtonDefaults.elevation(
            defaultElevation = 10.dp,
            pressedElevation = 15.dp,
            disabledElevation = 0.dp
        ),
        contentPadding = PaddingValues(),
        modifier = Modifier
            .height(60.dp)
            .width(43.dp)
            .defaultMinSize(minWidth = 1.dp, minHeight = 1.dp)
    )
    {
        Image(
            painter = painterResource(if (tileName == "") R.drawable.add else getIcon(tileName)),
            contentDescription = tileName)
        //val emptyTile = tile.name.isEmpty()
//        Crossfade(targetState = emptyTile) { empty ->
//            when (empty) {
//                true-> Image(
//                    painter = painterResource(R.drawable.add),
//                    contentDescription = tile.name
//                )
//                false -> Image(
//                    painter = painterResource(getIcon(tile.name)),
//                    contentDescription = tile.name
//                )
//            }
//        }
//        Image(
//            painter = painterResource(if (tile.name.isEmpty()) R.drawable.add else getIcon(tile.name)),
//            contentDescription = tile.name
//        )
    }
}

@Composable
fun MainBackground(
    @DrawableRes backgroundDrawableResId: Int = R.drawable.background,
    contentDescription: String? = "mainBackground",
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.BottomStart,
    contentScale: ContentScale = ContentScale.Fit,
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
fun MainButtons(navController: NavHostController) {
    Column(verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier=Modifier.fillMaxHeight()) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (discard.value == null) {
                TransparentOutLinedButton(
                    "Camera",
                    R.drawable.camera_alt,
                    navController,
                    "CameraView"
                )
                TransparentOutLinedButton("Gallery", R.drawable.folder_alt)
                TransparentOutLinedButton(
                    "Reset",
                    R.drawable.reset_alt,
                    onClick = { reset() })
            }
            else {
                DiscardInfo()
            }

//            Crossfade(targetState = discard) { it ->
//                when (it.value) {
//                    is String -> {DiscardInfo(discard = "1m"); Log.i("kilo", it.value.toString())}
//                    else -> {
//                        TransparentOutLinedButton(
//                            "Camera",
//                            R.drawable.camera_alt,
//                            navController,
//                            "CameraView"
//                        )
//                        TransparentOutLinedButton("Gallery", R.drawable.folder_alt)
//                        TransparentOutLinedButton(
//                            "Reset",
//                            R.drawable.reset_alt,
//                            onClick = { reset() })
//                    }
//                }
//            }
        }
    }
}

@Composable
fun TransparentOutLinedButton(
    text: String,
    icon: Int,
    navController: NavHostController? = null,
    destination: String = "",
    onClick: () -> Unit = {}
) {

    Button(
        onClick = { navController?.navigate(destination); onClick() },
        border = BorderStroke(0.8.dp, Color(0xff9d8eff), ),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White, backgroundColor = Color.Transparent),
        shape = RoundedCornerShape(10.dp),
        elevation =  ButtonDefaults.elevation(
            defaultElevation = 10.dp,
            pressedElevation = 15.dp,
            disabledElevation = 0.dp
        ),
        contentPadding = PaddingValues(),
        modifier = Modifier
            .height(77.dp)
            .width(77.dp)
            .defaultMinSize(minWidth = 1.dp, minHeight = 1.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.size(7.dp))
            Icon(
                painter = painterResource(id = icon),
                contentDescription = text,
                modifier = Modifier.size(35.dp)
            )
            Spacer(modifier = Modifier.size(3.dp))
            Text(text, fontSize = 14.sp)
        }
    }

}