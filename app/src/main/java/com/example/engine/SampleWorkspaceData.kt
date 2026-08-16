package com.example.engine

import com.example.model.*

object SampleWorkspaceData {

    fun createInitialDexClasses(): List<DexClass> {
        val mainActivitySmali = """
.class public Lcom/target/game/MainActivity;
.super Landroidx/appcompat/app/AppCompatActivity;
.source "MainActivity.java"

# static fields
.field private static final TAG:Ljava/lang/String; = "TargetModApp"
.field public static isVipUnlocked:Z = false

# instance fields
.field private authService:Lcom/target/game/auth/LicenseChecker;
.field private coinCount:I
.field private statusTextView:Landroid/widget/TextView;

# direct methods
.method public constructor <init>()V
    .registers 2

    .line 18
    invoke-direct {p0}, Landroidx/appcompat/app/AppCompatActivity;-><init>()V

    const/4 v0, 0x0
    iput v0, p0, Lcom/target/game/MainActivity;->coinCount:I

    return-void
.end method

.method protected onCreate(Landroid/os/Bundle;)V
    .registers 4
    .param p1, "savedInstanceState"    # Landroid/os/Bundle;

    .line 28
    invoke-super {p0, p1}, Landroidx/appcompat/app/AppCompatActivity;->onCreate(Landroid/os/Bundle;)V
    const v0, 0x7f0b001c # layout:activity_main
    invoke-virtual {p0, v0}, Lcom/target/game/MainActivity;->setContentView(I)V

    # Initialize license checker
    new-instance v0, Lcom/target/game/auth/LicenseChecker;
    invoke-direct {v0, p0}, Lcom/target/game/auth/LicenseChecker;-><init>(Landroid/content/Context;)V
    iput-object v0, p0, Lcom/target/game/MainActivity;->authService:Lcom/target/game/auth/LicenseChecker;

    # Check VIP Status
    invoke-virtual {v0}, Lcom/target/game/auth/LicenseChecker;->isPremiumUser()Z
    move-result v1
    sput-boolean v1, Lcom/target/game/MainActivity;->isVipUnlocked:Z

    if-eqz v1, :cond_vip_locked
    const-string v0, "VIP Aktif - Tum Ozellikler Acik!"
    invoke-direct {p0, v0}, Lcom/target/game/MainActivity;->showWelcomeToast(Ljava/lang/String;)V
    goto :goto_end

    :cond_vip_locked
    const-string v0, "Ucretsiz Surum - Satin Alin"
    invoke-direct {p0, v0}, Lcom/target/game/MainActivity;->showWelcomeToast(Ljava/lang/String;)V

    :goto_end
    return-void
.end method

.method private showWelcomeToast(Ljava/lang/String;)V
    .registers 4
    .param p1, "msg"    # Ljava/lang/String;

    const/4 v0, 0x1
    invoke-static {p0, p1, v0}, Landroid/widget/Toast;->makeText(Landroid/content/Context;Ljava/lang/CharSequence;I)Landroid/widget/Toast;
    move-result-object v1
    invoke-virtual {v1}, Landroid/widget/Toast;->show()V
    return-void
.end method
""".trimIndent()

        val licenseCheckerSmali = """
.class public Lcom/target/game/auth/LicenseChecker;
.super Ljava/lang/Object;
.source "LicenseChecker.java"

# instance fields
.field private mContext:Landroid/content/Context;

# direct methods
.method public constructor <init>(Landroid/content/Context;)V
    .registers 2
    .param p1, "context"    # Landroid/content/Context;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V
    iput-object p1, p0, Lcom/target/game/auth/LicenseChecker;->mContext:Landroid/content/Context;
    return-void
.end method

# Metot: isPremiumUser (Hedef VIP Kontrol Metodu)
.method public isPremiumUser()Z
    .registers 3

    # [VIP Mod Hedefi: const/4 v0, 0x1 ve return v0 yap]
    const/4 v0, 0x0
    # Orijinal sunucu lisans kontrol kodu burada calisir
    return v0
.end method

.method public checkSignatureIntegrity()Z
    .registers 4

    # Signature hash verification logic
    const/4 v0, 0x1
    return v0
.end method

.method public getCoinsBalance()I
    .registers 2

    const v0, 0x186a0 # 100000
    return v0
.end method
""".trimIndent()

        val networkHelperSmali = """
.class public Lcom/target/game/network/NetworkHelper;
.super Ljava/lang/Object;
.source "NetworkHelper.java"

# static fields
.field public static final BASE_URL:Ljava/lang/String; = "https://api.targetmodapp.com/v2"
.field public static final API_KEY:Ljava/lang/String; = "MT_KEY_9921_VIP_SECURE"

# direct methods
.method public constructor <init>()V
    .registers 1
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V
    return-void
.end method

.method public static fetchServerConfig()Ljava/lang/String;
    .registers 1
    const-string v0, "{\"status\":\"success\",\"vip_bypass\":true,\"tier\":\"VIP_GOLD\"}"
    return-object v0
.end method
""".trimIndent()

        val antiRootCheckerSmali = """
.class public Lcom/target/game/security/AntiRootChecker;
.super Ljava/lang/Object;
.source "AntiRootChecker.java"

# direct methods
.method public constructor <init>()V
    .registers 1
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V
    return-void
.end method

.method public static isDeviceRooted()Z
    .registers 2

    # Root / Magisk tespit kodu
    const/4 v0, 0x0
    return v0
.end method
""".trimIndent()

        return listOf(
            DexClass(
                className = "com.target.game.MainActivity",
                simpleName = "MainActivity",
                packageName = "com.target.game",
                superClassName = "androidx.appcompat.app.AppCompatActivity",
                methods = listOf(
                    DexMethod("<init>", "V", emptyList(), "<init>()V", "public", 2),
                    DexMethod("onCreate", "V", listOf("Landroid/os/Bundle;"), "onCreate(Landroid/os/Bundle;)V", "protected", 4),
                    DexMethod("showWelcomeToast", "V", listOf("Ljava/lang/String;"), "showWelcomeToast(Ljava/lang/String;)V", "private", 4)
                ),
                fields = listOf(
                    DexField("TAG", "Ljava/lang/String;", "private static final", "TargetModApp"),
                    DexField("isVipUnlocked", "Z", "public static", "false"),
                    DexField("coinCount", "I", "private", "0")
                ),
                smaliCode = mainActivitySmali
            ),
            DexClass(
                className = "com.target.game.auth.LicenseChecker",
                simpleName = "LicenseChecker",
                packageName = "com.target.game.auth",
                methods = listOf(
                    DexMethod("<init>", "V", listOf("Landroid/content/Context;"), "<init>(Landroid/content/Context;)V", "public", 2),
                    DexMethod("isPremiumUser", "Z", emptyList(), "isPremiumUser()Z", "public", 2, isVipTarget = true),
                    DexMethod("checkSignatureIntegrity", "Z", emptyList(), "checkSignatureIntegrity()Z", "public", 2),
                    DexMethod("getCoinsBalance", "I", emptyList(), "getCoinsBalance()I", "public", 2)
                ),
                fields = listOf(
                    DexField("mContext", "Landroid/content/Context;", "private")
                ),
                smaliCode = licenseCheckerSmali
            ),
            DexClass(
                className = "com.target.game.network.NetworkHelper",
                simpleName = "NetworkHelper",
                packageName = "com.target.game.network",
                methods = listOf(
                    DexMethod("<init>", "V", emptyList(), "<init>()V", "public", 1),
                    DexMethod("fetchServerConfig", "Ljava/lang/String;", emptyList(), "fetchServerConfig()Ljava/lang/String;", "public static", 2)
                ),
                fields = listOf(
                    DexField("BASE_URL", "Ljava/lang/String;", "public static final", "https://api.targetmodapp.com/v2"),
                    DexField("API_KEY", "Ljava/lang/String;", "public static final", "MT_KEY_9921_VIP_SECURE")
                ),
                smaliCode = networkHelperSmali
            ),
            DexClass(
                className = "com.target.game.security.AntiRootChecker",
                simpleName = "AntiRootChecker",
                packageName = "com.target.game.security",
                methods = listOf(
                    DexMethod("<init>", "V", emptyList(), "<init>()V", "public", 1),
                    DexMethod("isDeviceRooted", "Z", emptyList(), "isDeviceRooted()Z", "public static", 2)
                ),
                smaliCode = antiRootCheckerSmali
            )
        )
    }

    fun createInitialArscResources(): List<ArscResourceEntry> {
        return listOf(
            ArscResourceEntry("0x7f0f0001", "app_name", "string", "Target Pro Mod", "Target Pro Mod"),
            ArscResourceEntry("0x7f0f0002", "vip_locked_message", "string", "This feature requires a VIP subscription.", "Bu özellik VIP abonelik gerektirir."),
            ArscResourceEntry("0x7f0f0003", "vip_unlocked_toast", "string", "VIP Membership activated successfully!", "VIP Üyelik başarıyla aktifleştirildi!"),
            ArscResourceEntry("0x7f0f0004", "btn_buy_coins", "string", "Buy 100,000 Gold Coins ($9.99)", "100.000 Altın Satın Al (Ücretsiz Mod)"),
            ArscResourceEntry("0x7f0f0005", "dialog_title_warning", "string", "Security Verification Alert", "Güvenlik Doğrulama Uyarısı"),
            ArscResourceEntry("0x7f0f0006", "label_unlimited_energy", "string", "Unlimited Energy & Speed Booster", "Sınırsız Enerji & Hız Artırıcı"),
            ArscResourceEntry("0x7f0f0007", "menu_settings", "string", "Settings & Mod Menu", "Ayarlar & Mod Menüsü"),
            ArscResourceEntry("0x7f0f0008", "no_ads_text", "string", "Enjoy Ad-Free Experience", "Reklamsız Deneyimin Tadını Çıkarın"),
            ArscResourceEntry("0x7f060001", "primary_color", "color", "#FF1E88E5", "#FF1E88E5"),
            ArscResourceEntry("0x7f060002", "vip_gold_accent", "color", "#FFFFB300", "#FFFFB300"),
            ArscResourceEntry("0x7f080001", "ic_launcher", "drawable", "@drawable/ic_target_app", "@drawable/ic_target_app"),
            ArscResourceEntry("0x7f040001", "is_ad_enabled", "bool", "true", "false")
        )
    }

    val sampleAndroidManifest = """
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.target.game"
    android:versionCode="104"
    android:versionName="1.4.0">

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="com.android.vending.BILLING" />

    <application
        android:allowBackup="true"
        android:icon="@drawable/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@drawable/ic_launcher"
        android:supportsRtl="true"
        android:theme="@style/Theme.TargetApp">

        <activity
            android:name="com.target.game.MainActivity"
            android:exported="true"
            android:configChanges="orientation|screenSize"
            android:theme="@style/Theme.TargetApp.NoActionBar">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <activity
            android:name="com.target.game.ShopActivity"
            android:exported="false" />

        <service
            android:name="com.target.game.auth.LicenseService"
            android:exported="false" />

    </application>
</manifest>
""".trimIndent()

    val sampleHexData = "50 4B 03 04 14 00 08 08 08 00 23 8A 61 54 00 00 00 00 00 00 00 00 00 00 00 00 14 00 00 00 41 6E 64 72 6F 69 64 4D 61 6E 69 66 65 73 74 2E 78 6D 6C 03 00 08 00 00 00 63 6C 61 73 73 65 73 2E 64 65 78 72 65 73 6F 75 72 63 65 73 2E 61 72 73 63"
}
