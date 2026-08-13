package com.example.ui.screens

import com.example.ui.components.SsmLogoCard
import androidx.compose.foundation.background
import androidx.compose.ui.res.painterResource
import com.example.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.SsmViewModel
import com.example.ui.theme.SsmRoyalBlue
import com.example.ui.theme.SsmSuccessGreen

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AuthOnboardingScreen(
    viewModel: SsmViewModel,
    onBack: () -> Unit,
    onFinishOnboarding: () -> Unit
) {
    val userProfile by viewModel.userProfile.collectAsState()

    var step by remember { mutableStateOf(1) } // 1: Welcome & Google Login, 2: Business Sector, 3: Location Permission
    var nameInput by remember { mutableStateOf(userProfile?.googleDisplayName ?: "Mitra UMKM SSM") }
    var emailInput by remember { mutableStateOf(userProfile?.googleEmail ?: "mitra.ssm@gmail.com") }
    var selectedSector by remember { mutableStateOf(userProfile?.primarySector ?: "Petani") }
    var locationGranted by remember { mutableStateOf(true) }

    val sectors = listOf(
        "Petani", "Peternak", "Nelayan", "Pedagang", "Pengepul", "Toko",
        "Produsen", "Jasa", "Transportasi", "Kuliner", "Kerajinan",
        "Fashion", "Kreatif", "Digital", "Pendidikan", "Konstruksi", "Pariwisata", "Lainnya"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (step == 1) "Pendaftaran SSM" else if (step == 2) "Identitas Usaha" else "Izin Lokasi",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { if (step > 1) step-- else onBack() }) {
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
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (step) {
                1 -> {
                    // Step 1: Welcome & Google Sign-In
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            SsmLogoCard(
                                cardSize = 160.dp
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Selamat datang di SSM",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Daftar atau masuk untuk mulai menggunakan SSM secara gratis dan 100% aman.",
                                fontSize = 14.sp,
                                color = Color(0xFF64748B)
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            OutlinedTextField(
                                value = nameInput,
                                onValueChange = { nameInput = it },
                                label = { Text("Nama Tampilan / Usaha") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_onboarding_name")
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = emailInput,
                                onValueChange = { emailInput = it },
                                label = { Text("Akun Gmail / Google") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_onboarding_email")
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                onClick = {
                                    viewModel.loginWithGoogle(emailInput, nameInput, selectedSector)
                                    step = 2
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("btn_continue_google"),
                                colors = ButtonDefaults.buttonColors(containerColor = SsmRoyalBlue),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Security, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Lanjutkan dengan Google", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "🔒 Kami tidak pernah menyimpan password Google Anda. Autentikasi dilakukan melalui Google resmi.",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                }

                2 -> {
                    // Step 2: What do you do? (Single user = Primary Identity, multi-profession)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Storefront, contentDescription = null, tint = SsmSuccessGreen)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Apa yang Anda lakukan?",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E293B)
                                )
                            }
                            Text(
                                text = "Pilih sektor aktivitas utama Anda. Anda dapat menambahkan profesi/usaha lain nanti.",
                                fontSize = 13.sp,
                                color = Color(0xFF64748B),
                                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                            )

                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                sectors.forEach { sector ->
                                    val isSelected = selectedSector.equals(sector, ignoreCase = true)
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { selectedSector = sector },
                                        label = { Text(sector) },
                                        leadingIcon = if (isSelected) {
                                            { Icon(Icons.Default.Check, contentDescription = null) }
                                        } else null,
                                        modifier = Modifier.testTag("chip_sector_$sector")
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Button(
                                onClick = {
                                    viewModel.updateSectorAndRegion(selectedSector, "Kec. Natar, Kab. Lampung Selatan")
                                    step = 3
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("btn_next_sector"),
                                colors = ButtonDefaults.buttonColors(containerColor = SsmSuccessGreen),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Lanjutkan", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                3 -> {
                    // Step 3: Explicit Location Permission Prompt
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFEF3C7)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.GpsFixed,
                                    contentDescription = null,
                                    tint = Color(0xFFD97706),
                                    modifier = Modifier.size(36.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Gunakan lokasi Anda?",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Lokasi digunakan untuk mencari harga pasar, pemasok, pembeli, ongkos kirim, cuaca, dan informasi usaha yang paling dekat dengan Anda.",
                                fontSize = 14.sp,
                                color = Color(0xFF475569)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Lock, contentDescription = null, tint = SsmSuccessGreen, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Privasi Terjaga: Koordinat rumah Anda tidak pernah ditampilkan secara umum. Hanya tingkat Kecamatan/Kabupaten yang terlihat secara publik.",
                                        fontSize = 12.sp,
                                        color = Color(0xFF334155)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Button(
                                onClick = {
                                    locationGranted = true
                                    viewModel.updateLocationPermissionAndLevel(true, "KECAMATAN")
                                    onFinishOnboarding()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("btn_grant_location"),
                                colors = ButtonDefaults.buttonColors(containerColor = SsmSuccessGreen),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.LocationOn, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Izinkan Lokasi", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedButton(
                                onClick = {
                                    locationGranted = false
                                    viewModel.updateLocationPermissionAndLevel(false, "HIDDEN")
                                    onFinishOnboarding()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("btn_location_later"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Nanti Saja (Lokal Manual)")
                            }
                        }
                    }
                }
            }
        }
    }
}
