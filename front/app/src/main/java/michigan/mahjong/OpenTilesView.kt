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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import michigan.mahjong.TileStore.discard_pile
import michigan.mahjong.TileStore.open_tiles
import michigan.mahjong.TileStore.recmove
import michigan.mahjong.TileStore.reset
import michigan.mahjong.TileStore.setup

@Composable
fun OpenTilesView(context: Context, navController: NavHostController) {

    MainButtons(
        navController = navController,
        tileGroup = TileGroup.OPEN
    )

    Column(
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.fillMaxHeight()
    ) {
        Spacer(modifier = Modifier.size(30.dp))
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.fillMaxWidth()
        ) {
            TileGrid(navController, OPEN_KEY)
        }
        Spacer(modifier = Modifier.size(10.dp))
    }
}