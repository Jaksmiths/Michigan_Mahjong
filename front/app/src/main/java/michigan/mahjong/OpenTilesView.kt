package michigan.mahjong

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.runtime.Composable

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