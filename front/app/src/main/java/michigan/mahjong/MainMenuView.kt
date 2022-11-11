package michigan.mahjong

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
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
                color = Color(0xFFb8b0eb)
                )
        }
        Spacer(modifier = Modifier.fillMaxHeight(.05f))
        Column(
            //horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth(1f),
            horizontalAlignment = Alignment.CenterHorizontally,

        ) {
            Spacer(modifier = Modifier.fillMaxHeight(.2f))
            TransparentOutLinedButton(navController, "CurrentHandView","Get Started")
            Spacer(modifier = Modifier.fillMaxHeight(.05f))
            //TransparentOutLinedButton(navController, "Guidebook", "Guidebook")
            TransparentOutLinedButton(navController, "Rulebook", "Guidebook")
        }
    }

}

@Composable
fun TransparentOutLinedButton(navController: NavHostController?, path: String, text: String) {

    Button(
        onClick = { navController?.navigate(path) },
        border = BorderStroke(0.8.dp, Color(0xff9d8eff), ),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xff9d8eff), backgroundColor = Color.Transparent),
        shape = RoundedCornerShape(20.dp),
        elevation =  ButtonDefaults.elevation(
            defaultElevation = 10.dp,
            pressedElevation = 15.dp,
            disabledElevation = 0.dp
        ),
        contentPadding = PaddingValues(),
        modifier = Modifier
            .height(30.dp)
            .width(150.dp)
            .defaultMinSize(minWidth = 1.dp, minHeight = 1.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if(text == "Guidebook"){
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.menu_book),
                        "",
                        tint = Color(0xff9d8eff)
                    )
                    Spacer(modifier = Modifier.fillMaxWidth(.05f))
                    Text(text, fontSize = 15.sp)
                }
            }
            else {
                Text(text, fontSize = 15.sp)
            }
        }
    }

}