package com.example.engine

import com.example.model.ArscPackage
import com.example.model.ArscResourceEntry
import com.example.model.TranslationTask
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ArscEditorEngine {

    private val _resources = MutableStateFlow<List<ArscResourceEntry>>(SampleWorkspaceData.createInitialArscResources())
    val resources: StateFlow<List<ArscResourceEntry>> = _resources.asStateFlow()

    private val _currentFilter = MutableStateFlow("all")
    val currentFilter: StateFlow<String> = _currentFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _translationTask = MutableStateFlow(
        TranslationTask(
            sourceLang = "en",
            targetLang = "tr",
            totalStrings = 8,
            translatedStrings = 8,
            status = "Tamamlandı"
        )
    )
    val translationTask: StateFlow<TranslationTask> = _translationTask.asStateFlow()

    fun updateResourceEntry(id: String, newOriginal: String, newTranslated: String) {
        _resources.value = _resources.value.map {
            if (it.id == id) {
                it.copy(originalValue = newOriginal, translatedValue = newTranslated)
            } else it
        }
    }

    fun addResourceEntry(name: String, type: String, value: String) {
        val nextId = "0x7f0f" + String.format("%04x", _resources.value.size + 1)
        val newEntry = ArscResourceEntry(
            id = nextId,
            name = name,
            type = type,
            originalValue = value,
            translatedValue = value
        )
        _resources.value = _resources.value + newEntry
    }

    fun deleteResourceEntry(id: String) {
        _resources.value = _resources.value.filterNot { it.id == id }
    }

    fun setFilter(type: String) {
        _currentFilter.value = type
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // MT VIP Feature: Auto-translate all strings to Turkish
    fun autoTranslateAllToTurkish(): Int {
        val dictionary = mapOf(
            "Target Pro Mod" to "Target Pro Mod (TR)",
            "This feature requires a VIP subscription." to "Bu özellik VIP abonelik gerektirir.",
            "VIP Membership activated successfully!" to "VIP Üyelik başarıyla aktifleştirildi!",
            "Buy 100,000 Gold Coins ($9.99)" to "100.000 Altın Al (VIP Ücretsiz Mod)",
            "Security Verification Alert" to "Güvenlik Doğrulama Uyarısı",
            "Unlimited Energy & Speed Booster" to "Sınırsız Enerji & Hızlandırıcı",
            "Settings & Mod Menu" to "Ayarlar & Mod Menüsü",
            "Enjoy Ad-Free Experience" to "Reklamsız Deneyimin Tadını Çıkarın"
        )

        var count = 0
        _resources.value = _resources.value.map { entry ->
            if (entry.type == "string") {
                val tr = dictionary[entry.originalValue] ?: "${entry.originalValue} (TR)"
                count++
                entry.copy(translatedValue = tr)
            } else entry
        }

        _translationTask.value = _translationTask.value.copy(
            translatedStrings = count,
            status = "%100 Çevrildi ($count dize)"
        )
        return count
    }

    // MT VIP Feature: Resource Cleaner / Defragmenter
    fun cleanUnusedResources(): Int {
        val before = _resources.value.size
        // Keep active items
        val cleaned = _resources.value.filter { it.name.isNotBlank() }
        _resources.value = cleaned
        return before - cleaned.size
    }
}
