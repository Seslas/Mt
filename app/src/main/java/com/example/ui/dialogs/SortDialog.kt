package com.example.ui.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SortMode
import com.example.ui.theme.*

@Composable
fun SortDialog(
    currentSort: SortMode,
    onDismiss: () -> Unit,
    onSelectSort: (SortMode) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MtDarkSurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Sort, contentDescription = null, tint = MtGold)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Dosya Sıralama", color = MtTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(SortMode.values()) { mode ->
                    val isSelected = currentSort == mode
                    Surface(
                        color = if (isSelected) MtDarkSurfaceHighlight else MtDarkBg,
                        shape = RoundedCornerShape(8.dp),
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, MtGold) else null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clickable {
                                onSelectSort(mode)
                                onDismiss()
                            }
                            .testTag("sort_option_${mode.name}")
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = {
                                    onSelectSort(mode)
                                    onDismiss()
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = MtGold)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = mode.titleTr,
                                color = if (isSelected) MtGold else MtTextPrimary,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Kapat", color = MtTextSecondary)
            }
        }
    )
}
