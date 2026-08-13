package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.BusinessEntity
import com.example.data.local.InventoryAuditEntity
import com.example.data.local.InventoryEntity
import com.example.data.local.OfferEntity
import com.example.data.local.PriceReferenceEntity
import com.example.data.local.RegionalPriceEntity
import com.example.data.local.SupplierBuyerEntity
import com.example.data.local.TransactionEntity
import com.example.data.local.WeatherEntity
import com.example.data.provider.AIProvider
import com.example.data.provider.CloudAIProvider
import com.example.data.provider.CostControlEngine
import com.example.data.provider.CostControlStatus
import com.example.data.provider.DeviceGpsMapProvider
import com.example.data.provider.LocalCacheWeatherProvider
import com.example.data.provider.MapProvider
import com.example.data.provider.WeatherProvider
import com.example.engine.auth.AuthEngine
import com.example.engine.privacy.PrivacyLocationEngine
import com.example.engine.reputation.ReputationFraudEngine
import com.example.engine.security.SecurityEngine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SsmRepository(
    private val db: AppDatabase,
    val costControlEngine: CostControlEngine = CostControlEngine(),
    private val aiProvider: AIProvider = CloudAIProvider(costControlEngine),
    private val weatherProvider: WeatherProvider = LocalCacheWeatherProvider(costControlEngine),
    private val mapProvider: MapProvider = DeviceGpsMapProvider(costControlEngine)
) {

    val authEngine = AuthEngine(db)
    val securityEngine = SecurityEngine(db)
    val reputationFraudEngine = ReputationFraudEngine(db)
    val privacyLocationEngine = PrivacyLocationEngine(db)

    val costStatus: StateFlow<CostControlStatus> = costControlEngine.status

    val userProfile = authEngine.userProfile
    val securityEvents = securityEngine.getSecurityEvents()
    val allRatings = reputationFraudEngine.allRatings
    val allReports = reputationFraudEngine.allReports
    val latestBackupMeta = securityEngine.getLatestBackupMeta()

    val businesses: Flow<List<BusinessEntity>> = db.businessDao().getAllBusinesses()
    val prices: Flow<List<RegionalPriceEntity>> = db.regionalPriceDao().getAllPrices()
    val inventory: Flow<List<InventoryEntity>> = db.inventoryDao().getAllInventory()
    val publicInventory: Flow<List<InventoryEntity>> = db.inventoryDao().getPublicInventory()
    val lowStockInventory: Flow<List<InventoryEntity>> = db.inventoryDao().getLowStockItems()
    val partners: Flow<List<SupplierBuyerEntity>> = db.supplierBuyerDao().getAllPartners()
    val transactions: Flow<List<TransactionEntity>> = db.transactionDao().getAllTransactions()
    val weather: Flow<WeatherEntity?> = db.weatherDao().getWeather()
    val priceReferences: Flow<List<PriceReferenceEntity>> = db.priceReferenceDao().getAllPriceReferences()
    val offers: Flow<List<OfferEntity>> = db.offerDao().getAllOffers()
    val audits: Flow<List<InventoryAuditEntity>> = db.inventoryAuditDao().getAllAudits()

    suspend fun createOffer(
        inventoryId: Long,
        sellerName: String,
        buyerName: String,
        buyerDistrict: String,
        commodityName: String,
        quantity: Double,
        unit: String,
        offeredPricePerUnit: Double,
        sellerTargetPricePerUnit: Double,
        shippingCostEst: Double,
        messageText: String
    ): Boolean {
        if (quantity <= 0.0 || offeredPricePerUnit <= 0.0) return false

        val inventoryList = db.inventoryDao().getAllInventory().first()
        val targetItem = inventoryList.find { it.id == inventoryId || it.itemName.contains(commodityName, ignoreCase = true) }

        // Security check: Quantity cannot exceed available stock
        val actualQuantity = if (targetItem != null && quantity > targetItem.availableStock && targetItem.availableStock > 0) {
            targetItem.availableStock
        } else {
            quantity
        }

        val dateStr = SimpleDateFormat("dd MMM, HH:mm", Locale("id", "ID")).format(Date())
        val netAmt = (offeredPricePerUnit * actualQuantity) - shippingCostEst
        val isWarning = offeredPricePerUnit < (sellerTargetPricePerUnit * 0.88)

        val offer = OfferEntity(
            inventoryId = inventoryId,
            sellerName = sellerName,
            buyerName = buyerName,
            buyerDistrict = buyerDistrict,
            commodityName = commodityName,
            quantity = actualQuantity,
            unit = unit,
            buyerOfferedPricePerUnit = offeredPricePerUnit,
            sellerTargetPricePerUnit = sellerTargetPricePerUnit,
            shippingCostEst = shippingCostEst,
            counterPricePerUnit = 0.0,
            netAmountEst = netAmt,
            status = "PENDING", // Stock is NOT deducted on PENDING offer
            isWarningLowOffer = isWarning,
            messageText = messageText,
            timestampText = dateStr
        )
        db.offerDao().insertOffer(offer)
        return true
    }

    suspend fun updateOfferCounterPrice(offerId: Long, counterPrice: Double, msgText: String) {
        val currentOffers = db.offerDao().getAllOffers().first()
        val offer = currentOffers.find { it.id == offerId } ?: return
        val validCounter = counterPrice.coerceAtLeast(1.0)
        val updated = offer.copy(
            counterPricePerUnit = validCounter,
            status = "NEGOTIATING",
            messageText = msgText
        )
        db.offerDao().updateOffer(updated)
    }

    suspend fun acceptOfferAndDeductStock(offerId: Long, businessId: Long = 1) {
        val currentOffers = db.offerDao().getAllOffers().first()
        val offer = currentOffers.find { it.id == offerId } ?: return

        // 1. Mark offer completed
        val updatedOffer = offer.copy(status = "COMPLETED")
        db.offerDao().updateOffer(updatedOffer)

        // 2. Add transaction
        val pricePerUnit = if (offer.counterPricePerUnit > 0) offer.counterPricePerUnit else offer.buyerOfferedPricePerUnit
        val totalAmt = pricePerUnit * offer.quantity
        val dateStr = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID")).format(Date())
        val tx = TransactionEntity(
            businessId = businessId,
            title = "Penjualan ${offer.commodityName} (${offer.quantity.toInt()} ${offer.unit})",
            type = "PEMASUKAN",
            category = "PENJUALAN",
            amount = totalAmt,
            counterpartyName = offer.buyerName,
            date = dateStr,
            note = "Selesai lewat Negosiasi SSM @ Rp${pricePerUnit.toInt()}/${offer.unit}"
        )
        db.transactionDao().insertTransaction(tx)

        // 3. Deduct Inventory Stock automatically after transaction confirmation
        val inventoryList = db.inventoryDao().getAllInventory().first()
        val targetItem = inventoryList.find { it.id == offer.inventoryId || it.itemName.contains(offer.commodityName, ignoreCase = true) }
        if (targetItem != null) {
            val oldStock = targetItem.currentStock
            val newStock = (oldStock - offer.quantity).coerceAtLeast(0.0)
            val newReserved = (targetItem.reservedStock - offer.quantity).coerceAtLeast(0.0)
            val updatedItem = targetItem.copy(
                currentStock = newStock,
                reservedStock = newReserved,
                statusText = if (newStock <= 0) "HABIS" else if (newStock <= targetItem.minThreshold) "TERBATAS" else "TERSEDIA"
            )
            db.inventoryDao().updateInventory(updatedItem)

            // Audit Trail
            db.inventoryAuditDao().insertAudit(
                InventoryAuditEntity(
                    inventoryId = targetItem.id,
                    itemName = targetItem.itemName,
                    actionBy = offer.buyerName,
                    previousStock = oldStock,
                    newStock = newStock,
                    changeReason = "Pengurangan otomatis transaksi disetujui (${offer.quantity.toInt()} ${offer.unit})",
                    timestampText = dateStr
                )
            )
        }
    }

    suspend fun updateInventoryStock(inventoryId: Long, newStock: Double, reason: String) {
        val item = db.inventoryDao().getInventoryById(inventoryId) ?: return
        val validStock = newStock.coerceAtLeast(0.0) // Security rule: Reject negative stock
        val oldStock = item.currentStock
        val updated = item.copy(
            currentStock = validStock,
            statusText = if (validStock <= 0) "HABIS" else if (validStock <= item.minThreshold) "TERBATAS" else "TERSEDIA"
        )
        db.inventoryDao().updateInventory(updated)

        val dateStr = SimpleDateFormat("dd MMM, HH:mm", Locale("id", "ID")).format(Date())
        db.inventoryAuditDao().insertAudit(
            InventoryAuditEntity(
                inventoryId = item.id,
                itemName = item.itemName,
                actionBy = "Petani / User",
                previousStock = oldStock,
                newStock = validStock,
                changeReason = reason,
                timestampText = dateStr
            )
        )
    }

    suspend fun addBusiness(
        name: String,
        category: String,
        commodity: String,
        capacity: String,
        initialCapital: Double
    ) {
        val business = BusinessEntity(
            name = name,
            category = category,
            commodity = commodity,
            areaOrCapacity = capacity,
            initialCapital = initialCapital,
            assetValue = initialCapital * 1.25,
            cycleStage = "Pemeliharaan / Pertumbuhan",
            cycleProgressPercent = 45,
            location = "Kecamatan Natar, Lampung Selatan"
        )
        db.businessDao().insertBusiness(business)
    }

    suspend fun addInventoryItem(
        businessId: Long,
        itemName: String,
        category: String,
        stock: Double,
        unit: String,
        minThreshold: Double,
        unitPrice: Double
    ) {
        val item = InventoryEntity(
            businessId = businessId,
            itemName = itemName,
            category = category,
            currentStock = stock,
            unit = unit,
            minThreshold = minThreshold,
            unitPrice = unitPrice
        )
        db.inventoryDao().insertInventory(item)
    }

    suspend fun addTransaction(
        businessId: Long,
        title: String,
        type: String,
        category: String,
        amount: Double,
        counterpartyName: String,
        note: String
    ) {
        val dateStr = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID")).format(Date())
        val tx = TransactionEntity(
            businessId = businessId,
            title = title,
            type = type,
            category = category,
            amount = amount,
            counterpartyName = counterpartyName,
            date = dateStr,
            note = note
        )
        db.transactionDao().insertTransaction(tx)
    }

    suspend fun askAssistant(prompt: String): String {
        val currentBusinesses = db.businessDao().getAllBusinesses().first()
        val currentPrices = db.regionalPriceDao().getAllPrices().first()

        val contextString = "Daftar Usaha: ${currentBusinesses.map { "${it.name} (${it.commodity})" }}\nHarga Pasar: ${currentPrices.take(4).map { "${it.commodityName}: Rp${it.collectorPrice}" }}"
        return aiProvider.generateAssistantResponse(prompt, contextString)
    }

    var isSimulationClearedMode: Boolean = false

    suspend fun clearAllSimulationData() {
        isSimulationClearedMode = true
        db.businessDao().deleteAllBusinesses()
        db.inventoryDao().deleteAllInventory()
        db.transactionDao().deleteAllTransactions()
        db.offerDao().deleteAllOffers()
        db.supplierBuyerDao().deleteAllPartners()
        db.inventoryAuditDao().deleteAllAudits()
        authEngine.updateUserProfileDetails(
            displayName = "Mitra UMKM SSM",
            email = "mitra.ssm@gmail.com",
            region = "Indonesia",
            primarySector = "Petani"
        )
    }

    suspend fun seedInitialDataIfEmpty() {
        if (isSimulationClearedMode) return
        val existingBiz = db.businessDao().getAllBusinesses().first()
        if (existingBiz.isEmpty()) {
            performSeed()
        }
    }

    suspend fun reseedSampleData() {
        isSimulationClearedMode = false
        performSeed()
    }

    private suspend fun performSeed() {
        val existingBiz = db.businessDao().getAllBusinesses().first()
        if (existingBiz.isNotEmpty()) return
            db.businessDao().insertBusinesses(
                listOf(
                    BusinessEntity(
                        name = "Kebun Cabai Keriting Medokan",
                        category = "PETANI",
                        commodity = "Cabai Merah Keriting",
                        areaOrCapacity = "0.5 Hektar",
                        initialCapital = 12000000.0,
                        assetValue = 18500000.0,
                        cycleStage = "Generatif / Pembungaan",
                        cycleProgressPercent = 65,
                        location = "Natar, Lampung Selatan"
                    ),
                    BusinessEntity(
                        name = "Peternakan Sapi Potong Berkah",
                        category = "PETERNAKAN",
                        commodity = "Sapi Simmental & PO",
                        areaOrCapacity = "8 Ekor Sapi",
                        initialCapital = 110000000.0,
                        assetValue = 145000000.0,
                        cycleStage = "Penggemukan (Finishing)",
                        cycleProgressPercent = 80,
                        location = "Natar, Lampung Selatan"
                    ),
                    BusinessEntity(
                        name = "Budidaya Lele Sangkuriang",
                        category = "PERIKANAN",
                        commodity = "Ikan Lele Konsumsi",
                        areaOrCapacity = "4 Kolam Terpal",
                        initialCapital = 8500000.0,
                        assetValue = 14000000.0,
                        cycleStage = "Pembesaran Bulan Ke-2",
                        cycleProgressPercent = 50,
                        location = "Natar, Lampung Selatan"
                    )
                )
            )

            db.regionalPriceDao().insertPrices(
                listOf(
                    RegionalPriceEntity(
                        commodityName = "Cabai Merah Keriting",
                        category = "Hortikultura",
                        qualityGrade = "Grade A Super",
                        district = "Kecamatan Natar",
                        farmGatePrice = 32000.0,
                        collectorPrice = 35000.0,
                        wholesalePrice = 38000.0,
                        consumerPrice = 42000.0,
                        source = "Dinas Ketahanan Pangan & Pasar Induk",
                        dateText = "Hari ini",
                        status = "NAIK"
                    ),
                    RegionalPriceEntity(
                        commodityName = "Sapi Potong (Bobot Hidup)",
                        category = "Peternakan",
                        qualityGrade = "Jantan Sehat >400kg",
                        district = "Kecamatan Natar",
                        farmGatePrice = 52000.0,
                        collectorPrice = 54000.0,
                        wholesalePrice = 57000.0,
                        consumerPrice = 135000.0,
                        source = "Pasar Hewan Regional",
                        dateText = "Hari ini",
                        status = "STABIL"
                    ),
                    RegionalPriceEntity(
                        commodityName = "Ikan Lele Segar",
                        category = "Perikanan",
                        qualityGrade = "Ukuran 8-10 ekor/kg",
                        district = "Kecamatan Natar",
                        farmGatePrice = 18500.0,
                        collectorPrice = 20000.0,
                        wholesalePrice = 22500.0,
                        consumerPrice = 26000.0,
                        source = "Asosiasi Pembudidaya",
                        dateText = "Hari ini",
                        status = "STABIL"
                    ),
                    RegionalPriceEntity(
                        commodityName = "Bawang Merah Brebes",
                        category = "Hortikultura",
                        qualityGrade = "Kering Kaskado",
                        district = "Kecamatan Natar",
                        farmGatePrice = 24000.0,
                        collectorPrice = 26500.0,
                        wholesalePrice = 29000.0,
                        consumerPrice = 34000.0,
                        source = "Pasar Grosir Regional",
                        dateText = "Hari ini",
                        status = "TURUN"
                    )
                )
            )

            db.inventoryDao().insertInventories(
                listOf(
                    InventoryEntity(
                        businessId = 1,
                        itemName = "Cabai Merah Keriting Panen",
                        category = "HASIL_PANEN",
                        currentStock = 500.0,
                        unit = "kg",
                        minThreshold = 50.0,
                        unitPrice = 24000.0,
                        qualityGrade = "Grade A Super",
                        locationRegion = "Natar, Lampung Selatan",
                        isPublicShared = true,
                        readyDateText = "Siap Dijual",
                        statusText = "TERSEDIA"
                    ),
                    InventoryEntity(
                        businessId = 3,
                        itemName = "Ikan Lele Segar Konsumsi",
                        category = "HASIL_PANEN",
                        currentStock = 300.0,
                        unit = "kg",
                        minThreshold = 40.0,
                        unitPrice = 20000.0,
                        qualityGrade = "Ukuran 8-10 ekor/kg",
                        locationRegion = "Natar, Lampung Selatan",
                        isPublicShared = true,
                        readyDateText = "Siap Dijual",
                        statusText = "TERSEDIA"
                    ),
                    InventoryEntity(
                        businessId = 1,
                        itemName = "Pupuk NPK Mutiara 16-16-16",
                        category = "PUPUK",
                        currentStock = 2.0,
                        unit = "sak",
                        minThreshold = 3.0,
                        unitPrice = 185000.0,
                        isPublicShared = false
                    ),
                    InventoryEntity(
                        businessId = 2,
                        itemName = "Pakan Konsentrat Sapi Gemuk",
                        category = "PAKAN",
                        currentStock = 4.0,
                        unit = "sak",
                        minThreshold = 5.0,
                        unitPrice = 170000.0,
                        isPublicShared = false
                    ),
                    InventoryEntity(
                        businessId = 3,
                        itemName = "Pelet Ikan Lele LP-2",
                        category = "PAKAN",
                        currentStock = 12.0,
                        unit = "sak",
                        minThreshold = 4.0,
                        unitPrice = 340000.0,
                        isPublicShared = false
                    )
                )
            )

            db.priceReferenceDao().insertPriceReferences(
                listOf(
                    com.example.data.local.PriceReferenceEntity(
                        commodityName = "Cabai Merah Keriting",
                        sourceTypeLabel = "Acuan Resmi Government (HPP/HAP/HET)",
                        officialReferenceType = "HAP",
                        regionName = "Lampung & Nasional",
                        qualityGrade = "Grade A",
                        unit = "kg",
                        producerReferencePrice = 22000.0,
                        wholesaleReferencePrice = 35000.0,
                        retailReferencePrice = 42000.0,
                        confidenceScore = "Tinggi",
                        dateText = "Diperbarui Hari Ini"
                    ),
                    com.example.data.local.PriceReferenceEntity(
                        commodityName = "Sapi Potong (Bobot Hidup)",
                        sourceTypeLabel = "Harga Pasar Hewan Regional",
                        officialReferenceType = "HPP",
                        regionName = "Lampung Selatan",
                        qualityGrade = "Jantan Sehat >400kg",
                        unit = "kg Bobot",
                        producerReferencePrice = 50000.0,
                        wholesaleReferencePrice = 54000.0,
                        retailReferencePrice = 135000.0,
                        confidenceScore = "Tinggi",
                        dateText = "Diperbarui Hari Ini"
                    ),
                    com.example.data.local.PriceReferenceEntity(
                        commodityName = "Ikan Lele Segar",
                        sourceTypeLabel = "Asosiasi Pembudidaya & Pasar",
                        officialReferenceType = "ACUAN_PASAR",
                        regionName = "Kecamatan Natar",
                        qualityGrade = "Consumable 8-10/kg",
                        unit = "kg",
                        producerReferencePrice = 17500.0,
                        wholesaleReferencePrice = 20000.0,
                        retailReferencePrice = 26000.0,
                        confidenceScore = "Sedang",
                        dateText = "Diperbarui Hari Ini"
                    )
                )
            )

            db.offerDao().insertOffers(
                listOf(
                    com.example.data.local.OfferEntity(
                        inventoryId = 1,
                        sellerName = "Kebun Cabai Keriting Medokan",
                        buyerName = "Pak H. Supri (Pengepul)",
                        buyerDistrict = "Natar",
                        commodityName = "Cabai Merah Keriting",
                        quantity = 200.0,
                        unit = "kg",
                        buyerOfferedPricePerUnit = 19000.0, // Low offer! Target 24000
                        sellerTargetPricePerUnit = 24000.0,
                        shippingCostEst = 100000.0,
                        netAmountEst = 3700000.0,
                        status = "PENDING",
                        isWarningLowOffer = true,
                        messageText = "Saya ambil 200 kg di harga Rp19.000/kg ya Pak.",
                        timestampText = "10 Menit Lalu"
                    ),
                    com.example.data.local.OfferEntity(
                        inventoryId = 2,
                        sellerName = "Budidaya Lele Sangkuriang",
                        buyerName = "Warung Ikan Bakar Natar",
                        buyerDistrict = "Natar",
                        commodityName = "Ikan Lele Segar",
                        quantity = 100.0,
                        unit = "kg",
                        buyerOfferedPricePerUnit = 19500.0,
                        sellerTargetPricePerUnit = 20000.0,
                        shippingCostEst = 30000.0,
                        netAmountEst = 1920000.0,
                        status = "PENDING",
                        isWarningLowOffer = false,
                        messageText = "Tawar Rp19.500/kg untuk 100 kg dikirim besok pagi.",
                        timestampText = "1 Jam Lalu"
                    )
                )
            )

            db.supplierBuyerDao().insertPartners(
                listOf(
                    SupplierBuyerEntity(
                        name = "Koperasi Ternak Sejahtera Natar",
                        type = "SUPPLIER",
                        commoditiesOrItems = "Pakan Konsentrat, Obat Cacing, Mineral",
                        district = "Natar",
                        distanceKm = 3.2,
                        priceOfferedPerUnit = 170000.0,
                        transportCostEst = 15000.0,
                        rating = 4.8f,
                        isVerified = true,
                        phone = "0812-7890-1122",
                        address = "Jl. Raya Natar No. 45"
                    ),
                    SupplierBuyerEntity(
                        name = "Toko Tani Subur Makmur",
                        type = "SUPPLIER",
                        commoditiesOrItems = "Pupuk NPK, Bibit Cabai, Mulsa Hitam Perak",
                        district = "Natar",
                        distanceKm = 1.8,
                        priceOfferedPerUnit = 185000.0,
                        transportCostEst = 10000.0,
                        rating = 4.9f,
                        isVerified = true,
                        phone = "0813-6655-4433",
                        address = "Pasar Natar Blok B"
                    ),
                    SupplierBuyerEntity(
                        name = "Pak H. Supri (Pengepul Cabai & Sayur)",
                        type = "PENGEPUL",
                        commoditiesOrItems = "Menerima Cabai Merah, Bawang, Sayuran",
                        district = "Natar",
                        distanceKm = 2.5,
                        priceOfferedPerUnit = 35000.0,
                        transportCostEst = 8000.0,
                        rating = 4.7f,
                        isVerified = true,
                        phone = "0852-1122-3344",
                        address = "Dusun Medokan, Natar"
                    ),
                    SupplierBuyerEntity(
                        name = "PT Mitra Daging Lampung",
                        type = "PEMBELI",
                        commoditiesOrItems = "Menerima Sapi Potong Siap Potong",
                        district = "Bandar Lampung",
                        distanceKm = 14.5,
                        priceOfferedPerUnit = 55000.0,
                        transportCostEst = 150000.0,
                        rating = 5.0f,
                        isVerified = true,
                        phone = "0821-9988-7766",
                        address = "Kawasan Industri Lampung"
                    )
                )
            )

            db.transactionDao().insertTransactions(
                listOf(
                    TransactionEntity(
                        businessId = 1,
                        title = "Penjualan Cabai Petikan Ke-3",
                        type = "PEMASUKAN",
                        category = "PENJUALAN",
                        amount = 4200000.0,
                        counterpartyName = "Pak H. Supri (Pengepul)",
                        date = "10 Aug 2026",
                        note = "120 kg @ Rp35.000/kg"
                    ),
                    TransactionEntity(
                        businessId = 2,
                        title = "Pembelian Pakan Konsentrat 5 Sak",
                        type = "PENGELUARAN",
                        category = "PEMBELIAN_STOK",
                        amount = 850000.0,
                        counterpartyName = "Koperasi Ternak Sejahtera",
                        date = "08 Aug 2026",
                        note = "5 sak @ Rp170.000/sak"
                    )
                )
            )

            db.weatherDao().insertWeather(
                WeatherEntity(
                    locationName = "Kecamatan Natar, Lampung Selatan",
                    temperatureC = 30,
                    condition = "Cerah Berawan",
                    rainProbabilityPercent = 35,
                    humidityPercent = 75,
                    windSpeedKmh = 14,
                    advisoryText = "Kondisi sangat baik untuk pemupukan pagi & pemberian pakan. Sore hari berpotensi hujan ringan."
                )
            )
    }
}
