package com.example.sanctum

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Base64
import android.webkit.JavascriptInterface
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class BlobDownloadHelper(private val context: Context) {

    @JavascriptInterface
    fun getBase64FromBlobData(base64Data: String, mimeType: String, fileName: String?) {
        try {
            // Strip out the data URI prefix if it exists
            val cleanBase64 = base64Data.replaceFirst("^data:[^;]*;base64,".toRegex(), "")
            val decodedBytes = Base64.decode(cleanBase64, Base64.DEFAULT)

            val safeFileName = if (fileName.isNullOrBlank() || fileName == "null") {
                "download_${UUID.randomUUID().toString().substring(0, 8)}"
            } else {
                fileName
            }

            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (!downloadsDir.exists()) downloadsDir.mkdirs()

            var file = File(downloadsDir, safeFileName)
            
            // Handle duplicate file names by appending numbers
            var counter = 1
            val nameWithoutExt = safeFileName.substringBeforeLast(".")
            val ext = if (safeFileName.contains(".")) "." + safeFileName.substringAfterLast(".") else ""
            while (file.exists()) {
                file = File(downloadsDir, "$nameWithoutExt ($counter)$ext")
                counter++
            }

            val fos = FileOutputStream(file)
            fos.write(decodedBytes)
            fos.flush()
            fos.close()

            // Tell the system DownloadManager about the completed download so it shows in the UI/Notifications
            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadManager.addCompletedDownload(
                file.name,
                "File downloaded via Sanctum",
                true,
                mimeType,
                file.absolutePath,
                file.length(),
                true
            )

            // UI feedback
            android.os.Handler(android.os.Looper.getMainLooper()).post {
                Toast.makeText(context, "File downloaded: ${file.name}", Toast.LENGTH_LONG).show()
            }

        } catch (e: Exception) {
            android.os.Handler(android.os.Looper.getMainLooper()).post {
                Toast.makeText(context, "Blob download failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    companion object {
        fun getBlobJavascriptSnippet(blobUrl: String, mimeType: String, contentDisposition: String?): String {
            // Try to extract filename from content disposition, or fallback
            var fileName = "download"
            if (contentDisposition != null && contentDisposition.contains("filename=")) {
                val match = Regex("filename=\"?([^\"]+)\"?").find(contentDisposition)
                if (match != null) {
                    fileName = match.groupValues[1]
                }
            }
            
            return """
                javascript:(function() {
                    var xhr = new XMLHttpRequest();
                    xhr.open('GET', '$blobUrl', true);
                    xhr.responseType = 'blob';
                    xhr.onload = function(e) {
                        if (this.status == 200) {
                            var blob = this.response;
                            var reader = new FileReader();
                            reader.readAsDataURL(blob);
                            reader.onloadend = function() {
                                var base64data = reader.result;
                                AndroidBlobDownloader.getBase64FromBlobData(base64data, '$mimeType', '$fileName');
                            }
                        }
                    };
                    xhr.send();
                })();
            """.trimIndent()
        }
    }
}
