package michigan.mahjong

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.SemanticsActions.OnClick
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun MainMenuView(context: Context, navController: NavHostController){

    var isLaunching by rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        if (isLaunching) {
            isLaunching = false
        }
    }

    var isRefreshing by remember { mutableStateOf(false) }

    MainBackground()
    MenuButtons(navController)

    /*Column(verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier=Modifier.fillMaxHeight(1f)) {
        Row(){
            Text("AI Mahjong Assistant",
                fontWeight = FontWeight.Bold,
                style = TextStyle(fontSize = 44.sp),
                color = Color(0xFF8351AF)
            )
        }
        Spacer(modifier = Modifier.fillMaxHeight(.05f))
        Column(
            //horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth(1f),
            horizontalAlignment = Alignment.CenterHorizontally,

            ) {
            TransparentOutLinedButton({navController.navigate("CurrentHandView")}, "Get Started")
            Spacer(modifier = Modifier.fillMaxHeight(.02f))
            TransparentOutLinedButton({}, "Guidebook")
        }
    }
    */
}


@Composable
fun MenuButtons(navController: NavHostController) {
    /*
    Column (
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
            ) {
        TransparentOutLinedButton({}, "Get Started")
        TransparentOutLinedButton({}, "Guidebook")
    }
    */

    Column(verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier=Modifier.fillMaxHeight(1f)) {
        Row(){
            Text("AI Mahjong Assistant",
                fontWeight = FontWeight.Bold,
                style = TextStyle(fontSize = 44.sp),
                color = Color(0xFF8351AF)
                )
        }
        Spacer(modifier = Modifier.fillMaxHeight(.05f))
        Column(
            //horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth(1f),
            horizontalAlignment = Alignment.CenterHorizontally,

        ) {
            TransparentOutLinedButton({navController.navigate("CurrentHandView")}, "Get Started")
            Spacer(modifier = Modifier.fillMaxHeight(.02f))
            TransparentOutLinedButton({}, "Guidebook")
        }
    }

}


@Composable
fun TransparentOutLinedButton(onClick: () -> Unit = {}, text: String) {

    Button(
        onClick = { onClick },
        border = BorderStroke(0.8.dp, Color(0xff9d8eff), ),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xffe3e535), backgroundColor = Color.Transparent),
        shape = RoundedCornerShape(10.dp),
        elevation =  ButtonDefaults.elevation(
            defaultElevation = 10.dp,
            pressedElevation = 15.dp,
            disabledElevation = 0.dp
        ),
        contentPadding = PaddingValues(),
        modifier = Modifier
            .height(40.dp)
            .width(120.dp)
            .defaultMinSize(minWidth = 1.dp, minHeight = 1.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.size(3.dp))
            Text(text, fontSize = 14.sp)
        }
    }

}