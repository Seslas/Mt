package com.example.model

enum class FileType {
    FOLDER,
    APK,
    DEX,
    ARSC,
    SMALI,
    XML,
    SO,
    ZIP,
    RAR,
    SEVEN_Z,
    JAR,
    IMAGE,
    AUDIO,
    VIDEO,
    TEXT,
    JSON,
    CODE,
    DATABASE,
    UNKNOWN
}

data class MtFileItem(
    val id: String,
    val name: String,
    val path: String,
    val isDirectory: Boolean,
    val size: Long = 0L,
    val formattedSize: String = "",
    val lastModified: Long = System.currentTimeMillis(),
    val permissions: String = "rwxr-xr-x",
    val chmodOctal: String = "755",
    val fileType: FileType = FileType.UNKNOWN,
    val isSelected: Boolean = false,
    val isArchive: Boolean = false,
    val innerArchiveFiles: List<MtFileItem> = emptyList(),
    val content: String = "",
    val md5: String = "",
    val sha1: String = "",
    val sha256: String = ""
)

enum class SortMode(val titleTr: String) {
    NAME_ASC("Ada Göre (A-Z)"),
    NAME_DESC("Ada Göre (Z-A)"),
    SIZE_DESC("Boyuta Göre (Büyük-Küçük)"),
    SIZE_ASC("Boyuta Göre (Küçük-Büyük)"),
    DATE_DESC("Tarihe Göre (Yeni-Eski)"),
    DATE_ASC("Tarihe Göre (Eski-Yeni)"),
    TYPE_ASC("Türe Göre")
}

data class StorageVolumeInfo(
    val name: String,
    val path: String,
    val totalBytes: Long,
    val freeBytes: Long,
    val iconName: String
)

data class BookmarkItem(
    val name: String,
    val path: String,
    val isSystem: Boolean = false
)
