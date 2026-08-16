package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.engine.ApkToolEngine
import com.example.model.ApkSignConfig
import com.example.model.SignatureScheme
import com.example.model.SmaliHookType
import com.example.ui.theme.*

@Composable
fun ApkToolsDialog(
    apkEngine: ApkToolEngine,
    apkName: String = "target_game_mod.apk",
    onDismiss: () -> Unit,
    onApkSignedCreated: (String) -> Unit
) {
    val signLogs by apkEngine.signLogs.collectAsState()
    val isSigning by apkEngine.isSigning.collectAsState()

    var activeTab by remember { mutableStateOf(0) } // 0: İmzalayıcı, 1: Hook Enjektörü, 2: Klonlayıcı, 3: Koruma
    var signConfig by remember { mutableStateOf(ApkSignConfig()) }

    // Hook state
    var selectedHookType by remember { mutableStateOf(SmaliHookType.TOAST_POPUP) }
    var hookCustomText by remember { mutableStateOf("MT Manager VIP Mod Aktif Edildi!") }
    var generatedHookSmali by remember { mutableStateOf("") }

    // Cloner state
    var origPkg by remember { mutableStateOf("com.target.game") }
    var newPkg by remember { mutableStateOf("com.target.game.cloned") }
    var newAppName by remember { mutableStateOf("Target Game (Klon)") }
    var cloneResult by remember { mutableStateOf<String?>(null) }

    // Protection state
    var protectResult by remember { mutableStateOf<String?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .testTag("apk_tools_dialog"),
            shape = RoundedCornerShape(16.dp),
            color = MtDarkBg,
            border = androidx.compose.foundation.BorderStroke(1.dp, MtGreen.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Surface(
                    color = MtDarkSurface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(ColorApk.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Security, contentDescription = null, tint = ColorApk, modifier = Modifier.size(18.dp))
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "APK Mod Araçları & İmza",
                                    color = MtTextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = MtGoldContainer
                                ) {
                                    Text(
                                        text = "VIP PRO",
                                        color = MtGold,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Text(text = "Hedef: $apkName", color = MtTextMuted, fontSize = 11.sp)
                        }

                        IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Filled.Close, contentDescription = "Kapat", tint = MtTextSecondary)
                        }
                    }
                }

                // Tabs
                TabRow(
                    selectedTabIndex = activeTab,
                    containerColor = MtDarkSurfaceVariant,
                    contentColor = MtGold
                ) {
                    Tab(
                        selected = activeTab == 0,
                        onClick = { activeTab = 0 },
                        text = { Text("İmzalayıcı", fontSize = 11.sp) }
                    )
                    Tab(
                        selected = activeTab == 1,
                        onClick = { activeTab = 1 },
                        text = { Text("Hook Enjektörü", fontSize = 11.sp) }
                    )
                    Tab(
                        selected = activeTab == 2,
                        onClick = { activeTab = 2 },
                        text = { Text("APK Klonla", fontSize = 11.sp) }
                    )
                    Tab(
                        selected = activeTab == 3,
                        onClick = { activeTab = 3 },
                        text = { Text("Anti-Decompile", fontSize = 11.sp) }
                    )
                }

                // Content
                Box(modifier = Modifier.weight(1f).padding(12.dp)) {
                    when (activeTab) {
                        0 -> ApkSignerView(
                            apkName = apkName,
                            config = signConfig,
                            onConfigChange = { signConfig = it },
                            isSigning = isSigning,
                            logs = signLogs,
                            onStartSign = {
                                apkEngine.signApk(apkName, signConfig) { success, outputName ->
                                    if (success) {
                                        onApkSignedCreated(outputName)
                                    }
                                }
                            }
                        )
                        1 -> SmaliHookInjectorView(
                            selectedType = selectedHookType,
                            onSelectType = {
                                selectedHookType = it
                                generatedHookSmali = apkEngine.generateSmaliHookPayload(it, hookCustomText)
                            },
                            customText = hookCustomText,
                            onCustomTextChange = {
                                hookCustomText = it
                                generatedHookSmali = apkEngine.generateSmaliHookPayload(selectedHookType, it)
                            },
                            generatedCode = generatedHookSmali.ifBlank {
                                apkEngine.generateSmaliHookPayload(selectedHookType, hookCustomText)
                            }
                        )
                        2 -> ApkClonerView(
                            origPkg = origPkg,
                            newPkg = newPkg,
                            newAppName = newAppName,
                            onOrigPkgChange = { origPkg = it },
                            onNewPkgChange = { newPkg = it },
                            onNewAppNameChange = { newAppName = it },
                            result = cloneResult,
                            onClone = {
                                cloneResult = apkEngine.cloneApk(origPkg, newPkg, newAppName)
                            }
                        )
                        3 -> AntiDecompileView(
                            apkName = apkName,
                            result = protectResult,
                            onProtect = {
                                protectResult = apkEngine.protectApk(apkName)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ApkSignerView(
    apkName: String,
    config: ApkSignConfig,
    onConfigChange: (ApkSignConfig) -> Unit,
    isSigning: Boolean,
    logs: List<String>,
    onStartSign: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // VIP Signature Killer Toggle
        Surface(
            color = MtGoldContainer,
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, MtGold),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Shield, contentDescription = null, tint = MtGold, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Signature Killer Enjekte Et (VIP)", color = MtGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("Orijinal imza hash'ini sahteleyerek uygulama içi imza doğrulamasını otomatik kırar.", color = MtGoldLight, fontSize = 10.sp)
                }
                Switch(
                    checked = config.injectSignatureKiller,
                    onCheckedChange = { onConfigChange(config.copy(injectSignatureKiller = it)) },
                    colors = SwitchDefaults.colors(checkedThumbColor = MtGold, checkedTrackColor = Color(0xFF5E4500))
                )
            }
        }

        // Signature Schemes Checklist
        Surface(
            color = MtDarkSurface,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("İmza Şemaları", color = MtTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))

                SignatureScheme.values().forEach { scheme ->
                    val isChecked = config.signatureSchemes.contains(scheme)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val newSet = if (isChecked) config.signatureSchemes - scheme else config.signatureSchemes + scheme
                                onConfigChange(config.copy(signatureSchemes = newSet))
                            }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { checked ->
                                val newSet = if (checked) config.signatureSchemes + scheme else config.signatureSchemes - scheme
                                onConfigChange(config.copy(signatureSchemes = newSet))
                            },
                            colors = CheckboxDefaults.colors(checkedColor = MtGold, checkmarkColor = Color(0xFF1B1200))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = scheme.label, color = MtTextPrimary, fontSize = 12.sp)
                    }
                }
            }
        }

        // Key Type Selection
        Surface(
            color = MtDarkSurface,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("İmza Anahtarı", color = MtTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = config.useMtTestKey,
                        onClick = { onConfigChange(config.copy(useMtTestKey = true)) },
                        colors = RadioButtonDefaults.colors(selectedColor = MtGold)
                    )
                    Text("MT Manager VIP Varsayılan Test Anahtarı (TestKey)", color = MtTextPrimary, fontSize = 12.sp)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = !config.useMtTestKey,
                        onClick = { onConfigChange(config.copy(useMtTestKey = false)) },
                        colors = RadioButtonDefaults.colors(selectedColor = MtGold)
                    )
                    Text("Özel Keystore (.jks / .keystore)", color = MtTextPrimary, fontSize = 12.sp)
                }
            }
        }

        // Sign Button
        Button(
            onClick = onStartSign,
            enabled = !isSigning,
            colors = ButtonDefaults.buttonColors(containerColor = MtGreen, contentColor = Color(0xFF00210A)),
            modifier = Modifier.fillMaxWidth().testTag("btn_sign_apk_execute")
        ) {
            Icon(Icons.Filled.CheckCircle, contentDescription = null)
            Spacer(modifier = Modifier.width(6.dp))
            Text("APK'yı İmzala ve Çıkar", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }

        // Live Log Output
        if (logs.isNotEmpty()) {
            Surface(
                color = Color(0xFF0A0D12),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MtDivider),
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text("İşlem Konsolu:", color = MtGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    logs.forEach { log ->
                        Text(text = log, color = MtGreen, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }
    }
}

@Composable
private fun SmaliHookInjectorView(
    selectedType: SmaliHookType,
    onSelectType: (SmaliHookType) -> Unit,
    customText: String,
    onCustomTextChange: (String) -> Unit,
    generatedCode: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Enjekte Edilecek Hook Türünü Seçin:", color = MtGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)

        SmaliHookType.values().forEach { type ->
            val isSelected = selectedType == type
            Surface(
                color = if (isSelected) MtDarkSurfaceHighlight else MtDarkSurface,
                shape = RoundedCornerShape(8.dp),
                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, MtGold) else null,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectType(type) }
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(text = type.titleTr, color = if (isSelected) MtGold else MtTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(text = type.descriptionTr, color = MtTextMuted, fontSize = 10.sp)
                }
            }
        }

        if (selectedType == SmaliHookType.TOAST_POPUP || selectedType == SmaliHookType.DIALOG_POPUP) {
            Spacer(modifier = Modifier.height(4.dp))
            Text("Özel Mesaj:", color = MtTextSecondary, fontSize = 11.sp)
            TextField(
                value = customText,
                onValueChange = onCustomTextChange,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MtDarkSurface,
                    unfocusedContainerColor = MtDarkSurface,
                    focusedTextColor = MtTextPrimary,
                    unfocusedTextColor = MtTextPrimary,
                    focusedIndicatorColor = MtGold
                )
            )
        }

        Spacer(modifier = Modifier.height(6.dp))
        Text("Oluşturulan Smali Bytecode Payload:", color = MtCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)

        Surface(
            color = Color(0xFF0A0D12),
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, MtDivider),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = generatedCode,
                color = MtCyan,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(10.dp)
            )
        }
    }
}

@Composable
private fun ApkClonerView(
    origPkg: String,
    newPkg: String,
    newAppName: String,
    onOrigPkgChange: (String) -> Unit,
    onNewPkgChange: (String) -> Unit,
    onNewAppNameChange: (String) -> Unit,
    result: String?,
    onClone: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("APK Klonlama & Çift Paket Modu (VIP)", color = MtGold, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Text(
            "Aynı uygulamanın cihazda birden fazla kopya olarak kurulabilmesi için paket adını ve içerisindeki ContentProvider yetkilerini yeniden yapılandırır.",
            color = MtTextMuted,
            fontSize = 11.sp
        )

        TextField(
            value = origPkg,
            onValueChange = onOrigPkgChange,
            label = { Text("Orijinal Paket Adı") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MtDarkSurface,
                unfocusedContainerColor = MtDarkSurface,
                focusedTextColor = MtTextPrimary,
                unfocusedTextColor = MtTextPrimary
            )
        )

        TextField(
            value = newPkg,
            onValueChange = onNewPkgChange,
            label = { Text("Yeni Klon Paket Adı") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MtDarkSurface,
                unfocusedContainerColor = MtDarkSurface,
                focusedTextColor = MtTextPrimary,
                unfocusedTextColor = MtTextPrimary
            )
        )

        TextField(
            value = newAppName,
            onValueChange = onNewAppNameChange,
            label = { Text("Yeni Uygulama Başlığı") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MtDarkSurface,
                unfocusedContainerColor = MtDarkSurface,
                focusedTextColor = MtTextPrimary,
                unfocusedTextColor = MtTextPrimary
            )
        )

        Button(
            onClick = onClone,
            colors = ButtonDefaults.buttonColors(containerColor = MtGold, contentColor = Color(0xFF1B1200)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Filled.CopyAll, contentDescription = null)
            Spacer(modifier = Modifier.width(6.dp))
            Text("APK Klonla (VIP)", fontWeight = FontWeight.Bold)
        }

        if (result != null) {
            Surface(
                color = MtDarkSurfaceVariant,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                Text(text = result, color = MtGreen, fontSize = 11.sp, modifier = Modifier.padding(10.dp))
            }
        }
    }
}

@Composable
private fun AntiDecompileView(
    apkName: String,
    result: String?,
    onProtect: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("APK Anti-Decompile & Bytecode Koruması", color = MtCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Text(
            "APK'nızı JADX, Apktool ve diğer decompiler araçlarının çökmesini sağlayacak sahte AXML başlıkları ve karışık referanslar ekleyerek korur.",
            color = MtTextMuted,
            fontSize = 11.sp
        )

        Surface(
            color = MtDarkSurface,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Uygulanacak Korumalar:", color = MtGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("✓ AndroidManifest.xml sahte tag enjeksiyonu", color = MtTextSecondary, fontSize = 10.sp)
                Text("✓ DEX String Havuzu Şifreleme", color = MtTextSecondary, fontSize = 10.sp)
                Text("✓ Anti-JADX Junk Bytecode", color = MtTextSecondary, fontSize = 10.sp)
                Text("✓ ResGuard Kaynak İsim Karıştırma", color = MtTextSecondary, fontSize = 10.sp)
            }
        }

        Button(
            onClick = onProtect,
            colors = ButtonDefaults.buttonColors(containerColor = MtCyan, contentColor = Color(0xFF001B20)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Filled.Shield, contentDescription = null)
            Spacer(modifier = Modifier.width(6.dp))
            Text("Anti-Decompile Korumasını Uygula", fontWeight = FontWeight.Bold)
        }

        if (result != null) {
            Surface(
                color = MtDarkSurfaceVariant,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                Text(text = result, color = MtGreen, fontSize = 11.sp, modifier = Modifier.padding(10.dp))
            }
        }
    }
}
