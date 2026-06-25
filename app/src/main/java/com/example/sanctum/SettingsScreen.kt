package com.example.sanctum

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sanctum.ui.theme.*

@Composable
fun SettingsScreen(
    context: android.content.Context,
    familyMode: Boolean,
    onFamilyModeChange: (Boolean) -> Unit,
    onClearData: () -> Unit,
    onBack: () -> Unit
) {
    val searchEngine by PreferencesManager.searchEngine.collectAsState()
    val thirdPartyCookies by PreferencesManager.thirdPartyCookies.collectAsState()
    val doNotTrack by PreferencesManager.doNotTrack.collectAsState()
    val themeMode by PreferencesManager.theme.collectAsState()

    var showSearchEngineDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showClearDataDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Header
        Text(
            text = "Settings",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = OnSurface,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = "Manage your browser preferences and account details.",
            fontSize = 14.sp,
            color = OnSurfaceVariant,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // General Section
        SettingsSectionHeader("General")
        SettingsCard {
            SettingsRow(
                icon = Icons.Default.AccountCircle,
                title = "Sync and Google Services",
                subtitle = "Not logged in",
                onClick = { android.widget.Toast.makeText(context, "Coming Soon", android.widget.Toast.LENGTH_SHORT).show() }
            )
            Divider(color = SurfaceVariant, modifier = Modifier.padding(start = 56.dp))
            SettingsRow(
                icon = Icons.Default.Search,
                title = "Search Engine",
                value = searchEngine,
                onClick = { showSearchEngineDialog = true }
            )
            Divider(color = SurfaceVariant, modifier = Modifier.padding(start = 56.dp))
            SettingsRow(
                icon = Icons.Default.Lock,
                title = "Password Manager",
                onClick = { android.widget.Toast.makeText(context, "Coming Soon", android.widget.Toast.LENGTH_SHORT).show() }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Privacy & Security Section
        SettingsSectionHeader("Privacy & Security")
        SettingsCard {
            SettingsRow(
                icon = Icons.Default.History,
                title = "Clear Browsing Data",
                onClick = { showClearDataDialog = true }
            )
            Divider(color = SurfaceVariant, modifier = Modifier.padding(start = 56.dp))
            SettingsToggleRow(
                icon = Icons.Default.Cookie,
                title = "Third-party Cookies",
                subtitle = "Allow sites to save and read cookie data",
                checked = thirdPartyCookies,
                onCheckedChange = { PreferencesManager.setThirdPartyCookies(it) }
            )
            Divider(color = SurfaceVariant, modifier = Modifier.padding(start = 56.dp))
            SettingsToggleRow(
                icon = Icons.Default.DoNotDisturb,
                title = "Do Not Track",
                subtitle = "Send a \"Do Not Track\" request",
                checked = doNotTrack,
                onCheckedChange = { PreferencesManager.setDoNotTrack(it) }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Appearance Section
        SettingsSectionHeader("Appearance")
        SettingsCard {
            SettingsRow(
                icon = Icons.Default.DarkMode,
                title = "Theme",
                value = themeMode,
                onClick = { showThemeDialog = true }
            )
            Divider(color = SurfaceVariant, modifier = Modifier.padding(start = 56.dp))
            SettingsToggleRow(
                icon = Icons.Default.FamilyRestroom,
                title = "Family Mode",
                subtitle = "Enforce safe search and strict blocking",
                checked = familyMode,
                onCheckedChange = onFamilyModeChange
            )
        }
        
        Spacer(modifier = Modifier.height(48.dp))

        // Dialogs
        if (showSearchEngineDialog) {
            AlertDialog(
                onDismissRequest = { showSearchEngineDialog = false },
                title = { Text("Search Engine") },
                text = {
                    Column {
                        listOf("Google", "DuckDuckGo", "Bing").forEach { engine ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        PreferencesManager.setSearchEngine(engine)
                                        showSearchEngineDialog = false
                                    }
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = searchEngine == engine, onClick = null)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(engine, fontSize = 16.sp)
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showSearchEngineDialog = false }) { Text("Close") }
                }
            )
        }

        if (showThemeDialog) {
            AlertDialog(
                onDismissRequest = { showThemeDialog = false },
                title = { Text("Theme") },
                text = {
                    Column {
                        listOf("System", "Light", "Dark").forEach { t ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        PreferencesManager.setTheme(t)
                                        showThemeDialog = false
                                    }
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = themeMode == t, onClick = null)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(t, fontSize = 16.sp)
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showThemeDialog = false }) { Text("Close") }
                }
            )
        }

        if (showClearDataDialog) {
            AlertDialog(
                onDismissRequest = { showClearDataDialog = false },
                title = { Text("Clear Browsing Data") },
                text = { Text("This will clear your browsing history, cache, and cookies. This action cannot be undone.") },
                confirmButton = {
                    TextButton(onClick = {
                        onClearData()
                        showClearDataDialog = false
                        android.widget.Toast.makeText(context, "Data cleared", android.widget.Toast.LENGTH_SHORT).show()
                    }) {
                        Text("Clear Data", color = ErrorColor)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearDataDialog = false }) { Text("Cancel") }
                }
            )
        }
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = Primary,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
    )
}

@Composable
fun SettingsCard(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceContainerLowest)
    ) {
        content()
    }
}

@Composable
fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    value: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = OnSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 16.sp, color = OnSurface)
            if (subtitle != null) {
                Text(text = subtitle, fontSize = 12.sp, color = OnSurfaceVariant)
            }
        }
        if (value != null) {
            Text(text = value, fontSize = 14.sp, color = OnSurfaceVariant, modifier = Modifier.padding(end = 8.dp))
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Outline,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun SettingsToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = OnSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 16.sp, color = OnSurface)
            if (subtitle != null) {
                Text(text = subtitle, fontSize = 12.sp, color = OnSurfaceVariant)
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Primary,
                uncheckedThumbColor = Outline,
                uncheckedTrackColor = SurfaceContainerHighest
            )
        )
    }
}
