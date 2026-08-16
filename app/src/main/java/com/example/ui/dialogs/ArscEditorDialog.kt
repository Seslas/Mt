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
import com.example.engine.ArscEditorEngine
import com.example.model.ArscResourceEntry
import com.example.ui.theme.*

@Composable
fun ArscEditorDialog(
    arscEngine: ArscEditorEngine,
    onDismiss: () -> Unit
) {
    val resources by arscEngine.resources.collectAsState()
    val currentFilter by arscEngine.currentFilter.collectAsState()
    val searchQuery by arscEngine.searchQuery.collectAsState()
    val translationTask by arscEngine.translationTask.collectAsState()

    var editingEntry by remember { mutableStateOf<ArscResourceEntry?>(null) }
    var toastMessage by remember { mutableStateOf<String?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .testTag("arsc_editor_dialog"),
            shape = RoundedCornerShape(16.dp),
            color = MtDarkBg,
            border = androidx.compose.foundation.BorderStroke(1.dp, MtCyan.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Surface(
                    color = MtDarkSurface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(ColorArsc.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Style,
                                contentDescription = null,
                                tint = ColorArsc,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "ARSC & XML Çevirmen",
                                    color = MtTextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = MtGoldContainer
                                ) {
                                    Text(
                                        text = "VIP PRO",
                                        color = MtGold,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Text(
                                text = "resources.arsc (Kaynak Tablosu & Dize Yerelleştirme)",
                                color = MtTextMuted,
                                fontSize = 11.sp
                            )
                        }

                        IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Filled.Close, contentDescription = "Kapat", tint = MtTextSecondary)
                        }
                    }
                }

                // Fast VIP Action Toolbar
                Surface(
                    color = MtDarkSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                val count = arscEngine.autoTranslateAllToTurkish()
                                toastMessage = "⚡ $count İngilizce dize otomatik Türkçeye çevrildi!"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MtGold, contentColor = Color(0xFF1B1200)),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.weight(1f).testTag("btn_auto_translate")
                        ) {
                            Icon(Icons.Filled.Translate, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Otomatik Türkçe Çevir (VIP)", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                val cleaned = arscEngine.cleanUnusedResources()
                                toastMessage = "✅ Kaynak tablosu temizlendi ve optimize edildi!"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MtCyan, contentColor = Color(0xFF001B20)),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.weight(1f).testTag("btn_clean_resources")
                        ) {
                            Icon(Icons.Filled.CleaningServices, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Kaynakları Temizle", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Search Bar
                TextField(
                    value = searchQuery,
                    onValueChange = { arscEngine.setSearchQuery(it) },
                    placeholder = { Text("Dize adı veya metin ara...", fontSize = 12.sp, color = MtTextMuted) },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp).testTag("arsc_search_input"),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MtDarkSurface,
                        unfocusedContainerColor = MtDarkSurface,
                        focusedTextColor = MtTextPrimary,
                        unfocusedTextColor = MtTextPrimary,
                        focusedIndicatorColor = MtCyan,
                        unfocusedIndicatorColor = MtDivider
                    ),
                    singleLine = true
                )

                // Toast banner if active
                if (toastMessage != null) {
                    Surface(
                        color = MtGoldContainer,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = MtGold, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = toastMessage ?: "", color = MtGoldLight, fontSize = 11.sp, modifier = Modifier.weight(1f))
                            IconButton(onClick = { toastMessage = null }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Filled.Close, contentDescription = null, tint = MtGold, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }

                // Resources List
                val filteredResources = resources.filter {
                    (searchQuery.isBlank() || it.name.contains(searchQuery, ignoreCase = true) || it.originalValue.contains(searchQuery, ignoreCase = true) || it.translatedValue.contains(searchQuery, ignoreCase = true))
                }

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                ) {
                    items(filteredResources, key = { it.id }) { entry ->
                        Surface(
                            color = MtDarkSurface,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { editingEntry = entry }
                                .testTag("arsc_entry_${entry.name}")
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = ColorArsc.copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = entry.type,
                                                color = ColorArsc,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = entry.name,
                                            color = MtTextPrimary,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                    Text(
                                        text = entry.id,
                                        color = MtTextMuted,
                                        fontSize = 10.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "EN: ${entry.originalValue}",
                                    color = MtTextSecondary,
                                    fontSize = 11.sp
                                )

                                if (entry.translatedValue.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "TR: ${entry.translatedValue}",
                                        color = MtGold,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Edit String Dialog
    if (editingEntry != null) {
        var editOriginal by remember(editingEntry) { mutableStateOf(editingEntry?.originalValue ?: "") }
        var editTranslated by remember(editingEntry) { mutableStateOf(editingEntry?.translatedValue ?: "") }

        AlertDialog(
            onDismissRequest = { editingEntry = null },
            containerColor = MtDarkSurface,
            title = {
                Text("Dizeyi Düzenle: ${editingEntry?.name}", color = MtTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Orijinal Metin (EN):", color = MtTextSecondary, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    TextField(
                        value = editOriginal,
                        onValueChange = { editOriginal = it },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MtDarkBg,
                            unfocusedContainerColor = MtDarkBg,
                            focusedTextColor = MtTextPrimary,
                            unfocusedTextColor = MtTextPrimary,
                            focusedIndicatorColor = MtCyan
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Türkçe Çeviri (TR):", color = MtGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    TextField(
                        value = editTranslated,
                        onValueChange = { editTranslated = it },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MtDarkBg,
                            unfocusedContainerColor = MtDarkBg,
                            focusedTextColor = MtTextPrimary,
                            unfocusedTextColor = MtTextPrimary,
                            focusedIndicatorColor = MtGold
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        editingEntry?.let { entry ->
                            arscEngine.updateResourceEntry(entry.id, editOriginal, editTranslated)
                        }
                        editingEntry = null
                        toastMessage = "Dize başarıyla güncellendi!"
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MtGold, contentColor = Color(0xFF1B1200))
                ) {
                    Text("Kaydet", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { editingEntry = null }) {
                    Text("İptal", color = MtTextSecondary)
                }
            }
        )
    }
}
