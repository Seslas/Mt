package com.example.model

enum class SignatureScheme(val label: String) {
    V1_JAR("V1 (JAR İmza)"),
    V2_APK("V2 (APK İmza Bloğu)"),
    V3_APK("V3 (Android 9+)"),
    V4_APK("V4 (Android 11+ Streaming)")
}

data class ApkSignConfig(
    val signatureSchemes: Set<SignatureScheme> = setOf(
        SignatureScheme.V1_JAR,
        SignatureScheme.V2_APK,
        SignatureScheme.V3_APK
    ),
    val useMtTestKey: Boolean = true,
    val customKeystorePath: String = "",
    val keystorePassword: String = "",
    val keyAlias: String = "",
    val keyPassword: String = "",
    val injectSignatureKiller: Boolean = true, // MT VIP signature killer bypass
    val zipalignAfterSign: Boolean = true
)

enum class SmaliHookType(val titleTr: String, val descriptionTr: String) {
    TOAST_POPUP("Toast Mesajı Ekle", "Uygulama açılışında özel modlayıcı Toast mesajı gösterir."),
    DIALOG_POPUP("Açılış Diyaloğu Ekle", "Uygulama başladığında özel başlık ve butonlu duyuru diyaloğu çıkarır."),
    SIGNATURE_KILLER("İmza Doğrulama Bypass (VIP)", "PackageManager imza hash kontrolünü orijinal hash ile sahteler."),
    ANTI_ROOT_BYPASS("Root / Magisk Kontrolü Kırıcı (VIP)", "Root varlık kontrollerini (su binary, test-keys) otomatik devre dışı bırakır."),
    ANTI_DEBUG_KILLER("Anti-Debug & Frida Koruması Kırıcı (VIP)", "PTRACE_TRACEME ve debugger tespitlerini etkisizleştirir."),
    FORCE_RETURN_TRUE("VIP / Lisans Kontrolünü True Yap (VIP)", "isVip(), isPurchased(), checkLicense() metotlarının return değerini 1/true yapar.")
}
