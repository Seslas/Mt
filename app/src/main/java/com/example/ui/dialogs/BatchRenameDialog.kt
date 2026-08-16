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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun BatchRenameDialog(
    onDismiss: () -> Unit,
    onApply: (pattern: String, replacement: String, addCounter: Boolean) -> Unit
) {
    var pattern by remember { mutableStateOf("") }
    var replacement by remember { mutableStateOf("") }
    var addCounter by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MtDarkSurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.FindReplace, contentDescription = null, tint = MtGold)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Toplu Regex Yeniden Adlandırma (VIP)", color = MtTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Düzenli ifade (Regex) kullanarak seçili dosya veya tüm klasördeki dosyaları tek adımda yeniden adlandırın.",
                    color = MtTextMuted,
                    fontSize = 11.sp
                )

                TextField(
                    value = pattern,
                    onValueChange = { pattern = it },
                    label = { Text("Eşleşen Desen (Regex / Metin)") },
                    placeholder = { Text("örn. mod_.* veya _v1") },
                    modifier = Modifier.fillMaxWidth().testTag("batch_pattern_input"),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MtDarkBg,
                        unfocusedContainerColor = MtDarkBg,
                        focusedTextColor = MtTextPrimary,
                        unfocusedTextColor = MtTextPrimary,
                        focusedIndicatorColor = MtGold
                    )
                )

                TextField(
                    value = replacement,
                    onValueChange = { replacement = it },
                    label = { Text("Yeni Metin / Değiştirici") },
                    placeholder = { Text("örn. target_v2") },
                    modifier = Modifier.fillMaxWidth().testTag("batch_replacement_input"),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MtDarkBg,
                        unfocusedContainerColor = MtDarkBg,
                        focusedTextColor = MtTextPrimary,
                        unfocusedTextColor = MtTextPrimary,
                        focusedIndicatorColor = MtCyan
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = addCounter,
                        onCheckedChange = { addCounter = it },
                        colors = CheckboxDefaults.colors(checkedColor = MtGold, checkmarkColor = Color(0xFF1B1200))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sıralı Sayaç Ekle (_1, _2, _3...)", color = MtTextPrimary, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onApply(pattern, replacement, addCounter)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = MtGold, contentColor = Color(0xFF1B1200)),
                modifier = Modifier.testTag("btn_apply_batch_rename")
            ) {
                Text("Uygula (VIP)", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal", color = MtTextSecondary)
            }
        }
    )
}
