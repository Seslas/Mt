package com.example.engine

import android.content.Context
import android.os.Environment
import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FileManagerEngine(private val context: Context? = null) {

    // Left & Right Pane paths and states
    private val _leftPath = MutableStateFlow("/storage/emulated/0/MT2")
    val leftPath: StateFlow<String> = _leftPath.asStateFlow()
    val leftPanePath: StateFlow<String> = _leftPath.asStateFlow()

    private val _rightPath = MutableStateFlow("/storage/emulated/0/Download")
    val rightPath: StateFlow<String> = _rightPath.asStateFlow()
    val rightPanePath: StateFlow<String> = _rightPath.asStateFlow()

    private val _leftFiles = MutableStateFlow<List<MtFileItem>>(emptyList())
    val leftFiles: StateFlow<List<MtFileItem>> = _leftFiles.asStateFlow()
    val leftPaneFiles: StateFlow<List<MtFileItem>> = _leftFiles.asStateFlow()

    private val _rightFiles = MutableStateFlow<List<MtFileItem>>(emptyList())
    val rightFiles: StateFlow<List<MtFileItem>> = _rightFiles.asStateFlow()
    val rightPaneFiles: StateFlow<List<MtFileItem>> = _rightFiles.asStateFlow()

    private val _activePaneIndex = MutableStateFlow(0) // 0 = Left, 1 = Right
    val activePaneIndex: StateFlow<Int> = _activePaneIndex.asStateFlow()

    private val _currentSort = MutableStateFlow(SortMode.NAME_ASC)
    val currentSort: StateFlow<SortMode> = _currentSort.asStateFlow()
    val leftSort: StateFlow<SortMode> = _currentSort.asStateFlow()
    val rightSort: StateFlow<SortMode> = _currentSort.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    val searchFilterLeft: StateFlow<String> = _searchQuery.asStateFlow()
    val searchFilterRight: StateFlow<String> = _searchQuery.asStateFlow()

    private val _bookmarks = MutableStateFlow<List<BookmarkItem>>(
        listOf(
            BookmarkItem("MT2 Çalışma Alanı", "/storage/emulated/0/MT2", false),
            BookmarkItem("İndirilenler", "/storage/emulated/0/Download", false),
            BookmarkItem("Dahili Hafıza", "/storage/emulated/0", false),
            BookmarkItem("Android Verisi", "/storage/emulated/0/Android/data", false),
            BookmarkItem("Sistem Kökü (/)", "/", true),
            BookmarkItem("Uygulama Verisi", "/data/data", true)
        )
    )
    val bookmarks: StateFlow<List<BookmarkItem>> = _bookmarks.asStateFlow()

    // Virtual filesystem simulation map for realistic explorer experience
    private val virtualFileSystem = mutableMapOf<String, MutableList<MtFileItem>>()

    init {
        setupInitialFilesystem()
        refreshPanels()
    }

    private fun setupInitialFilesystem() {
        val rootItems = mutableListOf(
            createFolder("storage", "/storage", "755"),
            createFolder("sdcard", "/sdcard", "777"),
            createFolder("data", "/data", "771"),
            createFolder("system", "/system", "755"),
            createFolder("etc", "/etc", "755"),
            createFolder("vendor", "/vendor", "755"),
            createFile("build.prop", "/build.prop", 8420L, "644", FileType.TEXT, content = "ro.build.version.release=14\nro.product.model=Pixel 8 Pro\nro.build.type=userdebug\nro.mt.vip.version=2.14.0\n"),
            createFile("default.prop", "/default.prop", 1204L, "644", FileType.TEXT, content = "ro.secure=0\nro.debuggable=1\n")
        )
        virtualFileSystem["/"] = rootItems

        val storageItems = mutableListOf(
            createFolder("emulated", "/storage/emulated", "755"),
            createFolder("self", "/storage/self", "755")
        )
        virtualFileSystem["/storage"] = storageItems

        val emulatedItems = mutableListOf(
            createFolder("0", "/storage/emulated/0", "777")
        )
        virtualFileSystem["/storage/emulated"] = emulatedItems

        val sdcard0Items = mutableListOf(
            createFolder("MT2", "/storage/emulated/0/MT2", "777"),
            createFolder("Download", "/storage/emulated/0/Download", "777"),
            createFolder("Android", "/storage/emulated/0/Android", "777"),
            createFolder("DCIM", "/storage/emulated/0/DCIM", "777"),
            createFolder("Documents", "/storage/emulated/0/Documents", "777"),
            createFolder("Music", "/storage/emulated/0/Music", "777"),
            createFolder("Pictures", "/storage/emulated/0/Pictures", "777")
        )
        virtualFileSystem["/storage/emulated/0"] = sdcard0Items
        virtualFileSystem["/sdcard"] = sdcard0Items

        // Inside MT2 folder - APKs and reverse engineering files
        val sampleApkInnerFiles = listOf(
            createFile("AndroidManifest.xml", "AndroidManifest.xml", 2840L, "644", FileType.XML, content = SampleWorkspaceData.sampleAndroidManifest),
            createFile("classes.dex", "classes.dex", 482910L, "644", FileType.DEX, content = "DEX 039 Bytecode format - 142 classes"),
            createFile("classes2.dex", "classes2.dex", 215400L, "644", FileType.DEX, content = "DEX 039 Bytecode format - 68 classes"),
            createFile("resources.arsc", "resources.arsc", 94100L, "644", FileType.ARSC, content = "ARSC Resource Table - 412 strings"),
            createFolder("res", "res", "755"),
            createFolder("lib", "lib", "755"),
            createFolder("assets", "assets", "755"),
            createFolder("META-INF", "META-INF", "755")
        )

        val mt2Items = mutableListOf(
            createFile(
                name = "target_game_mod.apk",
                path = "/storage/emulated/0/MT2/target_game_mod.apk",
                size = 14250000L,
                chmod = "644",
                type = FileType.APK,
                isArchive = true,
                innerFiles = sampleApkInnerFiles,
                content = "APK Package: com.target.game (v1.4.0)\nTest Targets: LicenseChecker, MainActivity, AntiRootChecker"
            ),
            createFile(
                name = "classes.dex",
                path = "/storage/emulated/0/MT2/classes.dex",
                size = 482910L,
                chmod = "644",
                type = FileType.DEX,
                content = "DEX 039 Bytecode format"
            ),
            createFile(
                name = "resources.arsc",
                path = "/storage/emulated/0/MT2/resources.arsc",
                size = 94100L,
                chmod = "644",
                type = FileType.ARSC,
                content = "Android Resource Table"
            ),
            createFile(
                name = "AndroidManifest.xml",
                path = "/storage/emulated/0/MT2/AndroidManifest.xml",
                size = 2840L,
                chmod = "644",
                type = FileType.XML,
                content = SampleWorkspaceData.sampleAndroidManifest
            ),
            createFile(
                name = "MainActivity.smali",
                path = "/storage/emulated/0/MT2/MainActivity.smali",
                size = 5420L,
                chmod = "644",
                type = FileType.SMALI,
                content = SampleWorkspaceData.createInitialDexClasses().first().smaliCode
            ),
            createFile(
                name = "LicenseChecker.smali",
                path = "/storage/emulated/0/MT2/LicenseChecker.smali",
                size = 3210L,
                chmod = "644",
                type = FileType.SMALI,
                content = SampleWorkspaceData.createInitialDexClasses()[1].smaliCode
            ),
            createFile(
                name = "libnative-mod.so",
                path = "/storage/emulated/0/MT2/libnative-mod.so",
                size = 842000L,
                chmod = "755",
                type = FileType.SO,
                content = "ELF 64-bit LSB shared object, ARM aarch64"
            ),
            createFile(
                name = "mod_script.sh",
                path = "/storage/emulated/0/MT2/mod_script.sh",
                size = 850L,
                chmod = "755",
                type = FileType.CODE,
                content = "#!/system/bin/sh\n# MT Manager Shell Runner\necho 'MT VIP Hook Injection Starting...'\npm list packages | grep target\nexit 0\n"
            ),
            createFolder("apks_backup", "/storage/emulated/0/MT2/apks_backup", "777")
        )
        virtualFileSystem["/storage/emulated/0/MT2"] = mt2Items

        val downloadItems = mutableListOf(
            createFile("mod_patch_v2.zip", "/storage/emulated/0/Download/mod_patch_v2.zip", 2840000L, "644", FileType.ZIP, isArchive = true),
            createFile("strings_tr.xml", "/storage/emulated/0/Download/strings_tr.xml", 12500L, "644", FileType.XML, content = "<resources>\n    <string name=\"app_name\">Oyun Modu TR</string>\n</resources>"),
            createFile("readme_instructions.txt", "/storage/emulated/0/Download/readme_instructions.txt", 1450L, "644", FileType.TEXT, content = "MT Manager VIP Kullanım Kılavuzu:\n1. DEX Düzenleyici++ ile sınıfları açın.\n2. LicenseChecker sınıfında isPremiumUser metodunu bulun.\n3. Return true butonuna basarak yamalayın.\n4. APK İmzalayıcı ile imzalayın."),
            createFolder("Extracted_Assets", "/storage/emulated/0/Download/Extracted_Assets", "777")
        )
        virtualFileSystem["/storage/emulated/0/Download"] = downloadItems

        val androidDataItems = mutableListOf(
            createFolder("com.target.game", "/storage/emulated/0/Android/data/com.target.game", "777"),
            createFolder("com.android.chrome", "/storage/emulated/0/Android/data/com.android.chrome", "777"),
            createFolder("com.google.android.youtube", "/storage/emulated/0/Android/data/com.google.android.youtube", "777")
        )
        virtualFileSystem["/storage/emulated/0/Android/data"] = androidDataItems
        virtualFileSystem["/storage/emulated/0/Android"] = mutableListOf(
            createFolder("data", "/storage/emulated/0/Android/data", "777"),
            createFolder("obb", "/storage/emulated/0/Android/obb", "777")
        )
    }

    private fun createFolder(name: String, path: String, chmod: String): MtFileItem {
        return MtFileItem(
            id = path,
            name = name,
            path = path,
            isDirectory = true,
            size = 4096L,
            formattedSize = "Klasör",
            lastModified = System.currentTimeMillis() - (1000L * 60 * 60 * 2),
            permissions = formatPermissions(chmod, true),
            chmodOctal = chmod,
            fileType = FileType.FOLDER
        )
    }

    private fun createFile(
        name: String,
        path: String,
        size: Long,
        chmod: String,
        type: FileType,
        isArchive: Boolean = false,
        innerFiles: List<MtFileItem> = emptyList(),
        content: String = ""
    ): MtFileItem {
        val md5 = computeHash(name + size, "MD5")
        val sha1 = computeHash(name + size, "SHA-1")
        val sha256 = computeHash(name + size, "SHA-256")
        return MtFileItem(
            id = path,
            name = name,
            path = path,
            isDirectory = false,
            size = size,
            formattedSize = formatFileSize(size),
            lastModified = System.currentTimeMillis() - (1000L * 60 * 30),
            permissions = formatPermissions(chmod, false),
            chmodOctal = chmod,
            fileType = type,
            isArchive = isArchive,
            innerArchiveFiles = innerFiles,
            content = content,
            md5 = md5,
            sha1 = sha1,
            sha256 = sha256
        )
    }

    fun setActivePane(index: Int) {
        _activePaneIndex.value = index
    }

    fun refreshPanels() {
        _leftFiles.value = getSortedAndFilteredList(_leftPath.value, _currentSort.value, _searchQuery.value)
        _rightFiles.value = getSortedAndFilteredList(_rightPath.value, _currentSort.value, _searchQuery.value)
    }

    private fun getSortedAndFilteredList(path: String, sortMode: SortMode, filter: String): List<MtFileItem> {
        val items = virtualFileSystem[path] ?: mutableListOf()
        val filtered = if (filter.isBlank()) {
            items
        } else {
            items.filter { it.name.contains(filter, ignoreCase = true) }
        }

        return when (sortMode) {
            SortMode.NAME_ASC -> filtered.sortedWith(compareBy({ !it.isDirectory }, { it.name.lowercase(Locale.getDefault()) }))
            SortMode.NAME_DESC -> filtered.sortedWith(compareBy<MtFileItem> { !it.isDirectory }.thenByDescending { it.name.lowercase(Locale.getDefault()) })
            SortMode.SIZE_DESC -> filtered.sortedWith(compareBy<MtFileItem> { !it.isDirectory }.thenByDescending { it.size })
            SortMode.SIZE_ASC -> filtered.sortedWith(compareBy<MtFileItem> { !it.isDirectory }.thenBy { it.size })
            SortMode.DATE_DESC -> filtered.sortedWith(compareBy<MtFileItem> { !it.isDirectory }.thenByDescending { it.lastModified })
            SortMode.DATE_ASC -> filtered.sortedWith(compareBy<MtFileItem> { !it.isDirectory }.thenBy { it.lastModified })
            SortMode.TYPE_ASC -> filtered.sortedWith(compareBy({ !it.isDirectory }, { it.fileType.name }, { it.name }))
        }
    }

    fun navigateTo(paneIndex: Int, targetPath: String) {
        val normalized = if (targetPath.length > 1 && targetPath.endsWith("/")) targetPath.dropLast(1) else targetPath
        if (paneIndex == 0) {
            _leftPath.value = normalized
        } else {
            _rightPath.value = normalized
        }
        refreshPanels()
    }

    fun navigateTo(targetPath: String, paneIndex: Int) {
        navigateTo(paneIndex, targetPath)
    }

    fun navigateUp(paneIndex: Int) {
        val currentPath = if (paneIndex == 0) _leftPath.value else _rightPath.value
        if (currentPath == "/" || currentPath.isBlank()) return

        val parentPath = File(currentPath).parent ?: "/"
        navigateTo(paneIndex, parentPath)
    }

    fun toggleSelection(paneIndex: Int, itemId: String) {
        if (paneIndex == 0) {
            _leftFiles.value = _leftFiles.value.map {
                if (it.id == itemId) it.copy(isSelected = !it.isSelected) else it
            }
        } else {
            _rightFiles.value = _rightFiles.value.map {
                if (it.id == itemId) it.copy(isSelected = !it.isSelected) else it
            }
        }
    }

    fun toggleFileSelection(itemId: String, paneIndex: Int) {
        toggleSelection(paneIndex, itemId)
    }

    fun selectAll(paneIndex: Int) {
        if (paneIndex == 0) {
            val allSelected = _leftFiles.value.all { it.isSelected }
            _leftFiles.value = _leftFiles.value.map { it.copy(isSelected = !allSelected) }
        } else {
            val allSelected = _rightFiles.value.all { it.isSelected }
            _rightFiles.value = _rightFiles.value.map { it.copy(isSelected = !allSelected) }
        }
    }

    fun toggleSelectAll(paneIndex: Int) {
        selectAll(paneIndex)
    }

    fun invertSelection(paneIndex: Int) {
        if (paneIndex == 0) {
            _leftFiles.value = _leftFiles.value.map { it.copy(isSelected = !it.isSelected) }
        } else {
            _rightFiles.value = _rightFiles.value.map { it.copy(isSelected = !it.isSelected) }
        }
    }

    fun copySelected(sourcePaneIndex: Int): Int {
        val sourcePath = if (sourcePaneIndex == 0) _leftPath.value else _rightPath.value
        val destPath = if (sourcePaneIndex == 0) _rightPath.value else _leftPath.value
        val sourceItems = virtualFileSystem[sourcePath] ?: return 0
        val destItems = virtualFileSystem.getOrPut(destPath) { mutableListOf() }

        val selected = sourceItems.filter { it.isSelected }
        for (item in selected) {
            val newPath = "$destPath/${item.name}"
            val copied = item.copy(
                id = newPath,
                path = newPath,
                isSelected = false
            )
            destItems.removeAll { it.name == item.name }
            destItems.add(copied)
        }
        refreshPanels()
        return selected.size
    }

    fun copySelectedToOtherPane(): Int {
        return copySelected(_activePaneIndex.value)
    }

    fun moveSelected(sourcePaneIndex: Int): Int {
        val sourcePath = if (sourcePaneIndex == 0) _leftPath.value else _rightPath.value
        val destPath = if (sourcePaneIndex == 0) _rightPath.value else _leftPath.value
        val sourceItems = virtualFileSystem[sourcePath] ?: return 0
        val destItems = virtualFileSystem.getOrPut(destPath) { mutableListOf() }

        val selected = sourceItems.filter { it.isSelected }
        for (item in selected) {
            val newPath = "$destPath/${item.name}"
            val moved = item.copy(
                id = newPath,
                path = newPath,
                isSelected = false
            )
            destItems.removeAll { it.name == item.name }
            destItems.add(moved)
            sourceItems.remove(item)
        }
        refreshPanels()
        return selected.size
    }

    fun moveSelectedToOtherPane(): Int {
        return moveSelected(_activePaneIndex.value)
    }

    fun deleteSelected(paneIndex: Int): Int {
        val path = if (paneIndex == 0) _leftPath.value else _rightPath.value
        val items = virtualFileSystem[path] ?: return 0
        val count = items.count { it.isSelected }
        items.removeAll { it.isSelected }
        refreshPanels()
        return count
    }

    fun deleteFile(itemId: String, paneIndex: Int) {
        val path = if (paneIndex == 0) _leftPath.value else _rightPath.value
        val items = virtualFileSystem[path] ?: return
        items.removeAll { it.id == itemId }
        refreshPanels()
    }

    fun renameItem(paneIndex: Int, oldName: String, newName: String) {
        val path = if (paneIndex == 0) _leftPath.value else _rightPath.value
        val items = virtualFileSystem[path] ?: return
        val itemIndex = items.indexOfFirst { it.name == oldName }
        if (itemIndex != -1) {
            val oldItem = items[itemIndex]
            val newPath = "$path/$newName"
            val newType = determineFileType(newName, oldItem.isDirectory)
            items[itemIndex] = oldItem.copy(
                id = newPath,
                name = newName,
                path = newPath,
                fileType = newType
            )
            refreshPanels()
        }
    }

    fun renameFile(itemId: String, paneIndex: Int, newName: String) {
        val path = if (paneIndex == 0) _leftPath.value else _rightPath.value
        val items = virtualFileSystem[path] ?: return
        val itemIndex = items.indexOfFirst { it.id == itemId }
        if (itemIndex != -1) {
            val oldItem = items[itemIndex]
            val newPath = "$path/$newName"
            val newType = determineFileType(newName, oldItem.isDirectory)
            items[itemIndex] = oldItem.copy(
                id = newPath,
                name = newName,
                path = newPath,
                fileType = newType
            )
            refreshPanels()
        }
    }

    // MT VIP Feature: Batch Regex Rename
    fun batchRegexRename(paneIndex: Int, pattern: String, replacement: String, addCounter: Boolean): Int {
        val path = if (paneIndex == 0) _leftPath.value else _rightPath.value
        val items = virtualFileSystem[path] ?: return 0
        var count = 0
        val selectedOrAll = if (items.any { it.isSelected }) items.filter { it.isSelected } else items

        selectedOrAll.forEachIndexed { index, item ->
            try {
                var newName = item.name.replace(Regex(pattern), replacement)
                if (addCounter) {
                    val dotIdx = newName.lastIndexOf('.')
                    newName = if (dotIdx != -1) {
                        newName.substring(0, dotIdx) + "_${index + 1}" + newName.substring(dotIdx)
                    } else {
                        "${newName}_${index + 1}"
                    }
                }
                if (newName != item.name) {
                    val newPath = "$path/$newName"
                    val idx = items.indexOf(item)
                    if (idx != -1) {
                        items[idx] = item.copy(name = newName, id = newPath, path = newPath)
                        count++
                    }
                }
            } catch (_: Exception) {}
        }
        refreshPanels()
        return count
    }

    fun batchRename(paneIndex: Int, pattern: String, replacement: String, addCounter: Boolean): Int {
        return batchRegexRename(paneIndex, pattern, replacement, addCounter)
    }

    fun createNewFile(paneIndex: Int, name: String, initialContent: String = "") {
        val path = if (paneIndex == 0) _leftPath.value else _rightPath.value
        val items = virtualFileSystem.getOrPut(path) { mutableListOf() }
        val filePath = "$path/$name"
        val type = determineFileType(name, false)
        items.add(createFile(name, filePath, initialContent.length.toLong(), "644", type, content = initialContent))
        refreshPanels()
    }

    fun createFile(paneIndex: Int, fileName: String, content: String = "") {
        createNewFile(paneIndex, fileName, content)
    }

    fun createNewFolder(paneIndex: Int, name: String) {
        val path = if (paneIndex == 0) _leftPath.value else _rightPath.value
        val items = virtualFileSystem.getOrPut(path) { mutableListOf() }
        val folderPath = "$path/$name"
        items.add(createFolder(name, folderPath, "755"))
        virtualFileSystem.getOrPut(folderPath) { mutableListOf() }
        refreshPanels()
    }

    fun createFolder(paneIndex: Int, folderName: String) {
        createNewFolder(paneIndex, folderName)
    }

    fun updateChmod(paneIndex: Int, itemId: String, octalChmod: String) {
        val path = if (paneIndex == 0) _leftPath.value else _rightPath.value
        val items = virtualFileSystem[path] ?: return
        val idx = items.indexOfFirst { it.id == itemId }
        if (idx != -1) {
            val item = items[idx]
            items[idx] = item.copy(
                chmodOctal = octalChmod,
                permissions = formatPermissions(octalChmod, item.isDirectory)
            )
            refreshPanels()
        }
    }

    fun updateChmod(itemId: String, paneIndex: Int, octalChmod: String) {
        updateChmod(paneIndex, itemId, octalChmod)
    }

    fun setSortMode(paneIndex: Int, mode: SortMode) {
        _currentSort.value = mode
        refreshPanels()
    }

    fun setSortMode(mode: SortMode) {
        _currentSort.value = mode
        refreshPanels()
    }

    fun setSearchFilter(paneIndex: Int, filter: String) {
        _searchQuery.value = filter
        refreshPanels()
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        refreshPanels()
    }

    private fun determineFileType(name: String, isDir: Boolean): FileType {
        if (isDir) return FileType.FOLDER
        val lower = name.lowercase(Locale.getDefault())
        return when {
            lower.endsWith(".apk") -> FileType.APK
            lower.endsWith(".dex") -> FileType.DEX
            lower.endsWith(".arsc") -> FileType.ARSC
            lower.endsWith(".smali") -> FileType.SMALI
            lower.endsWith(".xml") -> FileType.XML
            lower.endsWith(".so") -> FileType.SO
            lower.endsWith(".zip") -> FileType.ZIP
            lower.endsWith(".rar") -> FileType.RAR
            lower.endsWith(".7z") -> FileType.SEVEN_Z
            lower.endsWith(".jar") -> FileType.JAR
            lower.endsWith(".png") || lower.endsWith(".jpg") || lower.endsWith(".webp") || lower.endsWith(".svg") -> FileType.IMAGE
            lower.endsWith(".mp3") || lower.endsWith(".ogg") || lower.endsWith(".wav") -> FileType.AUDIO
            lower.endsWith(".mp4") || lower.endsWith(".mkv") -> FileType.VIDEO
            lower.endsWith(".json") -> FileType.JSON
            lower.endsWith(".txt") || lower.endsWith(".log") || lower.endsWith(".prop") -> FileType.TEXT
            lower.endsWith(".java") || lower.endsWith(".kt") || lower.endsWith(".sh") || lower.endsWith(".c") || lower.endsWith(".cpp") -> FileType.CODE
            lower.endsWith(".db") || lower.endsWith(".sqlite") -> FileType.DATABASE
            else -> FileType.UNKNOWN
        }
    }

    private fun formatFileSize(bytes: Long): String {
        if (bytes <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (Math.log10(bytes.toDouble()) / Math.log10(1024.0)).toInt()
        return String.format(Locale.US, "%.1f %s", bytes / Math.pow(1024.0, digitGroups.toDouble()), units[digitGroups])
    }

    private fun formatPermissions(octal: String, isDir: Boolean): String {
        val prefix = if (isDir) "d" else "-"
        val map = mapOf(
            '0' to "---", '1' to "--x", '2' to "-w-", '3' to "-wx",
            '4' to "r--", '5' to "r-x", '6' to "rw-", '7' to "rwx"
        )
        val cleanOctal = if (octal.length >= 3) octal.takeLast(3) else "755"
        val u = map[cleanOctal[0]] ?: "rwx"
        val g = map[cleanOctal[1]] ?: "r-x"
        val o = map[cleanOctal[2]] ?: "r-x"
        return "$prefix$u$g$o"
    }

    private fun computeHash(input: String, algorithm: String): String {
        return try {
            val md = MessageDigest.getInstance(algorithm)
            val bytes = md.digest(input.toByteArray())
            bytes.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            "N/A"
        }
    }
}
