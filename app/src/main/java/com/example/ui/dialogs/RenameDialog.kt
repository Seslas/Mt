package com.example.ui.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DriveFileRenameOutline
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
fun RenameDialog(
    currentName: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var newName by remember { mutableStateOf(currentName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MtDarkSurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.DriveFileRenameOutline, contentDescription = null, tint = MtGold)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Yeniden Adlandır", color = MtTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                TextField(
                    value = newName,
                    onValueChange = { newName = it },
                    placeholder = { Text("Yeni dosya adı...", fontSize = 12.sp, color = MtTextMuted) },
                    modifier = Modifier.fillMaxWidth().testTag("rename_input_field"),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MtDarkBg,
                        unfocusedContainerColor = MtDarkBg,
                        focusedTextColor = MtTextPrimary,
                        unfocusedTextColor = MtTextPrimary,
                        focusedIndicatorColor = MtGold
                    ),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (newName.isNotBlank()) {
                        onConfirm(newName.trim())
                        onDismiss()
                    }
                },
                enabled = newName.isNotBlank() && newName != currentName,
                colors = ButtonDefaults.buttonColors(containerColor = MtGold, contentColor = Color(0xFF1B1200)),
                modifier = Modifier.testTag("btn_confirm_rename")
            ) {
                Text("Değiştir", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal", color = MtTextSecondary)
            }
        }
    )
}
