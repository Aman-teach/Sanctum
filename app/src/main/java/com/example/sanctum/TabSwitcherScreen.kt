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

// Sanctum Design System Tokens
val SurfaceContainerLow = Color(0xFFF5F3F2)
val SurfaceContainerLowest = Color(0xFFFFFFFF)
val PrimaryContainer = Color(0xFF00668B)
val OnSurface = Color(0xFF1B1C1B)
val OutlineVariant = Color(0xFFBFC8CF)

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
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNewTab,
                containerColor = PrimaryContainer,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Tab")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Text(
                text = "${tabManager.tabs.size} Tabs",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = OnSurface,
                modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 8.dp)
            )
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(tabManager.tabs) { index, tab ->
                    val isActive = index == tabManager.activeTabIndex.value
                    
                    Box(
                        modifier = Modifier
                            .aspectRatio(0.6f)
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
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = tab.title.value,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = OnSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
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
                                    .background(Color(0xFFE9E8E7)) // surface-container-high
                            ) {
                                tab.snapshot?.let { bmp ->
                                    Image(
                                        bitmap = bmp.asImageBitmap(),
                                        contentDescription = "Tab Preview",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
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
