package michigan.mahjong

import android.content.Context
import android.graphics.Color.parseColor
import android.service.autofill.OnClickAction
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.icons.filled.Phone
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import michigan.mahjong.TileStore.tiles
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import michigan.mahjong.TileStore.reset

@Composable
fun CurrentHandView(context: Context, navController: NavHostController) {
    var isLaunching by rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        if (isLaunching) {
            isLaunching = false
            reset()
        }
    }

    var isRefreshing by remember { mutableStateOf(false) }

    MainBackground()
    MainButtons()

    Column(
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.fillMaxHeight()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            for (tile in tiles) {
                TileButton(tile, navController)
            }
        }
        Spacer(modifier = Modifier.size(10.dp))
    }

}

//navController.navigate("ManualTileView?startTile=$tile.name")
@Composable
fun TileButton(tile: Tile, navController: NavHostController) {
    Button(onClick = { },
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xffe3e535)),
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
        Icon(
            painter = painterResource(R.drawable.add),
            contentDescription = tile.name
        )
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
fun MainButtons() {
    Column(verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier=Modifier.fillMaxHeight(1f)) {
        Spacer(modifier = Modifier.fillMaxHeight(.05f))
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
            modifier = Modifier.fillMaxWidth(1f)
        ) {
            TransparentOutLinedButton({}, "Camera", R.drawable.camera_alt)
            TransparentOutLinedButton({}, "Gallery", R.drawable.folder_alt)
            TransparentOutLinedButton({}, "Reset", R.drawable.reset_alt)
        }
    }
}

@Composable
fun TransparentOutLinedButton(onClick: () -> Unit = {}, text: String, icon: Int) {

    Button(
        onClick = { onClick },
        border = BorderStroke(0.8.dp, Color(0xff9d8eff), ),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xffe3e535), backgroundColor = Color.Transparent),
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