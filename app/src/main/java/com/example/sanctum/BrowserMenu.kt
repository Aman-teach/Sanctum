package com.example.sanctum

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sanctum.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowserMenuSheet(
    onDismissRequest: () -> Unit,
    onReload: () -> Unit,
    onNewTab: () -> Unit,
    onSettings: () -> Unit,
    onHistory: () -> Unit,
    onBookmarkAdd: () -> Unit,
    onBookmarksView: () -> Unit,
    onDownloadsView: () -> Unit,
    onShare: () -> Unit,
    onCopyLink: () -> Unit,
    onFindInPage: () -> Unit,
    desktopMode: Boolean,
    onDesktopModeToggle: (Boolean) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        containerColor = SurfaceContainerLowest,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = {
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .width(32.dp)
                        .height(4.dp)
                        .background(SurfaceVariant, RoundedCornerShape(50))
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp)
        ) {
            // Quick Action Row
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionItem(
                    icon = Icons.Default.Refresh,
                    label = "Reload",
                    tint = Primary,
                    modifier = Modifier.weight(1f),
                    onClick = { onReload(); onDismissRequest() }
                )
                QuickActionItem(
                    icon = Icons.Default.BookmarkAdd,
                    label = "Bookmark",
                    modifier = Modifier.weight(1f),
                    onClick = { onBookmarkAdd(); onDismissRequest() }
                )
                QuickActionItem(
                    icon = Icons.Default.Share,
                    label = "Share",
                    modifier = Modifier.weight(1f),
                    onClick = { onShare(); onDismissRequest() }
                )
                QuickActionItem(
                    icon = Icons.Default.ContentCopy,
                    label = "Copy Link",
                    modifier = Modifier.weight(1f),
                    onClick = { onCopyLink(); onDismissRequest() }
                )
            }

            // List Actions - Group 1
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceContainerLowest)
                    .border(1.dp, SurfaceVariant, RoundedCornerShape(12.dp))
            ) {
                ListActionItem(icon = Icons.Default.Tab, label = "New Tab", onClick = { onNewTab(); onDismissRequest() })
                Divider(color = SurfaceVariant, modifier = Modifier.padding(start = 48.dp))
                ListActionItem(icon = Icons.Default.History, label = "History", onClick = { onHistory(); onDismissRequest() })
                Divider(color = SurfaceVariant, modifier = Modifier.padding(start = 48.dp))
                ListActionItem(icon = Icons.Default.Bookmarks, label = "Bookmarks", onClick = { onBookmarksView(); onDismissRequest() })
                Divider(color = SurfaceVariant, modifier = Modifier.padding(start = 48.dp))
                ListActionItem(icon = Icons.Default.Download, label = "Downloads", onClick = { onDownloadsView(); onDismissRequest() })
            }

            Spacer(modifier = Modifier.height(16.dp))

            // List Actions - Group 2
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceContainerLowest)
                    .border(1.dp, SurfaceVariant, RoundedCornerShape(12.dp))
            ) {
                ListActionItem(icon = Icons.Default.FindInPage, label = "Find in Page", onClick = { onFindInPage(); onDismissRequest() })
                Divider(color = SurfaceVariant, modifier = Modifier.padding(start = 48.dp))
                
                // Desktop Site Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clickable { onDesktopModeToggle(!desktopMode) }
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.DesktopWindows, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Desktop Site", fontSize = 16.sp, color = OnSurface, modifier = Modifier.weight(1f))
                    Switch(
                        checked = desktopMode,
                        onCheckedChange = { onDesktopModeToggle(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Primary,
                            uncheckedThumbColor = Outline,
                            uncheckedTrackColor = SurfaceContainerHighest
                        )
                    )
                }
                
                Divider(color = SurfaceVariant, modifier = Modifier.padding(start = 48.dp))
                ListActionItem(icon = Icons.Default.Settings, label = "Settings", onClick = { onSettings(); onDismissRequest() })
            }
        }
    }
}

@Composable
fun QuickActionItem(
    icon: ImageVector,
    label: String,
    tint: Color = OnSurfaceVariant,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .height(96.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceContainerLow)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(icon, contentDescription = label, tint = tint, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.height(8.dp))
        Text(label, fontSize = 11.sp, color = OnSurfaceVariant, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun ListActionItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = label, tint = OnSurfaceVariant, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(label, fontSize = 16.sp, color = OnSurface)
    }
}
