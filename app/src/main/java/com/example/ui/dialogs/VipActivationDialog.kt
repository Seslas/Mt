package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.RedeemResult
import com.example.engine.VipManager
import com.example.ui.theme.*

@Composable
fun VipActivationDialog(
    vipManager: VipManager,
    featureNameTarget: String? = null,
    onDismiss: () -> Unit
) {
    val vipState by vipManager.vipState.collectAsState()
    var codeInput by remember { mutableStateOf("") }
    var feedbackMessage by remember { mutableStateOf<String?>(null) }
    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MtDarkSurface,
        shape = RoundedCornerShape(16.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MtGoldContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Key, contentDescription = null, tint = MtGold, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "VIP Yetkilendirme",
                        color = MtTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (featureNameTarget != null) {
                        Text(
                            text = "$featureNameTarget için VIP gereklidir",
                            color = MtGold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Current VIP status banner
                Surface(
                    color = if (vipState.isActive) MtGoldContainer else MtDarkBg,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (vipState.isActive) Icons.Filled.Star else Icons.Filled.Lock,
                            contentDescription = null,
                            tint = if (vipState.isActive) MtGold else MtTextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Durum: ${vipState.plan.titleTr}",
                                color = if (vipState.isActive) MtGoldLight else MtTextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (vipState.isActive) vipManager.getRemainingTimeFormatted() else "Özellikleri kullanabilmek için aktivasyon kodunuzu giriniz.",
                                color = if (vipState.isActive) MtGold else MtTextMuted,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                // Input field (Clean & Secret without leaking the valid codes)
                TextField(
                    value = codeInput,
                    onValueChange = {
                        codeInput = it
                        feedbackMessage = null
                    },
                    label = { Text("Aktivasyon Kodu") },
                    placeholder = { Text("VIP kodunu buraya giriniz...", fontSize = 11.sp, color = MtTextMuted) },
                    modifier = Modifier.fillMaxWidth().testTag("vip_code_input"),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MtDarkBg,
                        unfocusedContainerColor = MtDarkBg,
                        focusedTextColor = MtTextPrimary,
                        unfocusedTextColor = MtTextPrimary,
                        focusedIndicatorColor = MtGold,
                        unfocusedIndicatorColor = MtDivider
                    )
                )

                // Feedback Banner
                if (feedbackMessage != null) {
                    Surface(
                        color = if (isError) Color(0xFF3B0000) else MtGoldContainer,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = feedbackMessage ?: "",
                            color = if (isError) Color(0xFFFF8A80) else MtGoldLight,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
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
                modifier = Modifier.testTag("btn_redeem_vip_code")
            ) {
                Text("Doğrula & Aç", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Kapat", color = MtTextSecondary)
            }
        }
    )
}
