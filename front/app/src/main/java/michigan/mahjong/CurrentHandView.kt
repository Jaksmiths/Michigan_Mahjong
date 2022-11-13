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
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import michigan.mahjong.TileStore.cvresult
import michigan.mahjong.TileStore.discard
import michigan.mahjong.TileStore.recmove
import michigan.mahjong.TileStore.reset
import michigan.mahjong.TileStore.setup

@Composable
fun CurrentHandView(context: Context, navController: NavHostController) {

    var isLaunching by rememberSaveable { mutableStateOf(true) }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            if (uri != null) {
                Log.i("kilo", "Gallery")
                MainScope().launch {
                    cvresult(context, uri)
                }
            }
        }
    )

    LaunchedEffect(Unit) {
        if (isLaunching) {
            isLaunching = false
            setup()
        }
    }

    MainBackground()

    Crossfade(targetState = TileStore.isLoading.value) { loading ->
        when (loading) {
            true-> LoadingAnimation()
            false -> {

                MainButtons(navController, imagePicker)

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
                        }
                        Spacer(modifier = Modifier.size(12.dp))
                    }
                    Spacer(modifier = Modifier.size(30.dp))
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Bottom,
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
                }}
        }
    }
}

@Composable
fun DiscardIndicator() {
    Icon(
        painter = painterResource(id = R.drawable.indicator),
        contentDescription = "indicator icon",
        modifier = Modifier
            .size(30.dp)
            .rotate(90F),
        tint = Color.Yellow
    )
}

@Composable
fun DiscardInfo() {
    Box(
        modifier = Modifier
            .size(250.dp, 110.dp)
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
                    Text(if (discard.value == "") "Winning Hand!" else "Optimal Discard:", color = Color(0xFFFEFEFE), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(getIconName(discard.value ?: ""), color = Color(0xFF969B9D), fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.size(3.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        DiscardIndicator()
                        Text("Optimal Discard Indicator", color = Color(0xFF969B9D), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

        }
    }
}

@Composable
fun GuideButton(navController: NavHostController) {
    IconButton(onClick = { navController.navigate("Rulebook") },
        modifier = Modifier
            .padding(13.dp)
            .border(1.dp, Color(0xff9d8eff), shape = CircleShape)
            .then(Modifier.size(43.dp))
    ) {
        Icon(painterResource(R.drawable.book), contentDescription = "guidebook button", tint = Color.White)
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
fun TileButton(tileName: String, navController: NavHostController) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (tileName == discard.value) {
            //Text("Discard", fontSize = 5.sp)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                Text("Discard", fontSize = 10.sp, color = Color.White)
                DiscardIndicator()
            }

        }
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
fun MainButtons(navController: NavHostController, imagePicker: ManagedActivityResultLauncher<String, Uri?>) {
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
                TransparentOutLinedButton("Gallery", R.drawable.folder_alt, onClick = { imagePicker.launch("image/*") })
                TransparentOutLinedButton(
                    "Reset",
                    R.drawable.reset_alt,
                    navController,
                    "ResetView"
                )
            }
            else {
                DiscardInfo()
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