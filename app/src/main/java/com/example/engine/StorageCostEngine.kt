package com.example.engine

data class StorageCostAnalysis(
    val storageType: String, // "Gudang Kering / Biasa", "Cold Storage / Pendingin", "Depo / Silo"
    val volumeKg: Double,
    val storageDays: Int,
    val costPerKgPerDay: Double,
    val totalStorageCost: Double,
    val initialGrossValue: Double,
    val netValueAfterStorage: Double,
    val netValuePercentRemaining: Double,
    val warningText: String
)

object StorageCostEngine {

    fun calculateStorageImpact(
        storageType: String,
        volumeKg: Double,
        storageDays: Int,
        pricePerKg: Double
    ): StorageCostAnalysis {
        val costPerKgDay = when (storageType.uppercase()) {
            "COLD STORAGE", "COLD_STORAGE", "PENDINGIN" -> 350.0 // Rp350/kg/hari
            "SILO", "DEPO" -> 150.0
            else -> 80.0 // Gudang biasa
        }

        val totalCost = costPerKgDay * volumeKg * storageDays
        val initialGross = volumeKg * pricePerKg
        val netAfter = (initialGross - totalCost).coerceAtLeast(0.0)
        val remainingPct = if (initialGross > 0) (netAfter / initialGross) * 100.0 else 0.0

        val warning = when {
            remainingPct < 70.0 -> "PERINGATAN: Biaya penyimpanan selama $storageDays hari menggerus >30% potensi nilai barang Anda!"
            remainingPct < 90.0 -> "PERHATIAN: Biaya penyimpanan $storageDays hari cukup signifikan. Pertimbangkan penawaran pembeli terdekat."
            else -> "Biaya penyimpanan dalam batas aman terjangkau."
        }

        return StorageCostAnalysis(
            storageType = storageType,
            volumeKg = volumeKg,
            storageDays = storageDays,
            costPerKgPerDay = costPerKgDay,
            totalStorageCost = totalCost,
            initialGrossValue = initialGross,
            netValueAfterStorage = netAfter,
            netValuePercentRemaining = remainingPct,
            warningText = warning
        )
    }
}
