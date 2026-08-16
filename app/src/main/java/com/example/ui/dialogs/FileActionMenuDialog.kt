package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FileType
import com.example.model.MtFileItem
import com.example.ui.components.FileTypeIconBadge
import com.example.ui.components.QuickFormatBadge
import com.example.ui.theme.*

@Composable
fun FileActionMenuDialog(
    item: MtFileItem,
    onDismiss: () -> Unit,
    onOpenDexEditor: () -> Unit,
    onOpenArscEditor: () -> Unit,
    onOpenApkTools: () -> Unit,
    onOpenSmaliEditor: () -> Unit,
    onOpenXmlEditor: () -> Unit,
    onOpenHexEditor: () -> Unit,
    onOpenBatchRename: () -> Unit,
    onOpenFileProperties: () -> Unit,
    onOpenTextEditor: () -> Unit,
    onBrowseArchive: () -> Unit,
    onCopyOtherPane: () -> Unit,
    onMoveOtherPane: () -> Unit,
    onDelete: () -> Unit,
    onRename: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MtDarkSurface,
        titleContentColor = MtTextPrimary,
        shape = RoundedCornerShape(16.dp),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                FileTypeIconBadge(item.fileType, item.name)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MtTextPrimary,
                        maxLines = 1
                    )
                    Text(
                        text = "${item.formattedSize}  |  ${item.permissions}",
                        fontSize = 11.sp,
                        color = MtTextMuted
                    )
                }
                QuickFormatBadge(item.fileType)
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp)
            ) {
                // Specialized APK Actions
                if (item.fileType == FileType.APK) {
                    item {
                        ActionSectionHeader("APK & Tersine Mühendislik (VIP)")
                    }
                    item {
                        ActionMenuItem(
                            icon = Icons.Filled.Extension,
                            title = "DEX Düzenleyici++ (VIP)",
                            subtitle = "Multi-DEX, Smali sınıfları ve metot yamalama",
                            tint = MtGold,
                            isVip = true,
                            onClick = { onDismiss(); onOpenDexEditor() }
                        )
                    }
                    item {
                        ActionMenuItem(
                            icon = Icons.Filled.Style,
                            title = "ARSC & Dize Çevirici (VIP)",
                            subtitle = "resources.arsc dize tabloları ve otomatik Türkçe çeviri",
                            tint = MtCyan,
                            isVip = true,
                            onClick = { onDismiss(); onOpenArscEditor() }
                        )
                    }
                    item {
                        ActionMenuItem(
                            icon = Icons.Filled.Security,
                            title = "APK İmzalayıcı & Signature Killer (VIP)",
                            subtitle = "V1/V2/V3/V4 imzalama ve imza doğrulama bypass",
                            tint = MtGreen,
                            isVip = true,
                            onClick = { onDismiss(); onOpenApkTools() }
                        )
                    }
                    item {
                        ActionMenuItem(
                            icon = Icons.Filled.FolderZip,
                            title = "APK İçeriğini Aç (Arşiv Olarak Gez)",
                            subtitle = "APK içindeki dosyalara doğrudan eriş",
                            tint = ColorZip,
                            onClick = { onDismiss(); onBrowseArchive() }
                        )
                    }
                }

                // Specialized DEX Actions
                if (item.fileType == FileType.DEX) {
                    item {
                        ActionSectionHeader("DEX Bytecode Araçları (VIP)")
                    }
                    item {
                        ActionMenuItem(
                            icon = Icons.Filled.Extension,
                            title = "DEX Düzenleyici++ ile Aç (VIP)",
                            subtitle = "Sınıf ağacı, Smali kodları ve lisans yamalama",
                            tint = MtGold,
                            isVip = true,
                            onClick = { onDismiss(); onOpenDexEditor() }
                        )
                    }
                }

                // Specialized ARSC Actions
                if (item.fileType == FileType.ARSC) {
                    item {
                        ActionSectionHeader("Kaynak Düzenleme (VIP)")
                    }
                    item {
                        ActionMenuItem(
                            icon = Icons.Filled.Style,
                            title = "ARSC Kaynak Düzenleyici (VIP)",
                            subtitle = "Dizeler, renkler ve çoklu dil çevirmeni",
                            tint = MtCyan,
                            isVip = true,
                            onClick = { onDismiss(); onOpenArscEditor() }
                        )
                    }
                }

                // Specialized SMALI Actions
                if (item.fileType == FileType.SMALI) {
                    item {
                        ActionSectionHeader("Smali Bytecode Düzenleyici (VIP)")
                    }
                    item {
                        ActionMenuItem(
                            icon = Icons.Filled.Code,
                            title = "Smali Düzenleyici ile Aç (VIP)",
                            subtitle = "Opcode renklendirme ve direkt bytecode düzenleme",
                            tint = MtCyan,
                            isVip = true,
                            onClick = { onDismiss(); onOpenSmaliEditor() }
                        )
                    }
                }

                // Specialized XML Actions
                if (item.fileType == FileType.XML) {
                    item {
                        ActionSectionHeader("XML & Manifest Araçları")
                    }
                    item {
                        ActionMenuItem(
                            icon = Icons.Filled.Description,
                            title = "Binary XML / AXML Düzenleyici",
                            subtitle = "AndroidManifest.xml izin ve aktivite düzenleme",
                            tint = MtOrange,
                            onClick = { onDismiss(); onOpenXmlEditor() }
                        )
                    }
                }

                // Specialized ZIP / Archive Actions
                if (item.isArchive) {
                    item {
                        ActionSectionHeader("Arşiv İşlemleri")
                    }
                    item {
                        ActionMenuItem(
                            icon = Icons.Filled.FolderZip,
                            title = "Arşivi Çıkarmadan Aç",
                            subtitle = "İçeriği diğer panelde listele",
                            tint = ColorZip,
                            onClick = { onDismiss(); onBrowseArchive() }
                        )
                    }
                }

                // General File Tools
                item {
                    ActionSectionHeader("Genel Dosya Araçları")
                }
                item {
                    ActionMenuItem(
                        icon = Icons.Filled.Article,
                        title = "Metin Düzenleyici ile Aç",
                        subtitle = "Kod ve metin olarak görüntüle / düzenle",
                        tint = MtTextPrimary,
                        onClick = { onDismiss(); onOpenTextEditor() }
                    )
                }
                item {
                    ActionMenuItem(
                        icon = Icons.Filled.Memory,
                        title = "Hex Düzenleyici Pro (VIP)",
                        subtitle = "Bayt akışı, ofsetler ve ASCII görünümü",
                        tint = MtPurple,
                        isVip = true,
                        onClick = { onDismiss(); onOpenHexEditor() }
                    )
                }
                item {
                    ActionMenuItem(
                        icon = Icons.Filled.DriveFileRenameOutline,
                        title = "Yeniden Adlandır",
                        subtitle = "Dosya adını ve uzantısını değiştir",
                        tint = MtTextPrimary,
                        onClick = { onDismiss(); onRename() }
                    )
                }
                item {
                    ActionMenuItem(
                        icon = Icons.Filled.FindReplace,
                        title = "Toplu Regex Yeniden Adlandırma (VIP)",
                        subtitle = "Regex desenleriyle otomatik adlandırma",
                        tint = MtGold,
                        isVip = true,
                        onClick = { onDismiss(); onOpenBatchRename() }
                    )
                }
                item {
                    ActionMenuItem(
                        icon = Icons.Filled.ContentCopy,
                        title = "Diğer Panele Kopyala",
                        subtitle = "Karşı panele klonla",
                        tint = MtCyan,
                        onClick = { onDismiss(); onCopyOtherPane() }
                    )
                }
                item {
                    ActionMenuItem(
                        icon = Icons.Filled.DriveFileMove,
                        title = "Diğer Panele Taşı",
                        subtitle = "Karşı panele aktar",
                        tint = MtOrange,
                        onClick = { onDismiss(); onMoveOtherPane() }
                    )
                }
                item {
                    ActionMenuItem(
                        icon = Icons.Filled.Info,
                        title = "Özellikler & İzinler (Chmod)",
                        subtitle = "MD5/SHA1/SHA256 ve rwxrwxrwx izin matrisi",
                        tint = MtTextPrimary,
                        onClick = { onDismiss(); onOpenFileProperties() }
                    )
                }
                item {
                    ActionMenuItem(
                        icon = Icons.Filled.Delete,
                        title = "Sil",
                        subtitle = "Dosyayı kalıcı olarak sil",
                        tint = MtRed,
                        onClick = { onDismiss(); onDelete() }
                    )
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

@Composable
private fun ActionSectionHeader(title: String) {
    Text(
        text = title,
        color = MtGold,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)
    )
}

@Composable
private fun ActionMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    tint: Color,
    isVip: Boolean = false,
    onClick: () -> Unit
) {
    Surface(
        color = Color.Transparent,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(tint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        color = MtTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    if (isVip) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Surface(
                            shape = RoundedCornerShape(3.dp),
                            color = MtGoldContainer
                        ) {
                            Text(
                                text = "VIP",
                                color = MtGold,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp)
                            )
                        }
                    }
                }
                Text(
                    text = subtitle,
                    color = MtTextMuted,
                    fontSize = 10.sp
                )
            }
        }
    }
}
