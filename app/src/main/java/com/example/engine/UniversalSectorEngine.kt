package com.example.engine

data class SectorDefinition(
    val id: String,
    val name: String,
    val icon: String,
    val costCategories: List<String>,
    val defaultUnit: String,
    val description: String
)

object UniversalSectorEngine {

    val SECTORS = listOf(
        SectorDefinition("PERTANIAN", "Pertanian & Hortikultura", "🌾", listOf("Benih/Bibit", "Pupuk", "Pestisida", "Tenaga Kerja", "Sewa Lahan", "Kemasan", "Transportasi"), "kg", "Usaha budidaya tanaman pangan, sayuran, dan buah"),
        SectorDefinition("PETERNAKAN", "Peternakan", "🐄", listOf("Bibit/Bakalan", "Pakan", "Obat/Vaksin", "Kandang", "Tenaga Kerja", "Listrik/Air"), "kg Bobot", "Usaha penggemukan atau budidaya hewan ternak"),
        SectorDefinition("PERIKANAN", "Perikanan & Tambak", "🐟", listOf("Benur/Bibit", "Pakan Ikan", "Obat/Vitamin", "Air/Listrik", "Tenaga Kerja", "Es/Penyimpanan"), "kg", "Usaha budidaya ikan, udang, atau perikanan tangkap"),
        SectorDefinition("PERKEBUNAN", "Perkebunan", "🌴", listOf("Pupuk", "Pestisida", "Tenaga Panen", "Peralatan", "Transportasi"), "kg", "Usaha kelapa sawit, kopi, kakao, karet, dll"),
        SectorDefinition("KEHUTANAN_LEGAL", "Kehutanan Legal", "🌲", listOf("Izin Legal", "Bibit", "Pemeliharaan", "Tenaga Kerja", "Logistik"), "m³", "Usaha kayu legal dan hasil hutan non-kayu"),
        SectorDefinition("KULINER", "Kuliner & Makanan", "🍲", listOf("Bahan Baku", "Gas/BBM", "Listrik/Air", "Tenaga Kerja", "Kemasan", "Sewa Tempat"), "porsi", "Usaha warung, resto, katering, dan olahan makanan"),
        SectorDefinition("RETAIL", "Retail & Toko Kelontong", "🏪", listOf("Stok Barang Dagangan", "Sewa Tempat", "Listrik", "Karyawan", "Kantong/Kemasan"), "unit", "Toko kelontong, minimarket lokal, atau eceran"),
        SectorDefinition("PERDAGANGAN", "Perdagangan / Pengepul", "🤝", listOf("Beli Pembelian", "Bongkar Muat", "Transportasi", "Penyusutan Loss", "Sewa Gudang"), "ton", "Usaha jual beli grosir dan pengumpulan komoditas"),
        SectorDefinition("JASA", "Jasa & Servis", "🛠️", listOf("Waktu Kerja/Gaji", "Peralatan", "Transportasi", "Bahan Pembantu", "Sewa Operational"), "jam / project", "Jasa perbaikan, konsultasi, pangkas rambut, dll"),
        SectorDefinition("TRANSPORTASI", "Transportasi & Logistik", "🚚", listOf("BBM", "Tol/Parkir", "Penyusutan Kendaraan", "Servis/Ban", "Sopir/Kernet"), "trip", "Jasa angkutan barang atau penumpang"),
        SectorDefinition("KONSTRUKSI", "Konstruksi & Tukang", "🏗️", listOf("Bahan Bangunan", "Upah Tukang", "Sewa Alat", "Transportasi", "Keamanan"), "m²", "Usaha pertukangan, pengerjaan proyek fisik"),
        SectorDefinition("MANUFAKTUR", "Manufaktur & Pabrikasi", "⚙️", listOf("Bahan Mentah", "Tenaga Kerja Pabrik", "Listrik Mesin", "Perawatan Mesin", "Kemasan"), "pcs", "Pembuatan produk dalam skala industri mikro"),
        SectorDefinition("KERAJINAN", "Kerajinan & Craft", "🎨", listOf("Bahan Baku", "Alat Tangan", "Tenaga Kerja", "Kemasan Khusus", "Promosi"), "unit", "Produk souvenir, ukiran, anyaman, perhiasan"),
        SectorDefinition("FASHION", "Fashion & Pakaian", "👗", listOf("Kain/Benang", "Upah Jahit", "Aksesoris", "Kemasan Brand", "Listrik Mesin"), "potong", "Usaha konveksi, pakaian, tas, sepatu"),
        SectorDefinition("KECANTIKAN", "Kecantikan & Salon", "💅", listOf("Bahan Produk", "Peralatan Salon", "Sewa Tempat", "Tenaga Kerja", "Listrik/Air"), "treatment", "Usaha salon, perawatan wajah, produk kosmetik"),
        SectorDefinition("PARIWISATA", "Pariwisata & Homestay", "🏖️", listOf("Sewa/Kebersihan", "Makan Minum", "Listrik/Wi-Fi", "Pemandu", "Pemasaran"), "malam / orang", "Usaha homestay, tur lokal, tempat wisata"),
        SectorDefinition("DIGITAL", "Produk Digital / Online", "💻", listOf("Internet/Wi-Fi", "Langganan Software", "Listrik", "Waktu Kerja", "Iklan"), "service", "Desain, ketik, pulsa, transaksi digital"),
        SectorDefinition("TEKNOLOGI", "Teknologi & Solusi", "🤖", listOf("Hardware/Server", "Tenaga Dev", "Lisensi", "Operasional"), "lisensi", "Pengembangan sistem, IoT lokal, perakitan"),
        SectorDefinition("PENDIDIKAN", "Pendidikan & Bimbel", "📚", listOf("Buku/Materi", "Gaji Pengajar", "Sewa Ruangan", "ATK", "Listrik/AC"), "siswa / bulan", "Bimbingan belajar, kursus ketrampilan"),
        SectorDefinition("KREATIF", "Kreatif, Seni & Musik", "🎵", listOf("Sewa Alat", "Transportasi Crew", "Honor Seniman", "Listrik/Audio"), "event", "Group musik, dekorasi acara, tari tradisional"),
        SectorDefinition("FOTOGRAFI", "Fotografi & Videografi", "📸", listOf("Sewa Kamera/Lensa", "Editing Time", "Transportasi", "Cetak/Flashdisk"), "paket", "Jasa foto pernikahan, dokumentasi produk"),
        SectorDefinition("KONTEN", "Pembuat Konten / Media", "📹", listOf("Alat Rekam", "Internet Fast", "Editing", "Properti Konten"), "video", "Kreator media lokal, promosi produk UMKM"),
        SectorDefinition("CUSTOM", "Sektor Khusus / Lainnya", "📦", listOf("Bahan Utama", "Tenaga Kerja", "Operasional", "Transportasi"), "unit", "Sektor usaha kustom sesuai kebutuhan pengguna")
    )

    fun getSectorById(id: String): SectorDefinition {
        return SECTORS.find { it.id.equals(id, ignoreCase = true) } ?: SECTORS.last()
    }

    /**
     * Plain language term translator to assist non-technical laypersons (Req AL)
     */
    fun getPlainLanguageTerm(term: String): Pair<String, String> {
        return when (term.uppercase()) {
            "NET PROFIT" -> Pair("Uang Bersih yang Anda Terima", "Sisa uang yang benar-benar masuk ke kantong Anda setelah dipotong biaya operasional, ongkir, dan penanganan.")
            "BEP", "BREAK EVEN POINT" -> Pair("Balik Modal", "Jumlah penjualan minimal yang harus Anda capai agar modal produksi kembali penuh tanpa rugi.")
            "MARGIN" -> Pair("Persentase Keuntungan", "Berapa persen keuntungan yang Anda dapatkan dibandingkan dengan modal yang Anda keluarkan.")
            "COST OF GOODS SOLD", "COGS" -> Pair("Modal per Barang", "Biaya riil yang dikeluarkan untuk membuat atau membeli 1 buah/kg barang tersebut.")
            "VOLUME WEIGHTED PRICE" -> Pair("Harga Rata-Rata Sesuai Ukuran Pembelian", "Harga per unit yang sudah disesuaikan dengan besar kecilnya jumlah barang yang dibeli (eceran vs borongan).")
            "QUALITY GRADE" -> Pair("Tingkat Kualitas Barang", "Ukuran kebaikan fisik barang, seperti Grade A (super), Grade B (sedang), atau Grade C (biasa).")
            else -> Pair(term, "Penjelasan istilah usaha sederhana untuk membantu Anda mengambil keputusan.")
        }
    }
}
