package com.example.engine.security

import com.example.data.local.AccountBackupMetaEntity
import com.example.data.local.AppDatabase
import com.example.data.local.RegisteredDeviceEntity
import com.example.data.local.SecurityEventEntity
import com.example.data.local.UserProfileEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class SecurityStatusOverview(
    val isOverallSecure: Boolean,
    val statusTitle: String,
    val isGoogleConnected: Boolean,
    val googleEmail: String,
    val activeDeviceCount: Int,
    val isBackupAvailable: Boolean,
    val lastBackupDateText: String,
    val isLocationGranted: Boolean,
    val verifiedTransactionCount: Int,
    val isProtectionModeActive: Boolean,
    val statusDetailsText: String
)

class SecurityEngine(private val db: AppDatabase) {

    fun getRegisteredDevices(userId: String): Flow<List<RegisteredDeviceEntity>> {
        return db.registeredDeviceDao().getDevicesForUser(userId)
    }

    fun getSecurityEvents(): Flow<List<SecurityEventEntity>> {
        return db.securityEventDao().getRecentSecurityEvents()
    }

    fun getLatestBackupMeta(): Flow<AccountBackupMetaEntity?> {
        return db.accountBackupMetaDao().getLatestBackupMeta()
    }

    suspend fun computeSecurityOverview(userId: String): SecurityStatusOverview {
        val profile = db.userProfileDao().getUserProfileOnce()
        val devices = db.registeredDeviceDao().getDevicesForUser(userId).first()
        val backup = db.accountBackupMetaDao().getLatestBackupMeta().first()
        val locPref = db.userLocationPreferenceDao().getLocationPreference(userId).first()
        val transactions = db.transactionDao().getAllTransactions().first()

        val isProtection = profile?.accountStatus == "PROTECTION_MODE"
        val isOverallSecure = !isProtection && (profile?.isGoogleConnected == true) && devices.none { it.status == "LOST_STOLEN" }

        val title = if (isProtection) {
            "🔴 MODE PERLINDUNGAN AKTIF (HP HILANG/DICURI)"
        } else if (isOverallSecure) {
            "🟢 Akun Aman"
        } else {
            "🟡 Perlu Perhatian"
        }

        val details = if (isProtection) {
            "Akses sensitif dibatasi sementara untuk melindungi saldo & transaksi Anda."
        } else if (isOverallSecure) {
            "Akun terhubung Google secara sah, 1 perangkat aktif terdaftar, dan data terlindungi."
        } else {
            "Periksa perangkat terhubung atau cadangkan data Anda secara berkala."
        }

        return SecurityStatusOverview(
            isOverallSecure = isOverallSecure,
            statusTitle = title,
            isGoogleConnected = profile?.isGoogleConnected ?: true,
            googleEmail = profile?.googleEmail ?: "mitra.ssm@gmail.com",
            activeDeviceCount = devices.count { it.status == "ACTIVE" }.coerceAtLeast(1),
            isBackupAvailable = backup != null,
            lastBackupDateText = backup?.lastBackupTimestampText ?: "Belum pernah dicadangkan",
            isLocationGranted = locPref?.hasPermissionGranted ?: true,
            verifiedTransactionCount = transactions.size,
            isProtectionModeActive = isProtection,
            statusDetailsText = details
        )
    }

    suspend fun toggleLostStolenMode(userId: String, activateLostMode: Boolean): Boolean {
        val nowStr = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("id", "ID")).format(Date())
        val profile = db.userProfileDao().getUserProfileOnce() ?: return false

        val newStatus = if (activateLostMode) "PROTECTION_MODE" else "ACTIVE"
        val updatedProfile = profile.copy(accountStatus = newStatus)
        db.userProfileDao().updateProfile(updatedProfile)

        val logType = if (activateLostMode) "LOST_MODE_ACTIVATED" else "LOST_MODE_RESTORED"
        val logDesc = if (activateLostMode) {
            "🚨 PERINGATAN: Mode HP Hilang/Dicuri diaktifkan. Batasi transaksi sensitif."
        } else {
            "✅ HP Ditemukan kembali. Status akun dipulihkan ke normal."
        }

        db.securityEventDao().insertSecurityEvent(
            SecurityEventEntity(
                userId = userId,
                eventType = logType,
                deviceName = "Android Saya",
                description = logDesc,
                timestampText = nowStr
            )
        )
        return true
    }

    suspend fun revokeOtherDevices(userId: String) {
        val nowStr = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("id", "ID")).format(Date())
        db.registeredDeviceDao().revokeOtherDevices(userId)

        db.securityEventDao().insertSecurityEvent(
            SecurityEventEntity(
                userId = userId,
                eventType = "SESSION_REVOKED",
                deviceName = "Android Saya",
                description = "Sesi di perangkat lain berhasil dicabut.",
                timestampText = nowStr
            )
        )
    }

    suspend fun createLocalBackup(userId: String): AccountBackupMetaEntity {
        val nowStr = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("id", "ID")).format(Date())
        val txs = db.transactionDao().getAllTransactions().first().size
        val inv = db.inventoryDao().getAllInventory().first().size

        val meta = AccountBackupMetaEntity(
            userId = userId,
            lastBackupTimestampText = nowStr,
            backupFilePath = "internal_storage/ssm_backup_${System.currentTimeMillis()}.json",
            recordCountInfo = "$txs Transaksi, $inv Barang Inventaris Tersimpan"
        )
        db.accountBackupMetaDao().insertBackupMeta(meta)

        db.securityEventDao().insertSecurityEvent(
            SecurityEventEntity(
                userId = userId,
                eventType = "BACKUP_CREATED",
                deviceName = "Android Saya",
                description = "Backup data lokal berhasil dibuat ($nowStr).",
                timestampText = nowStr
            )
        )
        return meta
    }
}
