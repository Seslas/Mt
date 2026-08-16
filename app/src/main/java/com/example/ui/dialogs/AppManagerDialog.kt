package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.*

data class InstalledAppInfo(
    val appName: String,
    val packageName: String,
    val versionName: String,
    val versionCode: Int,
    val apkSize: String,
    val isSystemApp: Boolean
)

@Composable
fun AppManagerDialog(
    onDismiss: () -> Unit,
    onExtractApk: (appName: String, pkgName: String) -> Unit
) {
    val sampleApps = remember {
        listOf(
            InstalledAppInfo("Target 3D Game Mod", "com.target.game", "v2.4.1", 104, "34.5 MB", false),
            InstalledAppInfo("Spotify Music Pro", "com.spotify.music", "v8.9.12", 240, "48.2 MB", false),
            InstalledAppInfo("YouTube Revanced", "com.google.android.youtube", "v19.11.38", 812, "82.1 MB", false),
            InstalledAppInfo("Telegram Messenger", "org.telegram.messenger", "v10.8.2", 4120, "65.4 MB", false),
            InstalledAppInfo("Instagram Mod", "com.instagram.android", "v315.0.0", 521, "58.7 MB", false),
            InstalledAppInfo("WhatsApp Messenger", "com.whatsapp", "v2.24.5", 941, "42.1 MB", false),
            InstalledAppInfo("Google Chrome", "com.android.chrome", "v122.0.6261", 6261, "98.3 MB", false),
            InstalledAppInfo("System UI", "com.android.systemui", "v14.0", 34, "12.4 MB", true),
            InstalledAppInfo("Android System", "android", "v14.0", 34, "48.1 MB", true),
            InstalledAppInfo("Settings", "com.android.settings", "v14.0", 34, "18.2 MB", true)
        )
    }

    var searchQuery by remember { mutableStateOf("") }
    var showOnlyUserApps by remember { mutableStateOf(true) }
    var toastMsg by remember { mutableStateOf<String?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .testTag("app_manager_dialog"),
            shape = RoundedCornerShape(16.dp),
            color = MtDarkBg,
            border = androidx.compose.foundation.BorderStroke(1.dp, MtCyan.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Header
                Surface(color = MtDarkSurface, modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(ColorApk.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Android, contentDescription = null, tint = ColorApk, modifier = Modifier.size(18.dp))
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Uygulama Yöneticisi & APK Çıkarıcı", color = MtTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Cihazdaki uygulamaları yedekleyin ve APK dosyalarını çıkarın", color = MtTextMuted, fontSize = 10.sp)
                        }

                        IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Filled.Close, contentDescription = "Kapat", tint = MtTextSecondary)
                        }
                    }
                }

                // Search & Filter Bar
                Surface(color = MtDarkSurfaceVariant, modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Uygulama adı veya paket adı ara...", fontSize = 11.sp, color = MtTextMuted) },
                            modifier = Modifier.fillMaxWidth().testTag("app_search_input"),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MtDarkSurface,
                                unfocusedContainerColor = MtDarkSurface,
                                focusedTextColor = MtTextPrimary,
                                unfocusedTextColor = MtTextPrimary,
                                focusedIndicatorColor = MtCyan
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                FilterChip(
                                    selected = showOnlyUserApps,
                                    onClick = { showOnlyUserApps = true },
                                    label = { Text("Kullanıcı Uygulamaları", fontSize = 10.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MtGoldContainer,
                                        selectedLabelColor = MtGold
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                FilterChip(
                                    selected = !showOnlyUserApps,
                                    onClick = { showOnlyUserApps = false },
                                    label = { Text("Tümü (Sistem Dahil)", fontSize = 10.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MtCyan.copy(alpha = 0.2f),
                                        selectedLabelColor = MtCyan
                                    )
                                )
                            }
                        }
                    }
                }

                if (toastMsg != null) {
                    Text(
                        text = toastMsg ?: "",
                        color = MtGreen,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                    )
                }

                // Apps list
                val filteredApps = sampleApps.filter { app ->
                    (!showOnlyUserApps || !app.isSystemApp) &&
                            (searchQuery.isBlank() || app.appName.contains(searchQuery, ignoreCase = true) || app.packageName.contains(searchQuery, ignoreCase = true))
                }

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    items(filteredApps) { app ->
                        Surface(
                            color = MtDarkSurface,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(ColorApk.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Filled.Android, contentDescription = null, tint = ColorApk, modifier = Modifier.size(20.dp))
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = app.appName, color = MtTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        if (app.isSystemApp) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Surface(shape = RoundedCornerShape(3.dp), color = MtDarkSurfaceHighlight) {
                                                Text("Sistem", color = MtTextMuted, fontSize = 8.sp, modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp))
                                            }
                                        }
                                    }
                                    Text(text = app.packageName, color = MtTextMuted, fontSize = 10.sp)
                                    Text(text = "${app.versionName} (Build ${app.versionCode})  |  ${app.apkSize}", color = MtCyan, fontSize = 10.sp)
                                }

                                Button(
                                    onClick = {
                                        onExtractApk(app.appName, app.packageName)
                                        toastMsg = "✅ ${app.appName} APK'sı mevcut dizine çıkarıldı!"
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MtGoldContainer, contentColor = MtGold),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.testTag("btn_extract_${app.packageName}")
                                ) {
                                    Icon(Icons.Filled.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("APK Çıkar", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
