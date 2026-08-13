package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        BusinessEntity::class,
        RegionalPriceEntity::class,
        InventoryEntity::class,
        SupplierBuyerEntity::class,
        TransactionEntity::class,
        WeatherEntity::class,
        PriceReferenceEntity::class,
        OfferEntity::class,
        InventoryAuditEntity::class,
        UserProfileEntity::class,
        BusinessActivityEntity::class,
        UserLocationPreferenceEntity::class,
        RegisteredDeviceEntity::class,
        SecurityEventEntity::class,
        TransactionRatingEntity::class,
        TransactionReportEntity::class,
        AccountBackupMetaEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun businessDao(): BusinessDao
    abstract fun regionalPriceDao(): RegionalPriceDao
    abstract fun inventoryDao(): InventoryDao
    abstract fun supplierBuyerDao(): SupplierBuyerDao
    abstract fun transactionDao(): TransactionDao
    abstract fun weatherDao(): WeatherDao
    abstract fun priceReferenceDao(): PriceReferenceDao
    abstract fun offerDao(): OfferDao
    abstract fun inventoryAuditDao(): InventoryAuditDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun businessActivityDao(): BusinessActivityDao
    abstract fun userLocationPreferenceDao(): UserLocationPreferenceDao
    abstract fun registeredDeviceDao(): RegisteredDeviceDao
    abstract fun securityEventDao(): SecurityEventDao
    abstract fun transactionRatingDao(): TransactionRatingDao
    abstract fun transactionReportDao(): TransactionReportDao
    abstract fun accountBackupMetaDao(): AccountBackupMetaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ssm_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
