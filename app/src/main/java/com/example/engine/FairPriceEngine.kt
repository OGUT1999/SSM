package com.example.engine

enum class PriceSourceType(val label: String, val levelPriority: Int) {
    ACUAN_RESMI_HPP_HAP_HET("Acuan Resmi Government (HPP/HAP/HET)", 1),
    HARGA_PASAR_NASIONAL("Harga Pasar Nasional", 2),
    HARGA_PROVINSI("Harga Pasar Provinsi", 3),
    HARGA_KABUPATEN("Harga Pasar Kabupaten/Kota", 4),
    HARGA_LOKAL("Harga Pasar Lokal Kecamatan", 5),
    DATA_TRANSAKSI_SSM("Histori Transaksi SSM", 6),
    DATA_USER("Input Pengguna SSM", 7),
    ESTIMASI_MODEL("Estimasi Model Algoritma SSM", 8)
}

enum class ConfidenceLevel(val label: String, val icon: String, val colorHex: Long) {
    HIGH("Tinggi", "🟢", 0xFF2E7D32),
    MEDIUM("Sedang", "🟡", 0xFFF57F17),
    LOW("Rendah", "🔴", 0xFFC62828)
}

data class CostBreakdown(
    val seedCost: Double = 0.0,
    val fertilizerCost: Double = 0.0,
    val feedCost: Double = 0.0,
    val medicineCost: Double = 0.0,
    val laborCost: Double = 0.0,
    val waterElectricCost: Double = 0.0,
    val landRentCost: Double = 0.0,
    val machineryCost: Double = 0.0,
    val packagingCost: Double = 0.0,
    val transportCost: Double = 0.0,
    val storageCost: Double = 0.0,
    val otherCost: Double = 0.0,
    val totalProductionVolume: Double = 100.0,
    val unit: String = "kg"
) {
    val totalProductionCost: Double
        get() = seedCost + fertilizerCost + feedCost + medicineCost + laborCost +
                waterElectricCost + landRentCost + machineryCost + packagingCost +
                transportCost + storageCost + otherCost

    val costPerUnit: Double
        get() = if (totalProductionVolume > 0) totalProductionCost / totalProductionVolume else 0.0
}

data class EstimationInput(
    val category: String, // "PETANI", "PETERNAKAN", "PERIKANAN", "UMKM"
    val commodity: String,
    val areaOrScaleValue: Double, // Ha, Ekor, Kolam, Unit
    val isOwnLand: Boolean = true,
    val productionMethod: String = "KONVENSIONAL" // "ORGANIK", "KONVENSIONAL", "INTENSIF"
)

data class FairPriceRecommendation(
    val commodity: String,
    val qualityGrade: String,
    val costPerUnit: Double,
    val safeProfitPrice: Double,   // 🟢 UNTUNG AMAN
    val normalProfitPrice: Double, // 🔵 UNTUNG NORMAL
    val highProfitPrice: Double,   // 🟣 UNTUNG TINGGI
    val safeMarginPercent: Double,
    val normalMarginPercent: Double,
    val highMarginPercent: Double,
    val explanation: String,
    val confidence: ConfidenceLevel,
    val primarySourceType: PriceSourceType,
    val officialReferenceText: String,
    val timestamp: String = "Hari ini"
)

object FairPriceEngine {

    fun estimateCostFromScale(input: EstimationInput): CostBreakdown {
        val scale = if (input.areaOrScaleValue <= 0) 1.0 else input.areaOrScaleValue
        val landCostCoeff = if (input.isOwnLand) 0.0 else 2000000.0 * scale
        val methodCoeff = when (input.productionMethod.uppercase()) {
            "ORGANIK" -> 1.25
            "INTENSIF" -> 1.35
            else -> 1.0
        }

        return when (input.category.uppercase()) {
            "PETANI", "HORTIKULTURA" -> {
                // E.g. Cabai Keriting per 0.5 Ha ~ 5000 kg yield
                val totalYieldKg = scale * 5000.0
                CostBreakdown(
                    seedCost = 1500000.0 * scale * methodCoeff,
                    fertilizerCost = 3500000.0 * scale * methodCoeff,
                    medicineCost = 1800000.0 * scale * methodCoeff,
                    laborCost = 4000000.0 * scale * methodCoeff,
                    landRentCost = landCostCoeff,
                    packagingCost = 500000.0 * scale,
                    transportCost = 800000.0 * scale,
                    totalProductionVolume = totalYieldKg,
                    unit = "kg"
                )
            }
            "PETERNAKAN" -> {
                // E.g. Sapi 8 Ekor ~ 1200 kg gain or 8 head
                val totalYieldKg = scale * 150.0 // 150kg gain per ekor
                CostBreakdown(
                    seedCost = 12000000.0 * scale, // bakalan
                    feedCost = 3500000.0 * scale * methodCoeff,
                    medicineCost = 500000.0 * scale,
                    laborCost = 2000000.0 * scale,
                    landRentCost = landCostCoeff,
                    totalProductionVolume = totalYieldKg,
                    unit = "kg Bobot"
                )
            }
            "PERIKANAN" -> {
                // E.g. 4 Kolam Lele ~ 1000 kg yield
                val totalYieldKg = scale * 250.0
                CostBreakdown(
                    seedCost = 800000.0 * scale,
                    feedCost = 2800000.0 * scale * methodCoeff,
                    medicineCost = 200000.0 * scale,
                    waterElectricCost = 300000.0 * scale,
                    laborCost = 800000.0 * scale,
                    totalProductionVolume = totalYieldKg,
                    unit = "kg"
                )
            }
            else -> { // UMKM
                CostBreakdown(
                    seedCost = 2000000.0 * scale,
                    laborCost = 1500000.0 * scale,
                    packagingCost = 500000.0 * scale,
                    totalProductionVolume = scale * 200.0,
                    unit = "pcs"
                )
            }
        }
    }

    fun calculateFairPrices(
        commodity: String,
        qualityGrade: String,
        costBreakdown: CostBreakdown,
        marketPrice: Double = 0.0,
        officialRefPrice: Double = 0.0,
        officialRefLabel: String = "Acuan HAP/HET Tidak Ada",
        primarySource: PriceSourceType = PriceSourceType.HARGA_LOKAL,
        hasLocalTransactionData: Boolean = true
    ): FairPriceRecommendation {
        val costUnit = costBreakdown.costPerUnit

        // Base margins depend on sector & risk
        val (baseSafeMargin, baseNormMargin, baseHighMargin) = when {
            commodity.contains("Cabai", true) || commodity.contains("Bawang", true) -> Triple(0.18, 0.32, 0.55) // High risk horticulture
            commodity.contains("Sapi", true) || commodity.contains("Daging", true) -> Triple(0.12, 0.22, 0.35) // High capital livestock
            commodity.contains("Lele", true) || commodity.contains("Ikan", true) -> Triple(0.15, 0.28, 0.45) // Aquaculture
            else -> Triple(0.15, 0.25, 0.40)
        }

        val baseCost = if (costUnit > 0) costUnit else if (marketPrice > 0) marketPrice * 0.75 else 20000.0

        // Calculate scenarios
        var safePrice = baseCost * (1.0 + baseSafeMargin)
        var normPrice = baseCost * (1.0 + baseNormMargin)
        var highPrice = baseCost * (1.0 + baseHighMargin)

        // Adjust with market price if available
        if (marketPrice > 0) {
            // Blend 60% market price, 40% cost-plus
            normPrice = (normPrice * 0.4) + (marketPrice * 0.6)
            safePrice = normPrice * 0.90
            highPrice = normPrice * 1.15
        }

        // Adjust if official reference price exists
        if (officialRefPrice > 0) {
            normPrice = (normPrice + officialRefPrice) / 2.0
            safePrice = normPrice * 0.92
            highPrice = normPrice * 1.12
        }

        val safeMarginPct = if (baseCost > 0) ((safePrice - baseCost) / baseCost) * 100.0 else 18.0
        val normMarginPct = if (baseCost > 0) ((normPrice - baseCost) / baseCost) * 100.0 else 30.0
        val highMarginPct = if (baseCost > 0) ((highPrice - baseCost) / baseCost) * 100.0 else 50.0

        // Confidence logic
        val confidence = when {
            primarySource == PriceSourceType.ACUAN_RESMI_HPP_HAP_HET || (hasLocalTransactionData && marketPrice > 0) -> ConfidenceLevel.HIGH
            marketPrice > 0 || primarySource == PriceSourceType.HARGA_PROVINSI -> ConfidenceLevel.MEDIUM
            else -> ConfidenceLevel.LOW
        }

        // Build explanation text
        val explanationText = StringBuilder()
        explanationText.append("Rekomendasi harga dihitung dari ")
        if (costUnit > 0) {
            explanationText.append("estimasi biaya produksi Rp${costUnit.toInt()}/${costBreakdown.unit}, ")
        }
        if (marketPrice > 0) {
            explanationText.append("harga pasar rata-rata Rp${marketPrice.toInt()}/${costBreakdown.unit}, ")
        }
        if (officialRefPrice > 0) {
            explanationText.append("dan $officialRefLabel (Rp${officialRefPrice.toInt()}). ")
        } else {
            explanationText.append("serta mempertimbangkan risiko komoditas $commodity. ")
        }
        explanationText.append("Tingkat Untung Normal memberikan margin bersih ~${normMarginPct.toInt()}% yang aman di atas BEP.")

        return FairPriceRecommendation(
            commodity = commodity,
            qualityGrade = qualityGrade,
            costPerUnit = baseCost,
            safeProfitPrice = safePrice,
            normalProfitPrice = normPrice,
            highProfitPrice = highPrice,
            safeMarginPercent = safeMarginPct,
            normalMarginPercent = normMarginPct,
            highMarginPercent = highMarginPct,
            explanation = explanationText.toString(),
            confidence = confidence,
            primarySourceType = primarySource,
            officialReferenceText = if (officialRefPrice > 0) "$officialRefLabel: Rp${officialRefPrice.toInt()}/${costBreakdown.unit}" else "Belum tersedia data acuan resmi untuk komoditas/wilayah ini."
        )
    }
}
