package michigan.mahjong

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Lens
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import michigan.mahjong.TileStore.addPartUri
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import michigan.mahjong.TileStore.cvresult
import michigan.mahjong.TileStore.isLoading
import michigan.mahjong.TileStore.multiple_cvresult
import michigan.mahjong.TileStore.part_imgs

@Composable
fun CameraView(
    globalContext: Context,
    navController: NavHostController,
    outputDirectory: File,
    executor: Executor,
    tileGroup: TileGroup,
    onError: (ImageCaptureException) -> Unit
) {

    var progress by remember { mutableStateOf(0.75f) }
    val animatedProgress = animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
    ).value

    // 1
    val lensFacing = CameraSelector.LENS_FACING_BACK
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val preview = Preview.Builder().build()
    val previewView = remember { PreviewView(context) }
    val imageCapture: ImageCapture = remember { ImageCapture.Builder().build() }
    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(lensFacing)
        .build()

    // 2
    LaunchedEffect(lensFacing) {
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            imageCapture
        )

        preview.setSurfaceProvider(previewView.surfaceProvider)
    }

    MainBackground()

    progress = part_imgs.size / 4f
    if (isLoading.value) {
        LoadingAnimation()
    }
    else {
        CameraBox(
            globalContext,
            previewView,
            imageCapture,
            navController,
            outputDirectory,
            executor,
            tileGroup,
            onError
        )
        Column(
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier.fillMaxHeight()
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.size(15.dp))
                Column() {
                    CircularProgressIndicator(progress = animatedProgress)
                    Spacer(modifier = Modifier.size(15.dp))
                }

            }
        }

    }
}

@Composable
fun CameraBox(
    globalContext: Context,
    previewView: PreviewView,
    imageCapture: ImageCapture,
    navController: NavHostController,
    outputDirectory: File,
    executor: Executor,
    tileGroup: TileGroup,
    onError: (ImageCaptureException) -> Unit
) {
    // 3
    Box(contentAlignment = Alignment.BottomCenter, modifier = Modifier.fillMaxSize()) {
        AndroidView({ previewView }, modifier = Modifier.fillMaxSize())

        IconButton(
            modifier = Modifier
                .padding(bottom = 20.dp, end = 20.dp)
                .align(Alignment.CenterEnd),
            onClick = {
                Log.i("kilo", "ON CLICK")
                takePhoto(
                    globalContext,
                    navController,
                    filenameFormat = "yyyy-MM-dd-HH-mm-ss-SSS",
                    imageCapture = imageCapture,
                    outputDirectory = outputDirectory,
                    executor = executor,
                    tileGroup = tileGroup,
                    onError = onError
                )
            },
            content = {
                Icon(
                    imageVector = Icons.Sharp.Lens,
                    contentDescription = "Take picture",
                    tint = Color.White,
                    modifier = Modifier
                        .size(50.dp)
                        .padding(1.dp)
                        .border(1.dp, Color.White, CircleShape)
                )
            }
        )

    }
}

private fun takePhoto(
    globalContext: Context,
    navController: NavHostController,
    filenameFormat: String,
    imageCapture: ImageCapture,
    outputDirectory: File,
    executor: Executor,
    tileGroup: TileGroup,
    onError: (ImageCaptureException) -> Unit
) {

    val photoFile = File(
        outputDirectory,
        SimpleDateFormat(filenameFormat, Locale.US).format(System.currentTimeMillis()) + ".jpg"
    )

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    var uri: Uri? = null

    imageCapture.takePicture(outputOptions, executor, object: ImageCapture.OnImageSavedCallback {
        override fun onError(exception: ImageCaptureException) {
            Log.e("kilo", "Take photo error:", exception)
            onError(exception)
        }

        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
            val savedUri = Uri.fromFile(photoFile)
            Log.i("kilo", savedUri.toString())

            if (tileGroup != TileGroup.HAND) {
                addPartUri(savedUri)
                if (part_imgs.size == 4) {
                    MainScope().launch {
                        multiple_cvresult(globalContext, tileGroup)
                        navController.popBackStack("TileMenuTabs", inclusive = false)
                    }
                }
            }
            else {
                MainScope().launch {
                    navController.popBackStack("TileMenuTabs", inclusive = false)
                    cvresult(globalContext, savedUri, tileGroup)

                }
            }
        }
    })
}

private suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine { continuation ->
    ProcessCameraProvider.getInstance(this).also { cameraProvider ->
        cameraProvider.addListener({
            continuation.resume(cameraProvider.get())
        }, ContextCompat.getMainExecutor(this))
    }
}
