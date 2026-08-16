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
import com.example.model.BookmarkItem
import com.example.ui.theme.*

@Composable
fun BookmarksDialog(
    bookmarks: List<BookmarkItem>,
    onDismiss: () -> Unit,
    onSelectBookmark: (BookmarkItem) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MtDarkSurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Bookmark, contentDescription = null, tint = MtGold)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Hızlı Konumlar & Yer İmleri", color = MtTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(bookmarks) { b ->
                    Surface(
                        color = MtDarkBg,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clickable {
                                onSelectBookmark(b)
                                onDismiss()
                            }
                            .testTag("bookmark_${b.name}")
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (b.isSystem) Icons.Filled.FolderSpecial else Icons.Filled.Folder,
                                contentDescription = null,
                                tint = if (b.isSystem) MtGold else MtCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = b.name, color = MtTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Text(text = b.path, color = MtTextMuted, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Kapat", color = MtGold)
            }
        }
    )
}
