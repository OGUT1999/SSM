package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.BusinessEntity
import com.example.data.local.InventoryEntity
import com.example.data.local.RegionalPriceEntity
import com.example.data.local.SupplierBuyerEntity
import com.example.data.local.TransactionEntity
import com.example.data.local.WeatherEntity
import com.example.data.repository.SsmRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ChatMessage(
    val sender: String, // "USER" or "ASSISTANT"
    val text: String,
    val time: String = "Sekarang",
    val isAudioAvailable: Boolean = false
)

data class YieldSimResult(
    val commodity: String,
    val optimalYield: String,
    val optimalRev: Double,
    val normalYield: String,
    val normalRev: Double,
    val adverseYield: String,
    val adverseRev: Double,
    val bepPrice: Double
)

class SsmViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SsmRepository = SsmRepository(AppDatabase.getDatabase(application))

    val isSimpleMode = MutableStateFlow(false)
    val isHighContrast = MutableStateFlow(false)
    val isLargeText = MutableStateFlow(false)
    val isFullVoiceMode = MutableStateFlow(true)
    val isOnline = MutableStateFlow(true)
    val offlineDataPackDownloaded = MutableStateFlow(true)

    private val ttsManager = com.example.engine.TtsManager.getInstance(application)

    val costControlStatus: StateFlow<com.example.data.provider.CostControlStatus> = repository.costStatus

    val userProfile: StateFlow<com.example.data.local.UserProfileEntity?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val securityEvents: StateFlow<List<com.example.data.local.SecurityEventEntity>> = repository.securityEvents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allRatings: StateFlow<List<com.example.data.local.TransactionRatingEntity>> = repository.allRatings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allReports: StateFlow<List<com.example.data.local.TransactionReportEntity>> = repository.allReports
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val latestBackupMeta: StateFlow<com.example.data.local.AccountBackupMetaEntity?> = repository.latestBackupMeta
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val securityOverview = MutableStateFlow<com.example.engine.security.SecurityStatusOverview?>(null)
    val reputationSummary = MutableStateFlow<com.example.engine.reputation.UserReputationSummary?>(null)

    init {
        viewModelScope.launch {
            repository.authEngine.getOrCreateInitialProfile()
            refreshSecurityAndReputation()
        }
    }

    fun refreshSecurityAndReputation() {
        viewModelScope.launch {
            val prof = repository.userProfile.first() ?: repository.authEngine.getOrCreateInitialProfile()
            securityOverview.value = repository.securityEngine.computeSecurityOverview(prof.userId)
            reputationSummary.value = repository.reputationFraudEngine.getReputationSummary(prof.userId)
        }
    }

    fun loginWithGoogle(googleEmail: String, googleName: String, primarySector: String = "Petani") {
        viewModelScope.launch {
            repository.authEngine.loginWithGoogleSimulated(googleEmail, googleName, primarySector = primarySector)
            refreshSecurityAndReputation()
        }
    }

    fun updateSectorAndRegion(sector: String, region: String) {
        viewModelScope.launch {
            repository.authEngine.updatePrimarySectorAndRegion(sector, region)
            refreshSecurityAndReputation()
        }
    }

    fun updateUserProfile(displayName: String, email: String, region: String, primarySector: String = "Petani") {
        viewModelScope.launch {
            repository.authEngine.updateUserProfileDetails(displayName, email, region, primarySector)
            refreshSecurityAndReputation()
        }
    }

    fun addBusinessActivity(name: String, sector: String, district: String) {
        viewModelScope.launch {
            repository.authEngine.addBusinessActivity(name, sector, district)
            refreshSecurityAndReputation()
        }
    }

    fun toggleLostPhoneMode(activate: Boolean) {
        viewModelScope.launch {
            val prof = repository.userProfile.first() ?: return@launch
            repository.securityEngine.toggleLostStolenMode(prof.userId, activate)
            refreshSecurityAndReputation()
        }
    }

    fun revokeOtherDevices() {
        viewModelScope.launch {
            val prof = repository.userProfile.first() ?: return@launch
            repository.securityEngine.revokeOtherDevices(prof.userId)
            refreshSecurityAndReputation()
        }
    }

    fun createBackup() {
        viewModelScope.launch {
            val prof = repository.userProfile.first() ?: return@launch
            repository.securityEngine.createLocalBackup(prof.userId)
            refreshSecurityAndReputation()
        }
    }

    fun submitRating(
        txId: Long,
        targetUser: String,
        targetBiz: String,
        sector: String,
        overall: Float,
        qItem: Float,
        qQty: Float,
        qTime: Float,
        qComm: Float,
        qPay: Float,
        comment: String
    ) {
        viewModelScope.launch {
            val prof = repository.userProfile.first() ?: return@launch
            repository.reputationFraudEngine.submitTwoWayRating(
                transactionId = txId,
                raterUserId = prof.userId,
                raterRole = "PEMBELI",
                targetUserId = targetUser,
                targetBusinessName = targetBiz,
                sectorCategory = sector,
                overallRating = overall,
                itemQualityScore = qItem,
                quantityScore = qQty,
                timelinessScore = qTime,
                communicationScore = qComm,
                paymentScore = qPay,
                commentText = comment
            )
            refreshSecurityAndReputation()
        }
    }

    fun submitReport(
        txId: Long,
        reportedParty: String,
        category: String,
        description: String
    ) {
        viewModelScope.launch {
            val prof = repository.userProfile.first() ?: return@launch
            repository.reputationFraudEngine.submitTransactionReport(
                transactionId = txId,
                reporterUserId = prof.userId,
                reportedPartyName = reportedParty,
                category = category,
                description = description
            )
            refreshSecurityAndReputation()
        }
    }

    fun updateLocationPermissionAndLevel(hasPerm: Boolean, publicLevel: String) {
        viewModelScope.launch {
            val prof = repository.userProfile.first() ?: return@launch
            repository.privacyLocationEngine.updateLocationPreference(
                userId = prof.userId,
                hasPermission = hasPerm,
                publicLevel = publicLevel
            )
            refreshSecurityAndReputation()
        }
    }

    val businesses: StateFlow<List<BusinessEntity>> = repository.businesses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val prices: StateFlow<List<RegionalPriceEntity>> = repository.prices
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val inventory: StateFlow<List<InventoryEntity>> = repository.inventory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val lowStockInventory: StateFlow<List<InventoryEntity>> = repository.lowStockInventory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val partners: StateFlow<List<SupplierBuyerEntity>> = repository.partners
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val transactions: StateFlow<List<TransactionEntity>> = repository.transactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val weather: StateFlow<WeatherEntity?> = repository.weather
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val offers: StateFlow<List<com.example.data.local.OfferEntity>> = repository.offers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val priceReferences: StateFlow<List<com.example.data.local.PriceReferenceEntity>> = repository.priceReferences
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val publicInventory: StateFlow<List<InventoryEntity>> = repository.publicInventory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val audits: StateFlow<List<com.example.data.local.InventoryAuditEntity>> = repository.audits
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun createBuyerOffer(
        inventoryId: Long,
        sellerName: String,
        buyerName: String,
        buyerDistrict: String,
        commodityName: String,
        quantity: Double,
        unit: String,
        offeredPrice: Double,
        targetPrice: Double,
        shippingCost: Double,
        message: String
    ) {
        viewModelScope.launch {
            repository.createOffer(
                inventoryId = inventoryId,
                sellerName = sellerName,
                buyerName = buyerName,
                buyerDistrict = buyerDistrict,
                commodityName = commodityName,
                quantity = quantity,
                unit = unit,
                offeredPricePerUnit = offeredPrice,
                sellerTargetPricePerUnit = targetPrice,
                shippingCostEst = shippingCost,
                messageText = message
            )
        }
    }

    fun submitCounterOffer(offerId: Long, counterPrice: Double, messageDraft: String) {
        viewModelScope.launch {
            repository.updateOfferCounterPrice(offerId, counterPrice, messageDraft)
        }
    }

    fun acceptOffer(offerId: Long) {
        viewModelScope.launch {
            repository.acceptOfferAndDeductStock(offerId)
        }
    }

    fun updateStockQuantity(inventoryId: Long, newStock: Double, reason: String) {
        viewModelScope.launch {
            repository.updateInventoryStock(inventoryId, newStock, reason)
        }
    }

    fun getFairPriceRecommendation(
        commodity: String,
        grade: String = "Grade A Super",
        costBreakdown: com.example.engine.CostBreakdown = com.example.engine.CostBreakdown(),
        marketPrice: Double = 0.0
    ): com.example.engine.FairPriceRecommendation {
        val ref = priceReferences.value.find { it.commodityName.contains(commodity, ignoreCase = true) }
        val officialPrice = ref?.producerReferencePrice ?: 0.0
        val officialLabel = ref?.sourceTypeLabel ?: "Acuan HAP/HET Tidak Ada"
        val primarySource = if (ref != null) com.example.engine.PriceSourceType.ACUAN_RESMI_HPP_HAP_HET else com.example.engine.PriceSourceType.HARGA_LOKAL

        return com.example.engine.FairPriceEngine.calculateFairPrices(
            commodity = commodity,
            qualityGrade = grade,
            costBreakdown = costBreakdown,
            marketPrice = if (marketPrice > 0) marketPrice else ref?.wholesaleReferencePrice ?: 0.0,
            officialRefPrice = officialPrice,
            officialRefLabel = officialLabel,
            primarySource = primarySource,
            hasLocalTransactionData = true
        )
    }

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                sender = "ASSISTANT",
                text = "Halo Pak/Bu! Saya Asisten Suara SSM. Ada yang bisa saya bantu hari ini? Anda bisa menanyakan estimasi hasil panen, lokasi beli pupuk murah, atau harga pasar terbaru.",
                isAudioAvailable = true
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isAssistantThinking = MutableStateFlow(false)
    val isAssistantThinking: StateFlow<Boolean> = _isAssistantThinking.asStateFlow()

    val totalIncome = transactions.combine(transactions) { list, _ ->
        list.filter { it.type == "PEMASUKAN" }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalExpense = transactions.combine(transactions) { list, _ ->
        list.filter { it.type == "PENGELUARAN" }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    init {
        viewModelScope.launch {
            // User requested clean database state for simulation testing
            repository.clearAllSimulationData()
        }
    }

    fun clearSimulationData() {
        viewModelScope.launch {
            repository.clearAllSimulationData()
        }
    }

    fun loadSampleData() {
        viewModelScope.launch {
            repository.reseedSampleData()
        }
    }

    fun toggleSimpleMode() {
        isSimpleMode.value = !isSimpleMode.value
    }

    fun toggleHighContrast() {
        isHighContrast.value = !isHighContrast.value
    }

    fun toggleLargeText() {
        isLargeText.value = !isLargeText.value
    }

    fun toggleFullVoiceMode() {
        isFullVoiceMode.value = !isFullVoiceMode.value
    }

    fun speakText(text: String) {
        if (isFullVoiceMode.value) {
            ttsManager.speak(text)
        }
    }

    fun speakPageHelp(screenName: String) {
        val helpMessage = when (screenName.lowercase()) {
            "home" -> "Di halaman Beranda ini, Anda dapat melihat ringkasan keuangan, tombol cepat Saya Punya dan Saya Butuh, tawaran pembeli masuk, serta asisten suara."
            "procurement" -> "Halaman Saya Butuh dan Pengadaan. Di sini Anda dapat melihat bahan produksi dan mengajukan tawaran ke supplier atau pengepul."
            "sales" -> "Halaman Saya Punya dan Stok Panen. Di sini Anda dapat mempublikasikan stok panen Anda dan melihat tawaran dari pembeli."
            "prices" -> "Halaman Harga Pasar. Di sini Anda melihat daftar harga komoditas terkini beserta rekomendasi harga wajar SSM."
            else -> "Halaman bantuan SSM. Gunakan tombol berikon besar atau bicara langsung untuk mengoperasikan aplikasi."
        }
        ttsManager.speak(helpMessage)
    }

    fun toggleOnlineStatus() {
        isOnline.value = !isOnline.value
    }

    fun toggleOfflineDataPack() {
        offlineDataPackDownloaded.value = !offlineDataPackDownloaded.value
    }

    fun sendVoiceOrTextQuery(query: String) {
        if (query.isBlank()) return

        val userMsg = ChatMessage(sender = "USER", text = query)
        _chatMessages.value = _chatMessages.value + userMsg
        _isAssistantThinking.value = true

        viewModelScope.launch {
            val lower = query.lowercase()
            if (lower.contains("punya") || lower.contains("stok") || lower.contains("sisa") || lower.contains("panen")) {
                val parsed = com.example.engine.InventoryEngine.parseVoiceInventoryCommand(query)
                // Check if matching item exists in inventory
                val invList = inventory.value
                val existing = invList.find { it.itemName.contains(parsed.itemName, ignoreCase = true) }
                if (existing != null) {
                    repository.updateInventoryStock(existing.id, parsed.quantity, "Pembaruan via Perintah Suara SSM")
                } else {
                    repository.addInventoryItem(1, parsed.itemName, "HASIL_PANEN", parsed.quantity, parsed.unit, 10.0, 20000.0)
                }
                _isAssistantThinking.value = false
                _chatMessages.value = _chatMessages.value + ChatMessage(
                    sender = "ASSISTANT",
                    text = "${parsed.responseSpeech} [Intent: ${parsed.actionIntent}]",
                    isAudioAvailable = true
                )
            } else {
                val responseText = repository.askAssistant(query)
                _isAssistantThinking.value = false
                _chatMessages.value = _chatMessages.value + ChatMessage(
                    sender = "ASSISTANT",
                    text = responseText,
                    isAudioAvailable = true
                )
            }
        }
    }

    fun addBusiness(name: String, category: String, commodity: String, capacity: String, capital: Double) {
        viewModelScope.launch {
            repository.addBusiness(name, category, commodity, capacity, capital)
        }
    }

    fun addInventory(businessId: Long, name: String, category: String, stock: Double, unit: String, minThresh: Double, price: Double) {
        viewModelScope.launch {
            repository.addInventoryItem(businessId, name, category, stock, unit, minThresh, price)
        }
    }

    fun addTransaction(businessId: Long, title: String, type: String, category: String, amount: Double, counterparty: String, note: String) {
        viewModelScope.launch {
            repository.addTransaction(businessId, title, type, category, amount, counterparty, note)
        }
    }

    fun calculateYieldSimulation(category: String, scaleValue: Double): YieldSimResult {
        return when (category.uppercase()) {
            "PETANI", "HORTIKULTURA" -> {
                // scaleValue = area in Hectares (e.g. 0.5)
                val optKg = scaleValue * 8000
                val normKg = scaleValue * 5000
                val advKg = scaleValue * 2500
                val price = 35000.0
                val cost = scaleValue * 24000000.0
                YieldSimResult(
                    commodity = "Cabai Merah Keriting ($scaleValue Ha)",
                    optimalYield = "${optKg.toInt()} kg",
                    optimalRev = optKg * price,
                    normalYield = "${normKg.toInt()} kg",
                    normalRev = normKg * price,
                    adverseYield = "${advKg.toInt()} kg",
                    adverseRev = advKg * price,
                    bepPrice = if (normKg > 0) cost / normKg else 0.0
                )
            }
            "PETERNAKAN" -> {
                // scaleValue = head count (e.g. 8)
                val optGain = scaleValue * 150 // kg gain
                val normGain = scaleValue * 120
                val advGain = scaleValue * 80
                val pricePerKg = 54000.0
                val cost = scaleValue * 3500000.0
                YieldSimResult(
                    commodity = "Sapi Potong ($scaleValue Ekor)",
                    optimalYield = "${optGain.toInt()} kg Bobot",
                    optimalRev = optGain * pricePerKg,
                    normalYield = "${normGain.toInt()} kg Bobot",
                    normalRev = normGain * pricePerKg,
                    adverseYield = "${advGain.toInt()} kg Bobot",
                    adverseRev = advGain * pricePerKg,
                    bepPrice = if (normGain > 0) cost / normGain else 0.0
                )
            }
            else -> { // PERIKANAN
                // scaleValue = ponds (e.g. 4 ponds = 10000 fish)
                val optKg = scaleValue * 250
                val normKg = scaleValue * 200
                val advKg = scaleValue * 120
                val price = 20000.0
                val cost = scaleValue * 2200000.0
                YieldSimResult(
                    commodity = "Lele Sangkuriang ($scaleValue Kolam)",
                    optimalYield = "${optKg.toInt()} kg",
                    optimalRev = optKg * price,
                    normalYield = "${normKg.toInt()} kg",
                    normalRev = normKg * price,
                    adverseYield = "${advKg.toInt()} kg",
                    adverseRev = advKg * price,
                    bepPrice = if (normKg > 0) cost / normKg else 0.0
                )
            }
        }
    }
}
