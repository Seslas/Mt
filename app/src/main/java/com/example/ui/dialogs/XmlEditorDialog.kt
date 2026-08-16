package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.*

@Composable
fun XmlEditorDialog(
    fileName: String,
    initialContent: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var content by remember { mutableStateOf(initialContent) }
    var toastMsg by remember { mutableStateOf<String?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .testTag("xml_editor_dialog"),
            shape = RoundedCornerShape(16.dp),
            color = MtDarkBg,
            border = androidx.compose.foundation.BorderStroke(1.dp, ColorXml.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Surface(color = MtDarkSurface, modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Description, contentDescription = null, tint = ColorXml, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = fileName, color = MtTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(shape = RoundedCornerShape(4.dp), color = MtDarkSurfaceHighlight) {
                                    Text("AXML", color = ColorXml, fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                }
                            }
                            Text(text = "Binary XML Decompiled", color = MtTextMuted, fontSize = 10.sp)
                        }

                        Button(
                            onClick = {
                                onSave(content)
                                toastMsg = "XML dosyası kaydedildi!"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MtGold, contentColor = Color(0xFF1B1200)),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("btn_save_xml")
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

                if (toastMsg != null) {
                    Text(
                        text = toastMsg ?: "",
                        color = MtGreen,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    color = MtDarkBg,
                    modifier = Modifier.weight(1f).fillMaxWidth().padding(8.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MtDivider)
                ) {
                    TextField(
                        value = content,
                        onValueChange = { content = it },
                        modifier = Modifier.fillMaxSize().testTag("xml_text_editor"),
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
