package michigan.mahjong

import android.content.Context
import android.media.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController


@Composable
fun CustomTabs(context: Context, navController: NavHostController) {
    var selectedIndex by rememberSaveable { mutableStateOf(0) }

    val list = listOf("Rulebook", "Strategy Guide")

    TabRow(selectedTabIndex = selectedIndex,
        backgroundColor = Color.Transparent,
        modifier = Modifier
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .padding(1.dp),
        indicator = { tabPositions: List<TabPosition> ->
            Box {}
        }
    ) {
        list.forEachIndexed { index, text ->
            val selected = selectedIndex == index
            Tab(
                modifier = if (selected) Modifier
                    .drawBehind {
                        val strokeWidth = Stroke.DefaultMiter
                        val y = size.height - strokeWidth / 2

                        drawLine(
                            Color.LightGray,
                            Offset(0f, y),
                            Offset(size.width, y),
                            strokeWidth
                        )
                    }
                else Modifier,
                selected = selected,
                onClick = { selectedIndex = index
                            //navController?.navigate(path)
                          },
                text = { Text(text = text, color = Color(0xff9d8eff)) }
            )
        }
    }
}