package michigan.mahjong

import android.content.ClipData
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController


@Composable
fun Guidebook(context: Context, navController: NavHostController) {

    var isLaunching by rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        if (isLaunching) {
            isLaunching = false
        }
    }

    var isRefreshing by remember { mutableStateOf(false) }

    MainBackground()
    CustomTabs(context, navController)
    //Rulebook(context, navController)

}

@Composable
fun Rulebook(context: Context, navController: NavHostController) {
    //, rules: List<String>

    var isLaunching by rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        if (isLaunching) {
            isLaunching = false
        }
    }

    var isRefreshing by remember { mutableStateOf(false) }

    val scrollStateVertical = rememberScrollState()

    MainBackground()

    //temp
    Column(
        //horizontalAlignment = Alignment.End,
        //modifier=Modifier.fillMaxHeight(1f)
    ) {
        Row() {
            ExitButton(navController)
            Text(
                text = "RuleBook",
                color = Color.White
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = scrollStateVertical)
        ){
            repeat(25){ c ->
                Rule("Rule #$c")
            }
            Rule("Last Rule")
        }

    }
}

@Composable
fun StrategyGuide(context: Context, navController: NavHostController) {

    var isLaunching by rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        if (isLaunching) {
            isLaunching = false
        }
    }

    var isRefreshing by remember { mutableStateOf(false) }

    MainBackground()

    //temp
    Column() {
        Text(text = "Strategy")
    }
}


@Composable
fun Rule(text: String) {
    Text(
        modifier = Modifier
            .padding(5.dp),
        text = text,
        color = Color.White
    )
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