package com.example.sanctum

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sanctum.ui.theme.*

@Composable
fun SafetyShieldScreen(
    context: android.content.Context,
    totalBlockedCount: Int,
    adBlocking: Boolean,
    onAdBlockingChange: (Boolean) -> Unit,
    antiFingerprint: Boolean,
    onAntiFingerprintChange: (Boolean) -> Unit,
    httpsOnly: Boolean,
    onHttpsOnlyChange: (Boolean) -> Unit,
    shieldMode: ShieldMode,
    onShieldModeChange: (ShieldMode) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface)
    ) {
        // TopAppBar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Lock, contentDescription = "Security", tint = Primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sanctum", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Primary)
            }
            IconButton(onClick = { /* Menu */ }) {
                Icon(Icons.Default.MoreVert, contentDescription = "Menu", tint = OnSurfaceVariant)
            }
        }

        // Main Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .padding(bottom = 80.dp) // Clearance for Bottom Nav
        ) {
            // Status Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(PrimaryContainer)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background Glow Effects (Simulated)
                Box(
                    modifier = Modifier
                        .offset(x = 60.dp, y = (-40).dp)
                        .size(120.dp)
                        .blur(32.dp)
                        .background(Color.White.copy(alpha = 0.1f), CircleShape)
                )
                Box(
                    modifier = Modifier
                        .offset(x = (-40).dp, y = 40.dp)
                        .size(90.dp)
                        .blur(24.dp)
                        .background(Color.Black.copy(alpha = 0.1f), CircleShape)
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Security,
                        contentDescription = "Secure",
                        tint = OnPrimary,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Status: Secure",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "All protections are currently active and functioning normally.",
                        fontSize = 14.sp,
                        color = OnPrimary.copy(alpha = 0.9f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Stats Grid
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // Trackers Blocked
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .height(128.dp)
                        .background(SurfaceContainerLowest, RoundedCornerShape(12.dp))
                        .border(1.dp, OutlineVariant, RoundedCornerShape(12.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(Icons.Default.Block, contentDescription = "Blocked", tint = Primary)
                    Column {
                        Text(
                            totalBlockedCount.toString(),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Primary
                        )
                        Text(
                            "Trackers Blocked",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = OnSurfaceVariant
                        )
                    }
                }

                // Filtering Active
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .height(128.dp)
                        .background(SurfaceContainerLowest, RoundedCornerShape(12.dp))
                        .border(1.dp, OutlineVariant, RoundedCornerShape(12.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(Icons.Default.FilterAlt, contentDescription = "Filter", tint = Primary)
                    Column {
                        Text(
                            shieldMode.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Primary
                        )
                        Text(
                            "Filtering Active",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = OnSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Legacy Settings Switches integration
            Text(
                "Protection Settings",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = OnSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceContainerLowest, RoundedCornerShape(12.dp))
                    .border(1.dp, OutlineVariant, RoundedCornerShape(12.dp))
            ) {
                // Ad Blocking
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(40.dp).background(ErrorContainer, CircleShape), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.MoneyOff, contentDescription = null, tint = OnErrorContainer, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("Ad Blocking", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = OnSurface)
                            Text("Block intrusive advertisements", fontSize = 12.sp, color = OnSurfaceVariant)
                        }
                    }
                    Switch(
                        checked = adBlocking,
                        onCheckedChange = onAdBlockingChange,
                        colors = SwitchDefaults.colors(checkedThumbColor = SurfaceContainerLowest, checkedTrackColor = Primary)
                    )
                }
                HorizontalDivider(color = OutlineVariant.copy(alpha = 0.3f))
                
                // Anti-Fingerprinting
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(40.dp).background(SurfaceVariant, CircleShape), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Fingerprint, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("Anti-Fingerprint", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = OnSurface)
                            Text("Prevent cross-site tracking", fontSize = 12.sp, color = OnSurfaceVariant)
                        }
                    }
                    Switch(
                        checked = antiFingerprint,
                        onCheckedChange = onAntiFingerprintChange,
                        colors = SwitchDefaults.colors(checkedThumbColor = SurfaceContainerLowest, checkedTrackColor = Primary)
                    )
                }
                HorizontalDivider(color = OutlineVariant.copy(alpha = 0.3f))
                
                // HTTPS Only
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(40.dp).background(SurfaceVariant, CircleShape), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Https, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("HTTPS Only", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = OnSurface)
                            Text("Force encrypted connections", fontSize = 12.sp, color = OnSurfaceVariant)
                        }
                    }
                    Switch(
                        checked = httpsOnly,
                        onCheckedChange = onHttpsOnlyChange,
                        colors = SwitchDefaults.colors(checkedThumbColor = SurfaceContainerLowest, checkedTrackColor = Primary)
                    )
                }
            }
        }
    }
}
