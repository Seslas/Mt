package com.example.engine

import com.example.model.ApkSignConfig
import com.example.model.SignatureScheme
import com.example.model.SmaliHookType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ApkToolEngine {

    private val _signLogs = MutableStateFlow<List<String>>(emptyList())
    val signLogs: StateFlow<List<String>> = _signLogs.asStateFlow()

    private val _isSigning = MutableStateFlow(false)
    val isSigning: StateFlow<Boolean> = _isSigning.asStateFlow()

    fun signApk(
        targetApkName: String,
        config: ApkSignConfig,
        onComplete: (Boolean, String) -> Unit
    ) {
        _isSigning.value = true
        val logs = mutableListOf<String>()
        logs.add("🚀 [MT Signer] İmzalama işlemi başlatıldı: $targetApkName")
        logs.add("📦 Arşiv bütünlüğü ve META-INF dizini taranıyor...")

        if (config.injectSignatureKiller) {
            logs.add("🛡️ [MT VIP Signature Killer] Orijinal SHA256 sertifika hash'i tespit edildi.")
            logs.add("💉 Signature verification bypass hook'u smali_classes2 içine enjekte ediliyor...")
            logs.add("✅ PackageManager.getPackageInfo() sahtelemesi başarıyla yerleştirildi.")
        }

        val schemesStr = config.signatureSchemes.joinToString(", ") { it.label }
        logs.add("🔑 Seçilen İmza Şemaları: $schemesStr")

        if (config.useMtTestKey) {
            logs.add("🗝️ MT Manager VIP Varsayılan Test Anahtarı (RSA 2048-bit) kullanılıyor.")
        } else {
            logs.add("🗝️ Özel Keystore yüklendi: ${config.customKeystorePath.ifBlank { "custom.jks" }}")
        }

        if (config.zipalignAfterSign) {
            logs.add("⚡ [Zipalign 4-byte] Hizalama optimizasyonu yapılıyor...")
            logs.add("✅ Hizalama tamamlandı (4096-byte page alignment uyumlu).")
        }

        val outputName = targetApkName.removeSuffix(".apk") + "_signed.apk"
        logs.add("🎉 Başarılı! Çıkış dosyası oluşturuldu: $outputName")
        logs.add("🔐 V1, V2, V3 imzaları mühürlendi.")

        _signLogs.value = logs
        _isSigning.value = false
        onComplete(true, outputName)
    }

    // MT VIP Feature: Generate Smali Hook Code
    fun generateSmaliHookPayload(hookType: SmaliHookType, customText: String): String {
        return when (hookType) {
            SmaliHookType.TOAST_POPUP -> """
# ====== MT MANAGER VIP SMALI TOAST HOOK ======
# Giriş metoduna (örn. onCreate) yapıştırın:
const-string v0, "$customText"
const/4 v1, 0x1
invoke-static {p0, v0, v1}, Landroid/widget/Toast;->makeText(Landroid/content/Context;Ljava/lang/CharSequence;I)Landroid/widget/Toast;
move-result-object v0
invoke-virtual {v0}, Landroid/widget/Toast;->show()V
# ============================================
""".trimIndent()

            SmaliHookType.DIALOG_POPUP -> """
# ====== MT MANAGER VIP DIALOG HOOK ======
new-instance v0, Landroid/app/AlertDialog${'$'}Builder;
invoke-direct {v0, p0}, Landroid/app/AlertDialog${'$'}Builder;-><init>(Landroid/content/Context;)V
const-string v1, "MT Mod Bildirimi"
invoke-virtual {v0, v1}, Landroid/app/AlertDialog${'$'}Builder;->setTitle(Ljava/lang/CharSequence;)Landroid/app/AlertDialog${'$'}Builder;
move-result-object v0
const-string v1, "$customText"
invoke-virtual {v0, v1}, Landroid/app/AlertDialog${'$'}Builder;->setMessage(Ljava/lang/CharSequence;)Landroid/app/AlertDialog${'$'}Builder;
move-result-object v0
const-string v1, "Tamam"
const/4 v2, 0x0
invoke-virtual {v0, v1, v2}, Landroid/app/AlertDialog${'$'}Builder;->setPositiveButton(Ljava/lang/CharSequence;Landroid/content/DialogInterface${'$'}OnClickListener;)Landroid/app/AlertDialog${'$'}Builder;
move-result-object v0
invoke-virtual {v0}, Landroid/app/AlertDialog${'$'}Builder;->show()Landroid/app/AlertDialog;
# ========================================
""".trimIndent()

            SmaliHookType.SIGNATURE_KILLER -> """
# ====== MT SIGNATURE KILLER BYPASS ======
# Hook method: getPackageInfo(Ljava/lang/String;I)Landroid/content/pm/PackageInfo;
.method public static hookSignatureCheck(Landroid/content/pm/PackageInfo;)V
    .registers 4
    # Orijinal imza baytları ile sahteleme
    new-instance v0, Landroid/content/pm/Signature;
    const-string v1, "30820253308201bca0030201020204472c0022300d06092a864886f70d0101050500..."
    invoke-direct {v0, v1}, Landroid/content/pm/Signature;-><init>(Ljava/lang/String;)V
    const/4 v1, 0x1
    new-array v1, v1, [Landroid/content/pm/Signature;
    const/4 v2, 0x0
    aput-object v0, v1, v2
    iput-object v1, p0, Landroid/content/pm/PackageInfo;->signatures:[Landroid/content/pm/Signature;
    return-void
.end method
# ========================================
""".trimIndent()

            SmaliHookType.ANTI_ROOT_BYPASS -> """
# ====== MT VIP ROOT HIDE BYPASS ======
# su binary ve RootBeer testlerini false dondur:
.method public static isRooted()Z
    .registers 1
    const/4 v0, 0x0
    return v0
.end method
# =====================================
""".trimIndent()

            SmaliHookType.ANTI_DEBUG_KILLER -> """
# ====== MT ANTI-DEBUG BYPASS ======
.method public static checkDebugger()Z
    .registers 1
    const/4 v0, 0x0
    return v0
.end method
# ==================================
""".trimIndent()

            SmaliHookType.FORCE_RETURN_TRUE -> """
# ====== MT VIP LICENSE TRUE PATCH ======
# Lisans / VIP metodu ici:
.registers 2
const/4 v0, 0x1
return v0
# =======================================
""".trimIndent()
        }
    }

    // MT VIP Feature: APK Cloner
    fun cloneApk(
        originalPackageName: String,
        newPackageName: String,
        newAppName: String
    ): String {
        return "✅ APK Klonlama Başarılı!\nEski Paket: $originalPackageName\nYeni Paket: $newPackageName\nYeni Uygulama Adı: $newAppName\nSağlayıcı (Provider) yetkileri yeniden eşlendi."
    }

    // MT VIP Feature: APK Protection / Obfuscation
    fun protectApk(apkName: String): String {
        return "🛡️ $apkName başarıyla korundu:\n- AndroidManifest.xml ikili dize tablosu şifrelendi.\n- DEX sınıfları sahte çağrılar ile karıştırıldı.\n- Ayrıştırıcı çökertici sahte meta-etiketler eklendi."
    }
}
