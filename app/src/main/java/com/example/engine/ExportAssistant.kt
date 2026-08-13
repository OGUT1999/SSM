package com.example.engine

data class ExportDocumentChecklist(
    val title: String,
    val isRequired: Boolean,
    val description: String,
    val issuingAuthority: String
)

data class ExportRequirementGuide(
    val commodityName: String,
    val targetCountry: String,
    val hsCode: String,
    val packagingRequirement: String,
    val documentChecklist: List<ExportDocumentChecklist>,
    val logisticsTips: String,
    val disclaimer: String
)

object ExportAssistant {

    fun getExportGuide(commodity: String, country: String): ExportRequirementGuide {
        val docs = listOf(
            ExportDocumentChecklist("NIB & Akses Kepabeanan (Nomor Induk Berusaha)", true, "Pendaftaran izin usaha sebagai eksportir aktif", "Kementerian Investasi / OSS"),
            ExportDocumentChecklist("Sertifikat Fitosanitari (Karantina Tumbuhan)", true, "Pemeriksaan bebas hama & penyakit karantina", "Badan Karantina Indonesia"),
            ExportDocumentChecklist("Certificate of Origin (COO / SKA Form E / A)", true, "Surat Keterangan Asal barang untuk tarif preferensi", "Dinas Perdagangan"),
            ExportDocumentChecklist("Packing List & Commercial Invoice", true, "Faktur rincian komoditas, jumlah, dan nilai transaksi USD", "Eksportir Mandiri"),
            ExportDocumentChecklist("Sertifikat Halal / HACCP / GlobalGAP", false, "Rekomendasi tambahan untuk pembeli di Timur Tengah / Eropa", "BPJPH / Lembaga Sertifikasi")
        )

        return ExportRequirementGuide(
            commodityName = commodity,
            targetCountry = country,
            hsCode = "0709.60.10 (Cabai/Rempah Segar) / 0901.11.00 (Kopi)",
            packagingRequirement = "Kemasan karton corrugated berfentilasi dengan kemasan vakum inner plastic food grade.",
            documentChecklist = docs,
            logisticsTips = "Gunakan kontainer reefer (pendingin) 20ft dengan suhu terjaga 8-10°C untuk komoditas segar.",
            disclaimer = "DISCLAIMER: SSM menyediakan panduan alur dan checklist dokumen resmi. SSM tidak menjamin transaksi otomatis luar negeri tanpa pemeriksaan fisik otoritas bea cukai."
        )
    }
}
