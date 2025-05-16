package ng.wimika.moneyguardsdkclient.utils

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream


object FileUtils {
    fun isFileAnImage(context: Context, uri: Uri): Boolean {
        val mimeType = getMimeType(context, uri)
        return mimeType?.startsWith("image/") == true
    }

    fun isFileAVideo(context: Context, uri: Uri): Boolean {
        val mimeType = getMimeType(context, uri)
        return mimeType?.startsWith("video/") == true
    }

    fun isFileAnAudio(context: Context, uri: Uri): Boolean {
        val mimeType = getMimeType(context, uri)
        return mimeType?.startsWith("audio/") == true
    }


    fun getMimeType(context: Context, uri: Uri): String? {
        return when (uri.scheme) {
            "content" -> context.contentResolver.getType(uri)
            "file" -> {
                val extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
                MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.lowercase())
            }
            else -> null
        }
    }

    fun getFileName(context: Context, uri: Uri): String {
        var result: String? = null

        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val columnIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (columnIndex != -1) {
                        result = it.getString(columnIndex)
                    }
                }
            }
        }

        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != -1) {
                result = result?.substring(cut!! + 1)
            }
        }

        return result ?: "unknown_file_${System.currentTimeMillis()}"
    }
}

