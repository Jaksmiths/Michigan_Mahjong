package michigan.mahjong

import android.content.Context
import android.net.Uri
import android.widget.Toast
import java.io.File

fun Context.toast(message: String, short: Boolean = true) {
    Toast.makeText(this, message, if (short) Toast.LENGTH_SHORT else Toast.LENGTH_LONG).show()
}

fun createTmpFileFromUri(context: Context, uri: Uri, fileName: String): File? {
    return try {
        val stream = context.contentResolver.openInputStream(uri)
        val file = File.createTempFile(fileName, "", context.cacheDir)
        org.apache.commons.io.FileUtils.copyInputStreamToFile(stream,file)
        file
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}