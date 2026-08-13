package com.example.engine

data class CollectiveBuyGroup(
    val title: String,
    val itemNeeded: String,
    val currentParticipantsCount: Int,
    val totalAccumulatedQuantity: Double,
    val targetQuantityForDiscount: Double,
    val unit: String,
    val originalPricePerUnit: Double,
    val discountedPricePerUnit: Double,
    val totalSavingsEst: Double,
    val statusText: String
)

data class CollectiveSellGroup(
    val title: String,
    val commodityName: String,
    val currentProducersCount: Int,
    val totalAccumulatedStock: Double,
    val targetVolumeForWholesaler: Double,
    val unit: String,
    val premiumPriceOfferedPerUnit: Double,
    val standardPricePerUnit: Double,
    val totalExtraRevenueEst: Double,
    val statusText: String
)

object CollectiveTradeEngine {

    fun getSampleCollectiveBuyGroups(): List<CollectiveBuyGroup> {
        return listOf(
            CollectiveBuyGroup(
                title = "Grup Pupuk NPK Bersama Natar",
                itemNeeded = "Pupuk NPK Mutiara 16-16-16",
                currentParticipantsCount = 14,
                totalAccumulatedQuantity = 3800.0,
                targetQuantityForDiscount = 5000.0,
                unit = "kg",
                originalPricePerUnit = 16500.0,
                discountedPricePerUnit = 14200.0,
                totalSavingsEst = 8740000.0,
                statusText = "Sisa 1.200 kg lagi untuk diskon distributor Rp2.300/kg!"
            ),
            CollectiveBuyGroup(
                title = "Grup Pakan Ayam Broiler Rajabasa",
                itemNeeded = "Pakan Starter Ayam BR-1",
                currentParticipantsCount = 8,
                totalAccumulatedQuantity = 2400.0,
                targetQuantityForDiscount = 3000.0,
                unit = "kg",
                originalPricePerUnit = 9200.0,
                discountedPricePerUnit = 8300.0,
                totalSavingsEst = 2160000.0,
                statusText = "Sisa 600 kg lagi untuk promo pabrik pakan!"
            )
        )
    }

    fun getSampleCollectiveSellGroups(): List<CollectiveSellGroup> {
        return listOf(
            CollectiveSellGroup(
                title = "Konsolidasi Panen Cabai Merah Lampung",
                commodityName = "Cabai Merah Keriting Grade A",
                currentProducersCount = 18,
                totalAccumulatedStock = 3200.0,
                targetVolumeForWholesaler = 5000.0,
                unit = "kg",
                premiumPriceOfferedPerUnit = 28000.0,
                standardPricePerUnit = 23000.0,
                totalExtraRevenueEst = 16000000.0,
                statusText = "Pembeli Pasar Induk Kramat Jati butuh truk 5 Ton (+Rp5.000/kg)"
            ),
            CollectiveSellGroup(
                title = "Konsolidasi Sapi Potong Siap Panen",
                commodityName = "Sapi Simmental / Limousin",
                currentProducersCount = 6,
                totalAccumulatedStock = 24.0,
                targetVolumeForWholesaler = 30.0,
                unit = "Ekor",
                premiumPriceOfferedPerUnit = 22500000.0,
                standardPricePerUnit = 20500000.0,
                totalExtraRevenueEst = 48000000.0,
                statusText = "RPH Jabodetabek siap ambil jika terkumpul 30 ekor (+Rp2 Juta/ekor)"
            )
        )
    }
}
