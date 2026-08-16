package com.example.model

data class ArscResourceEntry(
    val id: String, // e.g. 0x7f0f0012
    val name: String, // e.g. app_name, vip_status_title
    val type: String, // string, color, drawable, id, layout, dimen, bool
    val originalValue: String,
    val translatedValue: String = "",
    val language: String = "tr"
)

data class ArscPackage(
    val packageName: String,
    val packageId: Int = 0x7f,
    val types: List<String> = listOf("string", "color", "drawable", "layout", "id", "dimen", "bool", "style"),
    val resources: List<ArscResourceEntry> = emptyList()
)

data class TranslationTask(
    val sourceLang: String = "en",
    val targetLang: String = "tr",
    val totalStrings: Int = 0,
    val translatedStrings: Int = 0,
    val status: String = "Hazır"
)
