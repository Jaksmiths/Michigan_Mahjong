package michigan.mahjong

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import michigan.mahjong.TileStore.cvresult
import michigan.mahjong.TileStore.discard
import michigan.mahjong.TileStore.findType
import michigan.mahjong.TileStore.recmove
import michigan.mahjong.TileStore.reset
import michigan.mahjong.TileStore.setup
import michigan.mahjong.TileStore.text
import michigan.mahjong.TileStore.total_tiles

@Composable
fun CurrentHandView(context: Context, navController: NavHostController) {

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            if (uri != null) {
                MainScope().launch {
                    cvresult(context, uri, TileGroup.HAND)
                }
            }
        }
    )

    MainButtons(navController, imagePicker, TileGroup.HAND)

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
                    ResetButton(navController)
                    Spacer(modifier = Modifier.size(12.dp))
                }
                CalculateButton()
                Spacer(modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.size(15.dp))
        }
        Spacer(modifier = Modifier.size(30.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.fillMaxWidth()
        ) {
            for (i in tiles.indices) {
                TileButton(i, navController, TileGroup.HAND)
            }
        }
        Spacer(modifier = Modifier.size(10.dp))
    }
}

//@Composable
//fun DiscardIndicator() {
//    Icon(
//        painter = painterResource(id = R.drawable.indicator),
//        contentDescription = "indicator icon",
//        modifier = Modifier
//            .size(30.dp)
//            .rotate(90F),
//        tint = Color.Yellow
//    )
//}

@Composable
fun DiscardIndicator() {
    Button(
        onClick = { },
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Yellow, backgroundColor = Color.Transparent),
        shape = RoundedCornerShape(25.dp),
        elevation =  ButtonDefaults.elevation(
            defaultElevation = 10.dp,
            pressedElevation = 15.dp,
            disabledElevation = 0.dp
        ),
        contentPadding = PaddingValues(),
        modifier = Modifier
            .height(25.dp)
            .width(30.dp)
            .defaultMinSize(minWidth = 1.dp, minHeight = 1.dp)
    )
    {
        Icon(
            painter = painterResource(id = R.drawable.indicator),
            contentDescription = "indicator icon",
            modifier = Modifier
            .rotate(90F),
        )
    }
}

@Composable
fun DiscardInfo() {
    Box(
        modifier = Modifier
            .size(340.dp, 130.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF2D3135))
    ) {
        Column(verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier=Modifier.fillMaxSize()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(15.dp, Alignment.CenterHorizontally),
                modifier = Modifier.fillMaxSize()
            ) {
                Spacer(modifier = Modifier.size(0.dp))
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
                    Text(if (discard.value == "") "Winning Hand!" else "Optimal Discard: ${getIconName(discard.value ?: "")}", color = Color(0xFFFEFEFE), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.size(3.dp))
                    Text(text.value ?: "", color = Color(0xFF969B9D), fontSize = 15.sp, fontWeight = FontWeight.Bold)
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                    ) {
//                        DiscardIndicator()
//                        Text("Optimal Discard Indicator", color = Color(0xFF969B9D), fontSize = 11.sp, fontWeight = FontWeight.Bold)
//                    }
                }
            }

        }
    }
}


@Composable
fun ResetButton(navController: NavHostController) {

    Button(
        onClick = { navController.navigate("ResetView") },
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

@Composable
fun TileButton(tileIndex: Int, navController: NavHostController, tileGroup: TileGroup) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (tileGroup == TileGroup.HAND) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                if (discard.value != null) {
                    if (isPressed) {
                        TotalIndicator(tileIndex = tileIndex)
                        Spacer(modifier = Modifier.size(7.dp))
                    }
                    else if (tiles[tileIndex].name == discard.value){
                        //Text("Discard", fontSize = 10.sp, color = Color.White)
                        DiscardIndicator()
                    }
                    else {
                        Spacer(modifier = Modifier.size(30.dp))
                    }
                }
            }

        }
        Button(
            interactionSource = interactionSource,
            onClick = { 
                tileSelected = tileName
                navController.navigate("ManualTileCorrection") 
            },
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
                painter = painterResource(if (findType(tileGroup)[tileIndex].name == "") R.drawable.add else getIcon(findType(tileGroup)[tileIndex].name)),
                contentDescription = findType(tileGroup)[tileIndex].name)
        }
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
fun MainButtons(navController: NavHostController, imagePicker: ManagedActivityResultLauncher<String, Uri?>? = null, tileGroup: TileGroup) {
    Column(verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier=Modifier.height(250.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
            modifier = Modifier.fillMaxWidth()
        ) {

            if (tileGroup == TileGroup.HAND) {
                if (discard.value == null) {
                    TransparentOutLinedButton(
                        text = "Camera",
                        icon = R.drawable.camera_alt,
                        navController = navController,
                        destination = "CameraView/$tileGroup"
                    )
                    TransparentOutLinedButton("Gallery", R.drawable.folder_alt, onClick = { imagePicker?.launch("image/*") })
                    TransparentOutLinedButton(
                        "Reset",
                        R.drawable.reset_alt,
                        navController,
                        "ResetView/$tileGroup"
                    )
                }
                else {
                    DiscardInfo()
                }
            }
            else {
                TransparentOutLinedButton(
                    text = "Camera",
                    icon = R.drawable.camera_alt,
                    navController = navController,
                    destination = "DiscardInstructionView/${tileGroup}/${true}"
                )
                TransparentOutLinedButton(
                    text = "Gallery",
                    icon = R.drawable.folder_alt,
                    navController = navController,
                    destination = "DiscardInstructionView/${tileGroup}/${false}"
                )
                TransparentOutLinedButton(
                    text = "Reset",
                    icon = R.drawable.reset_alt,
                    navController = navController,
                    destination = "ResetView/${tileGroup}"
                )
            }
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
        border = BorderStroke(0.8.dp, Color(0xff9d8eff)),
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

@Composable
fun TotalIndicator(tileIndex: Int) {
    Button(
        onClick = { },
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White, backgroundColor = Color(0xFF00B7FF)),
        shape = RoundedCornerShape(25.dp),
        elevation =  ButtonDefaults.elevation(
            defaultElevation = 10.dp,
            pressedElevation = 15.dp,
            disabledElevation = 0.dp
        ),
        contentPadding = PaddingValues(),
        modifier = Modifier
            .height(15.dp)
            .width(40.dp)
            .defaultMinSize(minWidth = 1.dp, minHeight = 1.dp)
    )
    {
        if (tiles[tileIndex].name.length == 2) {
            val number : Int = tiles[tileIndex].name[0].digitToInt()
            val type : Char = tiles[tileIndex].name[1]
            Text("${total_tiles[type]?.get(number - 1).toString()} / 4", fontSize = 7.sp)
        }
    }
}