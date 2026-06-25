package com.example.sanctum

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Add
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import com.example.sanctum.ui.theme.*

@Composable
fun HomeScreen(
    totalBlockedCount: Int,
    topSites: List<HistoryItem>,
    newsFeed: List<NewsItem>,
    onSearchSubmit: (String) -> Unit,
    onProfileClick: () -> Unit,
    onShieldClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var isFocused by remember { mutableStateOf(false) }
    var suggestions by remember { mutableStateOf<List<String>>(emptyList()) }
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(searchQuery, isFocused) {
        if (searchQuery.isNotBlank() && isFocused) {
            suggestions = SearchSuggestionManager.getSuggestions(searchQuery)
        } else {
            suggestions = emptyList()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top App Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onShieldClick) {
                    Icon(Icons.Default.Security, contentDescription = "Security", tint = MaterialTheme.colorScheme.onSurface.copy(alpha=0.6f))
                }
                Text(text = "Sanctum", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                IconButton(onClick = onSettingsClick) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings", tint = MaterialTheme.colorScheme.onSurface.copy(alpha=0.6f))
                }
            }

            // Main Content
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f).padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
            ) {
                item {
                    // Search Pill
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 32.dp)
                            .background(SurfaceContainerLowest, RoundedCornerShape(24.dp))
                            .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha=0.1f), RoundedCornerShape(24.dp))
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.onSurface.copy(alpha=0.6f))
                            Spacer(modifier = Modifier.width(12.dp))
                            BasicTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                modifier = Modifier
                                    .weight(1f)
                                    .focusRequester(focusRequester)
                                    .onFocusChanged { isFocused = it.isFocused },
                                textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                keyboardActions = KeyboardActions(onSearch = {
                                    focusManager.clearFocus()
                                    onSearchSubmit(searchQuery)
                                })
                            )
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }, modifier = Modifier.size(24.dp)) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear", tint = MaterialTheme.colorScheme.onSurface.copy(alpha=0.6f), modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }

                    // Quick Access Grid
                    var showAddShortcutDialog by remember { mutableStateOf(false) }

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Add Shortcut Button
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .width(72.dp)
                                .clickable { showAddShortcutDialog = true }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .background(SurfaceContainerLow, RoundedCornerShape(16.dp))
                                    .border(1.dp, SurfaceContainerHigh, RoundedCornerShape(16.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Add Shortcut", tint = MaterialTheme.colorScheme.onSurface)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Add", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)
                        }

                        if (topSites.isNotEmpty()) {
                            topSites.forEach { site ->
                                val domain = android.net.Uri.parse(site.url).host ?: site.title
                                val faviconUrl = "https://www.google.com/s2/favicons?domain=$domain&sz=128"
                                QuickAccessItem(
                                    name = if (site.title.length > 12) site.title.substring(0, 12) + "..." else site.title.ifEmpty { domain },
                                    iconUrl = faviconUrl,
                                    onClick = { onSearchSubmit(site.url) }
                                )
                            }
                        } else {
                            // Fallback
                            QuickAccessItem("GitHub", "https://github.githubassets.com/favicons/favicon.png") { onSearchSubmit("https://github.com") }
                            QuickAccessItem("YouTube", "https://www.youtube.com/s/desktop/2e12e8ba/img/favicon_96x96.png") { onSearchSubmit("https://youtube.com") }
                        }
                    }

                    if (showAddShortcutDialog) {
                        var newShortcutUrl by remember { mutableStateOf("") }
                        androidx.compose.material3.AlertDialog(
                            onDismissRequest = { showAddShortcutDialog = false },
                            title = { Text("Add Shortcut") },
                            text = {
                                androidx.compose.material3.OutlinedTextField(
                                    value = newShortcutUrl,
                                    onValueChange = { newShortcutUrl = it },
                                    label = { Text("URL") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            },
                            confirmButton = {
                                androidx.compose.material3.TextButton(onClick = {
                                    if (newShortcutUrl.isNotBlank()) {
                                        var finalUrl = newShortcutUrl.trim()
                                        if (!finalUrl.startsWith("http://") && !finalUrl.startsWith("https://")) {
                                            finalUrl = "https://$finalUrl"
                                        }
                                        HistoryManager.addHistory(context, finalUrl, android.net.Uri.parse(finalUrl).host ?: finalUrl)
                                        showAddShortcutDialog = false
                                    }
                                }) {
                                    Text("Add")
                                }
                            },
                            dismissButton = {
                                androidx.compose.material3.TextButton(onClick = { showAddShortcutDialog = false }) {
                                    Text("Cancel")
                                }
                            }
                        )
                    }

                    // Tech News Section
                    if (newsFeed.isNotEmpty()) {
                        Text(
                            text = "Latest Tech News",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        
                        newsFeed.forEach { news ->
                            NewsItemCard(news = news, onClick = { onSearchSubmit(news.link) })
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    } else {
                        // Loading news skeleton or just empty space
                        Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.primary, strokeWidth = 2.dp)
                        }
                    }
                }
            }
        }

        // Search Suggestions Overlay
        AnimatedVisibility(visible = isFocused, enter = fadeIn(), exit = fadeOut()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Surface.copy(alpha = 0.98f))
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Replicated Top Bar & Search Input for the overlay
                    Row(
                        modifier = Modifier.fillMaxWidth().height(56.dp).padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { focusManager.clearFocus() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onSurface.copy(alpha=0.6f))
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .background(SurfaceContainerLowest, RoundedCornerShape(24.dp))
                            .border(1.dp, Primary, RoundedCornerShape(24.dp))
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = Primary)
                            Spacer(modifier = Modifier.width(12.dp))
                            BasicTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                modifier = Modifier
                                    .weight(1f)
                                    .focusRequester(focusRequester),
                                textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                keyboardActions = KeyboardActions(onSearch = {
                                    focusManager.clearFocus()
                                    onSearchSubmit(searchQuery)
                                })
                            )
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }, modifier = Modifier.size(24.dp)) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear", tint = MaterialTheme.colorScheme.onSurface.copy(alpha=0.6f), modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f)) {
                        items(suggestions.size) { index ->
                            val suggestion = suggestions[index]
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        focusManager.clearFocus()
                                        onSearchSubmit(suggestion)
                                    }
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.onSurface.copy(alpha=0.6f), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(suggestion, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuickAccessItem(name: String, iconUrl: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(SurfaceContainerLow, RoundedCornerShape(12.dp))
                .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha=0.1f).copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = iconUrl,
                contentDescription = name,
                modifier = Modifier.size(32.dp),
                contentScale = ContentScale.Fit
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(name, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
fun NewsItemCard(news: NewsItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .background(SurfaceContainerLow)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (news.imageUrl.isNotEmpty()) {
            AsyncImage(
                model = news.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(12.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = news.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Hacker News • ${news.pubDate}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
