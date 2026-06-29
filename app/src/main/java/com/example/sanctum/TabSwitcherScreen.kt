package com.example.sanctum

import kotlinx.coroutines.launch

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.sanctum.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabSwitcherScreen(
    tabManager: TabManager,
    onTabSelected: (Int) -> Unit,
    onNewTab: () -> Unit,
    onCloseTab: (Int) -> Unit,
    onUndoClose: () -> Unit
) {
    val snackbarHostState = androidx.compose.runtime.remember { SnackbarHostState() }
    val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()

    Scaffold(
        containerColor = SurfaceContainerLow,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(Surface)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Lock, contentDescription = "Secure", tint = OnSurfaceVariant)
                }
                Text("Sanctum", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Primary)
                TextButton(onClick = {
                    val count = tabManager.tabs.size
                    for (i in count - 1 downTo 0) {
                        tabManager.closeTab(i)
                    }
                }) {
                    Text("Clear All", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Primary)
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNewTab,
                containerColor = PrimaryContainer,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(bottom = 64.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Tab")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).background(SurfaceContainerLow)) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(tabManager.tabs) { index, tab ->
                    val isActive = index == tabManager.activeTabIndex.value
                    
                    Box(
                        modifier = Modifier
                            .height(192.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceContainerLowest)
                            .border(
                                width = if (isActive) 2.dp else 1.dp,
                                color = if (isActive) PrimaryContainer else OutlineVariant,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { onTabSelected(index) }
                    ) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            // Header
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(SurfaceContainerLowest)
                                    .border(1.dp, SurfaceVariant)
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier.size(24.dp).background(SurfaceVariant, RoundedCornerShape(4.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.AutoMirrored.Filled.Article, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(16.dp))
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = tab.title.value,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = OnSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        onCloseTab(index)
                                        coroutineScope.launch {
                                            val result = snackbarHostState.showSnackbar(
                                                message = "Closed tab",
                                                actionLabel = "UNDO",
                                                duration = SnackbarDuration.Short
                                            )
                                            if (result == SnackbarResult.ActionPerformed) {
                                                onUndoClose()
                                            }
                                        }
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Close Tab",
                                        tint = OutlineVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            
                            // Snapshot Preview
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(SurfaceContainerLow)
                            ) {
                                tab.snapshot?.let { bmp ->
                                    Image(
                                        bitmap = bmp.asImageBitmap(),
                                        contentDescription = "Tab Preview",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                        alpha = 0.9f
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
