package michigan.mahjong

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.runtime.Composable
import michigan.mahjong.TileStore.discard_pile
import michigan.mahjong.TileStore.open_tiles

@Composable
fun DiscardView(context: Context, navController: NavHostController) {

    MainButtons(
        navController = navController,
        tileGroup = TileGroup.DISCARD
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
            TileGrid(navController, DISCARD_KEY)
        }
        Spacer(modifier = Modifier.size(10.dp))
    }



}

@Composable
fun TileGrid(
    navController: NavHostController,
    tileGroup: String
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 50.dp),
        modifier = Modifier.height(100.dp).fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
        horizontalArrangement = Arrangement.Center
    ) {
        if (tileGroup == DISCARD_KEY) {
            items(count = discard_pile.size, key = { index -> index }) { index ->
                TileButton(index, navController, TileGroup.DISCARD)
            }
        } else {
            items(count = open_tiles.size, key = { index -> index }) { index ->
                TileButton(index, navController, TileGroup.OPEN)
            }
        }
    }
}