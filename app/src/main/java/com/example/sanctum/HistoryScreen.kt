package com.example.sanctum

import android.text.format.DateUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sanctum.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(
    context: android.content.Context,
    onBack: () -> Unit,
    onNavigate: (String) -> Unit
) {
    val historyItems by HistoryManager.history.collectAsState()

    // Group items by date string
    val groupedHistory = remember(historyItems) {
        historyItems.groupBy { item ->
            when {
                DateUtils.isToday(item.timestamp) -> "Today"
                DateUtils.isToday(item.timestamp + DateUtils.DAY_IN_MILLIS) -> "Yesterday"
                else -> {
                    val sdf = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
                    sdf.format(Date(item.timestamp))
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface)
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = OnSurfaceVariant)
            }
            Text(
                text = "History",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Primary
            )
            TextButton(onClick = { HistoryManager.clearHistory(context) }) {
                Text(text = "Clear History", color = Primary, fontSize = 14.sp)
            }
        }

        if (historyItems.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "No history yet.", color = OnSurfaceVariant, fontSize = 16.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
            ) {
                groupedHistory.forEach { (dateGroup, items) ->
                    item {
                        Text(
                            text = dateGroup,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = OnSurface,
                            modifier = Modifier.padding(bottom = 8.dp, top = 8.dp)
                        )
                    }

                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(SurfaceContainerLowest)
                        ) {
                            items.forEachIndexed { index, item ->
                                HistoryRow(
                                    item = item,
                                    onClick = { onNavigate(item.url) },
                                    onDelete = { HistoryManager.removeHistory(context, item.id) }
                                )
                                if (index < items.lastIndex) {
                                    HorizontalDivider(color = SurfaceContainerHigh.copy(alpha = 0.5f))
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryRow(item: HistoryItem, onClick: () -> Unit, onDelete: () -> Unit) {
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val timeStr = timeFormat.format(Date(item.timestamp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .padding(top = 4.dp)
                .size(32.dp)
                .clip(CircleShape)
                .background(SurfaceContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Language, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                fontSize = 16.sp,
                color = OnSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = item.url,
                fontSize = 14.sp,
                color = OnSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column(horizontalAlignment = Alignment.End) {
            Text(text = timeStr, fontSize = 12.sp, color = OnSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = OnSurfaceVariant, modifier = Modifier.size(16.dp))
            }
        }
    }
}
