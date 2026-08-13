package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "businesses")
data class BusinessEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: String, // PETANI, PETERNAKAN, PERIKANAN, UMKM
    val commodity: String,
    val areaOrCapacity: String,
    val initialCapital: Double,
    val assetValue: Double,
    val cycleStage: String,
    val cycleProgressPercent: Int,
    val location: String
)

@Entity(tableName = "regional_prices")
data class RegionalPriceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val commodityName: String,
    val category: String,
    val qualityGrade: String,
    val district: String,
    val farmGatePrice: Double,
    val collectorPrice: Double,
    val wholesalePrice: Double,
    val consumerPrice: Double,
    val source: String,
    val dateText: String,
    val status: String // "STABIL", "NAIK", "TURUN"
)

@Entity(tableName = "inventory")
data class InventoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val businessId: Long,
    val itemName: String,
    val category: String, // PAKAN, PUPUK, BENIH, OBAT, HASIL_PANEN
    val currentStock: Double,
    val reservedStock: Double = 0.0,
    val unit: String, // kg, sak, liter, ekor
    val minThreshold: Double,
    val unitPrice: Double,
    val qualityGrade: String = "Grade A Super",
    val locationRegion: String = "Natar, Lampung Selatan",
    val isPublicShared: Boolean = true,
    val readyDateText: String = "Siap Dijual",
    val statusText: String = "TERSEDIA" // TERSEDIA, TERBATAS, HABIS, AKAN TERSEDIA
) {
    val availableStock: Double
        get() = (currentStock - reservedStock).coerceAtLeast(0.0)
}

@Entity(tableName = "price_references")
data class PriceReferenceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val commodityName: String,
    val sourceTypeLabel: String, // "Acuan Resmi Government (HPP/HAP/HET)", "Harga Pasar Nasional", "Harga Lokal"
    val officialReferenceType: String, // "HPP", "HAP", "HET", "ACUAN_PASAR"
    val regionName: String,
    val qualityGrade: String,
    val unit: String,
    val producerReferencePrice: Double,
    val wholesaleReferencePrice: Double,
    val retailReferencePrice: Double,
    val confidenceScore: String, // "Tinggi", "Sedang", "Rendah"
    val dateText: String
)

@Entity(tableName = "offers")
data class OfferEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val inventoryId: Long,
    val sellerName: String,
    val buyerName: String,
    val buyerDistrict: String,
    val commodityName: String,
    val quantity: Double,
    val unit: String,
    val buyerOfferedPricePerUnit: Double,
    val sellerTargetPricePerUnit: Double,
    val shippingCostEst: Double,
    val counterPricePerUnit: Double = 0.0,
    val netAmountEst: Double,
    val status: String, // "PENDING", "NEGOTIATING", "ACCEPTED", "REJECTED", "COMPLETED"
    val isWarningLowOffer: Boolean,
    val messageText: String,
    val timestampText: String
)

@Entity(tableName = "inventory_audits")
data class InventoryAuditEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val inventoryId: Long,
    val itemName: String,
    val actionBy: String,
    val previousStock: Double,
    val newStock: Double,
    val changeReason: String,
    val timestampText: String
)

@Entity(tableName = "supplier_buyers")
data class SupplierBuyerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: String, // SUPPLIER, PENGEPUL, PEMBELI, PASAR
    val commoditiesOrItems: String,
    val district: String,
    val distanceKm: Double,
    val priceOfferedPerUnit: Double,
    val transportCostEst: Double,
    val rating: Float,
    val isVerified: Boolean,
    val phone: String,
    val address: String
)

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val businessId: Long,
    val title: String,
    val type: String, // PEMASUKAN, PENGELUARAN
    val category: String, // PENJUALAN, PEMBELIAN_STOK, OPERASIONAL
    val amount: Double,
    val counterpartyName: String,
    val date: String,
    val note: String
)

@Entity(tableName = "weather_info")
data class WeatherEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val locationName: String,
    val temperatureC: Int,
    val condition: String,
    val rainProbabilityPercent: Int,
    val humidityPercent: Int,
    val windSpeedKmh: Int,
    val advisoryText: String
)

@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey val userId: String, // e.g., "SSM-USR-88219" or Google ID
    val googleEmail: String,
    val googleDisplayName: String,
    val photoUrl: String = "",
    val registrationDateText: String,
    val accountStatus: String = "ACTIVE", // ACTIVE, PROTECTION_MODE, SUSPENDED
    val primarySector: String = "Petani",
    val secondarySectorsJson: String = "[]",
    val operationalRegion: String = "Kec. Natar, Kab. Lampung Selatan",
    val languagePref: String = "id", // Indonesian
    val accessibilityHighContrast: Boolean = false,
    val accessibilityLargeText: Boolean = false,
    val isGoogleConnected: Boolean = true,
    val lastLoginTimestamp: String
)

@Entity(tableName = "business_activities")
data class BusinessActivityEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val activityName: String, // e.g., "Tani Jagung Hibrida", "Jasa Angkut Truk"
    val sector: String, // Petani, Jasa, Toko, Pengepul, dll.
    val district: String,
    val isPrimary: Boolean = false
)

@Entity(tableName = "user_location_preferences")
data class UserLocationPreferenceEntity(
    @PrimaryKey val userId: String,
    val hasPermissionGranted: Boolean,
    val latitude: Double,
    val longitude: Double,
    val desaKelurahan: String,
    val kecamatan: String,
    val kabupatenKota: String,
    val provinsi: String,
    val publicLocationLevel: String = "KECAMATAN", // DESA, KECAMATAN, KABUPATEN, HIDDEN
    val lastUpdatedText: String
)

@Entity(tableName = "registered_devices")
data class RegisteredDeviceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val deviceIdHash: String,
    val deviceDisplayName: String, // e.g. "Android Saya"
    val isCurrentDevice: Boolean,
    val isLostModeActive: Boolean = false,
    val status: String = "ACTIVE", // ACTIVE, LOST_STOLEN, REVOKED
    val lastActiveTimestamp: String
)

@Entity(tableName = "security_events")
data class SecurityEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val eventType: String, // LOGIN, LOGOUT, LOST_MODE_ACTIVATED, LOST_MODE_RESTORED, SESSION_REVOKED, SENSITIVE_ACTION
    val deviceName: String,
    val description: String,
    val timestampText: String
)

@Entity(tableName = "transaction_ratings")
data class TransactionRatingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val transactionId: Long,
    val raterUserId: String,
    val raterRole: String, // BUYER, SELLER
    val targetUserId: String,
    val targetBusinessName: String,
    val sectorCategory: String, // Perdagangan, Jasa, Pertanian, dll.
    val overallRating: Float,
    val itemQualityScore: Float,
    val quantityScore: Float,
    val timelinessScore: Float,
    val communicationScore: Float,
    val paymentScore: Float,
    val commentText: String,
    val isBlindPending: Boolean = true, // Blind rating
    val replyText: String = "",
    val replyTimestampText: String = "",
    val verificationStatus: String = "VERIFIED", // VERIFIED, REVIEWED, PENDING_REVIEW
    val timestampText: String
)

@Entity(tableName = "transaction_reports")
data class TransactionReportEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val transactionId: Long,
    val reporterUserId: String,
    val reportedPartyName: String,
    val category: String, // BARANG_TIDAK_SESUAI, JUMLAH_TIDAK_SESUAI, HARGA_BERUBAH, PEMBAYARAN_BERMASALAH, PENIPUAN, RATING_PALSU, LAINNYA
    val description: String,
    val evidencePhotoPath: String = "",
    val status: String = "SEDANG_DITINJAU", // SEDANG_DITINJAU, PELANGGARAN_TERVERIFIKASI, DITOLAK
    val timestampText: String
)

@Entity(tableName = "account_backup_metas")
data class AccountBackupMetaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val lastBackupTimestampText: String,
    val backupFilePath: String,
    val recordCountInfo: String
)
