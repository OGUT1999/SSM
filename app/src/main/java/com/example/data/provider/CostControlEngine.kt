package com.example.data.provider

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class CostControlStatus(
    val budgetMode: String = "MODAL_RP0_ACTIVE",
    val monthlyCostIdr: Double = 0.0,
    val apiCallsTotal: Int = 0,
    val localFallbackCallsTotal: Int = 0,
    val cacheHitRatePercent: Int = 100,
    val costProtectionActive: Boolean = true,
    val activeAiProvider: String = "Local Rule Engine ($0)",
    val activeMapProvider: String = "Device GPS + Regional Cache ($0)",
    val activeWeatherProvider: String = "Local Cache Provider ($0)"
)

class CostControlEngine {

    private val _status = MutableStateFlow(CostControlStatus())
    val status: StateFlow<CostControlStatus> = _status.asStateFlow()

    fun recordApiCall(providerName: String, isLocalFallback: Boolean) {
        val current = _status.value
        val newTotal = current.apiCallsTotal + 1
        val newFallback = if (isLocalFallback) current.localFallbackCallsTotal + 1 else current.localFallbackCallsTotal
        val cacheHit = if (newTotal > 0) ((newFallback.toDouble() / newTotal) * 100).toInt() else 100

        _status.value = current.copy(
            apiCallsTotal = newTotal,
            localFallbackCallsTotal = newFallback,
            cacheHitRatePercent = cacheHit,
            activeAiProvider = if (isLocalFallback) "Local Rule Engine ($0)" else providerName
        )
    }

    fun updateMapProvider(providerName: String) {
        _status.value = _status.value.copy(activeMapProvider = providerName)
    }

    fun updateWeatherProvider(providerName: String) {
        _status.value = _status.value.copy(activeWeatherProvider = providerName)
    }
}
