package com.example.sanctum

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Close
import androidx.compose.foundation.border
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun NativeSearchScreen(
    query: String,
    results: List<SearchResult>,
    imageResults: List<ImageSearchResult>,
    videoResults: List<VideoSearchResult>,
    currentTab: String,
    isLoading: Boolean,
    hasNextPage: Boolean,
    isPaging: Boolean,
    onResultClick: (String) -> Unit,
    onLoadMore: () -> Unit,
    onTabClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFFFF)) // Google-like super light gray background
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        
        // Search Pill Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .background(Color.White, RoundedCornerShape(24.dp))
                .border(1.dp, Color(0xFFDFE1E5), RoundedCornerShape(24.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Color(0xFF9AA0A6),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = query,
                fontSize = 14.sp,
                fontFamily = Inter,
                color = Color(0xFF202124),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Clear",
                tint = Color(0xFF9AA0A6),
                modifier = Modifier.size(20.dp)
                // In a real app this would trigger an event to clear search
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        val tabs = listOf("All", "Images", "Videos", "News", "Maps")
        val selectedIndex = tabs.indexOf(currentTab).takeIf { it >= 0 } ?: 0
        
        // Navigation Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedIndex,
            containerColor = Color.Transparent,
            contentColor = Color(0xFF8C52FF),
            edgePadding = 16.dp,
            divider = {},
            indicator = { tabPositions ->
                SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                    color = Color(0xFF1A73E8) // Standard Blue
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = title == currentTab,
                    onClick = { if (title != currentTab) onTabClick(title) },
                    text = { 
                        Text(
                            text = title, 
                            fontFamily = Inter, 
                            fontWeight = if (title == currentTab) FontWeight.SemiBold else FontWeight.Medium,
                            color = if (title == currentTab) Color(0xFF1A73E8) else Color(0xFF5F6368),
                            fontSize = 13.sp
                        ) 
                    }
                )
            }
        }
        
        HorizontalDivider(color = Color(0xFFEBEBEB), thickness = 1.dp)

        if (isLoading && results.isEmpty() && imageResults.isEmpty() && videoResults.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF1A73E8))
            }
        } else if (currentTab == "Videos") {
            if (videoResults.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No videos found.", fontFamily = Inter, color = EditorialMutedInk)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(videoResults) { video ->
                        VideoSearchResultCard(video, onResultClick)
                    }
                }
            }
        } else if (currentTab == "Images") {
            if (imageResults.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No images found.", fontFamily = Inter, color = EditorialMutedInk)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(imageResults) { img ->
                        AsyncImage(
                            model = img.thumbnail,
                            contentDescription = img.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onResultClick(img.url) }
                        )
                    }
                }
            }
        } else if (results.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No results found.", fontFamily = Inter, color = EditorialMutedInk)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 48.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                itemsIndexed(results) { index, result ->
                    SearchResultCard(result, onResultClick)
                    if (index < results.size - 1) {
                        HorizontalDivider(
                            color = Color(0xFFEBEBEB), 
                            thickness = 1.dp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
                
                if (hasNextPage) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isPaging) {
                                CircularProgressIndicator(color = Color(0xFF1A73E8))
                            } else {
                                Button(
                                    onClick = onLoadMore,
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A73E8)),
                                    shape = RoundedCornerShape(24.dp),
                                    modifier = Modifier.height(40.dp)
                                ) {
                                    Text("Load more results", fontFamily = Inter, fontWeight = FontWeight.Medium, color = Color.White, fontSize = 14.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchResultCard(result: SearchResult, onClick: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .clickable { onClick(result.url) }
            .padding(vertical = 2.dp, horizontal = 4.dp)
    ) {
        // Breadcrumb URL
        Text(
            text = result.url.replace("https://", "").replace("http://", "").substringBefore("/"),
            fontSize = 11.sp,
            fontFamily = Inter,
            color = Color(0xFF202124), // Dark gray URL
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(2.dp))
        
        // Title (Google Title Blue)
        Text(
            text = result.title,
            fontSize = 16.sp, // Reduced from 18.sp
            fontWeight = FontWeight.Medium,
            fontFamily = Inter,
            color = Color(0xFF1A0DAB), // Standard blue
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(2.dp))
        
        // Snippet
        Text(
            text = result.snippet,
            fontSize = 12.sp, // Reduced from 13.sp
            fontFamily = Inter,
            color = Color(0xFF4D5156), // Standard gray
            lineHeight = 16.sp, // Reduced from 18.sp
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun VideoSearchResultCard(video: VideoSearchResult, onClick: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick(video.url) }
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            AsyncImage(
                model = video.thumbnail,
                contentDescription = video.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(8.dp))
            )
            // Duration overlay
            if (video.duration.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .background(Color.Black.copy(alpha = 0.8f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = video.duration,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontFamily = Inter,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = video.title,
            fontSize = 16.sp,
            fontFamily = Inter,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF202124),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        
        val viewsText = if (video.views > 0) {
            val v = video.views
            when {
                v >= 1000000 -> "${v / 1000000}M views"
                v >= 1000 -> "${v / 1000}K views"
                else -> "$v views"
            }
        } else ""
        
        val metaText = listOf(video.publisher, video.published, viewsText)
            .filter { it.isNotEmpty() }
            .joinToString(" • ")
            
        Text(
            text = metaText,
            fontSize = 13.sp,
            fontFamily = Inter,
            color = Color(0xFF5F6368),
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}