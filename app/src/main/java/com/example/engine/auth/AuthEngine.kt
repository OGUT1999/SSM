package com.example.engine.auth

import com.example.data.local.AppDatabase
import com.example.data.local.BusinessActivityEntity
import com.example.data.local.RegisteredDeviceEntity
import com.example.data.local.SecurityEventEntity
import com.example.data.local.UserProfileEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class AuthEngine(private val db: AppDatabase) {

    val userProfile: Flow<UserProfileEntity?> = db.userProfileDao().getUserProfile()

    fun getBusinessActivities(userId: String): Flow<List<BusinessActivityEntity>> {
        return db.businessActivityDao().getActivitiesForUser(userId)
    }

    suspend fun getOrCreateInitialProfile(): UserProfileEntity {
        val existing = db.userProfileDao().getUserProfileOnce()
        if (existing != null) return existing

        val nowStr = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("id", "ID")).format(Date())
        val defaultUserId = "SSM-USR-" + (10000..99999).random()

        val defaultProfile = UserProfileEntity(
            userId = defaultUserId,
            googleEmail = "mitra.ssm@gmail.com",
            googleDisplayName = "Mitra UMKM SSM",
            photoUrl = "",
            registrationDateText = nowStr,
            accountStatus = "ACTIVE",
            primarySector = "Petani",
            secondarySectorsJson = "[\"Petani\"]",
            operationalRegion = "Kec. Natar, Kab. Lampung Selatan",
            languagePref = "id",
            accessibilityHighContrast = false,
            accessibilityLargeText = false,
            isGoogleConnected = true,
            lastLoginTimestamp = nowStr
        )

        db.userProfileDao().insertProfile(defaultProfile)

        // Seed default primary activity
        val defaultActivity = BusinessActivityEntity(
            userId = defaultUserId,
            activityName = "Usaha Tani Utama",
            sector = "Petani",
            district = "Kec. Natar, Kab. Lampung Selatan",
            isPrimary = true
        )
        db.businessActivityDao().insertActivity(defaultActivity)

        // Register default current device
        val device = RegisteredDeviceEntity(
            userId = defaultUserId,
            deviceIdHash = "DEV-" + UUID.randomUUID().toString().take(8),
            deviceDisplayName = "Android Saya (Perangkat Ini)",
            isCurrentDevice = true,
            isLostModeActive = false,
            status = "ACTIVE",
            lastActiveTimestamp = nowStr
        )
        db.registeredDeviceDao().insertDevice(device)

        // Log security event
        val log = SecurityEventEntity(
            userId = defaultUserId,
            eventType = "LOGIN",
            deviceName = "Android Saya",
            description = "Pendaftaran & Login Google Berhasil",
            timestampText = nowStr
        )
        db.securityEventDao().insertSecurityEvent(log)

        return defaultProfile
    }

    suspend fun loginWithGoogleSimulated(
        googleEmail: String,
        googleName: String,
        photoUrl: String = "",
        primarySector: String = "Petani",
        region: String = "Kec. Natar, Kab. Lampung Selatan"
    ): UserProfileEntity {
        val nowStr = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("id", "ID")).format(Date())
        val currentProfile = db.userProfileDao().getUserProfileOnce()

        val userId = currentProfile?.userId ?: ("SSM-USR-" + (10000..99999).random())

        val updatedProfile = UserProfileEntity(
            userId = userId,
            googleEmail = googleEmail.ifBlank { "mitra.ssm@gmail.com" },
            googleDisplayName = googleName.ifBlank { "Mitra UMKM SSM" },
            photoUrl = photoUrl,
            registrationDateText = currentProfile?.registrationDateText ?: nowStr,
            accountStatus = "ACTIVE",
            primarySector = primarySector,
            secondarySectorsJson = "[\"$primarySector\"]",
            operationalRegion = region,
            languagePref = currentProfile?.languagePref ?: "id",
            accessibilityHighContrast = currentProfile?.accessibilityHighContrast ?: false,
            accessibilityLargeText = currentProfile?.accessibilityLargeText ?: false,
            isGoogleConnected = true,
            lastLoginTimestamp = nowStr
        )

        db.userProfileDao().insertProfile(updatedProfile)

        // Log audit event
        val event = SecurityEventEntity(
            userId = userId,
            eventType = "LOGIN",
            deviceName = "Android Saya",
            description = "Masuk via Google ($googleEmail)",
            timestampText = nowStr
        )
        db.securityEventDao().insertSecurityEvent(event)

        return updatedProfile
    }

    suspend fun updatePrimarySectorAndRegion(newSector: String, newRegion: String) {
        val current = db.userProfileDao().getUserProfileOnce() ?: getOrCreateInitialProfile()
        val updated = current.copy(
            primarySector = newSector,
            operationalRegion = newRegion
        )
        db.userProfileDao().updateProfile(updated)
    }

    suspend fun updateUserProfileDetails(displayName: String, email: String, region: String, primarySector: String) {
        val current = db.userProfileDao().getUserProfileOnce() ?: getOrCreateInitialProfile()
        val updated = current.copy(
            googleDisplayName = displayName.ifBlank { "Mitra UMKM SSM" },
            googleEmail = email.ifBlank { "mitra.ssm@gmail.com" },
            operationalRegion = region.ifBlank { "Indonesia" },
            primarySector = primarySector.ifBlank { "Petani" }
        )
        db.userProfileDao().updateProfile(updated)
    }

    suspend fun addBusinessActivity(activityName: String, sector: String, district: String) {
        val current = db.userProfileDao().getUserProfileOnce() ?: getOrCreateInitialProfile()
        val newActivity = BusinessActivityEntity(
            userId = current.userId,
            activityName = activityName,
            sector = sector,
            district = district,
            isPrimary = false
        )
        db.businessActivityDao().insertActivity(newActivity)
    }

    suspend fun removeBusinessActivity(activityId: Long) {
        db.businessActivityDao().deleteActivity(activityId)
    }
}
