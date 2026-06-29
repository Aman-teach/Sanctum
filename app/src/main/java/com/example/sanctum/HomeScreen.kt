package com.example.sanctum

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import com.example.sanctum.ui.theme.bounceClick
import com.example.sanctum.ui.theme.combinedBounceClick
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
import androidx.activity.compose.BackHandler
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
import kotlinx.coroutines.delay

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

    val greeting = remember {
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        val timeGreeting = when (hour) {
            in 5..11 -> "Good morning"
            in 12..16 -> "Good afternoon"
            in 17..20 -> "Good evening"
            else -> "Night owl"
        }
        val funPhrases = listOf(
            "Ready to explore?",
            "Where to next?",
            "What are we searching for?",
            "Let's discover the web"
        )
        "$timeGreeting. ${funPhrases.random()}"
    }

    LaunchedEffect(searchQuery, isFocused) {
        if (searchQuery.isNotBlank() && isFocused) {
            suggestions = SearchSuggestionManager.getSuggestions(searchQuery)
        } else {
            suggestions = emptyList()
        }
    }

    BackHandler(enabled = isFocused) {
        isFocused = false
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
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .clickable { onShieldClick() },
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_sanctum_logo),
                        contentDescription = "Security Shield",
                        modifier = Modifier.size(36.dp).clip(CircleShape)
                    )
                }
                Text(text = greeting, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                IconButton(onClick = onSettingsClick) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings", tint = MaterialTheme.colorScheme.onSurface.copy(alpha=0.6f))
                }
            }

            // Main Content
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f).padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp)
            ) {
                item {
                    // Search Pill
                    val searchElevation by androidx.compose.animation.core.animateDpAsState(
                        targetValue = if (isFocused) 8.dp else 0.dp,
                        animationSpec = androidx.compose.animation.core.tween(200),
                        label = "searchElevation"
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp)
                            .shadow(elevation = searchElevation, shape = RoundedCornerShape(24.dp))
                            .background(SurfaceContainerLowest, RoundedCornerShape(24.dp))
                            .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha=0.1f), RoundedCornerShape(24.dp))
                            .clickable { isFocused = true }
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.onSurface.copy(alpha=0.6f))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = if (searchQuery.isNotEmpty()) searchQuery else "Search...",
                                color = if (searchQuery.isNotEmpty()) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha=0.6f),
                                fontSize = 16.sp,
                                modifier = Modifier.weight(1f)
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
                    var shortcutToDelete by remember { mutableStateOf<HistoryItem?>(null) }

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp, start = 16.dp, end = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally)
                    ) {
                        // Add Shortcut Button (Circular & smaller)
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .width(52.dp)
                                .clickable { showAddShortcutDialog = true }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(SurfaceContainerLow)
                                    .border(1.dp, SurfaceContainerHigh, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Add Shortcut", tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Add", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }

                        if (topSites.isNotEmpty()) {
                            topSites.forEach { site ->
                                val domain = android.net.Uri.parse(site.url).host ?: site.title
                                val faviconUrl = "https://icon.horse/icon/$domain"
                                QuickAccessItem(
                                    name = if (site.title.length > 10) site.title.substring(0, 10) + "..." else site.title.ifEmpty { domain },
                                    iconUrl = faviconUrl,
                                    onLongClick = { shortcutToDelete = site },
                                    onClick = { onSearchSubmit(site.url) }
                                )
                            }
                        } else {
                            // Fallback
                            QuickAccessItem("GitHub", "https://icon.horse/icon/github.com") { onSearchSubmit("https://github.com") }
                            QuickAccessItem("YouTube", "https://icon.horse/icon/youtube.com") { onSearchSubmit("https://youtube.com") }
                            QuickAccessItem("Google", "https://icon.horse/icon/google.com") { onSearchSubmit("https://google.com") }
                        }
                    }

                    if (showAddShortcutDialog) {
                        var shortcutName by remember { mutableStateOf("") }
                        var shortcutUrl by remember { mutableStateOf("") }
                        
                        androidx.compose.ui.window.Dialog(onDismissRequest = { showAddShortcutDialog = false }) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .shadow(8.dp, RoundedCornerShape(16.dp))
                                    .background(EditorialPaper, RoundedCornerShape(16.dp))
                                    .border(1.dp, EditorialBorder, RoundedCornerShape(16.dp))
                                    .padding(20.dp)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.Start
                                ) {
                                    Text(
                                        text = "Add Shortcut",
                                        fontFamily = Inter,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EditorialInk
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    
                                    // Name Field
                                    Text("Name", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EditorialMutedInk, fontFamily = Inter)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(38.dp)
                                            .background(EditorialSurface, RoundedCornerShape(6.dp))
                                            .border(1.dp, EditorialBorder, RoundedCornerShape(6.dp))
                                            .padding(horizontal = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        BasicTextField(
                                            value = shortcutName,
                                            onValueChange = { shortcutName = it },
                                            modifier = Modifier.weight(1f),
                                            textStyle = TextStyle(fontFamily = Inter, fontSize = 13.sp, color = EditorialInk),
                                            singleLine = true,
                                            decorationBox = { innerTextField ->
                                                androidx.compose.foundation.layout.Box(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    contentAlignment = Alignment.CenterStart
                                                ) {
                                                    if (shortcutName.isEmpty()) {
                                                        Text("e.g. ChatGPT", color = EditorialMutedInk, fontFamily = Inter, fontSize = 13.sp)
                                                    }
                                                    innerTextField()
                                                }
                                            }
                                        )
                                    }
                                    
                                    Spacer(modifier = Modifier.height(14.dp))
                                    
                                    // URL Field
                                    Text("Web Address (URL)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EditorialMutedInk, fontFamily = Inter)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(38.dp)
                                            .background(EditorialSurface, RoundedCornerShape(6.dp))
                                            .border(1.dp, EditorialBorder, RoundedCornerShape(6.dp))
                                            .padding(horizontal = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        BasicTextField(
                                            value = shortcutUrl,
                                            onValueChange = { shortcutUrl = it },
                                            modifier = Modifier.weight(1f),
                                            textStyle = TextStyle(fontFamily = Inter, fontSize = 13.sp, color = EditorialInk),
                                            singleLine = true,
                                            decorationBox = { innerTextField ->
                                                androidx.compose.foundation.layout.Box(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    contentAlignment = Alignment.CenterStart
                                                ) {
                                                    if (shortcutUrl.isEmpty()) {
                                                        Text("e.g. chatgpt.com", color = EditorialMutedInk, fontFamily = Inter, fontSize = 13.sp)
                                                    }
                                                    innerTextField()
                                                }
                                            }
                                        )
                                    }
                                    
                                    Spacer(modifier = Modifier.height(20.dp))
                                    
                                    // Buttons
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Cancel",
                                            fontFamily = Inter,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = EditorialMutedInk,
                                            modifier = Modifier
                                                .clickable { showAddShortcutDialog = false }
                                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(EditorialForest)
                                                .clickable {
                                                    if (shortcutUrl.isNotBlank()) {
                                                        var finalUrl = shortcutUrl.trim()
                                                        if (!finalUrl.startsWith("http://") && !finalUrl.startsWith("https://")) {
                                                            finalUrl = "https://$finalUrl"
                                                        }
                                                        val host = android.net.Uri.parse(finalUrl).host ?: finalUrl
                                                        val finalTitle = shortcutName.trim().ifEmpty { host }
                                                        HistoryManager.addHistory(context, finalUrl, finalTitle)
                                                        showAddShortcutDialog = false
                                                    }
                                                }
                                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                        ) {
                                            Text(
                                                text = "Add",
                                                fontFamily = Inter,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = androidx.compose.ui.graphics.Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (shortcutToDelete != null) {
                        val site = shortcutToDelete!!
                        androidx.compose.ui.window.Dialog(onDismissRequest = { shortcutToDelete = null }) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .shadow(8.dp, RoundedCornerShape(16.dp))
                                    .background(EditorialPaper, RoundedCornerShape(16.dp))
                                    .border(1.dp, EditorialBorder, RoundedCornerShape(16.dp))
                                    .padding(20.dp)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.Start
                                ) {
                                    Text(
                                        text = "Remove Shortcut",
                                        fontFamily = Inter,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EditorialInk
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "Are you sure you want to remove \"${site.title}\" from your home screen?",
                                        fontFamily = Inter,
                                        fontSize = 13.sp,
                                        color = EditorialMutedInk
                                    )
                                    Spacer(modifier = Modifier.height(20.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Cancel",
                                            fontFamily = Inter,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = EditorialMutedInk,
                                            modifier = Modifier
                                                .clickable { shortcutToDelete = null }
                                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(androidx.compose.ui.graphics.Color(0xFFEF4444))
                                                .clickable {
                                                    HistoryManager.removeAllHistoryForUrl(context, site.url)
                                                    shortcutToDelete = null
                                                }
                                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                        ) {
                                            Text(
                                                text = "Remove",
                                                fontFamily = Inter,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = androidx.compose.ui.graphics.Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Tech News Section
                    if (newsFeed.isNotEmpty()) {
                        Text(
                            text = "Latest Tech News",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        
                        newsFeed.forEachIndexed { index, news ->
                            var visible by remember { mutableStateOf(false) }
                            LaunchedEffect(news.link) {
                                delay(index * 100L)
                                visible = true
                            }
                            androidx.compose.animation.AnimatedVisibility(
                                visible = visible,
                                enter = androidx.compose.animation.fadeIn(animationSpec = androidx.compose.animation.core.tween(300)) + slideInVertically(
                                    initialOffsetY = { 20 },
                                    animationSpec = androidx.compose.animation.core.tween(300)
                                )
                            ) {
                                Column {
                                    NewsItemCard(news = news, onClick = { onSearchSubmit(news.link) })
                                    Spacer(modifier = Modifier.height(12.dp))
                                }
                            }
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
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        var hasGainedFocus by remember { mutableStateOf(false) }
                        
                        LaunchedEffect(Unit) {
                            try {
                                focusRequester.requestFocus()
                            } catch (e: Exception) {}
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = Primary)
                            Spacer(modifier = Modifier.width(12.dp))
                            BasicTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                modifier = Modifier
                                    .weight(1f)
                                    .focusRequester(focusRequester)
                                    .onFocusChanged { 
                                        if (it.isFocused) {
                                            hasGainedFocus = true
                                        } else if (hasGainedFocus) {
                                            isFocused = false
                                        }
                                    },
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
fun QuickAccessItem(
    name: String,
    iconUrl: String,
    onLongClick: (() -> Unit)? = null,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(52.dp)
            .combinedBounceClick(onLongClick = onLongClick, onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(SurfaceContainerLow)
                .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = iconUrl,
                contentDescription = name,
                modifier = Modifier.size(20.dp).clip(CircleShape),
                contentScale = ContentScale.Fit
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(name, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}



@Composable
fun NewsItemCard(news: NewsItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .bounceClick { onClick() }
            .background(SurfaceContainerLow)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (news.imageUrl.isNotEmpty()) {
            AsyncImage(
                model = news.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(10.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = news.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.sp
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
