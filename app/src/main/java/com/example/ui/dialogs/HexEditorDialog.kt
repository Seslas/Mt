package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
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
fun HexEditorDialog(
    fileName: String,
    initialHex: String = "50 4B 03 04 14 00 08 08 08 00 23 8A 61 54 00 00 00 00 00 00 00 00 00 00 00 00 14 00 00 00 41 6E 64 72 6F 69 64 4D 61 6E 69 66 65 73 74 2E 78 6D 6C",
    onDismiss: () -> Unit
) {
    val hexBytes = remember {
        initialHex.split(" ").filter { it.isNotBlank() }.toMutableStateList()
    }

    var selectedByteIndex by remember { mutableStateOf<Int?>(null) }
    var editByteVal by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .testTag("hex_editor_dialog"),
            shape = RoundedCornerShape(16.dp),
            color = MtDarkBg,
            border = androidx.compose.foundation.BorderStroke(1.dp, MtPurple.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Surface(color = MtDarkSurface, modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Memory, contentDescription = null, tint = MtPurple, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "Hex Düzenleyici Pro", color = MtTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(shape = RoundedCornerShape(4.dp), color = MtGoldContainer) {
                                    Text("VIP", color = MtGold, fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                }
                            }
                            Text(text = "$fileName (${hexBytes.size} Bayt)", color = MtTextMuted, fontSize = 10.sp)
                        }

                        IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Filled.Close, contentDescription = "Kapat", tint = MtTextSecondary)
                        }
                    }
                }

                // Hex Column Header
                Surface(color = MtDarkSurfaceHighlight, modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Ofset", color = MtGold, fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        Text("00 01 02 03 04 05 06 07", color = MtCyan, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        Text("ASCII Metin", color = MtGreen, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    }
                }

                // Hex Rows
                val rows = hexBytes.chunked(8)

                LazyColumn(modifier = Modifier.weight(1f).padding(8.dp)) {
                    itemsIndexed(rows) { rowIndex, chunk ->
                        val offsetStr = String.format("%08X", rowIndex * 8)
                        val hexChunkStr = chunk.joinToString(" ").padEnd(23, ' ')
                        val asciiStr = chunk.map { byteHex ->
                            try {
                                val charCode = byteHex.toInt(16)
                                if (charCode in 32..126) charCode.toChar() else '.'
                            } catch (e: Exception) {
                                '.'
                            }
                        }.joinToString("")

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .background(if (rowIndex % 2 == 0) MtDarkSurface else MtDarkSurfaceVariant, RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = offsetStr, color = MtGoldLight, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                            Text(text = hexChunkStr, color = MtTextPrimary, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                            Text(text = asciiStr, color = MtGreen, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }
    }
}
