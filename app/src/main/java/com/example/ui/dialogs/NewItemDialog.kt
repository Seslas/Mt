package com.example.ui.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun NewItemDialog(
    onDismiss: () -> Unit,
    onCreateFolder: (String) -> Unit,
    onCreateFile: (String) -> Unit
) {
    var isFolder by remember { mutableStateOf(true) }
    var nameInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MtDarkSurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isFolder) Icons.Filled.CreateNewFolder else Icons.Filled.NoteAdd,
                    contentDescription = null,
                    tint = if (isFolder) ColorFolder else MtCyan
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isFolder) "Yeni Klasör Oluştur" else "Yeni Dosya Oluştur",
                    color = MtTextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = isFolder,
                        onClick = { isFolder = true },
                        label = { Text("Klasör", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ColorFolder.copy(alpha = 0.2f),
                            selectedLabelColor = ColorFolder
                        )
                    )

                    FilterChip(
                        selected = !isFolder,
                        onClick = { isFolder = false },
                        label = { Text("Dosya", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MtCyan.copy(alpha = 0.2f),
                            selectedLabelColor = MtCyan
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                TextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    placeholder = { Text(if (isFolder) "Klasör adı..." else "Dosya adı (örn. script.sh, patch.smali)...", fontSize = 12.sp, color = MtTextMuted) },
                    modifier = Modifier.fillMaxWidth().testTag("new_item_name_input"),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MtDarkBg,
                        unfocusedContainerColor = MtDarkBg,
                        focusedTextColor = MtTextPrimary,
                        unfocusedTextColor = MtTextPrimary,
                        focusedIndicatorColor = if (isFolder) ColorFolder else MtCyan
                    ),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nameInput.isNotBlank()) {
                        if (isFolder) onCreateFolder(nameInput.trim()) else onCreateFile(nameInput.trim())
                        onDismiss()
                    }
                },
                enabled = nameInput.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = MtGold, contentColor = Color(0xFF1B1200)),
                modifier = Modifier.testTag("btn_confirm_create_item")
            ) {
                Text("Oluştur", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal", color = MtTextSecondary)
            }
        }
    )
}
