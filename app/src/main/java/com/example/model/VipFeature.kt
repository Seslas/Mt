package com.example.model

enum class VipPlan(val titleTr: String, val badgeTr: String) {
    FREE("Ücretsiz", "FREE"),
    DAILY("1 Günlük VIP", "1 GÜN"),
    WEEKLY("7 Günlük VIP", "7 GÜN"),
    LIFETIME("Sınırsız Yönetici VIP", "ÖMÜR BOYU")
}

data class VipState(
    val isActive: Boolean = false,
    val plan: VipPlan = VipPlan.FREE,
    val expiryTimestamp: Long = 0L,
    val activatedCode: String = ""
)

data class VipFeature(
    val id: String,
    val titleTr: String,
    val categoryTr: String,
    val descriptionTr: String,
    val isUnlocked: Boolean = true,
    val iconName: String = "crown"
)

object VipFeaturesRegistry {
    val ALL_VIP_FEATURES = listOf(
        VipFeature(
            id = "dex_editor_plus",
            titleTr = "DEX Düzenleyici++",
            categoryTr = "DEX & Bytecode",
            descriptionTr = "Sınırsız DEX ayrıştırma, Smali sözdizimi vurgulama, metot imza araması ve anlık sınıf yamalama."
        ),
        VipFeature(
            id = "arsc_translator",
            titleTr = "ARSC & XML Çevirmen Pro",
            categoryTr = "Kaynaklar & Dil",
            descriptionTr = "Tüm resources.arsc dizelerini otomatik Türkçe ve çoklu dillere çevirme, eksik yerelleştirmeleri tamamlama."
        ),
        VipFeature(
            id = "signature_killer",
            titleTr = "Signature Killer (İmza Katili)",
            categoryTr = "APK İmzası",
            descriptionTr = "APK imzası doğrulama korumalarını (V1/V2/V3) otomatik olarak devre dışı bırakan bytecode enjektörü."
        ),
        VipFeature(
            id = "smali_hook_injector",
            titleTr = "Smali Hook & Kod Enjektörü",
            categoryTr = "Tersine Mühendislik",
            descriptionTr = "Açılış Toast'ı, Özel Dialog, Root Gizleme ve Lisans Kontrolü atlatma kodlarını tek tıkla enjekte etme."
        ),
        VipFeature(
            id = "apk_cloner",
            titleTr = "APK Klonlayıcı (Çift Paket)",
            categoryTr = "Paket Araçları",
            descriptionTr = "Paket adı ve Provider yetkilerini otomatik değiştirerek aynı cihazda birden fazla kopya çalıştırma."
        ),
        VipFeature(
            id = "anti_decompile_protector",
            titleTr = "Anti-Decompile & Kod Koruyucu",
            categoryTr = "Güvenlik & Obfuscation",
            descriptionTr = "Manifest gizleme, dize şifreleme ve sahte bytecode ekleyerek ayrıştırıcıları yanıltma."
        ),
        VipFeature(
            id = "binary_xml_editor",
            titleTr = "Binary AXML Decompiler & Düzenleyici",
            categoryTr = "XML Araçları",
            descriptionTr = "Android ikili XML ve AndroidManifest.xml dosyalarını anında okunabilir XML'e çevirme ve düzenleme."
        ),
        VipFeature(
            id = "hex_editor_pro",
            titleTr = "Hex Düzenleyici Pro",
            categoryTr = "İkili Düzenleme",
            descriptionTr = "Bayt akışı, ofset atlama, bayt dizisi arama ve doğrudan ikili veri yamalama."
        ),
        VipFeature(
            id = "batch_tools",
            titleTr = "Toplu Regex ve Dosya İşleme",
            categoryTr = "Dosya Sistemi",
            descriptionTr = "Regex desenleriyle toplu dosya yeniden adlandırma, toplu çıkarma ve arşiv içi doğrudan dosya değişimi."
        ),
        VipFeature(
            id = "root_terminal",
            titleTr = "Gelişmiş Root Terminal & Shell",
            categoryTr = "Sistem & Root",
            descriptionTr = "Linux shell, paket yöneticisi (pm), aktivite yöneticisi (am) ve dumpsys logcat konsolu."
        )
    )
}
