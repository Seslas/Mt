package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.engine.RedeemResult
import com.example.engine.VipManager
import com.example.ui.theme.*

data class VipPrivilege(
    val icon: ImageVector,
    val title: String,
    val description: String,
    val badge: String = "VIP"
)

@Composable
fun VipCenterDialog(
    vipManager: VipManager,
    onDismiss: () -> Unit
) {
    val vipState by vipManager.vipState.collectAsState()
    var codeInput by remember { mutableStateOf("") }
    var feedbackMessage by remember { mutableStateOf<String?>(null) }
    var isError by remember { mutableStateOf(false) }

    val vipPrivileges = listOf(
        VipPrivilege(Icons.Filled.Extension, "DEX Düzenleyici++ (Multi-DEX)", "Sınırsız classes.dex ve classes2.dex sınıf/metot ağacı, Smali sözdizimi arama ve tek tıkla VIP yama (return-true, return-void, const/4 v0, 0x1) enjektörü."),
        VipPrivilege(Icons.Filled.Style, "ARSC & Dize Çevirmen Pro", "resources.arsc dize havuzunu inceleme, kaynak kimliği (ID) ile arama, anında düzenleme ve otomatik Türkçe çeviri motoru."),
        VipPrivilege(Icons.Filled.Security, "APK Araçları & İmzalama (APK Tools Suite)", "V1, V2, V3, V4 APK imzalayıcı, Signature Killer bypass, TestKey/Custom Keystore desteği."),
        VipPrivilege(Icons.Filled.Bolt, "Smali Kanca (Hook) Enjeksiyonu", "Toast bildirimi, Logcat izleyici, Metod yönlendirici ve Root gizleme Smali kanca kodlarını doğrudan enjekte etme."),
        VipPrivilege(Icons.Filled.CopyAll, "APK Klonlama & Paket Değiştirici", "Bağımsız klon APK üretimi ve manifest paket adı güncellemesi."),
        VipPrivilege(Icons.Filled.Shield, "Anti-Split & Bytecode Korumaları", "Çoklu Split APK'ları birleştirme ve koruma önleme."),
        VipPrivilege(Icons.Filled.Memory, "Hex, Smali ve XML Editörleri", "Monospace font, satır numaraları, hızlı Opcode ekleme çubuğu, Hex bayt ve ASCII görüntüleyici."),
        VipPrivilege(Icons.Filled.FindReplace, "Toplu Regex Yeniden Adlandırma", "Düzenli ifadeler (Regex) ve otomatik sıralı sayaç ile binlerce dosyayı toplu adlandırma."),
        VipPrivilege(Icons.Filled.Terminal, "Root Terminali Konsolu", "Doğrudan root (uid=0) terminali ile su, pm, chmod, dumpsys, logcat komutları."),
        VipPrivilege(Icons.Filled.Lock, "İzin & Hash Yöneticisi (File Properties)", "Chmod 755/644 sekizli bit hesaplayıcı, MD5, SHA-1 ve SHA-256 hash doğrulayıcı.")
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .testTag("vip_center_dialog"),
            shape = RoundedCornerShape(16.dp),
            color = MtDarkBg,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, MtGold)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // VIP Hero Header
                Surface(
                    color = Color(0xFF261C02),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "MT VIP MERKEZİ", color = MtGold, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                            IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                                Icon(Icons.Filled.Close, contentDescription = "Kapat", tint = MtGold)
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MtGold),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFF1B1200), modifier = Modifier.size(28.dp))
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "MT Manager VIP Üyeliği",
                            color = MtGoldLight,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (vipState.isActive) MtGoldContainer else Color(0xFF332020)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (vipState.isActive) Icons.Filled.CheckCircle else Icons.Filled.Lock,
                                    contentDescription = null,
                                    tint = if (vipState.isActive) MtGold else Color(0xFFFF8A80),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (vipState.isActive) "DURUM: ${vipState.plan.titleTr.uppercase()} (${vipManager.getRemainingTimeFormatted()})" else "DURUM: AKTİF DEĞİL",
                                    color = if (vipState.isActive) MtGold else Color(0xFFFF8A80),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }
                }

                // Activation Code Redemption Section (Clean input, no spoiler chips)
                Surface(
                    color = MtDarkSurface,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.Key, contentDescription = null, tint = MtGold, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "VIP Aktivasyon Kodu Girişi",
                                color = MtTextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextField(
                                value = codeInput,
                                onValueChange = {
                                    codeInput = it
                                    feedbackMessage = null
                                },
                                placeholder = { Text("Aktivasyon kodunuzu giriniz...", fontSize = 11.sp, color = MtTextMuted) },
                                modifier = Modifier.weight(1f).testTag("vip_center_code_input"),
                                singleLine = true,
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = MtDarkBg,
                                    unfocusedContainerColor = MtDarkBg,
                                    focusedTextColor = MtTextPrimary,
                                    unfocusedTextColor = MtTextPrimary,
                                    focusedIndicatorColor = MtGold
                                )
                            )

                            Button(
                                onClick = {
                                    if (codeInput.isNotBlank()) {
                                        when (val res = vipManager.redeemCode(codeInput)) {
                                            is RedeemResult.Success -> {
                                                feedbackMessage = res.message
                                                isError = false
                                            }
                                            is RedeemResult.Error -> {
                                                feedbackMessage = res.message
                                                isError = true
                                            }
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MtGold, contentColor = Color(0xFF1B1200)),
                                modifier = Modifier.testTag("btn_redeem_vip_center")
                            ) {
                                Text("Aktifleştir", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }

                        if (feedbackMessage != null) {
                            Surface(
                                color = if (isError) Color(0xFF3B0000) else MtGoldContainer,
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = feedbackMessage ?: "",
                                    color = if (isError) Color(0xFFFF8A80) else MtGoldLight,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(6.dp)
                                )
                            }
                        }
                    }
                }

                // Privileges list
                Text(
                    text = "MT Manager VIP Özellik Seti (${vipPrivileges.size}):",
                    color = MtGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(vipPrivileges) { priv ->
                        Surface(
                            color = MtDarkSurface,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MtGoldContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = priv.icon, contentDescription = null, tint = MtGold, modifier = Modifier.size(18.dp))
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = priv.title, color = MtTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(shape = RoundedCornerShape(4.dp), color = MtGoldContainer) {
                                            Text(text = priv.badge, color = MtGold, fontSize = 8.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(text = priv.description, color = MtTextSecondary, fontSize = 10.sp, lineHeight = 14.sp)
                                }
                            }
                        }
                    }
                }

                Surface(
                    color = MtDarkSurface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(containerColor = MtGold, contentColor = Color(0xFF1B1200)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Tamam", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
