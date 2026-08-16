package com.example.model

data class DexClass(
    val className: String, // e.g. com.example.vip.LicenseChecker
    val simpleName: String,
    val packageName: String,
    val superClassName: String = "java.lang.Object",
    val accessFlags: String = "public final",
    val fields: List<DexField> = emptyList(),
    val methods: List<DexMethod> = emptyList(),
    val smaliCode: String = ""
)

data class DexField(
    val name: String,
    val type: String,
    val accessFlags: String = "private",
    val initialValue: String = ""
)

data class DexMethod(
    val name: String,
    val returnType: String,
    val parameters: List<String> = emptyList(),
    val signature: String, // e.g. checkLicense(Landroid/content/Context;)Z
    val accessFlags: String = "public",
    val registersCount: Int = 4,
    val smaliBody: String = "",
    val isVipTarget: Boolean = false
)

data class DexStringEntry(
    val index: Int,
    val value: String,
    val referencesCount: Int = 1
)

data class DexPackageNode(
    val name: String,
    val fullPath: String,
    val isPackage: Boolean,
    val classes: List<DexClass> = emptyList(),
    val subPackages: List<DexPackageNode> = emptyList()
)
