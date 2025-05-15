package ng.wimika.moneyguardsdkclient.utils

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import java.io.File



object FileUtils {
    fun isFileAnImage(context: Context, file: File): Boolean  {
        val mimeType = getMimeType2(context, Uri.fromFile(file))
        return mimeType?.startsWith("image/") == true
    }

    fun isFileAVideo(context: Context, file: File): Boolean  {
        val mimeType = getMimeType2(context, Uri.fromFile(file))
        return mimeType?.startsWith("video/") == true
    }

    fun isFileAnAudio(context: Context, file: File): Boolean  {
        val mimeType = getMimeType2(context, Uri.fromFile(file))
        return mimeType?.startsWith("audio/") == true
    }


    fun getMimeType(context: Context, file: File): String? {
        val fileUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val mimeType = context.contentResolver.getType(fileUri)
            ?: when {
                file.name.lowercase().endsWith(".jpg") ||
                        file.name.lowercase().endsWith(".jpeg") -> "image/jpeg"
                file.name.lowercase().endsWith(".png") -> "image/png"
                file.name.lowercase().endsWith(".gif") -> "image/gif"
                file.name.lowercase().endsWith(".webp") -> "image/webp"
                else -> null
            }

        return mimeType
    }


    fun getMimeType2(context: Context, uri: Uri): String? {
        return context.contentResolver.getType(uri) ?: run {
            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
            if (!fileExtension.isNullOrEmpty()) {
                MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension)
            } else {
                when {
                    uri.toString().lowercase().contains(".jpg") ||
                            uri.toString().lowercase().contains(".jpeg") -> "image/jpeg"
                    uri.toString().lowercase().contains(".png") -> "image/png"
                    uri.toString().lowercase().contains(".gif") -> "image/gif"
                    uri.toString().lowercase().contains(".webp") -> "image/webp"
                    else -> null
                }
            }
        }
    }
}

