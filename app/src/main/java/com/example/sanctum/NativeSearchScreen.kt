package com.example.sanctum

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NativeSearchScreen(
    query: String,
    results: List<SearchResult>,
    isLoading: Boolean,
    onResultClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EditorialPaper) // Using the slate background
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Search Results for \"$query\"",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Inter,
            color = EditorialInk
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = SecondaryAccent)
            }
        } else if (results.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "No results found.",
                    fontFamily = Inter,
                    color = EditorialMutedInk
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(results) { result ->
                    SearchResultCard(result, onResultClick)
                }
            }
        }
    }
}

@Composable
fun SearchResultCard(result: SearchResult, onClick: (String) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(EditorialSurface)
            .border(1.dp, EditorialBorder, RoundedCornerShape(12.dp))
            .clickable { onClick(result.url) }
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = result.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = Inter,
                color = EditorialInk,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = result.url,
                fontSize = 12.sp,
                fontFamily = Inter,
                color = SecondaryAccent,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = result.snippet,
                fontSize = 14.sp,
                fontFamily = Inter,
                color = EditorialMutedInk,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
