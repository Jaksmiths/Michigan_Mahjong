package michigan.mahjong

import android.content.Context
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.google.accompanist.pager.*

@OptIn(ExperimentalPagerApi::class)
@Composable
fun TileMenuTabs(context: Context, navController: NavHostController) {

    var isLaunching by rememberSaveable { mutableStateOf(true) }
    val pagerState = rememberPagerState(0)

    LaunchedEffect(Unit) {
        if (isLaunching) {
            isLaunching = false
            TileStore.setup()
        }
    }

    MainBackground()

    Crossfade(targetState = TileStore.isLoading.value) { loading ->
        when (loading) {
            true-> LoadingAnimation()
            false -> TabLayout(context, navController, pagerState)
        }
    }
}

// on below line we are creating a
// composable function for our tab layout
@OptIn(ExperimentalUnitApi::class)
@ExperimentalPagerApi
@Composable
fun TabLayout(context: Context, navController: NavHostController, pagerState: PagerState) {

    Column(
        modifier = Modifier.background(Color.Transparent)
    ) {
        Tabs(navController, pagerState = pagerState)
        TabsContent(context, navController, pagerState)
    }
}

// on below line we are
// creating a function for tabs
@ExperimentalPagerApi
@Composable
fun Tabs(navController: NavHostController, pagerState: PagerState) {
    // in this function we are creating a list
    // in this list we are specifying data as
    // name of the tab and the icon for it.
    val list = listOf(
        "Hand" to Icons.Default.Home,
        "Discard" to Icons.Default.ShoppingCart,
        "Open Tiles" to Icons.Default.Settings
    )
    // on below line we are creating
    // a variable for the scope.
    val scope = rememberCoroutineScope()
    // on below line we are creating a
    // individual row for our tab layout.


    Row() {
        ScrollableTabRow(
            // on below line we are specifying
            // the selected index.
            selectedTabIndex = pagerState.currentPage,
            edgePadding = 0.dp,
            modifier = Modifier.width(719.dp),

            // on below line we are
            // specifying background color.
            backgroundColor = Color.Transparent,

            // on below line we are specifying content color.

            // on below line we are specifying
            // the indicator for the tab
            indicator = { tabPositions ->
                // on below line we are specifying the styling
                // for tab indicator by specifying height
                // and color for the tab indicator.
                TabRowDefaults.Indicator(
                    Modifier.pagerTabIndicatorOffset(pagerState, tabPositions),
                    height = 2.dp,
                    color = Color(0xff9d8eff)
                )
            }
        ) {
            // on below line we are specifying icon
            // and text for the individual tab item
            list.forEachIndexed { index, _ ->
                // on below line we are creating a tab.
                Tab(
                    // on below line we are specifying icon
                    // for each tab item and we are calling
                    // image from the list which we have created.
//                icon = {
//                    Icon(imageVector = list[index].second, contentDescription = null)
//                },
                    // on below line we are specifying the text for
                    // the each tab item and we are calling data
                    // from the list which we have created.
                    text = {
                        Text(
                            list[index].first,
                            // on below line we are specifying the text color
                            // for the text in that tab
                            color = if (pagerState.currentPage == index) Color(0xff9d8eff) else Color.LightGray
                        )
                    },
                    // on below line we are specifying
                    // the tab which is selected.
                    selected = pagerState.currentPage == index,
                    // on below line we are specifying the
                    // on click for the tab which is selected.
                    onClick = {
                        // on below line we are specifying the scope.
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    }
                )
            }

        }
        GuideButton(navController)
    }




}

// on below line we are creating a tab content method
// in which we will be displaying the individual page of our tab .
@ExperimentalPagerApi
@Composable
fun TabsContent(
    context: Context,
    navController: NavHostController,
    pagerState: PagerState,
) {
    // on below line we are creating
    // horizontal pager for our tab layout.
    HorizontalPager(count = 3, state = pagerState) { page ->
        when (page) {
            // on below line we are calling tab content screen
            // and specifying data as Home Screen.
            0 -> CurrentHandView(context, navController)
            // on below line we are calling tab content screen
            // and specifying data as Shopping Screen.
            1 -> DiscardView(context, navController)
            // on below line we are calling tab content screen
            // and specifying data as Settings Screen.
            2 -> OpenTilesView(context, navController)
        }
    }
}

@Composable
fun GuideButton(navController: NavHostController) {
    IconButton(onClick = { navController.navigate("Rulebook") },
        modifier = Modifier
            .padding(6.dp)
            .border(1.dp, Color(0xff9d8eff), shape = CircleShape)
            .then(Modifier.size(43.dp))
    ) {
        Icon(painterResource(R.drawable.book), contentDescription = "guidebook button", tint = Color.White)
    }
}