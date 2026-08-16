package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun MtBottomActionBar(
    selectedCount: Int,
    onOpenMainMenu: () -> Unit,
    onSelectAll: () -> Unit,
    onCopySelected: () -> Unit,
    onMoveSelected: () -> Unit,
    onDeleteSelected: () -> Unit,
    onMoreOptions: () -> Unit
) {
    Surface(
        color = MtDarkSurface,
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth().testTag("mt_bottom_action_bar")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 4.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomBarItem(
                icon = Icons.Filled.Menu,
                label = "Menü",
                onClick = onOpenMainMenu,
                testTag = "btn_bottom_menu"
            )

            BottomBarItem(
                icon = Icons.Filled.SelectAll,
                label = if (selectedCount > 0) "($selectedCount) Seç" else "Tümünü Seç",
                tint = if (selectedCount > 0) MtGold else MtTextPrimary,
                onClick = onSelectAll,
                testTag = "btn_bottom_select_all"
            )

            BottomBarItem(
                icon = Icons.Filled.ContentCopy,
                label = "Kopyala ➡️",
                enabled = selectedCount > 0,
                tint = if (selectedCount > 0) MtCyan else MtTextMuted,
                onClick = onCopySelected,
                testTag = "btn_bottom_copy"
            )

            BottomBarItem(
                icon = Icons.Filled.DriveFileMove,
                label = "Taşı ➡️",
                enabled = selectedCount > 0,
                tint = if (selectedCount > 0) MtOrange else MtTextMuted,
                onClick = onMoveSelected,
                testTag = "btn_bottom_move"
            )

            BottomBarItem(
                icon = Icons.Filled.Delete,
                label = "Sil",
                enabled = selectedCount > 0,
                tint = if (selectedCount > 0) MtRed else MtTextMuted,
                onClick = onDeleteSelected,
                testTag = "btn_bottom_delete"
            )

            BottomBarItem(
                icon = Icons.Filled.MoreVert,
                label = "Daha Fazla",
                onClick = onMoreOptions,
                testTag = "btn_bottom_more"
            )
        }
    }
}

@Composable
private fun BottomBarItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    testTag: String,
    enabled: Boolean = true,
    tint: androidx.compose.ui.graphics.Color = MtTextPrimary
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 4.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (enabled) tint else MtTextMuted,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = if (enabled) tint else MtTextMuted,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
