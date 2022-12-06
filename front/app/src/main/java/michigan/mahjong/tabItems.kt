package michigan.mahjong

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.*
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch

@ExperimentalPagerApi
@Composable
fun GuideTabs(pagerState: PagerState) {

    val list = listOf("Rulebook", "Strategy Guide", "Credits")

    val scope = rememberCoroutineScope()

    TabRow(
        //selectedTabIndex = selectedIndex,
        selectedTabIndex = pagerState.currentPage,
        backgroundColor = Color.Transparent,
        modifier = Modifier
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .padding(1.dp)
            .width(480.dp),
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
    }
}

@ExperimentalPagerApi
@Composable
fun GuideTabsContent(pagerState: PagerState) {
    HorizontalPager(state = pagerState, count = 3) {
            page ->
        when (page) {
            0 -> Rulebook()
            1 -> StrategyGuide()
            2 -> Credits()
        }
    }
}
