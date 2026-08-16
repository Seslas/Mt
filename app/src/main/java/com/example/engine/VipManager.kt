package com.example.engine

import com.example.model.VipPlan
import com.example.model.VipState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed class RedeemResult {
    data class Success(val plan: VipPlan, val message: String) : RedeemResult()
    data class Error(val message: String) : RedeemResult()
}

class VipManager {
    private val _vipState = MutableStateFlow(
        VipState(
            isActive = false,
            plan = VipPlan.FREE,
            expiryTimestamp = 0L,
            activatedCode = ""
        )
    )
    val vipState: StateFlow<VipState> = _vipState.asStateFlow()

    fun isVipActive(): Boolean {
        val state = _vipState.value
        if (!state.isActive) return false
        if (state.plan == VipPlan.LIFETIME) return true
        return System.currentTimeMillis() < state.expiryTimestamp
    }

    fun redeemCode(inputCode: String): RedeemResult {
        val normalized = inputCode.trim().lowercase(Locale.ROOT)
            .replace("ü", "u")
            .replace("ı", "i")
            .replace("ö", "o")
            .replace("ş", "s")
            .replace("ç", "c")
            .replace("ğ", "g")

        val now = System.currentTimeMillis()
        val currentExpiry = if (_vipState.value.expiryTimestamp > now) _vipState.value.expiryTimestamp else now

        return when (normalized) {
            "1gunluk" -> {
                val oneDayMillis = 24L * 60L * 60L * 1000L
                val newExpiry = currentExpiry + oneDayMillis
                _vipState.value = VipState(
                    isActive = true,
                    plan = VipPlan.DAILY,
                    expiryTimestamp = newExpiry,
                    activatedCode = "1gunluk"
                )
                RedeemResult.Success(
                    VipPlan.DAILY,
                    "🎉 Tebrikler! 1 Günlük VIP Üyeliğiniz başarıyla aktifleştirildi."
                )
            }
            "7gunluk" -> {
                val sevenDaysMillis = 7L * 24L * 60L * 60L * 1000L
                val newExpiry = currentExpiry + sevenDaysMillis
                _vipState.value = VipState(
                    isActive = true,
                    plan = VipPlan.WEEKLY,
                    expiryTimestamp = newExpiry,
                    activatedCode = "7gunluk"
                )
                RedeemResult.Success(
                    VipPlan.WEEKLY,
                    "🎉 Harika! 7 Günlük VIP Üyeliğiniz başarıyla aktifleştirildi."
                )
            }
            "yoneticivipi1234" -> {
                _vipState.value = VipState(
                    isActive = true,
                    plan = VipPlan.LIFETIME,
                    expiryTimestamp = -1L,
                    activatedCode = "yoneticivipi1234"
                )
                RedeemResult.Success(
                    VipPlan.LIFETIME,
                    "👑 SINIRSIZ YÖNETİCİ VIP Aktif! Tüm MT Manager VIP ayrıcalıkları ömür boyu kilitsiz."
                )
            }
            else -> {
                RedeemResult.Error(
                    "❌ Geçersiz VIP Kodu! Lütfen geçerli bir kod girin (Örn: 1gunluk, 7gunluk, yoneticivipi1234)."
                )
            }
        }
    }

    fun getExpiryFormatted(): String {
        val state = _vipState.value
        if (!state.isActive) return "Aktif Değil (Ücretsiz Sürüm)"
        if (state.plan == VipPlan.LIFETIME) return "Sınırsız (Ömür Boyu)"
        return SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(state.expiryTimestamp))
    }

    fun getRemainingTimeFormatted(): String {
        val state = _vipState.value
        if (!state.isActive) return "Kilitli"
        if (state.plan == VipPlan.LIFETIME) return "Sınırsız / Ömür Boyu"
        val remainingMillis = state.expiryTimestamp - System.currentTimeMillis()
        if (remainingMillis <= 0) return "Süresi Doldu"

        val hours = remainingMillis / (1000 * 60 * 60)
        val minutes = (remainingMillis / (1000 * 60)) % 60
        val days = hours / 24
        val remHours = hours % 24

        return if (days > 0) {
            "$days gün $remHours saat kaldı"
        } else {
            "$hours saat $minutes dakika kaldı"
        }
    }

    fun revokeVip() {
        _vipState.value = VipState(
            isActive = false,
            plan = VipPlan.FREE,
            expiryTimestamp = 0L,
            activatedCode = ""
        )
    }
}
