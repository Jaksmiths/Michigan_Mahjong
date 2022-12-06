package michigan.mahjong

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavType
import androidx.navigation.compose.rememberNavController
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.accompanist.pager.ExperimentalPagerApi

class MainActivity : ComponentActivity() {

    private lateinit var fileOutputDirectory: File
    private lateinit var cameraExecutor: ExecutorService

    @OptIn(ExperimentalPagerApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            val navController = rememberNavController()
            NavHost(navController, startDestination = "MainMenuView") {
                composable("MainMenuView") {
                    MainMenuView(this@MainActivity, navController)
                }
                composable("Guidebook"){
                    Guidebook(this@MainActivity, navController)
                }
                composable(
                    route = "ResetView/{tileGroup}",
                    arguments = listOf(navArgument("tileGroup") {
                        type = NavType.StringType
                    })
                ){
                    ResetView(
                        this@MainActivity,
                        navController,
                        when(it.arguments?.getString("tileGroup")) {
                            HAND_KEY -> TileGroup.HAND
                            DISCARD_KEY -> TileGroup.DISCARD
                            OPEN_KEY -> TileGroup.OPEN
                            ALL_KEY -> null
                            else -> null
                        }
                    )
                }
                composable(
                    route = "CameraView/{tileGroup}",
                    arguments = listOf(navArgument("tileGroup") {
                        type = NavType.StringType
                    })
                ){
                    CameraView(
                        this@MainActivity,
                        navController,
                        outputDirectory = fileOutputDirectory,
                        executor = cameraExecutor,
                        when(it.arguments?.getString("tileGroup")) {
                            HAND_KEY -> TileGroup.HAND
                            DISCARD_KEY -> TileGroup.DISCARD
                            OPEN_KEY -> TileGroup.OPEN
                            else -> TileGroup.HAND
                        }
                    ) { Log.e("kilo", "View error:", it) }
                }
                composable(
                    route = "ManualTileCorrection/{tileIndex}/{tileGroup}",
                    arguments = listOf(
                        navArgument("tileIndex") {
                            type = NavType.IntType
                        },
                        navArgument("tileGroup") {
                            type = NavType.StringType
                        }
                    )
                ){
                    ManualTileCorrection(
                        this@MainActivity,
                        navController,
                        it.arguments?.getInt("tileIndex") ?: 0,
                        when(it.arguments?.getString("tileGroup")) {
                            HAND_KEY -> TileGroup.HAND
                            DISCARD_KEY -> TileGroup.DISCARD
                            OPEN_KEY -> TileGroup.OPEN
                            else -> TileGroup.HAND
                        }
                    )
                }
                composable("TileMenuTabs"){
                    TileMenuTabs(this@MainActivity, navController)
                }
                composable(
                    route = "DiscardInstructionView/{tileGroup}/{usingCamera}",
                    arguments = listOf(
                        navArgument("tileGroup") {
                            type = NavType.StringType
                        },
                        navArgument("usingCamera") {
                            type = NavType.BoolType
                        }
                    )
                ){
                    DiscardInstructionView(
                        this@MainActivity,
                        navController,
                        when(it.arguments?.getString("tileGroup")) {
                            HAND_KEY -> TileGroup.HAND
                            DISCARD_KEY -> TileGroup.DISCARD
                            OPEN_KEY -> TileGroup.OPEN
                            else -> TileGroup.HAND
                        },
                        it.arguments?.getBoolean("usingCamera") ?: true
                    )
                }
            }
        }

        requestCameraPermission()

        fileOutputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun requestCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.i("kilo", "Permission previously granted")
            }

            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.i("kilo", "Permission previously granted")
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.CAMERA
            ) -> Log.i("kilo", "Show camera permissions dialog")

            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) -> Log.i("kilo", "Show external storage permissions dialog")

            else -> requestPermissionLauncher.launch(arrayOf(Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE))
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
        results.forEach {
            if (!it.value) {
                toast("Please allow ${it.key} access to utilize app")
                finish()
            }
        }
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }

        return if (mediaDir != null && mediaDir.exists()) mediaDir else filesDir
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}
