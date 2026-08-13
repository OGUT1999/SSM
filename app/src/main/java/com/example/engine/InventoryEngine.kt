package com.example.engine

enum class StockStatus(val label: String, val icon: String, val colorHex: Long) {
    TERSEDIA("TERSEDIA", "🟢", 0xFF2E7D32),
    TERBATAS("TERBATAS", "🟡", 0xFFF57F17),
    HABIS("HABIS", "🔴", 0xFFC62828),
    AKAN_TERSEDIA("AKAN TERSEDIA", "🔵", 0xFF1565C0)
}

data class ParsedVoiceCommand(
    val actionIntent: String, // "UPDATE_INVENTORY", "ADD_INVENTORY", "UNKNOWN"
    val itemName: String,
    val quantity: Double,
    val unit: String,
    val responseSpeech: String
)

object InventoryEngine {

    fun determineStockStatus(
        currentStock: Double,
        minThreshold: Double,
        isFutureDate: Boolean = false
    ): StockStatus {
        return when {
            isFutureDate -> StockStatus.AKAN_TERSEDIA
            currentStock <= 0.0 -> StockStatus.HABIS
            currentStock <= minThreshold -> StockStatus.TERBATAS
            else -> StockStatus.TERSEDIA
        }
    }

    fun parseVoiceInventoryCommand(command: String): ParsedVoiceCommand {
        val lower = command.lowercase()
        
        // Match quantity number
        val regexNum = Regex("""(\d+[\d.,]*)""")
        val matchNum = regexNum.find(lower)
        val qty = matchNum?.value?.replace(",", ".")?.toDoubleOrNull() ?: 100.0

        // Match units
        val unit = when {
            lower.contains("kilo") || lower.contains("kg") -> "kg"
            lower.contains("sak") || lower.contains("karung") -> "sak"
            lower.contains("ekor") -> "ekor"
            lower.contains("liter") || lower.contains("l ") -> "liter"
            lower.contains("ton") -> "ton"
            else -> "kg"
        }

        // Match items
        val item = when {
            lower.contains("cabai") || lower.contains("cabe") -> "Cabai Merah Keriting"
            lower.contains("sapi") -> "Sapi Potong Simmental"
            lower.contains("lele") || lower.contains("ikan") -> "Ikan Lele Konsumsi"
            lower.contains("bawang") -> "Bawang Merah Brebes"
            lower.contains("pupuk") -> "Pupuk NPK Mutiara"
            lower.contains("pakan") -> "Pakan Konsentrat"
            else -> "Hasil Panen / Produk"
        }

        val speech = "Siap Pak/Bu! Stok $item telah saya perbarui menjadi ${qty.toInt()} $unit."

        return ParsedVoiceCommand(
            actionIntent = "UPDATE_INVENTORY",
            itemName = item,
            quantity = qty,
            unit = unit,
            responseSpeech = speech
        )
    }
}
