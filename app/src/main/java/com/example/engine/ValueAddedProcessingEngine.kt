package com.example.engine

data class ProcessingValueComparison(
    val rawCommodityName: String,
    val rawPricePerUnit: Double,
    val rawVolume: Double,
    val rawTotalRevenue: Double,
    val processedProductName: String,
    val processingYieldUnits: Double,
    val processedPricePerUnit: Double,
    val processedGrossRevenue: Double,
    val additionalLaborCost: Double,
    val additionalEnergyCost: Double,
    val additionalPackagingCost: Double,
    val totalProcessingCost: Double,
    val processedNetRevenue: Double,
    val netProfitDifference: Double,
    val isProcessingMoreProfitable: Boolean,
    val recommendationSummary: String
)

object ValueAddedProcessingEngine {

    fun compareRawVsProcessed(
        rawName: String,
        rawPricePerUnit: Double,
        rawVolumeKg: Double,
        processedName: String,
        processedYieldPcsPerKg: Double = 3.0, // e.g. 1kg Singkong -> 3 bungkus Keripik
        processedPricePerPcs: Double = 15000.0,
        laborCost: Double = 100000.0,
        energyGasCost: Double = 50000.0,
        packagingCostPerPcs: Double = 1000.0
    ): ProcessingValueComparison {
        val rawRev = rawPricePerUnit * rawVolumeKg

        val totalProcessedUnits = rawVolumeKg * processedYieldPcsPerKg
        val processedGross = totalProcessedUnits * processedPricePerPcs
        val packCostTotal = totalProcessedUnits * packagingCostPerPcs
        val totalAddCost = laborCost + energyGasCost + packCostTotal

        val processedNet = processedGross - totalAddCost
        val profitDiff = processedNet - rawRev
        val isBetter = profitDiff > 0

        val rec = if (isBetter) {
            "DIOLAH LEBIH MENGUNTUNGKAN! Mengolah $rawName menjadi $processedName memberikan potensi tambahan keuntungan bersih sebesar Rp${profitDiff.toInt()} (+${((profitDiff / rawRev.coerceAtLeast(1.0)) * 100).toInt()}%)."
        } else {
            "DIJUAL MENTAH LEBIH AMAN. Biaya pengolahan & kemasan tinggi dibanding harga jual produk olahan. Disarankan langsung menjual $rawName segar."
        }

        return ProcessingValueComparison(
            rawCommodityName = rawName,
            rawPricePerUnit = rawPricePerUnit,
            rawVolume = rawVolumeKg,
            rawTotalRevenue = rawRev,
            processedProductName = processedName,
            processingYieldUnits = totalProcessedUnits,
            processedPricePerUnit = processedPricePerPcs,
            processedGrossRevenue = processedGross,
            additionalLaborCost = laborCost,
            additionalEnergyCost = energyGasCost,
            additionalPackagingCost = packCostTotal,
            totalProcessingCost = totalAddCost,
            processedNetRevenue = processedNet,
            netProfitDifference = profitDiff,
            isProcessingMoreProfitable = isBetter,
            recommendationSummary = rec
        )
    }
}
