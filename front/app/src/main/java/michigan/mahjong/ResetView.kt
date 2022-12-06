package michigan.mahjong

import android.content.Context
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign
import michigan.mahjong.TileStore.reset
import michigan.mahjong.TileStore.reset_all

@Composable
fun ResetView(context: Context, navController: NavHostController, tileGroup: TileGroup? = null) {

    MainBackground()

    Column(verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier=Modifier.fillMaxHeight()) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            ResetConfirmationButton()
        }
        Spacer(modifier = Modifier.size(50.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(30.dp, Alignment.CenterHorizontally),
            modifier = Modifier.fillMaxWidth()
        ) {
            ConfirmButton("No", navController)
            ConfirmButton("Yes", navController) { if (tileGroup != null) reset(tileGroup) else reset_all() }
        }
    }
}

@Composable
fun ResetConfirmationButton() {

    Button(
        onClick = { },
        border = BorderStroke(1.dp, Color(0xff9d8eff), ),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White, backgroundColor = Color.Transparent),
        shape = RoundedCornerShape(10.dp),
        elevation =  ButtonDefaults.elevation(
            defaultElevation = 10.dp,
            pressedElevation = 15.dp,
            disabledElevation = 0.dp
        ),
        contentPadding = PaddingValues(10.dp),
        modifier = Modifier
            .height(75.dp)
            .width(180.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text("Are you sure you want to reset?", fontSize = 14.sp, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun ConfirmButton(
    text: String,
    navController: NavHostController,
    onClick: () -> Unit = {}
) {

    Button(
        onClick = { onClick(); navController.popBackStack("TileMenuTabs", inclusive = false) },
        border = BorderStroke(1.dp, Color(0xff9d8eff), ),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White, backgroundColor = Color.Transparent),
        shape = RoundedCornerShape(10.dp),
        elevation =  ButtonDefaults.elevation(
            defaultElevation = 10.dp,
            pressedElevation = 15.dp,
            disabledElevation = 0.dp
        ),
        modifier = Modifier
            .height(50.dp)
            .width(140.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text, fontSize = 14.sp, modifier = Modifier.align(Alignment.CenterVertically))
        }
    }
}