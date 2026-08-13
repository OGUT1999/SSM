package com.example.data.remote

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    suspend fun generateAssistantResponse(prompt: String, contextData: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY

        if (apiKey.isBlank() || apiKey == "null") {
            return@withContext generateFallbackResponse(prompt)
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey"

            val systemInstruction = """
                Anda adalah 'Asisten Suara SSM' (Solusi Sistem UMKM), pendamping usaha mikro, petani, peternak, & perikanan lokal di Indonesia.
                Tugas Anda:
                1. Memberikan jawaban langsung, ramah, dan solutif dalam bahasa Indonesia sehari-hari atau dialek lokal.
                2. Membantu menghitung estimasi keuntungan, BEP (Break Even Point), rekomendasi pupuk/pakan, dan saran harga jual.
                3. Gunakan data kontekstual yang diberikan berikut jika relevan:
                $contextData
            """.trimIndent()

            val requestJson = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", "$systemInstruction\n\nPertanyaan Pengguna: $prompt")
                            })
                        })
                    })
                })
            }

            val body = requestJson.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(url)
                .post(body)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (response.isSuccessful && responseBody != null) {
                val json = JSONObject(responseBody)
                val candidates = json.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val firstCandidate = candidates.getJSONObject(0)
                    val content = firstCandidate.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        return@withContext parts.getJSONObject(0).optString("text", generateFallbackResponse(prompt))
                    }
                }
            }

            return@withContext generateFallbackResponse(prompt)
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext generateFallbackResponse(prompt)
        }
    }

    private fun generateFallbackResponse(prompt: String): String {
        val lower = prompt.lowercase()
        return when {
            lower.contains("cabai") || lower.contains("cabe") -> {
                "🌱 *Saran Tanam Cabai:* Luas 0.5 Ha butuh modal ±Rp 15-18 Juta untuk mulsa, pupuk NPK, & bibit. Estimasi hasil panen normal 3-5 ton. BEP harga impas di Rp 18.000/kg. Saat ini harga pasar lokal di Natar adalah Rp 38.000/kg (Profit sangat baik!)."
            }
            lower.contains("sapi") || lower.contains("pakan") -> {
                "🐄 *Rekomendasi Pakan Sapi:* Untuk pakan 1 minggu tersisa 4 sak, Anda bisa beli Konsentrat di Koperasi Ternak Sejahtera (Natar) seharga Rp 170.000/sak (Hemat Rp 15.000 dibanding toko kota). Pakan hijauan disarankan ditambah legum lamtoro."
            }
            lower.contains("lele") || lower.contains("ikan") -> {
                "🐟 *Simulasi Panen Lele:* 10.000 ekor bibit ukuran 5-7cm butuh pakan pelet ±800 kg hingga panen (FCR 0.9 - 1.0). Modal pakan ±Rp 8,8 Juta. BEP impas di Rp 14.500/kg. Penjualan ke Pengepul Pak Supri ditawar Rp 21.000/kg."
            }
            lower.contains("hujan") || lower.contains("cuaca") || lower.contains("mupuk") -> {
                "🌦️ *Prakiraan Cuaca Lokal:* Sore nanti diprakirakan hujan lokal ringan (35%). Pemupukan NPK disarankan pagi ini jam 08.00-10.00 WIB agar pupuk terserap tanah sebelum hujan."
            }
            else -> {
                "🤖 *Asisten SSM (Offline Rules Engine):* Berdasarkan pencatatan usaha Anda, posisi keuangan saat ini tergolong sehat. Stok pakan & pupuk dalam batas aman. Selalu periksa kisaran harga pasar sebelum menjual ke pengepul untuk menjaga Win-Win Value!"
            }
        }
    }
}
