package michigan.mahjong

import android.content.Context
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.runtime.Composable
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import michigan.mahjong.TileStore.addPartUri
import michigan.mahjong.TileStore.clearPartImgs
import michigan.mahjong.TileStore.multiple_cvresult

@Composable
fun DiscardInstructionView(context: Context, navController: NavHostController, tileGroup: TileGroup, usingCamera: Boolean = true) {

    Log.i("kilo", usingCamera.toString())
    val multiImagePicker =
        rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uriList ->
            for(uri in uriList) {
                addPartUri(uri)
            }
            MainScope().launch {
                multiple_cvresult(context, tileGroup)
                navController.popBackStack("TileMenuTabs", inclusive = false)
            }
        }

    MainBackground()

    Crossfade(targetState = TileStore.isLoading.value) { loading ->
        when (loading) {
            true-> LoadingAnimation()
            false -> {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier=Modifier.fillMaxSize()
                ) {
                    Text("Please pick/take 4 photos from your seat (Hold to select in Gallery)", color = Color(0xFF969B9D), fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Text("Each photo should not be crooked", color = Color(0xFF969B9D), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("The order should be:  You -> Right Player -> Opposite Player -> Left Player", color = Color(0xFF969B9D), fontSize = 12.sp, fontWeight = FontWeight.Normal)
                    Spacer(modifier = Modifier.size(10.dp))
                    Image(
                        painter = painterResource(R.drawable.instruction),
                        contentDescription = "instructions"
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                    MultipleSelectConfirmButton(navController, { multiImagePicker.launch("image/*") }, tileGroup, usingCamera)
                }
            }
        }
    }

}

@Composable
fun MultipleSelectConfirmButton(
    navController: NavHostController,
    onClick: () -> Unit = {},
    tileGroup: TileGroup,
    usingCamera: Boolean
) {

    Button(
        onClick = {
            clearPartImgs()
            if (usingCamera) {
                navController.navigate("CameraView/$tileGroup")
            }
            else {
                onClick()
            }
        },
        border = BorderStroke(1.dp, Color(0xff9d8eff), ),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White, backgroundColor = Color(0xff9d8eff)),
        shape = RoundedCornerShape(20.dp),
        elevation =  ButtonDefaults.elevation(
            defaultElevation = 10.dp,
            pressedElevation = 15.dp,
            disabledElevation = 0.dp
        ),
        contentPadding = PaddingValues(),
        modifier = Modifier
            .height(25.dp)
            .width(100.dp)
            .defaultMinSize(minWidth = 1.dp, minHeight = 1.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text("Confirm", fontSize = 12.sp, modifier = Modifier.align(Alignment.CenterVertically))
        }
    }
}