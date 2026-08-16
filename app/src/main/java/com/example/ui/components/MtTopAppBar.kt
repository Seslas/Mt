package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SortMode
import com.example.model.VipPlan
import com.example.model.VipState
import com.example.ui.theme.*

@Composable
fun MtTopAppBar(
    currentPath: String,
    activePaneIndex: Int,
    searchQuery: String,
    currentSort: SortMode,
    isSearchVisible: Boolean,
    vipState: VipState,
    onToggleSearch: () -> Unit,
    onSearchChange: (String) -> Unit,
    onNavigateUp: () -> Unit,
    onOpenBookmarks: () -> Unit,
    onOpenSortMenu: () -> Unit,
    onOpenVipCenter: () -> Unit,
    onOpenTerminal: () -> Unit,
    onOpenAppManager: () -> Unit,
    onOpenNewItemDialog: () -> Unit
) {
    Surface(
        color = MtDarkSurface,
        shadowElevation = 4.dp,
        modifier = Modifier.fillMaxWidth().testTag("mt_top_app_bar")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Top Row: Logo, Title, VIP Badge, Quick Actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // MT Icon Badge
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MtGold)
                        .clickable { onOpenVipCenter() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "MT",
                        color = Color(0xFF1B1200),
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "MT Manager",
                            color = MtTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        // VIP Crown Badge
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (vipState.isActive) MtGoldContainer else Color(0xFF3B2020),
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .clickable { onOpenVipCenter() }
                                .testTag("vip_badge_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (vipState.isActive) Icons.Filled.Star else Icons.Filled.Lock,
                                    contentDescription = "VIP Durumu",
                                    tint = if (vipState.isActive) MtGold else Color(0xFFFF8A80),
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = if (vipState.isActive) "VIP: ${vipState.plan.badgeTr}" else "VIP AKTİFLEŞTİR",
                                    color = if (vipState.isActive) MtGold else Color(0xFFFF8A80),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }
                    Text(
                        text = if (activePaneIndex == 0) "Panel 1 (Sol)" else "Panel 2 (Sağ)",
                        color = MtCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Action Icons
                IconButton(
                    onClick = onOpenNewItemDialog,
                    modifier = Modifier.size(36.dp).testTag("btn_new_item")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Yeni Dosya/Klasör",
                        tint = MtTextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = onToggleSearch,
                    modifier = Modifier.size(36.dp).testTag("btn_search_toggle")
                ) {
                    Icon(
                        imageVector = if (isSearchVisible) Icons.Filled.Close else Icons.Filled.Search,
                        contentDescription = "Ara",
                        tint = if (isSearchVisible) MtGold else MtTextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = onOpenSortMenu,
                    modifier = Modifier.size(36.dp).testTag("btn_sort_menu")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Sort,
                        contentDescription = "Sırala",
                        tint = MtTextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = onOpenTerminal,
                    modifier = Modifier.size(36.dp).testTag("btn_terminal")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Terminal,
                        contentDescription = "Root Terminal",
                        tint = MtGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = onOpenAppManager,
                    modifier = Modifier.size(36.dp).testTag("btn_app_manager")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Android,
                        contentDescription = "Uygulama Yöneticisi",
                        tint = MtCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = onOpenBookmarks,
                    modifier = Modifier.size(36.dp).testTag("btn_bookmarks")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Bookmark,
                        contentDescription = "Yer İmleri",
                        tint = MtGold,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Search Bar (Expandable)
            AnimatedVisibility(visible = isSearchVisible) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                        .background(MtDarkSurfaceVariant, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = null,
                        tint = MtTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    TextField(
                        value = searchQuery,
                        onValueChange = onSearchChange,
                        placeholder = { Text("Dosya adı filtrele...", fontSize = 13.sp, color = MtTextMuted) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = MtTextPrimary,
                            unfocusedTextColor = MtTextPrimary,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier.weight(1f).testTag("search_input_field"),
                        singleLine = true
                    )
                    if (searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { onSearchChange("") },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Clear,
                                contentDescription = "Temizle",
                                tint = MtTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Breadcrumb Path Navigation Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MtDarkSurfaceHighlight)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateUp,
                    modifier = Modifier.size(28.dp).testTag("btn_navigate_up")
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowUpward,
                        contentDescription = "Üst Dizin",
                        tint = MtGold,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                Icon(
                    imageVector = Icons.Filled.FolderOpen,
                    contentDescription = null,
                    tint = MtGoldLight,
                    modifier = Modifier.size(16.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = currentPath,
                    color = MtTextPrimary,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = MtDarkSurfaceVariant,
                    modifier = Modifier.padding(start = 4.dp)
                ) {
                    Text(
                        text = currentSort.titleTr,
                        color = MtTextSecondary,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}
