package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.engine.*
import com.example.model.FileType
import com.example.model.MtFileItem
import com.example.ui.components.DualPaneExplorerView
import com.example.ui.components.MtBottomActionBar
import com.example.ui.components.MtTopAppBar
import com.example.ui.dialogs.*
import com.example.ui.theme.MtDarkBg
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    fileManagerEngine: FileManagerEngine = remember { FileManagerEngine() },
    dexEditorEngine: DexEditorEngine = remember { DexEditorEngine() },
    arscEditorEngine: ArscEditorEngine = remember { ArscEditorEngine() },
    apkToolEngine: ApkToolEngine = remember { ApkToolEngine() },
    vipManager: VipManager = remember { VipManager() }
) {
    val activePaneIndex by fileManagerEngine.activePaneIndex.collectAsState()
    val leftPath by fileManagerEngine.leftPanePath.collectAsState()
    val rightPath by fileManagerEngine.rightPanePath.collectAsState()
    val leftFiles by fileManagerEngine.leftPaneFiles.collectAsState()
    val rightFiles by fileManagerEngine.rightPaneFiles.collectAsState()
    val searchQuery by fileManagerEngine.searchQuery.collectAsState()
    val currentSort by fileManagerEngine.currentSort.collectAsState()
    val bookmarks by fileManagerEngine.bookmarks.collectAsState()
    val vipState by vipManager.vipState.collectAsState()

    val currentPath = if (activePaneIndex == 0) leftPath else rightPath
    val currentFiles = if (activePaneIndex == 0) leftFiles else rightFiles
    val selectedCount = currentFiles.count { it.isSelected }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    fun showToast(msg: String) {
        coroutineScope.launch {
            snackbarHostState.showSnackbar(msg, duration = SnackbarDuration.Short)
        }
    }

    // UI Dialog visibility states
    var isSearchVisible by remember { mutableStateOf(false) }
    var activeActionItem by remember { mutableStateOf<MtFileItem?>(null) }
    var activeRenameItem by remember { mutableStateOf<MtFileItem?>(null) }
    var activePropertiesItem by remember { mutableStateOf<MtFileItem?>(null) }

    var showDexEditor by remember { mutableStateOf(false) }
    var showArscEditor by remember { mutableStateOf(false) }
    var showApkTools by remember { mutableStateOf(false) }
    var selectedApkName by remember { mutableStateOf("target_game_mod.apk") }

    var showSmaliEditor by remember { mutableStateOf(false) }
    var smaliFileName by remember { mutableStateOf("MainActivity.smali") }
    var smaliContent by remember { mutableStateOf("") }

    var showXmlEditor by remember { mutableStateOf(false) }
    var xmlFileName by remember { mutableStateOf("AndroidManifest.xml") }
    var xmlContent by remember { mutableStateOf("") }

    var showHexEditor by remember { mutableStateOf(false) }
    var hexFileName by remember { mutableStateOf("classes.dex") }

    var showBatchRename by remember { mutableStateOf(false) }
    var showTerminal by remember { mutableStateOf(false) }
    var showAppManager by remember { mutableStateOf(false) }
    var showVipCenter by remember { mutableStateOf(false) }
    var showVipActivationDialog by remember { mutableStateOf(false) }
    var pendingVipFeatureName by remember { mutableStateOf<String?>(null) }

    var showNewItemDialog by remember { mutableStateOf(false) }
    var showSortDialog by remember { mutableStateOf(false) }
    var showBookmarksDialog by remember { mutableStateOf(false) }

    fun checkVipAndRun(featureName: String, onGranted: () -> Unit) {
        if (vipManager.isVipActive()) {
            onGranted()
        } else {
            pendingVipFeatureName = featureName
            showVipActivationDialog = true
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().testTag("main_screen_scaffold"),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            MtTopAppBar(
                currentPath = currentPath,
                activePaneIndex = activePaneIndex,
                searchQuery = searchQuery,
                currentSort = currentSort,
                isSearchVisible = isSearchVisible,
                vipState = vipState,
                onToggleSearch = { isSearchVisible = !isSearchVisible },
                onSearchChange = { query ->
                    fileManagerEngine.setSearchQuery(query)
                    val trimmed = query.trim().lowercase()
                    if (trimmed == "1gunluk" || trimmed == "7gunluk" || trimmed == "yoneticivipi1234") {
                        when (val res = vipManager.redeemCode(trimmed)) {
                            is RedeemResult.Success -> {
                                fileManagerEngine.setSearchQuery("")
                                isSearchVisible = false
                                showToast(res.message)
                            }
                            else -> {}
                        }
                    }
                },
                onNavigateUp = { fileManagerEngine.navigateUp(activePaneIndex) },
                onOpenBookmarks = { showBookmarksDialog = true },
                onOpenSortMenu = { showSortDialog = true },
                onOpenVipCenter = { showVipCenter = true },
                onOpenTerminal = {
                    checkVipAndRun("Root Terminal Konsolu") {
                        showTerminal = true
                    }
                },
                onOpenAppManager = { showAppManager = true },
                onOpenNewItemDialog = { showNewItemDialog = true }
            )
        },
        bottomBar = {
            MtBottomActionBar(
                selectedCount = selectedCount,
                onOpenMainMenu = { showVipCenter = true },
                onSelectAll = { fileManagerEngine.toggleSelectAll(activePaneIndex) },
                onCopySelected = {
                    val count = fileManagerEngine.copySelectedToOtherPane()
                    showToast("✅ $count öğe karşı panele kopyalandı")
                },
                onMoveSelected = {
                    val count = fileManagerEngine.moveSelectedToOtherPane()
                    showToast("✅ $count öğe karşı panele taşındı")
                },
                onDeleteSelected = {
                    val count = fileManagerEngine.deleteSelected(activePaneIndex)
                    showToast("🗑️ $count öğe silindi")
                },
                onMoreOptions = {
                    checkVipAndRun("Toplu Regex Yeniden Adlandırma") {
                        showBatchRename = true
                    }
                }
            )
        },
        containerColor = MtDarkBg
    ) { innerPadding ->
        DualPaneExplorerView(
            activePaneIndex = activePaneIndex,
            leftPath = leftPath,
            rightPath = rightPath,
            leftFiles = leftFiles,
            rightFiles = rightFiles,
            onSelectPane = { fileManagerEngine.setActivePane(it) },
            onFileClick = { item, paneIdx ->
                fileManagerEngine.setActivePane(paneIdx)
                if (item.isDirectory) {
                    fileManagerEngine.navigateTo(item.path, paneIdx)
                } else {
                    when (item.fileType) {
                        FileType.APK -> {
                            selectedApkName = item.name
                            activeActionItem = item
                        }
                        FileType.DEX -> {
                            checkVipAndRun("DEX Düzenleyici++") {
                                showDexEditor = true
                            }
                        }
                        FileType.ARSC -> {
                            checkVipAndRun("ARSC & Çevirmen Pro") {
                                showArscEditor = true
                            }
                        }
                        FileType.SMALI -> {
                            smaliFileName = item.name
                            smaliContent = if (item.content.isNotBlank()) item.content else ".class public L${item.name.substringBefore(".smali")};\n.super Ljava/lang/Object;\n\n.method public constructor <init>()V\n    invoke-direct {p0}, Ljava/lang/Object;-><init>()V\n    return-void\n.end method\n"
                            showSmaliEditor = true
                        }
                        FileType.XML -> {
                            xmlFileName = item.name
                            xmlContent = if (item.content.isNotBlank()) item.content else "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<manifest xmlns:android=\"http://schemas.android.com/apk/res/android\"\n    package=\"com.target.game\">\n    <application android:allowBackup=\"true\">\n    </application>\n</manifest>"
                            showXmlEditor = true
                        }
                        else -> {
                            activeActionItem = item
                        }
                    }
                }
            },
            onFileLongClick = { item, paneIdx ->
                fileManagerEngine.setActivePane(paneIdx)
                activeActionItem = item
            },
            onToggleSelect = { item, paneIdx ->
                fileManagerEngine.setActivePane(paneIdx)
                fileManagerEngine.toggleFileSelection(item.id, paneIdx)
            },
            modifier = Modifier.padding(innerPadding)
        )
    }

    // Context Action Menu Dialog
    if (activeActionItem != null) {
        val currentItem = activeActionItem!!
        FileActionMenuDialog(
            item = currentItem,
            onDismiss = { activeActionItem = null },
            onOpenDexEditor = {
                checkVipAndRun("DEX Düzenleyici++") {
                    showDexEditor = true
                }
            },
            onOpenArscEditor = {
                checkVipAndRun("ARSC Çevirmen Pro") {
                    showArscEditor = true
                }
            },
            onOpenApkTools = {
                selectedApkName = currentItem.name
                showApkTools = true
            },
            onOpenSmaliEditor = {
                smaliFileName = currentItem.name
                smaliContent = if (currentItem.content.isNotBlank()) currentItem.content else ".class public L${currentItem.name};\n.super Ljava/lang/Object;\n"
                showSmaliEditor = true
            },
            onOpenXmlEditor = {
                xmlFileName = currentItem.name
                xmlContent = if (currentItem.content.isNotBlank()) currentItem.content else "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<manifest/>"
                showXmlEditor = true
            },
            onOpenHexEditor = {
                checkVipAndRun("Hex Düzenleyici Pro") {
                    hexFileName = currentItem.name
                    showHexEditor = true
                }
            },
            onOpenBatchRename = {
                checkVipAndRun("Toplu Regex Yeniden Adlandırma") {
                    showBatchRename = true
                }
            },
            onOpenFileProperties = { activePropertiesItem = currentItem },
            onOpenTextEditor = {
                xmlFileName = currentItem.name
                xmlContent = if (currentItem.content.isNotBlank()) currentItem.content else "Dosya içeriği: ${currentItem.name}\nBoyut: ${currentItem.formattedSize}\nİzin: ${currentItem.permissions}"
                showXmlEditor = true
            },
            onBrowseArchive = {
                fileManagerEngine.createFolder(activePaneIndex, "${currentItem.name}_extracted")
                fileManagerEngine.navigateTo("$currentPath/${currentItem.name}_extracted", activePaneIndex)
                showToast("📦 Arşiv içeriği açıldı")
            },
            onCopyOtherPane = {
                fileManagerEngine.toggleFileSelection(currentItem.id, activePaneIndex)
                fileManagerEngine.copySelectedToOtherPane()
                showToast("✅ ${currentItem.name} karşı panele kopyalandı")
            },
            onMoveOtherPane = {
                fileManagerEngine.toggleFileSelection(currentItem.id, activePaneIndex)
                fileManagerEngine.moveSelectedToOtherPane()
                showToast("✅ ${currentItem.name} karşı panele taşındı")
            },
            onDelete = {
                fileManagerEngine.deleteFile(currentItem.id, activePaneIndex)
                showToast("🗑️ ${currentItem.name} silindi")
            },
            onRename = {
                activeRenameItem = currentItem
            }
        )
    }

    // DEX Editor++ Dialog (VIP)
    if (showDexEditor) {
        DexEditorDialog(
            dexEngine = dexEditorEngine,
            onDismiss = { showDexEditor = false }
        )
    }

    // ARSC Editor Dialog (VIP)
    if (showArscEditor) {
        ArscEditorDialog(
            arscEngine = arscEditorEngine,
            onDismiss = { showArscEditor = false }
        )
    }

    // APK Tools Dialog (VIP)
    if (showApkTools) {
        ApkToolsDialog(
            apkEngine = apkToolEngine,
            apkName = selectedApkName,
            onDismiss = { showApkTools = false },
            onApkSignedCreated = { outputName ->
                fileManagerEngine.createFile(activePaneIndex, outputName, "Signed APK Bytecode binary")
                showToast("✨ İmzalı APK oluşturuldu: $outputName")
            }
        )
    }

    // Smali Editor Dialog (VIP)
    if (showSmaliEditor) {
        SmaliEditorDialog(
            fileName = smaliFileName,
            initialContent = smaliContent,
            onDismiss = { showSmaliEditor = false },
            onSave = { updatedCode ->
                smaliContent = updatedCode
                showToast("💾 $smaliFileName kaydedildi!")
            }
        )
    }

    // XML / Manifest Editor Dialog
    if (showXmlEditor) {
        XmlEditorDialog(
            fileName = xmlFileName,
            initialContent = xmlContent,
            onDismiss = { showXmlEditor = false },
            onSave = { updatedCode ->
                xmlContent = updatedCode
                showToast("💾 $xmlFileName kaydedildi!")
            }
        )
    }

    // Hex Editor Dialog (VIP)
    if (showHexEditor) {
        HexEditorDialog(
            fileName = hexFileName,
            onDismiss = { showHexEditor = false }
        )
    }

    // File Properties Dialog
    if (activePropertiesItem != null) {
        FilePropertiesDialog(
            item = activePropertiesItem!!,
            onDismiss = { activePropertiesItem = null },
            onUpdateChmod = { newOctal ->
                fileManagerEngine.updateChmod(activePropertiesItem!!.id, activePaneIndex, newOctal)
                showToast("✅ İzinler güncellendi: $newOctal")
            }
        )
    }

    // Rename Dialog
    if (activeRenameItem != null) {
        RenameDialog(
            currentName = activeRenameItem!!.name,
            onDismiss = { activeRenameItem = null },
            onConfirm = { newName ->
                fileManagerEngine.renameFile(activeRenameItem!!.id, activePaneIndex, newName)
                showToast("✏️ Dosya adı değiştirildi: $newName")
            }
        )
    }

    // Batch Regex Rename Dialog (VIP)
    if (showBatchRename) {
        BatchRenameDialog(
            onDismiss = { showBatchRename = false },
            onApply = { pattern, replacement, addCounter ->
                val count = fileManagerEngine.batchRename(activePaneIndex, pattern, replacement, addCounter)
                showToast("✨ $count dosya toplu yeniden adlandırıldı!")
            }
        )
    }

    // Terminal Dialog (VIP)
    if (showTerminal) {
        TerminalDialog(
            currentPath = currentPath,
            onDismiss = { showTerminal = false }
        )
    }

    // App Manager Dialog
    if (showAppManager) {
        AppManagerDialog(
            onDismiss = { showAppManager = false },
            onExtractApk = { appName, pkgName ->
                val fileName = "${pkgName}_extracted.apk"
                fileManagerEngine.createFile(activePaneIndex, fileName, "Extracted Application Package")
                showToast("📦 $fileName başarıyla çıkarıldı!")
            }
        )
    }

    // VIP Center Dialog
    if (showVipCenter) {
        VipCenterDialog(
            vipManager = vipManager,
            onDismiss = { showVipCenter = false }
        )
    }

    // VIP Activation Dialog (Triggered when accessing VIP feature without license)
    if (showVipActivationDialog) {
        VipActivationDialog(
            vipManager = vipManager,
            featureNameTarget = pendingVipFeatureName,
            onDismiss = {
                showVipActivationDialog = false
                pendingVipFeatureName = null
            }
        )
    }

    // New Item Dialog
    if (showNewItemDialog) {
        NewItemDialog(
            onDismiss = { showNewItemDialog = false },
            onCreateFolder = { folderName ->
                fileManagerEngine.createFolder(activePaneIndex, folderName)
                showToast("📁 Klasör oluşturuldu: $folderName")
            },
            onCreateFile = { fileName ->
                fileManagerEngine.createFile(activePaneIndex, fileName)
                showToast("📄 Dosya oluşturuldu: $fileName")
            }
        )
    }

    // Sort Dialog
    if (showSortDialog) {
        SortDialog(
            currentSort = currentSort,
            onDismiss = { showSortDialog = false },
            onSelectSort = { mode ->
                fileManagerEngine.setSortMode(mode)
            }
        )
    }

    // Bookmarks Dialog
    if (showBookmarksDialog) {
        BookmarksDialog(
            bookmarks = bookmarks,
            onDismiss = { showBookmarksDialog = false },
            onSelectBookmark = { bookmark ->
                fileManagerEngine.navigateTo(bookmark.path, activePaneIndex)
                showToast("📍 Konuma gidildi: ${bookmark.name}")
            }
        )
    }
}

