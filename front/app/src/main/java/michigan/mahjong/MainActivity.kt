package michigan.mahjong

import android.Manifest
import android.app.Application
import android.media.Image
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            NavHost(navController, startDestination = "Guidebook") {
                composable("MainMenuView") {
                    MainMenuView(this@MainActivity, navController)
                }
                composable("CurrentHandView") {
                    CurrentHandView(this@MainActivity, navController)
                }
                composable("Guidebook"){
                    Guidebook(this@MainActivity, navController)
                }
            }
        }
        
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
        { granted ->
            granted.forEach{
                if (!it.value) {
                    toast("${it.key} Access denied")
                    finish()
                }
            }
        }.launch(arrayOf(Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE))
    }
}
