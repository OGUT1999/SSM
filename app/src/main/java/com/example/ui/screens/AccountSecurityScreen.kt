package com.example.ui.screens

import com.example.ui.components.SsmLogoCard
import androidx.compose.foundation.background
import androidx.compose.ui.res.painterResource
import com.example.R
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.SsmViewModel
import com.example.ui.theme.SsmRoyalBlue
import com.example.ui.theme.SsmSuccessGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountSecurityScreen(
    viewModel: SsmViewModel,
    onBack: () -> Unit
) {
    val profile by viewModel.userProfile.collectAsState()
    val secOverview by viewModel.securityOverview.collectAsState()
    val repSummary by viewModel.reputationSummary.collectAsState()
    val ratings by viewModel.allRatings.collectAsState()
    val backupMeta by viewModel.latestBackupMeta.collectAsState()
    val secEvents by viewModel.securityEvents.collectAsState()

    var selectedTab by remember { mutableStateOf(0) } // 0: Overview, 1: Usaha, 2: Lokasi, 3: Reputasi, 4: Keamanan & Perangkat, 5: HP Hilang, 6: Backup, 7: Laporan & Privasi

    val isProtectionActive = secOverview?.isProtectionModeActive ?: false

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Akun & Keamanan SSM",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SsmSuccessGreen)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF8FAFC))
        ) {
            // Tab Header
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = SsmSuccessGreen
            ) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Overview", fontSize = 12.sp) })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Usaha Saya", fontSize = 12.sp) })
                Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("Lokasi", fontSize = 12.sp) })
                Tab(selected = selectedTab == 3, onClick = { selectedTab = 3 }, text = { Text("Reputasi", fontSize = 12.sp) })
                Tab(selected = selectedTab == 4, onClick = { selectedTab = 4 }, text = { Text("Keamanan", fontSize = 12.sp) })
                Tab(selected = selectedTab == 5, onClick = { selectedTab = 5 }, text = { Text("HP Hilang", fontSize = 12.sp) })
                Tab(selected = selectedTab == 6, onClick = { selectedTab = 6 }, text = { Text("Backup", fontSize = 12.sp) })
                Tab(selected = selectedTab == 7, onClick = { selectedTab = 7 }, text = { Text("Privasi", fontSize = 12.sp) })
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Top Status Card (Dashboard Keamanan)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isProtectionActive) Color(0xFFFEF2F2) else Color(0xFFF0FDF4)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isProtectionActive) Icons.Default.Warning else Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = if (isProtectionActive) Color(0xFFDC2626) else Color(0xFF16A34A),
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = secOverview?.statusTitle ?: "🟢 Akun Aman",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isProtectionActive) Color(0xFF991B1B) else Color(0xFF14532D)
                            )
                            Text(
                                text = secOverview?.statusDetailsText ?: "Data terhubung Google & terlindungi di perangkat ini.",
                                fontSize = 12.sp,
                                color = Color(0xFF475569)
                            )
                        }
                    }
                }

                when (selectedTab) {
                    0 -> OverviewTab(profile, secOverview, repSummary, onNavigateTab = { selectedTab = it })
                    1 -> BusinessActivitiesTab(viewModel, profile)
                    2 -> LocationPrivacyTab(viewModel, profile)
                    3 -> ReputationTab(viewModel, repSummary, ratings)
                    4 -> SecurityAndDevicesTab(viewModel, profile, secOverview, secEvents)
                    5 -> LostPhoneModeTab(viewModel, profile, isProtectionActive)
                    6 -> BackupTab(viewModel, backupMeta)
                    7 -> PrivacyAndReportsTab(viewModel, profile)
                }
            }
        }
    }
}

@Composable
fun OverviewTab(
    profile: com.example.data.local.UserProfileEntity?,
    secOverview: com.example.engine.security.SecurityStatusOverview?,
    repSummary: com.example.engine.reputation.UserReputationSummary?,
    onNavigateTab: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SsmLogoCard(
                    cardSize = 72.dp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = profile?.googleDisplayName ?: "Mitra UMKM SSM",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    Text(
                        text = "ID: ${profile?.userId ?: "-"} • Google: ${profile?.googleEmail ?: "-"}",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider()
            Spacer(modifier = Modifier.height(12.dp))

            Text("Ringkasan Akses Cepat", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
            Spacer(modifier = Modifier.height(8.dp))

            MenuItemRow("🏪 Usaha Saya", "Sektor Utama: ${profile?.primarySector ?: "Petani"}", Icons.Default.Storefront) { onNavigateTab(1) }
            MenuItemRow("📍 Lokasi & Wilayah", profile?.operationalRegion ?: "-", Icons.Default.LocationOn) { onNavigateTab(2) }
            MenuItemRow("⭐ Reputasi Saya", "${repSummary?.reputationStatusText ?: "Terpercaya"} (${repSummary?.totalVerifiedTransactions ?: 0} Transaksi)", Icons.Default.Star) { onNavigateTab(3) }
            MenuItemRow("🔐 Keamanan & Perangkat", "Google Terhubung • 1 Perangkat Aktif", Icons.Default.Security) { onNavigateTab(4) }
            MenuItemRow("🚨 HP Hilang / Dicuri", "Mode Perlindungan Akun & Pemulihan", Icons.Default.Warning) { onNavigateTab(5) }
            MenuItemRow("💾 Backup Data", "Cadangkan data transaksi & inventaris lokal", Icons.Default.Backup) { onNavigateTab(6) }
        }
    }
}

@Composable
fun MenuItemRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = SsmSuccessGreen, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1E293B))
            Text(subtitle, fontSize = 12.sp, color = Color(0xFF64748B))
        }
        Text("➔", color = Color.Gray)
    }
}

@Composable
fun BusinessActivitiesTab(
    viewModel: SsmViewModel,
    profile: com.example.data.local.UserProfileEntity?
) {
    var newActivityName by remember { mutableStateOf("") }
    var selectedSector by remember { mutableStateOf("Jasa") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("🏪 Profil Usaha Saya", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
            Text(
                "Satu identitas SSM dapat mengelola beberapa profesi/usaha sekaligus tanpa perlu membuat akun baru.",
                fontSize = 12.sp,
                color = Color(0xFF64748B),
                modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SsmSuccessGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Sektor Utama", fontSize = 12.sp, color = Color.Gray)
                        Text(profile?.primarySector ?: "Petani", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Tambah Usaha / Aktivitas Lain", fontSize = 14.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = newActivityName,
                onValueChange = { newActivityName = it },
                label = { Text("Nama Usaha/Profesi (mis. Jasa Angkut Truk)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_new_business_name")
            )

            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = selectedSector,
                onValueChange = { selectedSector = it },
                label = { Text("Sektor (Jasa, Pedagang, Kuliner, dll.)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_new_business_sector")
            )

            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = {
                    if (newActivityName.isNotBlank()) {
                        viewModel.addBusinessActivity(newActivityName, selectedSector, profile?.operationalRegion ?: "Kec. Natar")
                        newActivityName = ""
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_add_business"),
                colors = ButtonDefaults.buttonColors(containerColor = SsmSuccessGreen)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Tambah Aktivitas Usaha")
            }
        }
    }
}

@Composable
fun LocationPrivacyTab(
    viewModel: SsmViewModel,
    profile: com.example.data.local.UserProfileEntity?
) {
    var selectedLevel by remember { mutableStateOf("KECAMATAN") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("📍 Pengaturan Privasi Lokasi", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
            Text(
                "SSM membedakan Lokasi Pribadi (Koordinat GPS) dari Wilayah Usaha Publik. Koordinat rumah Anda TIDAK PERNAH ditampilkan publik.",
                fontSize = 12.sp,
                color = Color(0xFF64748B),
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            )

            Text("Pilih Tingkat Lokasi Publik Yang Tampil:", fontSize = 14.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(8.dp))

            val options = listOf(
                "DESA" to "Tingkat Desa/Kelurahan (e.g. Desa Natar)",
                "KECAMATAN" to "Tingkat Kecamatan (e.g. Kec. Natar) [Rekomendasi]",
                "KABUPATEN" to "Tingkat Kabupaten/Kota (e.g. Kab. Lampung Selatan)",
                "HIDDEN" to "Sembunyikan Lokasi Publik Secara Total"
            )

            options.forEach { (key, label) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedLevel = key
                            viewModel.updateLocationPermissionAndLevel(true, key)
                        }
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedLevel == key,
                        onClick = {
                            selectedLevel = key
                            viewModel.updateLocationPermissionAndLevel(true, key)
                        }
                    )
                    Text(label, fontSize = 13.sp, color = Color(0xFF334155))
                }
            }
        }
    }
}

@Composable
fun ReputationTab(
    viewModel: SsmViewModel,
    repSummary: com.example.engine.reputation.UserReputationSummary?,
    ratings: List<com.example.data.local.TransactionRatingEntity>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("⭐ Reputasi & Rating Dua Arah", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
            Text(
                "Rating terikat pada transaksi sah di SSM. Sistem Blind Rating mencegah pembalasan ulasan palsu.",
                fontSize = 12.sp,
                color = Color(0xFF64748B),
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Status Reputasi: ${repSummary?.reputationStatusText ?: "Terpercaya"}", fontWeight = FontWeight.Bold, color = SsmSuccessGreen)
                    Text("Total Transaksi Terverifikasi: ${repSummary?.totalVerifiedTransactions ?: 0}", fontSize = 12.sp)
                    Text("Tingkat Penyelesaian Transaksi: ${repSummary?.completionRatePercent ?: 100}%", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (repSummary?.isFraudSuspicionActive == true) {
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7))) {
                    Text(
                        repSummary.fraudWarningText,
                        modifier = Modifier.padding(10.dp),
                        fontSize = 12.sp,
                        color = Color(0xFFB45309)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text("Ulasan Terbaru", fontSize = 14.sp, fontWeight = FontWeight.Bold)

            if (ratings.isEmpty()) {
                Text("Belum ada ulasan transaksi.", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(top = 4.dp))
            } else {
                ratings.take(5).forEach { r ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Pemberi Rating: ${r.targetBusinessName} (${r.raterRole})", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("Skor: ⭐ ${r.overallRating} / 5.0 • Status: ${r.verificationStatus}", fontSize = 11.sp, color = SsmSuccessGreen)
                            Text(r.commentText, fontSize = 12.sp, color = Color.DarkGray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SecurityAndDevicesTab(
    viewModel: SsmViewModel,
    profile: com.example.data.local.UserProfileEntity?,
    secOverview: com.example.engine.security.SecurityStatusOverview?,
    secEvents: List<com.example.data.local.SecurityEventEntity>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("🔐 Keamanan Akun & Perangkat Terdaftar", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))

            Spacer(modifier = Modifier.height(12.dp))

            Text("Akun Google Terhubung:", fontSize = 12.sp, color = Color.Gray)
            Text(profile?.googleEmail ?: "mitra.ssm@gmail.com", fontSize = 14.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(12.dp))

            Text("Perangkat Aktif:", fontSize = 12.sp, color = Color.Gray)
            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9))) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = SsmSuccessGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Android Saya (Perangkat Ini)", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("Status: Aktif & Terverifikasi", fontSize = 11.sp, color = Color(0xFF15803D))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.revokeOtherDevices() },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_revoke_devices"),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
            ) {
                Text("Keluar Dari Semua Perangkat Lain")
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Audit Aktivitas Login & Keamanan:", fontSize = 14.sp, fontWeight = FontWeight.Bold)

            secEvents.take(5).forEach { ev ->
                Text("• [${ev.timestampText}] ${ev.eventType}: ${ev.description}", fontSize = 11.sp, color = Color.DarkGray)
            }
        }
    }
}

@Composable
fun LostPhoneModeTab(
    viewModel: SsmViewModel,
    profile: com.example.data.local.UserProfileEntity?,
    isProtectionActive: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("🚨 Mode HP Hilang / Dicuri", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF991B1B))
            Text(
                "Fitur ini membantu Anda membekukan transaksi sensitif dan mencabut sesi di perangkat yang hilang.",
                fontSize = 12.sp,
                color = Color(0xFF64748B),
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            )

            if (isProtectionActive) {
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2))) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("🔴 MODE PERLINDUNGAN AKUN AKTIF", fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
                        Text("Akses transaksi & perubahan rekening dibekukan sementara.", fontSize = 12.sp)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { viewModel.toggleLostPhoneMode(false) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = SsmSuccessGreen)
                ) {
                    Text("Saya Sudah Mendapatkan HP Kembali (Pulihkan)")
                }
            } else {
                Button(
                    onClick = { viewModel.toggleLostPhoneMode(true) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Icon(Icons.Default.Warning, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ya, Amankan Akun Saya Sekarang")
                }
            }
        }
    }
}

@Composable
fun BackupTab(
    viewModel: SsmViewModel,
    backupMeta: com.example.data.local.AccountBackupMetaEntity?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("💾 Backup & Pemulihan Data Lokal", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
            Text(
                "Backup tersimpan di penyimpanan lokal perangkat Anda (Rp0 Biaya Server).",
                fontSize = 12.sp,
                color = Color(0xFF64748B),
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            )

            Text("Backup Terakhir: ${backupMeta?.lastBackupTimestampText ?: "Belum Ada"}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            if (backupMeta != null) {
                Text("Isi: ${backupMeta.recordCountInfo}", fontSize = 11.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.createBackup() },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_create_backup"),
                colors = ButtonDefaults.buttonColors(containerColor = SsmSuccessGreen)
            ) {
                Icon(Icons.Default.Backup, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cadangkan Data Saya Sekarang")
            }
        }
    }
}

@Composable
fun PrivacyAndReportsTab(
    viewModel: SsmViewModel,
    profile: com.example.data.local.UserProfileEntity?
) {
    var reportParty by remember { mutableStateOf("") }
    var reportDesc by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("📜 Laporkan Masalah & Kebijakan Privasi", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))

            Spacer(modifier = Modifier.height(12.dp))

            Text("🚨 Laporkan Transaksi Bermasalah:", fontSize = 14.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = reportParty,
                onValueChange = { reportParty = it },
                label = { Text("Pihak Yang Dilaporkan") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = reportDesc,
                onValueChange = { reportDesc = it },
                label = { Text("Deskripsi Masalah (Barang/Pembayaran)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (reportParty.isNotBlank() && reportDesc.isNotBlank()) {
                        viewModel.submitReport(1, reportParty, "PENIPUAN", reportDesc)
                        reportParty = ""
                        reportDesc = ""
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706))
            ) {
                Text("Kirim Laporan Masalah")
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider()
            Spacer(modifier = Modifier.height(16.dp))

            Text("Ketentuan Privasi SSM:", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(
                "• Kami hanya menyimpan data minimum yang diperlukan untuk operasional usaha Anda.\n" +
                        "• Koordinat rumah Anda tidak pernah dipublikasikan.\n" +
                        "• Aplikasi dapat digunakan 100% offline secara mandiri.\n" +
                        "• Anda berhak meminta penghapusan akun kapan saja.",
                fontSize = 12.sp,
                color = Color(0xFF475569)
            )
        }
    }
}
