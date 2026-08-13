package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Phonelink
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SettingsSuggest
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.SsmViewModel
import com.example.ui.components.SsmBottomNavBar
import com.example.ui.components.SsmLogoHeader
import com.example.ui.theme.SsmRoyalBlue
import com.example.ui.theme.SsmSuccessGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: SsmViewModel,
    onNavigateToRoute: (String) -> Unit
) {
    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()
    val offersList by viewModel.offers.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.SettingsSuggest,
                            contentDescription = null,
                            tint = SsmRoyalBlue,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "SSM",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = SsmRoyalBlue
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { onNavigateToRoute("settings") }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = SsmRoyalBlue
                        )
                    }
                },
                actions = {
                    com.example.ui.components.VoiceHelpButton(
                        screenName = "home",
                        viewModel = viewModel,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Box(modifier = Modifier.padding(end = 8.dp)) {
                        IconButton(onClick = { onNavigateToRoute("otomatis") }) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Pemberitahuan",
                                tint = SsmRoyalBlue
                            )
                        }
                        // Red Notification Badge
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Color.Red)
                                .align(Alignment.TopEnd)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = {
            SsmBottomNavBar(
                currentRoute = "home",
                onTabSelected = { route -> onNavigateToRoute(route) }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF4F6FB))
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // Logo & Title Header Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        SsmLogoHeader()

                        Spacer(modifier = Modifier.height(16.dp))

                        // Quick Feature Pills Row
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            item {
                                QuickPillBadge(icon = Icons.Default.Mic, text = "Bisa Bicara", color = Color(0xFF0C4AA6))
                            }
                            item {
                                QuickPillBadge(icon = Icons.Default.Cloud, text = "Cek Cuaca", color = Color(0xFF0288D1))
                            }
                            item {
                                QuickPillBadge(icon = Icons.Default.LocationOn, text = "Lokasi Terdekat", color = Color(0xFF00897B))
                            }
                            item {
                                QuickPillBadge(icon = Icons.Default.Calculate, text = "Hitung Modal & Hasil", color = Color(0xFF6A1B9A))
                            }
                        }
                    }
                }
            }

            // Simulation Data Control Card
            item {
                val currentBizList by viewModel.businesses.collectAsStateWithLifecycle()
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCD34D))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Tag,
                                    contentDescription = null,
                                    tint = Color(0xFFB45309),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Mode Simulasi Uji Coba",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF92400E)
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (currentBizList.isEmpty()) "Database saat ini KOSONG (Siap untuk pengujian simulasi)." else "Database berisi ${currentBizList.size} usaha.",
                                fontSize = 12.sp,
                                color = Color(0xFFB45309)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        if (currentBizList.isNotEmpty()) {
                            androidx.compose.material3.Button(
                                onClick = { viewModel.clearSimulationData() },
                                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFDC2626)
                                ),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text("Kosongkan Data", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        } else {
                            androidx.compose.material3.OutlinedButton(
                                onClick = { viewModel.loadSampleData() },
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD97706)),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text("Isi Data Contoh", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD97706))
                            }
                        }
                    }
                }
            }

            // Hero Microphone Action Button ("BICARA SEKARANG")
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToRoute("voice_assistant") }
                        .testTag("voice_hero_button"),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = SsmRoyalBlue),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = "BICARA SEKARANG",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Text(
                                text = "Cukup bicara, SSM yang bekerja",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
            }

            // Pending Offers Banner if available
            val pendingOffers = offersList.filter { it.status == "PENDING" }
            if (pendingOffers.isNotEmpty()) {
                item {
                    val firstOffer = pendingOffers.first()
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToRoute("sales") },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (firstOffer.isWarningLowOffer) Color(0xFFFFF5F5) else Color(0xFFE3F2FD)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(if (firstOffer.isWarningLowOffer) Color(0xFFFFEBEE) else SsmRoyalBlue),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = null,
                                    tint = if (firstOffer.isWarningLowOffer) Color(0xFFC62828) else Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "📩 Tawaran Masuk: ${firstOffer.buyerName}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (firstOffer.isWarningLowOffer) Color(0xFFC62828) else SsmRoyalBlue
                                )
                                Text(
                                    text = "${firstOffer.commodityName} (${firstOffer.quantity.toInt()} ${firstOffer.unit}) • Tawaran Rp${firstOffer.buyerOfferedPricePerUnit.toInt()}/${firstOffer.unit}",
                                    fontSize = 12.sp,
                                    color = Color(0xFF334155)
                                )
                            }
                            Text(
                                text = "Buka Nego ›",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = SsmRoyalBlue
                            )
                        }
                    }
                }
            }

            // 6 Grid Feature Cards (2 Columns x 3 Rows)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Row 1: Saya Butuh & Saya Punya
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        MockupFeatureCard(
                            modifier = Modifier.weight(1f),
                            title = "SAYA BUTUH",
                            subtitle = "Cari barang, supplier, jasa",
                            icon = Icons.Default.ShoppingBag,
                            iconBgColor = Color(0xFFE8F5E9),
                            iconTint = Color(0xFF2E7D32),
                            onClick = { onNavigateToRoute("procurement") },
                            testTag = "card_saya_butuh"
                        )
                        MockupFeatureCard(
                            modifier = Modifier.weight(1f),
                            title = "SAYA PUNYA",
                            subtitle = "Jual produk, cari pembeli",
                            icon = Icons.Default.Inventory2,
                            iconBgColor = Color(0xFFE3F2FD),
                            iconTint = Color(0xFF1976D2),
                            onClick = { onNavigateToRoute("sales") },
                            testTag = "card_saya_punya"
                        )
                    }

                    // Row 2: Harga Pasar & Cuaca
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        MockupFeatureCard(
                            modifier = Modifier.weight(1f),
                            title = "HARGA PASAR",
                            subtitle = "Info harga terbaru",
                            icon = Icons.Default.Tag,
                            iconBgColor = Color(0xFFF3E5F5),
                            iconTint = Color(0xFF7B1FA2),
                            onClick = { onNavigateToRoute("prices") },
                            testTag = "card_harga_pasar"
                        )
                        MockupFeatureCard(
                            modifier = Modifier.weight(1f),
                            title = "CUACA",
                            subtitle = "Prakiraan & peringatan",
                            icon = Icons.Default.Cloud,
                            iconBgColor = Color(0xFFFFF3E0),
                            iconTint = Color(0xFFF57C00),
                            onClick = { onNavigateToRoute("weather") },
                            testTag = "card_cuaca"
                        )
                    }

                    // Row 3: Pasar & Lokasi & Usaha Saya
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        MockupFeatureCard(
                            modifier = Modifier.weight(1f),
                            title = "PASAR & LOKASI",
                            subtitle = "Pasar, supplier, pembeli terdekat",
                            icon = Icons.Default.LocationOn,
                            iconBgColor = Color(0xFFE0F2F1),
                            iconTint = Color(0xFF00796B),
                            onClick = { onNavigateToRoute("around_me") },
                            testTag = "card_pasar_lokasi"
                        )
                        MockupFeatureCard(
                            modifier = Modifier.weight(1f),
                            title = "USAHA SAYA",
                            subtitle = "Kelola usaha & hitung hasil",
                            icon = Icons.Default.TrendingUp,
                            iconBgColor = Color(0xFFFFEBEE),
                            iconTint = Color(0xFFD32F2F),
                            onClick = { onNavigateToRoute("businesses") },
                            testTag = "card_usaha_saya"
                        )
                    }

                    // Row 4: Universal Tools & Calculator Card (Req AS)
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        MockupFeatureCard(
                            modifier = Modifier.fillMaxWidth(),
                            title = "🧮 ALAT UNIVERSAL & KALKULATOR",
                            subtitle = "Simulasi Risiko, BEP, Cold Storage, Beli/Jual Bersama, Nota Digital",
                            icon = Icons.Default.Calculate,
                            iconBgColor = Color(0xFFEFF6FF),
                            iconTint = SsmRoyalBlue,
                            onClick = { onNavigateToRoute("tools") },
                            testTag = "card_alat_universal"
                        )
                    }

                    // Row 5: Account, Identity, Security & Reputation Card
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        MockupFeatureCard(
                            modifier = Modifier.fillMaxWidth(),
                            title = "🔐 AKUN, KEAMANAN & REPUTASI",
                            subtitle = "Profil Usaha, Login Google, HP Hilang, Status Keamanan & Rating Transaksi",
                            icon = Icons.Default.Security,
                            iconBgColor = Color(0xFFF0FDF4),
                            iconTint = SsmSuccessGreen,
                            onClick = { onNavigateToRoute("account_security") },
                            testTag = "card_akun_keamanan"
                        )
                    }
                }
            }

            // Bottom Highlights / Value Proposition Section
            item {
                Column(
                    modifier = Modifier.padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        ValueHighlightCard(
                            modifier = Modifier.weight(1f),
                            title = "Mudah Digunakan",
                            desc = "Tampilan sederhana cocok untuk semua kalangan",
                            icon = Icons.Default.Phonelink,
                            iconBg = Color(0xFFE3F2FD),
                            iconTint = SsmRoyalBlue
                        )
                        ValueHighlightCard(
                            modifier = Modifier.weight(1f),
                            title = "Bisa Offline",
                            desc = "Data tetap aman meski tanpa internet",
                            icon = Icons.Default.WifiOff,
                            iconBg = Color(0xFFE1F5FE),
                            iconTint = Color(0xFF0288D1)
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        ValueHighlightCard(
                            modifier = Modifier.weight(1f),
                            title = "Hemat & Akurat",
                            desc = "Hitung modal, hasil, keuntungan secara akurat",
                            icon = Icons.Default.Calculate,
                            iconBg = Color(0xFFE8EAF6),
                            iconTint = Color(0xFF3F51B5)
                        )
                        ValueHighlightCard(
                            modifier = Modifier.weight(1f),
                            title = "Aman & Terpercaya",
                            desc = "Data Anda aman dengan sistem terpercaya",
                            icon = Icons.Default.Security,
                            iconBg = Color(0xFFE0F2F1),
                            iconTint = Color(0xFF00796B)
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }
        }
    }
}

@Composable
fun QuickPillBadge(
    icon: ImageVector,
    text: String,
    color: Color
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFF1F5F9))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF334155)
        )
    }
}

@Composable
fun MockupFeatureCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconBgColor: Color,
    iconTint: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        modifier = modifier
            .height(135.dp)
            .clickable(onClick = onClick)
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1E293B)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun ValueHighlightCard(
    modifier: Modifier = Modifier,
    title: String,
    desc: String,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
                Text(
                    text = desc,
                    fontSize = 10.sp,
                    color = Color.Gray,
                    lineHeight = 13.sp
                )
            }
        }
    }
}
