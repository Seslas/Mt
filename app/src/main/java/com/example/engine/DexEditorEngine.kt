package com.example.engine

import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DexEditorEngine {

    private val _dexClasses = MutableStateFlow<List<DexClass>>(SampleWorkspaceData.createInitialDexClasses())
    val dexClasses: StateFlow<List<DexClass>> = _dexClasses.asStateFlow()

    private val _selectedClass = MutableStateFlow<DexClass?>(_dexClasses.value.firstOrNull())
    val selectedClass: StateFlow<DexClass?> = _selectedClass.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Pair<DexClass, String>>>(emptyList())
    val searchResults: StateFlow<List<Pair<DexClass, String>>> = _searchResults.asStateFlow()

    fun selectClass(dexClass: DexClass) {
        _selectedClass.value = dexClass
    }

    fun updateSmaliCode(className: String, newCode: String) {
        val updated = _dexClasses.value.map {
            if (it.className == className) {
                it.copy(smaliCode = newCode)
            } else it
        }
        _dexClasses.value = updated
        if (_selectedClass.value?.className == className) {
            _selectedClass.value = updated.firstOrNull { it.className == className }
        }
    }

    // MT VIP Feature: Force Return True / Bypass Check
    fun patchMethodToReturnTrue(className: String, methodName: String): Boolean {
        val currentClass = _dexClasses.value.firstOrNull { it.className == className } ?: return false
        val smali = currentClass.smaliCode
        
        // Find method block
        val methodHeader = ".method public $methodName"
        val altHeader = ".method public final $methodName"
        val altHeader2 = ".method public static $methodName"

        var patchedSmali = smali
        val targetSnippet = """
.method public isPremiumUser()Z
    .registers 2
    const/4 v0, 0x1
    return v0
.end method
""".trimIndent()

        if (smali.contains("isPremiumUser()Z")) {
            val startIdx = smali.indexOf(".method public isPremiumUser()Z")
            if (startIdx != -1) {
                val endIdx = smali.indexOf(".end method", startIdx)
                if (endIdx != -1) {
                    val fullMethod = smali.substring(startIdx, endIdx + ".end method".length)
                    patchedSmali = smali.replace(fullMethod, targetSnippet)
                }
            }
        } else {
            patchedSmali = smali.replace("const/4 v0, 0x0", "const/4 v0, 0x1")
        }

        updateSmaliCode(className, patchedSmali)
        return true
    }

    fun searchDex(query: String) {
        _searchQuery.value = query
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }
        val results = mutableListOf<Pair<DexClass, String>>()
        for (c in _dexClasses.value) {
            if (c.className.contains(query, ignoreCase = true)) {
                results.add(Pair(c, "Sınıf adı eşleşti: ${c.className}"))
            }
            if (c.smaliCode.contains(query, ignoreCase = true)) {
                val lines = c.smaliCode.lines()
                val matchedLine = lines.firstOrNull { it.contains(query, ignoreCase = true) } ?: ""
                results.add(Pair(c, "Kod satırı: ${matchedLine.trim()}"))
            }
        }
        _searchResults.value = results
    }

    // MT VIP Feature: Batch String Replacement
    fun batchReplaceString(oldStr: String, newStr: String): Int {
        var count = 0
        val updated = _dexClasses.value.map { c ->
            if (c.smaliCode.contains(oldStr)) {
                count++
                c.copy(smaliCode = c.smaliCode.replace(oldStr, newStr))
            } else c
        }
        _dexClasses.value = updated
        _selectedClass.value = updated.firstOrNull { it.className == _selectedClass.value?.className }
        return count
    }

    // MT VIP Feature: DEX Obfuscate Simulation
    fun obfuscateDex(): String {
        var mapCount = 0
        val updated = _dexClasses.value.map { c ->
            mapCount++
            c.copy(
                smaliCode = "# MT Manager VIP Obfuscation Applied\n# ProGuard/Allatori bytecode scrambled\n" + c.smaliCode
            )
        }
        _dexClasses.value = updated
        return "$mapCount sınıf başarıyla şifrelendi ve gizlendi (Obfuscated)."
    }
}
