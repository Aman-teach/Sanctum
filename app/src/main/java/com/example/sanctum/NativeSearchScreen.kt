package com.example.sanctum

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Results for \"$query\"",
            fontSize = 18.sp,
            fontFamily = Inter,
            color = EditorialMutedInk,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
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
                    color = Color(0xFF8C52FF)
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
                            fontWeight = if (title == currentTab) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (title == currentTab) Color(0xFF8C52FF) else EditorialMutedInk
                        ) 
                    }
                )
            }
        }
        
        HorizontalDivider(color = Color(0xFF8C52FF).copy(alpha = 0.2f), thickness = 1.dp)

        if (isLoading && results.isEmpty() && imageResults.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF8C52FF))
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
                                .clickable { onResultClick(img.image) }
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
                            color = Color(0xFF8C52FF).copy(alpha = 0.2f), 
                            thickness = 1.dp,
                            modifier = Modifier.padding(vertical = 12.dp)
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
                                CircularProgressIndicator(color = Color(0xFF8C52FF))
                            } else {
                                Button(
                                    onClick = onLoadMore,
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8C52FF)),
                                    shape = RoundedCornerShape(24.dp),
                                    modifier = Modifier.height(48.dp)
                                ) {
                                    Text("Load more results", fontFamily = Inter, fontWeight = FontWeight.Medium, color = Color.White)
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
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick(result.url) }
            .padding(vertical = 4.dp, horizontal = 4.dp)
    ) {
        // Breadcrumb URL
        Text(
            text = result.url.replace("https://", "").replace("http://", "").substringBefore("/"),
            fontSize = 12.sp,
            fontFamily = Inter,
            color = Color(0xFF8C52FF), // Google Dark
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(4.dp))
        
        // Title (Pro Blue)
        Text(
            text = result.title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = Inter,
            color = Color(0xFF8C52FF), // Google Title Blue
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(4.dp))
        
        // Snippet
        Text(
            text = result.snippet,
            fontSize = 14.sp,
            fontFamily = Inter,
            color = Color(0xFF8C52FF).copy(alpha = 0.8f), // Google Snippet Gray
            lineHeight = 20.sp,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
    }
}