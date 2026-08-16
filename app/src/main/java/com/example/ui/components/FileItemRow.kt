package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FileType
import com.example.model.MtFileItem
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FileItemRow(
    item: MtFileItem,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onToggleSelect: () -> Unit
) {
    val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(item.lastModified))

    Surface(
        color = if (item.isSelected) MtGoldContainer else Color.Transparent,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("file_row_${item.name}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox for selection
            Checkbox(
                checked = item.isSelected,
                onCheckedChange = { onToggleSelect() },
                colors = CheckboxDefaults.colors(
                    checkedColor = MtGold,
                    uncheckedColor = MtTextMuted,
                    checkmarkColor = Color(0xFF1B1200)
                ),
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.width(6.dp))

            // File Type Icon Badge
            FileTypeIconBadge(item.fileType, item.name)

            Spacer(modifier = Modifier.width(10.dp))

            // File Details Column
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.name,
                        color = if (item.isSelected) MtGold else MtTextPrimary,
                        fontWeight = if (item.isDirectory) FontWeight.SemiBold else FontWeight.Normal,
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    // Quick VIP Tag for moddable formats
                    QuickFormatBadge(item.fileType)
                }

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (item.isDirectory) item.formattedSize else "${item.formattedSize}  |  $dateStr",
                        color = MtTextMuted,
                        fontSize = 11.sp
                    )

                    Text(
                        text = item.permissions,
                        color = MtTextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Light
                    )
                }
            }
        }
    }
}

@Composable
fun FileTypeIconBadge(type: FileType, fileName: String) {
    val (icon, bgColor, tintColor) = when (type) {
        FileType.FOLDER -> Triple(Icons.Filled.Folder, ColorFolder.copy(alpha = 0.2f), ColorFolder)
        FileType.APK -> Triple(Icons.Filled.Android, ColorApk.copy(alpha = 0.2f), ColorApk)
        FileType.DEX -> Triple(Icons.Filled.Extension, ColorDex.copy(alpha = 0.2f), ColorDex)
        FileType.ARSC -> Triple(Icons.Filled.Style, ColorArsc.copy(alpha = 0.2f), ColorArsc)
        FileType.SMALI -> Triple(Icons.Filled.Code, ColorSmali.copy(alpha = 0.2f), ColorSmali)
        FileType.XML -> Triple(Icons.Filled.Description, ColorXml.copy(alpha = 0.2f), ColorXml)
        FileType.SO -> Triple(Icons.Filled.Memory, ColorSo.copy(alpha = 0.2f), ColorSo)
        FileType.ZIP, FileType.RAR, FileType.SEVEN_Z, FileType.JAR -> Triple(Icons.Filled.FolderZip, ColorZip.copy(alpha = 0.2f), ColorZip)
        FileType.IMAGE -> Triple(Icons.Filled.Image, Color(0xFFE91E63).copy(alpha = 0.2f), Color(0xFFE91E63))
        FileType.AUDIO -> Triple(Icons.Filled.Audiotrack, Color(0xFF9C27B0).copy(alpha = 0.2f), Color(0xFF9C27B0))
        FileType.VIDEO -> Triple(Icons.Filled.VideoFile, Color(0xFFFF5722).copy(alpha = 0.2f), Color(0xFFFF5722))
        FileType.CODE -> Triple(Icons.Filled.Terminal, MtGreen.copy(alpha = 0.2f), MtGreen)
        FileType.DATABASE -> Triple(Icons.Filled.Storage, MtCyan.copy(alpha = 0.2f), MtCyan)
        FileType.TEXT -> Triple(Icons.Filled.Article, ColorTxt.copy(alpha = 0.2f), ColorTxt)
        else -> Triple(Icons.Filled.InsertDriveFile, MtDarkSurfaceHighlight, MtTextSecondary)
    }

    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tintColor,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun QuickFormatBadge(type: FileType) {
    val (label, bg, fg) = when (type) {
        FileType.APK -> Triple("APK PRO", MtGoldContainer, MtGold)
        FileType.DEX -> Triple("DEX++", ColorDex.copy(alpha = 0.25f), ColorDex)
        FileType.ARSC -> Triple("ARSC", ColorArsc.copy(alpha = 0.25f), ColorArsc)
        FileType.SMALI -> Triple("SMALI", ColorSmali.copy(alpha = 0.25f), ColorSmali)
        FileType.XML -> Triple("AXML", ColorXml.copy(alpha = 0.25f), ColorXml)
        FileType.SO -> Triple("ELF/SO", ColorSo.copy(alpha = 0.25f), ColorSo)
        FileType.ZIP -> Triple("ZIP", ColorZip.copy(alpha = 0.25f), ColorZip)
        else -> return
    }

    Surface(
        shape = RoundedCornerShape(4.dp),
        color = bg,
        modifier = Modifier.padding(start = 4.dp)
    ) {
        Text(
            text = label,
            color = fg,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
        )
    }
}
