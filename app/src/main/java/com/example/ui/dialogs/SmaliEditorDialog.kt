package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.*

@Composable
fun SmaliEditorDialog(
    fileName: String,
    initialContent: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var content by remember { mutableStateOf(initialContent) }
    var searchStr by remember { mutableStateOf("") }
    var isSearchOpen by remember { mutableStateOf(false) }
    var toastMsg by remember { mutableStateOf<String?>(null) }

    val quickOpcodes = listOf(
        "const/4 v0, 0x1",
        "const/4 v0, 0x0",
        "return v0",
        "return-void",
        "const-string v0, \"...\"",
        "invoke-virtual",
        "invoke-static",
        "sget-boolean",
        "if-eqz v0, :cond",
        "goto :goto_0"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .testTag("smali_editor_dialog"),
            shape = RoundedCornerShape(16.dp),
            color = MtDarkBg,
            border = androidx.compose.foundation.BorderStroke(1.dp, MtCyan.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top bar
                Surface(color = MtDarkSurface, modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Code, contentDescription = null, tint = ColorSmali, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = fileName, color = MtTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(shape = RoundedCornerShape(4.dp), color = MtGoldContainer) {
                                    Text("SMALI VIP", color = MtGold, fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                }
                            }
                            Text(text = "${content.lines().size} satır bytecode", color = MtTextMuted, fontSize = 10.sp)
                        }

                        IconButton(onClick = { isSearchOpen = !isSearchOpen }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Filled.Search, contentDescription = "Bul", tint = MtTextSecondary)
                        }

                        Button(
                            onClick = {
                                onSave(content)
                                toastMsg = "Smali dosyası kaydedildi!"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MtGold, contentColor = Color(0xFF1B1200)),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("btn_save_smali_file")
                        ) {
                            Icon(Icons.Filled.Save, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Kaydet", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Filled.Close, contentDescription = "Kapat", tint = MtTextSecondary)
                        }
                    }
                }

                // Search Row
                if (isSearchOpen) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MtDarkSurfaceVariant)
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = searchStr,
                            onValueChange = { searchStr = it },
                            placeholder = { Text("Smali içinde ara...", fontSize = 11.sp, color = MtTextMuted) },
                            modifier = Modifier.weight(1f),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedTextColor = MtTextPrimary,
                                unfocusedTextColor = MtTextPrimary,
                                focusedIndicatorColor = MtCyan
                            ),
                            singleLine = true
                        )
                    }
                }

                // Quick Opcode Bar
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MtDarkSurfaceHighlight)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(quickOpcodes) { op ->
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MtDarkSurfaceVariant,
                            modifier = Modifier.clickable {
                                content = "$content\n$op"
                                toastMsg = "Eklendi: $op"
                            }
                        ) {
                            Text(
                                text = op,
                                color = MtCyan,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                if (toastMsg != null) {
                    Text(
                        text = toastMsg ?: "",
                        color = MtGold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                    )
                }

                // Editor Canvas
                Surface(
                    color = MtDarkBg,
                    modifier = Modifier.weight(1f).fillMaxWidth().padding(8.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MtDivider)
                ) {
                    TextField(
                        value = content,
                        onValueChange = { content = it },
                        modifier = Modifier.fillMaxSize().testTag("smali_main_editor"),
                        textStyle = LocalTextStyle.current.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = MtTextPrimary,
                            lineHeight = 16.sp
                        ),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }
    }
}
