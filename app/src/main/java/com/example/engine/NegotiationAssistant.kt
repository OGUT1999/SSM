package com.example.engine

data class OfferEvaluation(
    val isBelowFairRange: Boolean,
    val isFarBelowCost: Boolean,
    val offeredPrice: Double,
    val sellerTargetPrice: Double,
    val costPerUnit: Double,
    val normalFairPrice: Double,
    val discountPercentFromTarget: Double,
    val estimatedProfitPerUnit: Double,
    val warningTitle: String,
    val warningMessage: String
)

data class NetValueBuyer(
    val partnerId: Long,
    val buyerName: String,
    val district: String,
    val distanceKm: Double,
    val offeredPricePerUnit: Double,
    val transportCostPerUnit: Double,
    val otherCostPerUnit: Double,
    val netValuePerUnit: Double,
    val totalNetValue: Double,
    val rating: Float,
    val phone: String
)

object NegotiationAssistant {

    fun evaluateBuyerOffer(
        offeredPrice: Double,
        sellerTargetPrice: Double,
        costPerUnit: Double,
        normalFairPrice: Double
    ): OfferEvaluation {
        val discountPct = if (sellerTargetPrice > 0) ((sellerTargetPrice - offeredPrice) / sellerTargetPrice) * 100.0 else 0.0
        val profitPerUnit = offeredPrice - costPerUnit
        val isFarBelowCost = offeredPrice < costPerUnit
        val isBelowFair = offeredPrice < (normalFairPrice * 0.88) || isFarBelowCost

        val title = when {
            isFarBelowCost -> "🔴 TAWARAN DI BAWAH ESTIMASI BIAYA PRODUKSI"
            isBelowFair -> "🔴 TAWARAN DI BAWAH RENTANG WAJAR"
            else -> "🟢 TAWARAN MASUK DALAM RENTANG WAJAR"
        }

        val msg = when {
            isFarBelowCost -> "Tawaran ini (Rp${offeredPrice.toInt()}) berada di bawah estimasi biaya produksi Anda (Rp${costPerUnit.toInt()}). Jika disetujui, potensi kerugian adalah Rp${(-profitPerUnit).toInt()}/unit."
            isBelowFair -> "Tawaran ini sekitar ${discountPct.toInt()}% di bawah harga target Anda (Rp${sellerTargetPrice.toInt()}). Estimasi keuntungan tipis Rp${profitPerUnit.toInt()}/unit."
            else -> "Penawaran ini cukup wajar dan memberikan margin untung bersih Rp${profitPerUnit.toInt()}/unit di atas biaya."
        }

        return OfferEvaluation(
            isBelowFairRange = isBelowFair,
            isFarBelowCost = isFarBelowCost,
            offeredPrice = offeredPrice,
            sellerTargetPrice = sellerTargetPrice,
            costPerUnit = costPerUnit,
            normalFairPrice = normalFairPrice,
            discountPercentFromTarget = discountPct,
            estimatedProfitPerUnit = profitPerUnit,
            warningTitle = title,
            warningMessage = msg
        )
    }

    fun generateNegoDraft(
        sellerName: String,
        buyerName: String,
        commodity: String,
        quantity: Double,
        unit: String,
        offeredPrice: Double,
        targetPrice: Double,
        counterPrice: Double
    ): String {
        return "Halo Pak/Bu $buyerName, terima kasih banyak atas penawarannya sebesar Rp${offeredPrice.toInt()}/$unit. Berdasarkan kualitas super barang dan harga acuan pasar saat ini, apakah sekiranya memungkinkan jika disepakati di harga Rp${counterPrice.toInt()}/$unit untuk volume $quantity $unit? Mohon konfirmasinya, terima kasih."
    }

    fun calculateNetValueBuyers(
        buyers: List<com.example.data.local.SupplierBuyerEntity>,
        targetPrice: Double,
        volume: Double = 100.0
    ): List<NetValueBuyer> {
        return buyers.map { b ->
            val transportPerUnit = if (volume > 0) b.transportCostEst / volume else 0.0
            val otherCostPerUnit = 200.0 // small handling fee
            val netValuePerUnit = b.priceOfferedPerUnit - transportPerUnit - otherCostPerUnit
            NetValueBuyer(
                partnerId = b.id,
                buyerName = b.name,
                district = b.district,
                distanceKm = b.distanceKm,
                offeredPricePerUnit = b.priceOfferedPerUnit,
                transportCostPerUnit = transportPerUnit,
                otherCostPerUnit = otherCostPerUnit,
                netValuePerUnit = netValuePerUnit,
                totalNetValue = netValuePerUnit * volume,
                rating = b.rating,
                phone = b.phone
            )
        }.sortedByDescending { it.netValuePerUnit }
    }
}
