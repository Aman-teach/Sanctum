package com.example.sanctum

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Downloading
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class DownloadItem(
    val id: Long,
    val title: String,
    val status: Int,
    val totalSizeBytes: Long,
    val downloadedBytes: Long,
    val mediaType: String,
    val localUri: String?
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadsScreen(context: Context, onBack: () -> Unit) {
    var downloads by remember { mutableStateOf<List<DownloadItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        while (true) {
            val currentDownloads = withContext(Dispatchers.IO) { fetchDownloads(context) }
            downloads = currentDownloads
            isLoading = false
            
            val hasRunning = currentDownloads.any { it.status == DownloadManager.STATUS_RUNNING || it.status == DownloadManager.STATUS_PENDING }
            if (hasRunning) {
                kotlinx.coroutines.delay(1000)
            } else {
                kotlinx.coroutines.delay(3000)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EditorialPaper)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.size(40.dp)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = EditorialInk,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Downloads",
                color = EditorialInk,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Divider(color = EditorialBorder)

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = EditorialInk)
            }
        } else if (downloads.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "No recent downloads",
                    color = EditorialMutedInk,
                    fontSize = 16.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(downloads) { download ->
                    DownloadCard(download = download, context = context)
                }
            }
        }
    }
}

@Composable
fun DownloadCard(download: DownloadItem, context: Context) {
    val isCompleted = download.status == DownloadManager.STATUS_SUCCESSFUL
    val isFailed = download.status == DownloadManager.STATUS_FAILED
    val isRunning = download.status == DownloadManager.STATUS_RUNNING || download.status == DownloadManager.STATUS_PENDING

    val statusIcon = when {
        isCompleted -> Icons.Default.CheckCircle
        isRunning -> Icons.Default.Downloading
        else -> Icons.Default.Error
    }

    val statusColor = when {
        isCompleted -> Color(0xFF10B981) // Emerald Green
        isRunning -> Color(0xFF3B82F6) // Blue
        else -> Color(0xFFEF4444) // Red
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(EditorialSurface)
            .clickable(enabled = isCompleted) {
                openDownloadedFile(context, download.id, download.title, download.mediaType)
            }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // File Icon
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(EditorialPaper),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.InsertDriveFile,
                contentDescription = "File",
                tint = EditorialMutedInk,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Details
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = download.title,
                color = EditorialInk,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            
            val sizeStr = if (download.totalSizeBytes > 0) formatFileSize(download.totalSizeBytes) else "Unknown size"
            val statusStr = when {
                isCompleted -> "Completed • $sizeStr"
                isRunning -> {
                    val percent = if (download.totalSizeBytes > 0) (download.downloadedBytes * 100 / download.totalSizeBytes).toInt() else 0
                    if (percent > 0) "Downloading... $percent%" else "Downloading..."
                }
                else -> "Failed"
            }
            
            Text(
                text = statusStr,
                color = EditorialMutedInk,
                fontSize = 13.sp,
                maxLines = 1
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Status Indicator
        Icon(
            imageVector = statusIcon,
            contentDescription = "Status",
            tint = statusColor,
            modifier = Modifier.size(24.dp)
        )
    }
}

private fun fetchDownloads(context: Context): List<DownloadItem> {
    val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    val query = DownloadManager.Query()
    val cursor: Cursor? = downloadManager.query(query)
    val list = mutableListOf<DownloadItem>()

    cursor?.use {
        val idIdx = it.getColumnIndex(DownloadManager.COLUMN_ID)
        val titleIdx = it.getColumnIndex(DownloadManager.COLUMN_TITLE)
        val statusIdx = it.getColumnIndex(DownloadManager.COLUMN_STATUS)
        val totalSizeIdx = it.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
        val downloadedIdx = it.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
        val mediaTypeIdx = it.getColumnIndex(DownloadManager.COLUMN_MEDIA_TYPE)
        val localUriIdx = it.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)

        while (it.moveToNext()) {
            val id = if (idIdx != -1) it.getLong(idIdx) else 0L
            val title = if (titleIdx != -1) it.getString(titleIdx) ?: "Unknown" else "Unknown"
            val status = if (statusIdx != -1) it.getInt(statusIdx) else -1
            val totalSize = if (totalSizeIdx != -1) it.getLong(totalSizeIdx) else 0L
            val downloaded = if (downloadedIdx != -1) it.getLong(downloadedIdx) else 0L
            val mediaType = if (mediaTypeIdx != -1) it.getString(mediaTypeIdx) ?: "*/*" else "*/*"
            val localUri = if (localUriIdx != -1) it.getString(localUriIdx) else null

            list.add(DownloadItem(id, title, status, totalSize, downloaded, mediaType, localUri))
        }
    }
    
    // Sort so newest are likely first (assuming higher ID = newer)
    return list.sortedByDescending { it.id }
}

private fun openDownloadedFile(context: Context, downloadId: Long, title: String, mimeType: String) {
    try {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri = downloadManager.getUriForDownloadedFile(downloadId)
        
        var finalMimeType = mimeType
        if (finalMimeType.contains("octet-stream") || finalMimeType == "*/*" || finalMimeType.isBlank()) {
            val extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(title) ?: ""
            val guessed = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.lowercase())
            if (guessed != null) {
                finalMimeType = guessed
            } else if (title.lowercase().endsWith(".apk")) {
                finalMimeType = "application/vnd.android.package-archive"
            }
        }
        
        if (uri != null) {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, finalMimeType)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } else {
            Toast.makeText(context, "File no longer exists", Toast.LENGTH_SHORT).show()
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Could not open file: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

private fun formatFileSize(size: Long): String {
    if (size <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt()
    return String.format("%.1f %s", size / Math.pow(1024.0, digitGroups.toDouble()), units[digitGroups])
}
