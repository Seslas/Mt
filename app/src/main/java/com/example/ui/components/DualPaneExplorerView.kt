package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MtFileItem
import com.example.ui.theme.*

@Composable
fun DualPaneExplorerView(
    activePaneIndex: Int,
    leftPath: String,
    rightPath: String,
    leftFiles: List<MtFileItem>,
    rightFiles: List<MtFileItem>,
    onSelectPane: (Int) -> Unit,
    onFileClick: (MtFileItem, Int) -> Unit,
    onFileLongClick: (MtFileItem, Int) -> Unit,
    onToggleSelect: (MtFileItem, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MtDarkBg)
            .testTag("dual_pane_explorer")
    ) {
        // Dual Pane Tab Switcher Header (Left Pane vs Right Pane)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MtDarkSurface)
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Left Pane Selector
            PaneTabHeader(
                title = "Panel 1 (Sol)",
                path = leftPath,
                itemCount = leftFiles.size,
                selectedCount = leftFiles.count { it.isSelected },
                isActive = activePaneIndex == 0,
                modifier = Modifier.weight(1f),
                onClick = { onSelectPane(0) },
                testTag = "tab_left_pane"
            )

            // Right Pane Selector
            PaneTabHeader(
                title = "Panel 2 (Sağ)",
                path = rightPath,
                itemCount = rightFiles.size,
                selectedCount = rightFiles.count { it.isSelected },
                isActive = activePaneIndex == 1,
                modifier = Modifier.weight(1f),
                onClick = { onSelectPane(1) },
                testTag = "tab_right_pane"
            )
        }

        // Active Pane File List
        val currentFiles = if (activePaneIndex == 0) leftFiles else rightFiles

        if (currentFiles.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.FolderOpen,
                        contentDescription = null,
                        tint = MtTextMuted,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Bu dizin boş veya dosya bulunamadı",
                        color = MtTextMuted,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(currentFiles, key = { it.id }) { item ->
                    FileItemRow(
                        item = item,
                        onClick = { onFileClick(item, activePaneIndex) },
                        onLongClick = { onFileLongClick(item, activePaneIndex) },
                        onToggleSelect = { onToggleSelect(item, activePaneIndex) }
                    )
                    HorizontalDivider(color = MtDivider.copy(alpha = 0.4f), thickness = 0.5.dp)
                }
            }
        }
    }
}

@Composable
private fun PaneTabHeader(
    title: String,
    path: String,
    itemCount: Int,
    selectedCount: Int,
    isActive: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isActive) MtDarkSurfaceHighlight else MtDarkSurfaceVariant,
        border = if (isActive) androidx.compose.foundation.BorderStroke(1.5.dp, MtGold) else null,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = if (isActive) MtGold else MtTextSecondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                if (selectedCount > 0) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MtGold,
                        modifier = Modifier.padding(start = 4.dp)
                    ) {
                        Text(
                            text = "$selectedCount seçildi",
                            color = Color(0xFF1B1200),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                        )
                    }
                } else {
                    Text(
                        text = "$itemCount öğe",
                        color = MtTextMuted,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            val folderName = if (path == "/") "/" else path.substringAfterLast('/')
            Text(
                text = "📁 $folderName",
                color = if (isActive) MtTextPrimary else MtTextMuted,
                fontSize = 11.sp,
                maxLines = 1
            )
        }
    }
}
