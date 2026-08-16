package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
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
import com.example.engine.DexEditorEngine
import com.example.model.DexClass
import com.example.model.DexMethod
import com.example.ui.theme.*

@Composable
fun DexEditorDialog(
    dexEngine: DexEditorEngine,
    onDismiss: () -> Unit
) {
    val classes by dexEngine.dexClasses.collectAsState()
    val selectedClass by dexEngine.selectedClass.collectAsState()
    val searchQuery by dexEngine.searchQuery.collectAsState()
    val searchResults by dexEngine.searchResults.collectAsState()

    var activeTab by remember { mutableStateOf(0) } // 0: Sınıflar, 1: Smali Kodu, 2: Dize Arama, 3: VIP Araçlar
    var editableSmali by remember(selectedClass) { mutableStateOf(selectedClass?.smaliCode ?: "") }
    var toastMessage by remember { mutableStateOf<String?>(null) }

    // Search & Replace state
    var searchStr by remember { mutableStateOf("") }
    var replaceStr by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .testTag("dex_editor_dialog"),
            shape = RoundedCornerShape(16.dp),
            color = MtDarkBg,
            border = androidx.compose.foundation.BorderStroke(1.dp, MtGold.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Header Bar
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
                                .background(ColorDex.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Extension,
                                contentDescription = null,
                                tint = ColorDex,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "DEX Düzenleyici++",
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
                            Text(
                                text = "classes.dex (Multi-DEX Ayrıştırıcı)",
                                color = MtTextMuted,
                                fontSize = 11.sp
                            )
                        }

                        IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Kapat",
                                tint = MtTextSecondary
                            )
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
                        text = { Text("Sınıflar (${classes.size})", fontSize = 11.sp) }
                    )
                    Tab(
                        selected = activeTab == 1,
                        onClick = { activeTab = 1 },
                        text = { Text("Smali Kod", fontSize = 11.sp) }
                    )
                    Tab(
                        selected = activeTab == 2,
                        onClick = { activeTab = 2 },
                        text = { Text("Dize Arama", fontSize = 11.sp) }
                    )
                    Tab(
                        selected = activeTab == 3,
                        onClick = { activeTab = 3 },
                        text = { Text("VIP Araçlar", fontSize = 11.sp) }
                    )
                }

                // Banner Toast if any
                if (toastMessage != null) {
                    Surface(
                        color = MtGoldContainer,
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = MtGold, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = toastMessage ?: "", color = MtGoldLight, fontSize = 12.sp, modifier = Modifier.weight(1f))
                            IconButton(onClick = { toastMessage = null }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Filled.Close, contentDescription = null, tint = MtGold, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }

                // Tab Contents
                Box(modifier = Modifier.weight(1f).padding(8.dp)) {
                    when (activeTab) {
                        0 -> ClassesListView(
                            classes = classes,
                            selectedClass = selectedClass,
                            onSelectClass = {
                                dexEngine.selectClass(it)
                                editableSmali = it.smaliCode
                                activeTab = 1
                            }
                        )
                        1 -> SmaliEditorView(
                            currentClass = selectedClass,
                            code = editableSmali,
                            onCodeChange = { editableSmali = it },
                            onSave = {
                                selectedClass?.let { c ->
                                    dexEngine.updateSmaliCode(c.className, editableSmali)
                                    toastMessage = "${c.simpleName} Smali kodu kaydedildi ve derlendi!"
                                }
                            },
                            onPatchReturnTrue = {
                                selectedClass?.let { c ->
                                    val success = dexEngine.patchMethodToReturnTrue(c.className, "isPremiumUser")
                                    if (success) {
                                        editableSmali = dexEngine.selectedClass.value?.smaliCode ?: ""
                                        toastMessage = "⚡ isPremiumUser() metodu return 1 (TRUE) olarak yamalandı!"
                                    }
                                }
                            }
                        )
                        2 -> StringSearchView(
                            dexEngine = dexEngine,
                            searchQuery = searchStr,
                            replaceQuery = replaceStr,
                            onSearchQueryChange = { searchStr = it; dexEngine.searchDex(it) },
                            onReplaceQueryChange = { replaceStr = it },
                            onBatchReplace = {
                                if (searchStr.isNotBlank()) {
                                    val count = dexEngine.batchReplaceString(searchStr, replaceStr)
                                    editableSmali = dexEngine.selectedClass.value?.smaliCode ?: ""
                                    toastMessage = "✅ $count dize başarıyla değiştirildi!"
                                }
                            },
                            onSelectClass = {
                                dexEngine.selectClass(it)
                                editableSmali = it.smaliCode
                                activeTab = 1
                            }
                        )
                        3 -> VipDexToolsView(
                            onObfuscate = {
                                val res = dexEngine.obfuscateDex()
                                toastMessage = res
                            },
                            onPatchAllLicenses = {
                                var patchedCount = 0
                                classes.forEach { c ->
                                    if (c.smaliCode.contains("isPremiumUser") || c.smaliCode.contains("checkLicense")) {
                                        dexEngine.patchMethodToReturnTrue(c.className, "isPremiumUser")
                                        patchedCount++
                                    }
                                }
                                editableSmali = dexEngine.selectedClass.value?.smaliCode ?: ""
                                toastMessage = "⚡ $patchedCount lisans kontrol metodu başarıyla bypass edildi!"
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ClassesListView(
    classes: List<DexClass>,
    selectedClass: DexClass?,
    onSelectClass: (DexClass) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(classes) { dexClass ->
            val isSelected = selectedClass?.className == dexClass.className
            Surface(
                color = if (isSelected) MtDarkSurfaceHighlight else MtDarkSurface,
                shape = RoundedCornerShape(8.dp),
                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, MtGold) else null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onSelectClass(dexClass) }
                    .testTag("class_item_${dexClass.simpleName}")
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(ColorDex.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "C",
                            color = ColorDex,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = dexClass.simpleName,
                                color = MtTextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            if (dexClass.methods.any { it.isVipTarget }) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = MtGoldContainer
                                ) {
                                    Text(
                                        text = "🎯 HEDEF LİSANS",
                                        color = MtGold,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = dexClass.packageName,
                            color = MtTextMuted,
                            fontSize = 10.sp
                        )
                        Text(
                            text = "${dexClass.methods.size} Metot  |  ${dexClass.fields.size} Alan",
                            color = MtCyan,
                            fontSize = 10.sp
                        )
                    }

                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        contentDescription = null,
                        tint = MtTextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SmaliEditorView(
    currentClass: DexClass?,
    code: String,
    onCodeChange: (String) -> Unit,
    onSave: () -> Unit,
    onPatchReturnTrue: () -> Unit
) {
    if (currentClass == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Lütfen bir sınıf seçin", color = MtTextMuted)
        }
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Quick Action Bar for Smali
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MtDarkSurface, RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${currentClass.simpleName}.smali",
                    color = MtCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = currentClass.className,
                    color = MtTextMuted,
                    fontSize = 10.sp,
                    maxLines = 1
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Button(
                    onClick = onPatchReturnTrue,
                    colors = ButtonDefaults.buttonColors(containerColor = MtGoldContainer, contentColor = MtGold),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("btn_patch_return_true")
                ) {
                    Icon(Icons.Filled.Bolt, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Return True Yap (VIP)", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onSave,
                    colors = ButtonDefaults.buttonColors(containerColor = MtGold, contentColor = Color(0xFF1B1200)),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("btn_save_smali")
                ) {
                    Icon(Icons.Filled.Save, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Kaydet", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Code Editor
        Surface(
            color = MtDarkBg,
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, MtDivider),
            modifier = Modifier.weight(1f).fillMaxWidth()
        ) {
            TextField(
                value = code,
                onValueChange = onCodeChange,
                modifier = Modifier.fillMaxSize().testTag("smali_text_editor"),
                textStyle = LocalTextStyle.current.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = MtTextPrimary,
                    lineHeight = 16.sp
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
        }
    }
}

@Composable
private fun StringSearchView(
    dexEngine: DexEditorEngine,
    searchQuery: String,
    replaceQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onReplaceQueryChange: (String) -> Unit,
    onBatchReplace: () -> Unit,
    onSelectClass: (DexClass) -> Unit
) {
    val results by dexEngine.searchResults.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        TextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { Text("Aranacak dize, metot veya sınıf...", fontSize = 12.sp, color = MtTextMuted) },
            modifier = Modifier.fillMaxWidth().testTag("dex_search_input"),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MtDarkSurface,
                unfocusedContainerColor = MtDarkSurface,
                focusedTextColor = MtTextPrimary,
                unfocusedTextColor = MtTextPrimary,
                focusedIndicatorColor = MtGold,
                unfocusedIndicatorColor = MtDivider
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = replaceQuery,
                onValueChange = onReplaceQueryChange,
                placeholder = { Text("Yeni dize ile değiştir...", fontSize = 12.sp, color = MtTextMuted) },
                modifier = Modifier.weight(1f).testTag("dex_replace_input"),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MtDarkSurface,
                    unfocusedContainerColor = MtDarkSurface,
                    focusedTextColor = MtTextPrimary,
                    unfocusedTextColor = MtTextPrimary,
                    focusedIndicatorColor = MtCyan,
                    unfocusedIndicatorColor = MtDivider
                ),
                singleLine = true
            )

            Button(
                onClick = onBatchReplace,
                colors = ButtonDefaults.buttonColors(containerColor = MtCyan, contentColor = Color(0xFF001B20)),
                enabled = searchQuery.isNotBlank(),
                modifier = Modifier.testTag("btn_batch_replace")
            ) {
                Text("Değiştir (VIP)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Arama Sonuçları (${results.size})",
            color = MtGold,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(results) { (dexClass, snippet) ->
                Surface(
                    color = MtDarkSurface,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                        .clickable { onSelectClass(dexClass) }
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(text = dexClass.simpleName, color = MtCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(text = snippet, color = MtTextSecondary, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }
    }
}

@Composable
private fun VipDexToolsView(
    onObfuscate: () -> Unit,
    onPatchAllLicenses: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Surface(
            color = MtDarkSurface,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Bolt, contentDescription = null, tint = MtGold, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("1-Tık Lisans & VIP Bypass Motoru", color = MtGold, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tüm sınıfları tarar, isPremium(), isVip(), checkLicense() gibi metotları otomatik bularak return true enjekte eder.",
                    color = MtTextMuted,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onPatchAllLicenses,
                    colors = ButtonDefaults.buttonColors(containerColor = MtGold, contentColor = Color(0xFF1B1200)),
                    modifier = Modifier.fillMaxWidth().testTag("btn_patch_all_licenses")
                ) {
                    Text("⚡ Tüm Lisans Kontrollerini Yamala (VIP)", fontWeight = FontWeight.Bold)
                }
            }
        }

        Surface(
            color = MtDarkSurface,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Security, contentDescription = null, tint = MtCyan, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("DEX Obfuscator & Deobfuscator", color = MtCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Sınıf ve metot isimlerini karıştırarak APK'nın tersine mühendisliğe karşı korunmasını sağlar.",
                    color = MtTextMuted,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onObfuscate,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF004D40), contentColor = MtCyan),
                    modifier = Modifier.fillMaxWidth().testTag("btn_obfuscate_dex")
                ) {
                    Text("🛡️ DEX Bytecode Şifrele & Karıştır", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
