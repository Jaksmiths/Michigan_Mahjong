package michigan.mahjong

import android.content.Context
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState

@OptIn(ExperimentalUnitApi::class)
@ExperimentalPagerApi
@Composable
fun Guidebook(context: Context, navController: NavHostController) {

    var isLaunching by rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        if (isLaunching) {
            isLaunching = false
        }
    }

    var isRefreshing by remember { mutableStateOf(false) }

    val pagerState = rememberPagerState(0)

    //MainBackground()
    //CustomTabs(context, navController)
    //Rulebook(context, navController)
    Column() {
        Row(
            modifier = Modifier.fillMaxWidth(1f),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            GuideTabs(pagerState = pagerState)
            ExitButton(navController)
        }
        GuideTabsContent(pagerState = pagerState)
    }

}

@Composable
fun Rulebook() {
    //context: Context, navController: NavHostController
    //, rules: List<String>

    var isLaunching by rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        if (isLaunching) {
            isLaunching = false
        }
    }

    var isRefreshing by remember { mutableStateOf(false) }

    val scrollStateVertical = rememberScrollState()

    //background from CurrentHandView.kt
    MainBackground()
    //CustomTabs(context, navController)

    Column(
        Modifier.fillMaxSize()
    ) {
        Row(
            //horizontalArrangement = Arrangement.End,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            //CustomTabs(context, navController)
            //ExitButton(navController)
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = scrollStateVertical)
                .fillMaxWidth(1f),
            //verticalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            /*Sources include Mahjong Soul, https://riichi.wiki/, and
            https://mjg-repo.neocities.org/pastebin/mahjong-terms.html*/

            Spacer(modifier = Modifier.height(10.dp))
            Rule("Introduction", stringResource(R.string.rule_intro), null)
            Rule("Tiles", stringResource(R.string.rule_tiles),R.drawable.all_tiles_display)
            Rule("Basic Mechanics", stringResource(R.string.rule_basic), R.drawable.tile_hand)
            Rule("Winning", stringResource(R.string.rule_win), R.drawable.tile_combo)
            Rule("Basic Yaku - Tanyao (All Simples)", stringResource(R.string.rule_tanyao),
                R.drawable.tiles_tanyao)
            Rule("Basic Yaku - Yakuhai", stringResource(R.string.rule_yakuhai),
                R.drawable.tiles_yakuhai)
            Rule("Basic Yaku - Riichi", stringResource(R.string.rule_riichi), null)
            Rule("Advanced Mechanics", stringResource(R.string.rule_advanced), null)
            Rule("Open Tiles - Chii, Pon, Kan", stringResource(R.string.rule_open),
                R.drawable.tiles_open)
            Rule("Furiten", stringResource(R.string.rule_furiten), null)
            Rule("Draw", stringResource(R.string.rule_draw), null)
            Rule("Yaku Overview", stringResource(R.string.rule_yaku_overview), null)
            Rule("1 Han", stringResource(R.string.rule_1han), null)
            Rule("2 Han", stringResource(R.string.rule_2han), null)
            Rule("3 Han", stringResource(R.string.rule_3han), null)
            Rule("6 Han", stringResource(R.string.rule_6han), null)
            Rule("Mangan", stringResource(R.string.rule_mangan), null)
            Rule("Yakuman", stringResource(R.string.rule_yakuman), null)
            Rule("Dora", stringResource(R.string.rule_dora), null);
        }
    }

}

@Composable
fun StrategyGuide() {
    //context: Context, navController: NavHostController
    var isLaunching by rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        if (isLaunching) {
            isLaunching = false
        }
    }

    val scrollStateVertical = rememberScrollState()
    var isRefreshing by remember { mutableStateOf(false) }

    MainBackground()

    //remainingIndicator(3, 4)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(state = scrollStateVertical)
            .fillMaxWidth(1f),
        //verticalArrangement = Arrangement.Center,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
        Spacer(modifier = Modifier.height(10.dp))
        Rule("Introduction", "", null)
        Rule("Steal to complete a Yaku", stringResource(R.string.strat_yaku), null)
    }


}

@Composable
fun Credits(){
    var isLaunching by rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        if (isLaunching) {
            isLaunching = false
        }
    }

    val scrollStateVertical = rememberScrollState()
    var isRefreshing by remember { mutableStateOf(false) }

    MainBackground()

    //remainingIndicator(3, 4)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(state = scrollStateVertical)
            .fillMaxWidth(1f),
        //verticalArrangement = Arrangement.Center,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
        Spacer(modifier = Modifier.height(10.dp))
        Rule("Credits", "", null)
    }

}

@Composable
fun Rule(title: String, text: String, image: Int?) {
    //Spacer(modifier = Modifier.fillMaxHeight(1f))
    Card(
        modifier = Modifier
            .width(700.dp),
            //.clip(RoundedCornerShape(20.dp))
            //.align(Alignment.CenterHorizontally)
        //shape = RoundedCornerShape(20.dp)
        border = BorderStroke(2.dp,Color(0xff9d8eff)),
        backgroundColor = Color.Transparent
    ) {
        if (image != null){
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    modifier = Modifier
                        .padding(4.dp),
                    text = title,
                    color = Color(0xff9d8eff),
                    //fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
                Text(
                    modifier = Modifier
                        .padding(8.dp),
                    text = text,
                    color = Color(0xff9d8eff)
                    //fontWeight = FontWeight.Bold
                )
                Image(
                    painter = painterResource(id = image),
                    contentDescription = null,
                    modifier = Modifier.clip(RoundedCornerShape(3.dp))
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
        else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    modifier = Modifier
                        .padding(4.dp),
                    text = title,
                    color = Color(0xff9d8eff),
                    //fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
                Text(
                    modifier = Modifier
                        .padding(8.dp),
                    text = text,
                    color = Color(0xff9d8eff)
                    //fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ExitButton(navController: NavHostController) {
    IconButton(
        modifier = Modifier.size(48.dp),
        onClick = { navController.popBackStack() }
    ) {
        Icon(Icons.Filled.Close, "", tint = Color.White)
        //all icons list https://fonts.google.com/icons
    }
}