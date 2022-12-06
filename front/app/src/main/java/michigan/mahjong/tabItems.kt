package michigan.mahjong

import android.content.Context
import android.media.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.navigation.NavHostController
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch

@Composable
fun remainingIndicator(remain: Int, total: Int){
    Box(
        modifier = Modifier.width(40.dp).height(15.dp)
        .clip(RoundedCornerShape(20.dp)).background(Color.Blue),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = remain.toString() + " / " + total.toString(),
            color = Color(0xff9d8eff),
            fontSize = 12.sp,
        )
    }
}

/*
@OptIn(ExperimentalUnitApi::class)
@ExperimentalPagerApi
@Composable
fun TabLayout() {

    // on below line we are creating variable for pager state.
    val pagerState = rememberPagerState(0)

    // on below line we are creating a column for our widgets.
    Column(
        // for column we are specifying modifier on below line.
        modifier = Modifier.background(Color.Transparent)
    ) {
        // on below line we are calling tabs
        GuideTabs(pagerState = pagerState)
        // on below line we are calling tabs content
        // for displaying our page for each tab layout
        GuideTabsContent(pagerState = pagerState)
    }
}
*/



@ExperimentalPagerApi
@Composable
fun GuideTabs(pagerState: PagerState) {
    //context: Context, navController: NavHostController
    //var selectedIndex by rememberSaveable { mutableStateOf(0) }

    val list = listOf("Rulebook", "Strategy Guide")

    val scope = rememberCoroutineScope()

    TabRow(
        //selectedTabIndex = selectedIndex,
        selectedTabIndex = pagerState.currentPage,
        backgroundColor = Color.Transparent,
        modifier = Modifier
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .padding(1.dp)
            .width(360.dp),
        indicator = { tabPositions: List<TabPosition> ->
            //Box {}
            TabRowDefaults.Indicator(
                Modifier.pagerTabIndicatorOffset(pagerState, tabPositions),
                height = 2.dp,
                color = Color.White
            )
        }

    ) {
        list.forEachIndexed { index, text ->
            Tab(
                text = { Text(text = text, color = Color(0xff9d8eff)) },
                selected = pagerState.currentPage == index,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            )
        }
        /*list.forEachIndexed { index, text ->
            //val selected = selectedIndex == index
            Tab(
                modifier = if (selected) Modifier
                    .drawBehind {
                        val strokeWidth = Stroke.DefaultMiter
                        val y = size.height - strokeWidth / 2

                        drawLine(
                            Color(0xff9d8eff),
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

            )
        }
        */
    }
}

@ExperimentalPagerApi
@Composable
fun GuideTabsContent(pagerState: PagerState) {
    // on below line we are creating
    // horizontal pager for our tab layout.
    HorizontalPager(state = pagerState, count = 2) {
        // on below line we are specifying
        // the different pages.
            page ->
        when (page) {
            // on below line we are calling tab content screen
            // and specifying data as Home Screen.
            0 -> Rulebook()
            // on below line we are calling tab content screen
            // and specifying data as Shopping Screen.
            1 -> StrategyGuide()
            // on below line we are calling tab content screen
            // and specifying data as Settings Screen.
        }
    }
}
