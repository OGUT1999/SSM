package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BusinessDao {
    @Query("SELECT * FROM businesses ORDER BY id DESC")
    fun getAllBusinesses(): Flow<List<BusinessEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBusiness(business: BusinessEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBusinesses(businesses: List<BusinessEntity>)

    @Update
    suspend fun updateBusiness(business: BusinessEntity)

    @Query("DELETE FROM businesses")
    suspend fun deleteAllBusinesses()
}

@Dao
interface RegionalPriceDao {
    @Query("SELECT * FROM regional_prices ORDER BY commodityName ASC")
    fun getAllPrices(): Flow<List<RegionalPriceEntity>>

    @Query("SELECT * FROM regional_prices WHERE commodityName LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%'")
    fun searchPrices(query: String): Flow<List<RegionalPriceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrices(prices: List<RegionalPriceEntity>)

    @Query("DELETE FROM regional_prices")
    suspend fun deleteAllPrices()
}

@Dao
interface InventoryDao {
    @Query("SELECT * FROM inventory ORDER BY itemName ASC")
    fun getAllInventory(): Flow<List<InventoryEntity>>

    @Query("SELECT * FROM inventory WHERE isPublicShared = 1 ORDER BY itemName ASC")
    fun getPublicInventory(): Flow<List<InventoryEntity>>

    @Query("SELECT * FROM inventory WHERE currentStock <= minThreshold")
    fun getLowStockItems(): Flow<List<InventoryEntity>>

    @Query("SELECT * FROM inventory WHERE id = :id LIMIT 1")
    suspend fun getInventoryById(id: Long): InventoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInventory(inventory: InventoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInventories(inventories: List<InventoryEntity>)

    @Update
    suspend fun updateInventory(inventory: InventoryEntity)

    @Query("DELETE FROM inventory")
    suspend fun deleteAllInventory()
}

@Dao
interface PriceReferenceDao {
    @Query("SELECT * FROM price_references ORDER BY commodityName ASC")
    fun getAllPriceReferences(): Flow<List<PriceReferenceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPriceReferences(refs: List<PriceReferenceEntity>)

    @Query("DELETE FROM price_references")
    suspend fun deleteAllPriceReferences()
}

@Dao
interface OfferDao {
    @Query("SELECT * FROM offers ORDER BY id DESC")
    fun getAllOffers(): Flow<List<OfferEntity>>

    @Query("SELECT * FROM offers WHERE status = :status ORDER BY id DESC")
    fun getOffersByStatus(status: String): Flow<List<OfferEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOffer(offer: OfferEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOffers(offers: List<OfferEntity>)

    @Update
    suspend fun updateOffer(offer: OfferEntity)

    @Query("DELETE FROM offers")
    suspend fun deleteAllOffers()
}

@Dao
interface InventoryAuditDao {
    @Query("SELECT * FROM inventory_audits ORDER BY id DESC")
    fun getAllAudits(): Flow<List<InventoryAuditEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAudit(audit: InventoryAuditEntity): Long

    @Query("DELETE FROM inventory_audits")
    suspend fun deleteAllAudits()
}

@Dao
interface SupplierBuyerDao {
    @Query("SELECT * FROM supplier_buyers ORDER BY distanceKm ASC")
    fun getAllPartners(): Flow<List<SupplierBuyerEntity>>

    @Query("SELECT * FROM supplier_buyers WHERE type = :type ORDER BY distanceKm ASC")
    fun getPartnersByType(type: String): Flow<List<SupplierBuyerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPartners(partners: List<SupplierBuyerEntity>)

    @Query("DELETE FROM supplier_buyers")
    suspend fun deleteAllPartners()
}

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY id DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<TransactionEntity>)

    @Query("DELETE FROM transactions")
    suspend fun deleteAllTransactions()
}

@Dao
interface WeatherDao {
    @Query("SELECT * FROM weather_info LIMIT 1")
    fun getWeather(): Flow<WeatherEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: WeatherEntity)
}

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profiles LIMIT 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profiles LIMIT 1")
    suspend fun getUserProfileOnce(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: UserProfileEntity)

    @Update
    suspend fun updateProfile(profile: UserProfileEntity)
}

@Dao
interface BusinessActivityDao {
    @Query("SELECT * FROM business_activities WHERE userId = :userId ORDER BY isPrimary DESC, id ASC")
    fun getActivitiesForUser(userId: String): Flow<List<BusinessActivityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: BusinessActivityEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivities(activities: List<BusinessActivityEntity>)

    @Query("DELETE FROM business_activities WHERE id = :id")
    suspend fun deleteActivity(id: Long)
}

@Dao
interface UserLocationPreferenceDao {
    @Query("SELECT * FROM user_location_preferences WHERE userId = :userId LIMIT 1")
    fun getLocationPreference(userId: String): Flow<UserLocationPreferenceEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateLocationPref(pref: UserLocationPreferenceEntity)
}

@Dao
interface RegisteredDeviceDao {
    @Query("SELECT * FROM registered_devices WHERE userId = :userId ORDER BY isCurrentDevice DESC, id ASC")
    fun getDevicesForUser(userId: String): Flow<List<RegisteredDeviceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevice(device: RegisteredDeviceEntity): Long

    @Update
    suspend fun updateDevice(device: RegisteredDeviceEntity)

    @Query("UPDATE registered_devices SET status = 'REVOKED' WHERE userId = :userId AND isCurrentDevice = 0")
    suspend fun revokeOtherDevices(userId: String)
}

@Dao
interface SecurityEventDao {
    @Query("SELECT * FROM security_events ORDER BY id DESC LIMIT 50")
    fun getRecentSecurityEvents(): Flow<List<SecurityEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSecurityEvent(event: SecurityEventEntity): Long
}

@Dao
interface TransactionRatingDao {
    @Query("SELECT * FROM transaction_ratings ORDER BY id DESC")
    fun getAllRatings(): Flow<List<TransactionRatingEntity>>

    @Query("SELECT * FROM transaction_ratings WHERE transactionId = :transactionId")
    fun getRatingsForTransaction(transactionId: Long): Flow<List<TransactionRatingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRating(rating: TransactionRatingEntity): Long

    @Update
    suspend fun updateRating(rating: TransactionRatingEntity)
}

@Dao
interface TransactionReportDao {
    @Query("SELECT * FROM transaction_reports ORDER BY id DESC")
    fun getAllReports(): Flow<List<TransactionReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: TransactionReportEntity): Long

    @Update
    suspend fun updateReport(report: TransactionReportEntity)
}

@Dao
interface AccountBackupMetaDao {
    @Query("SELECT * FROM account_backup_metas ORDER BY id DESC LIMIT 1")
    fun getLatestBackupMeta(): Flow<AccountBackupMetaEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBackupMeta(meta: AccountBackupMetaEntity): Long
}
