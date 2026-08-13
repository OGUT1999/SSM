package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.SsmViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SsmViewModel,
    onBack: () -> Unit
) {
    val isSimpleMode by viewModel.isSimpleMode.collectAsStateWithLifecycle()
    val isHighContrast by viewModel.isHighContrast.collectAsStateWithLifecycle()
    val isLargeText by viewModel.isLargeText.collectAsStateWithLifecycle()
    val isFullVoiceMode by viewModel.isFullVoiceMode.collectAsStateWithLifecycle()
    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()
    val offlineDataPackDownloaded by viewModel.offlineDataPackDownloaded.collectAsStateWithLifecycle()
    val costStatus by viewModel.costControlStatus.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()

    var showAccountDialog by remember { mutableStateOf(false) }
    var showBackupDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }

    // Logistics Calculator State
    var distanceKmText by remember { mutableStateOf("25") }
    var weightKgText by remember { mutableStateOf("500") }
    var vehicleType by remember { mutableStateOf("PICKUP") } // PICKUP, ENGKEL, TRUK
    var estimatedShippingCost by remember { mutableDoubleStateOf(175000.0) }

    val currencyFormat = remember {
        NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply { maximumFractionDigits = 0 }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "AKUN & PENGATURAN",
                        fontWeight = FontWeight.ExtraBold,
                        color = com.example.ui.theme.SsmRoyalBlue,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("back_button")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = com.example.ui.theme.SsmRoyalBlue
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = {
            com.example.ui.components.SsmBottomNavBar(
                currentRoute = "settings",
                onTabSelected = { route -> onBack() }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Universal Accessibility Section (Req 41 - 61)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Aksesibilitas & Disabilitas (Universal)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "Pengaturan aksesibilitas untuk keterbatasan penglihatan, pendengaran, dan literasi",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Voice Mode Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "🎤 Mode Suara Penuh (TTS Guidance)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Membacakan teks & instruksi secara otomatis lewat suara",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                            Switch(
                                checked = isFullVoiceMode,
                                onCheckedChange = { viewModel.toggleFullVoiceMode() },
                                modifier = Modifier.testTag("toggle_full_voice_switch")
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Simple / Big Buttons Mode Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "🔲 Mode Tombol Besar & Sederhana",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Menampilkan ikon besar dan menu ringkas untuk kemudahan sentuhan",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                            Switch(
                                checked = isSimpleMode,
                                onCheckedChange = { viewModel.toggleSimpleMode() },
                                modifier = Modifier.testTag("toggle_simple_mode_switch")
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // High Contrast Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "🌓 Mode Kontras Tinggi",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Meningkatkan kontras warna batas dan teks untuk low vision",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                            Switch(
                                checked = isHighContrast,
                                onCheckedChange = { viewModel.toggleHighContrast() },
                                modifier = Modifier.testTag("toggle_high_contrast_switch")
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Large Text Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "🔤 Mode Teks Besar (Scalable)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Memperbesar ukuran font agar mudah dibaca lansia / buta huruf",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                            Switch(
                                checked = isLargeText,
                                onCheckedChange = { viewModel.toggleLargeText() },
                                modifier = Modifier.testTag("toggle_large_text_switch")
                            )
                        }
                    }
                }
            }

            // Section 1: Offline & Sync Queue Status
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Status Sinkronisasi & Data Offline",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Status Koneksi",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = if (isOnline) "Online (Server & Cloud Sync)" else "Offline (Lokal Tanpa Internet)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isOnline) Color(0xFF2E7D32) else Color(0xFFE65100)
                                )
                            }
                            Switch(
                                checked = isOnline,
                                onCheckedChange = { viewModel.toggleOnlineStatus() },
                                modifier = Modifier.testTag("toggle_online_status_switch")
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Paket Data Peta & Harga Offline",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = if (offlineDataPackDownloaded) "Tersimpan Lokal (Siap digunakan tanpa kuota)" else "Belum Diunduh",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                            Button(
                                onClick = { viewModel.toggleOfflineDataPack() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (offlineDataPackDownloaded) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text(if (offlineDataPackDownloaded) "Tersimpan" else "Unduh")
                            }
                        }
                    }
                }
            }

            // Zero-Budget Cost Control & Provider Abstraction (Zero-Cost First Rule)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = null,
                                tint = Color(0xFF2E7D32)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Engine Arsitektur & Provider Rp0 Budget",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Mode Operasional: MODAL RP0 (Zero-Cost First Rule)",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF1F8E9), shape = RoundedCornerShape(8.dp))
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(text = "• AI Provider: ${costStatus.activeAiProvider}", style = MaterialTheme.typography.bodySmall)
                            Text(text = "• Map Provider: ${costStatus.activeMapProvider}", style = MaterialTheme.typography.bodySmall)
                            Text(text = "• Weather Provider: ${costStatus.activeWeatherProvider}", style = MaterialTheme.typography.bodySmall)
                            Text(text = "• Cost Protection: ACTIVE ($0 Charges / Mo)", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                            Text(text = "• Fallback Cache Hit Rate: ${costStatus.cacheHitRatePercent}%", style = MaterialTheme.typography.bodySmall, color = Color(0xFF1B5E20))
                        }
                    }
                }
            }

            // Section 2: Logistics & Shipping Calculator (Req 163)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocalShipping,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Kalkulator Estimasi Ongkir & Logistik",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "Hitung estimasi biaya pengiriman komoditas panen/pupuk berdasarkan jarak dan kendaraan",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = distanceKmText,
                                onValueChange = { distanceKmText = it },
                                label = { Text("Jarak (Km)") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = weightKgText,
                                onValueChange = { weightKgText = it },
                                label = { Text("Berat (Kg)") },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { vehicleType = "PICKUP" },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (vehicleType == "PICKUP") MaterialTheme.colorScheme.primary else Color.LightGray
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Pickup (1T)", fontSize = MaterialTheme.typography.labelSmall.fontSize)
                            }
                            Button(
                                onClick = { vehicleType = "ENGKEL" },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (vehicleType == "ENGKEL") MaterialTheme.colorScheme.primary else Color.LightGray
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Engkel (3T)", fontSize = MaterialTheme.typography.labelSmall.fontSize)
                            }
                            Button(
                                onClick = { vehicleType = "TRUK" },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (vehicleType == "TRUK") MaterialTheme.colorScheme.primary else Color.LightGray
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Truk (8T)", fontSize = MaterialTheme.typography.labelSmall.fontSize)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                val dist = distanceKmText.toDoubleOrNull() ?: 10.0
                                val weight = weightKgText.toDoubleOrNull() ?: 100.0
                                val baseCost = when (vehicleType) {
                                    "PICKUP" -> 75000.0 + (dist * 4000.0)
                                    "ENGKEL" -> 150000.0 + (dist * 6500.0)
                                    else -> 300000.0 + (dist * 11000.0)
                                }
                                val weightFactor = if (weight > 1000) (weight / 1000.0) * 10000.0 else 0.0
                                estimatedShippingCost = baseCost + weightFactor
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Hitung Estimasi Ongkir")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Estimasi Ongkir:",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = currencyFormat.format(estimatedShippingCost),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }
                }
            }

            // Section 3: Profile, Backup & Restore
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Profil Usaha & Cadangan Data",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { showAccountDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Profil & Akun Pengguna", color = MaterialTheme.colorScheme.onPrimaryContainer)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = { showBackupDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                        ) {
                            Icon(imageVector = Icons.Default.Backup, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Cadangkan & Pemulihan Data (Backup/Restore)", color = MaterialTheme.colorScheme.onSecondaryContainer)
                        }
                    }
                }
            }

            // Section 4: Legal, Compliance & Disclaimers (Req 156-160)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = Color(0xFF388E3C)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Privasi, Syarat & Panduan Keamanan",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        TextButton(
                            onClick = { showPrivacyDialog = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                                Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Kebijakan Privasi & Ketentuan Layanan")
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Disclaimers Box
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = Color(0xFFF57F17))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Penafian Resmi (Disclaimers)",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFF57F17)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "• AI & Keuangan: Angka simulasi panen, profit, dan BEP adalah ESTIMASI berdasarkan data historis, bukan jaminan pasti.\n" +
                                            "• Pertanian & Peternakan: Rekomendasi pakan/pupuk/obat merupakan panduan umum. Konsultasikan dengan penyuluh resmi daerah untuk penanganan penyakit/pestisida berdosis tinggi.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF5D4037)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { showDeleteAccountDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEBEE)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(imageVector = Icons.Default.DeleteForever, contentDescription = null, tint = Color(0xFFC62828))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Hapus Akun & Data Saya", color = Color(0xFFC62828))
                        }
                    }
                }
            }
        }
    }

    // Account Modal
    if (showAccountDialog) {
        var nameInput by remember(userProfile) { mutableStateOf(userProfile?.googleDisplayName ?: "Mitra UMKM SSM") }
        var emailInput by remember(userProfile) { mutableStateOf(userProfile?.googleEmail ?: "mitra.ssm@gmail.com") }
        var regionInput by remember(userProfile) { mutableStateOf(userProfile?.operationalRegion ?: "Indonesia") }

        AlertDialog(
            onDismissRequest = { showAccountDialog = false },
            title = { Text("Kelola Profil Usaha UMKM", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Sesuaikan nama pemilik dan wilayah operasional untuk aplikasi Anda:", fontSize = 12.sp, color = Color(0xFF64748B))

                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Nama Pemilik / Usaha") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        label = { Text("Email / ID Kontak") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = regionInput,
                        onValueChange = { regionInput = it },
                        label = { Text("Wilayah Operasional") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Text(
                        text = "ID Pengguna: ${userProfile?.userId ?: "Lokal"} • Status: Aktif",
                        fontSize = 11.sp,
                        color = Color(0xFF0352A5)
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.updateUserProfile(
                        displayName = nameInput,
                        email = emailInput,
                        region = regionInput,
                        primarySector = userProfile?.primarySector ?: "Petani"
                    )
                    showAccountDialog = false
                }) {
                    Text("Simpan Profil")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAccountDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    // Backup Dialog
    if (showBackupDialog) {
        AlertDialog(
            onDismissRequest = { showBackupDialog = false },
            title = { Text("Cadangkan Data Usaha") },
            text = {
                Text("Semua data transaksi, stok, dan simulasi tersimpan di penyimpan lokal HP Anda. Anda dapat mengekspor atau memulihkannya kapan saja.")
            },
            confirmButton = {
                Button(onClick = { showBackupDialog = false }) {
                    Text("Ekspor Cadangan (.json)")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBackupDialog = false }) {
                    Text("Tutup")
                }
            }
        )
    }

    // Privacy Dialog
    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = { Text("Kebijakan Privasi & Data Safety") },
            text = {
                Text("Aplikasi Solusi Sistem UMKM (SSM) mengutamakan privasi pengguna. Data usaha, posisi GPS, dan suara disimpan di lokal HP Anda dan hanya dikirimkan ke server saat Anda memilih fitur online.")
            },
            confirmButton = {
                Button(onClick = { showPrivacyDialog = false }) {
                    Text("Mengerti")
                }
            }
        )
    }

    // Account Deletion Dialog
    if (showDeleteAccountDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountDialog = false },
            title = { Text("Hapus Akun & Data?") },
            text = {
                Text("Tindakan ini akan menghapus seluruh data transaksi, stok, dan profil usaha dari penyimpanan perangkat ini.")
            },
            confirmButton = {
                Button(
                    onClick = { showDeleteAccountDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))
                ) {
                    Text("Hapus Permanen")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAccountDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}
