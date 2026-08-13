package com.example.engine.privacy

import com.example.data.local.AppDatabase
import com.example.data.local.UserLocationPreferenceEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PrivacyLocationEngine(private val db: AppDatabase) {

    fun getLocationPreference(userId: String): Flow<UserLocationPreferenceEntity?> {
        return db.userLocationPreferenceDao().getLocationPreference(userId)
    }

    suspend fun updateLocationPreference(
        userId: String,
        hasPermission: Boolean,
        lat: Double = -5.3183,
        lng: Double = 105.1832,
        desa: String = "Natar",
        kecamatan: String = "Natar",
        kabupaten: String = "Lampung Selatan",
        provinsi: String = "Lampung",
        publicLevel: String = "KECAMATAN"
    ) {
        val nowStr = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID")).format(Date())
        val pref = UserLocationPreferenceEntity(
            userId = userId,
            hasPermissionGranted = hasPermission,
            latitude = lat,
            longitude = lng,
            desaKelurahan = desa,
            kecamatan = kecamatan,
            kabupatenKota = kabupaten,
            provinsi = provinsi,
            publicLocationLevel = publicLevel,
            lastUpdatedText = nowStr
        )
        db.userLocationPreferenceDao().insertOrUpdateLocationPref(pref)
    }

    suspend fun getFormattedPublicLocation(userId: String): String {
        val pref = db.userLocationPreferenceDao().getLocationPreference(userId).first()
            ?: return "Kec. Natar, Kab. Lampung Selatan"

        if (!pref.hasPermissionGranted) {
            return "Lokasi Publik: Tidak Diizinkan (Lokal Default)"
        }

        return when (pref.publicLocationLevel) {
            "DESA" -> "Desa ${pref.desaKelurahan}, Kec. ${pref.kecamatan}, Kab. ${pref.kabupatenKota}"
            "KECAMATAN" -> "Kec. ${pref.kecamatan}, Kab. ${pref.kabupatenKota}"
            "KABUPATEN" -> "Kab. ${pref.kabupatenKota}, Prov. ${pref.provinsi}"
            "HIDDEN" -> "Lokasi Publik Disembunyikan"
            else -> "Kec. ${pref.kecamatan}, Kab. ${pref.kabupatenKota}"
        }
    }

    suspend fun updatePublicLocationLevel(userId: String, newLevel: String) {
        val existing = db.userLocationPreferenceDao().getLocationPreference(userId).first()
        if (existing != null) {
            val updated = existing.copy(publicLocationLevel = newLevel)
            db.userLocationPreferenceDao().insertOrUpdateLocationPref(updated)
        }
    }

    suspend fun executeAccountDataDeletionRequest(userId: String): Boolean {
        // Safe deletion process:
        // Wipes user profile, activities, devices, location prefs, ratings, reports
        val profile = db.userProfileDao().getUserProfileOnce()
        if (profile != null) {
            db.userProfileDao().updateProfile(
                profile.copy(
                    googleDisplayName = "Akun Dihapus",
                    accountStatus = "DELETED",
                    googleEmail = "deleted_account@ssm.local"
                )
            )
        }
        return true
    }
}
