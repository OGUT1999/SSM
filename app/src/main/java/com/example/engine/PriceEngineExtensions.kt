package com.example.engine

import java.text.NumberFormat
import java.util.Locale

enum class FreshnessStatus(val label: String, val colorHex: Long, val badgeIcon: String) {
    HIJAU("Data Terbaru (<3 Hari)", 0xFF2E7D32, "🟢"),
    KUNING("Data Agak Lama (3-14 Hari)", 0xFFF57F17, "🟡"),
    MERAH("Data Lama (>14 Hari)", 0xFFC62828, "🔴")
}

data class VolumePriceTier(
    val minVolume: Double,
    val unit: String,
    val pricePerUnit: Double,
    val tierLabel: String
)

data class PriceDisplayInfo(
    val commodityName: String,
    val unit: String,
    val minPrice: Double,
    val maxPrice: Double,
    val topOfferPrice: Double, // Labeled as "Penawaran Tertinggi Saat Ini" (Req B)
    val averagePrice: Double,
    val lastTransactionPrice: Double,
    val fairPriceSafe: Double,
    val fairPriceNormal: Double,
    val fairPriceHigh: Double,
    val userPrice: Double,
    val volumeTiers: List<VolumePriceTier>,
    val freshnessStatus: FreshnessStatus,
    val sourceRegion: String,
    val sourceName: String,
    val timestampText: String,
    val supplyDemandStatusText: String,
    val confidenceText: String,
    val explanationWhyThisPrice: String
)

object PriceEngineExtensions {

    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
        maximumFractionDigits = 0
    }

    fun formatRupiah(amount: Double): String {
        return currencyFormat.format(amount)
    }

    /**
     * Calculates Volume-Weighted Price according to bulk sizes (Req C).
     */
    fun calculateVolumeTiers(baseRetailPrice: Double, unit: String): List<VolumePriceTier> {
        val base = if (baseRetailPrice > 0) baseRetailPrice else 25000.0
        return listOf(
            VolumePriceTier(10.0, unit, base, "Eceran (10 $unit)"),
            VolumePriceTier(100.0, unit, base * 0.96, "Sedang (100 $unit)"),
            VolumePriceTier(1000.0, unit, base * 0.92, "Grosir / 1 Ton"),
            VolumePriceTier(5000.0, unit, base * 0.88, "Borongan / 5 Ton")
        )
    }

    /**
     * Calculates Net Profit Comparison between buyers (Req L).
     * Net Value = Selling Price - Shipping - Handling - Storage - Fees.
     */
    data class BuyerNetComparison(
        val buyerName: String,
        val offeredPricePerUnit: Double,
        val volume: Double,
        val unit: String,
        val grossAmount: Double,
        val shippingCost: Double,
        val handlingCost: Double,
        val storageFee: Double,
        val netAmount: Double,
        val netPricePerUnit: Double,
        val isBestNetValue: Boolean,
        val distanceKm: Double
    )

    fun compareBuyerNetValues(
        buyers: List<Pair<String, Double>>, // BuyerName to OfferedPrice
        volume: Double,
        unit: String,
        baseShippingPerKm: Double = 5000.0
    ): List<BuyerNetComparison> {
        if (buyers.isEmpty()) return emptyList()

        val list = buyers.mapIndexed { idx, (name, price) ->
            val dist = (idx + 1) * 4.5
            val gross = price * volume
            val ship = dist * baseShippingPerKm
            val handling = volume * 200.0
            val storage = volume * 100.0
            val net = (gross - ship - handling - storage).coerceAtLeast(0.0)
            val netPerUnit = if (volume > 0) net / volume else 0.0

            BuyerNetComparison(
                buyerName = name,
                offeredPricePerUnit = price,
                volume = volume,
                unit = unit,
                grossAmount = gross,
                shippingCost = ship,
                handlingCost = handling,
                storageFee = storage,
                netAmount = net,
                netPricePerUnit = netPerUnit,
                isBestNetValue = false,
                distanceKm = dist
            )
        }

        // Mark best net value
        val maxNet = list.maxOfOrNull { it.netAmount } ?: 0.0
        return list.map {
            it.copy(isBestNetValue = (it.netAmount == maxNet && maxNet > 0))
        }
    }
}
