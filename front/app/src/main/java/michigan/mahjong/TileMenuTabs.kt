package michigan.mahjong

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.*
import com.google.accompanist.pager.*

@OptIn(ExperimentalPagerApi::class)
@Composable
fun TileMenuTabs(context: Context, navController: NavHostController) {

    var isLaunching by rememberSaveable { mutableStateOf(true) }
    val pagerState = rememberPagerState(0)

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            if (uri != null) {
                MainScope().launch {
                    TileStore.cvresult(context, uri, TileGroup.HAND)
                }
            }
        }
    )

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
            false -> TabLayout(context, navController, pagerState, imagePicker)
        }
    }
}

@OptIn(ExperimentalUnitApi::class)
@ExperimentalPagerApi
@Composable
fun TabLayout(context: Context, navController: NavHostController, pagerState: PagerState, imagePicker: ManagedActivityResultLauncher<String, Uri?>) {

    Column() {
        Row(
            modifier = Modifier.fillMaxWidth(1f).background(Color.Transparent),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Tabs(navController, pagerState = pagerState)
            GuideButton(navController)
        }
        TabsContent(context, navController, pagerState, imagePicker)
    }
}

@ExperimentalPagerApi
@Composable
fun Tabs(navController: NavHostController, pagerState: PagerState) {

    val list = listOf(
        "Hand" to Icons.Default.Home,
        "Discard" to Icons.Default.ShoppingCart,
        "Open Tiles" to Icons.Default.Settings
    )

    val scope = rememberCoroutineScope()

    TabRow(

        selectedTabIndex = pagerState.currentPage,
        modifier = Modifier
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .padding(1.dp)
            .width(480.dp),

        backgroundColor = Color.Transparent,

        indicator = { tabPositions ->

            TabRowDefaults.Indicator(
                Modifier.pagerTabIndicatorOffset(pagerState, tabPositions),
                height = 2.dp,
                color = Color(0xff9d8eff)
            )
        }
    ) {

        list.forEachIndexed { index, _ ->
            Tab(
                text = {
                    Text(
                        list[index].first,
                        color = if (pagerState.currentPage == index) Color(0xff9d8eff) else Color.LightGray
                    )
                },
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
fun TabsContent(
    context: Context,
    navController: NavHostController,
    pagerState: PagerState,
    imagePicker: ManagedActivityResultLauncher<String, Uri?>
) {
    HorizontalPager(count = 3, state = pagerState) { page ->
        when (page) {
            0 -> CurrentHandView(context, navController, imagePicker)
            1 -> DiscardView(context, navController)
            2 -> OpenTilesView(context, navController)
        }
    }
}

@Composable
fun GuideButton(navController: NavHostController) {
    IconButton(onClick = { navController.navigate("Guidebook") },
        modifier = Modifier
            .padding(6.dp)
            .border(1.dp, Color(0xff9d8eff), shape = CircleShape)
            .then(Modifier.size(43.dp))
    ) {
        Icon(painterResource(R.drawable.book), contentDescription = "guidebook button", tint = Color.White)
    }
}