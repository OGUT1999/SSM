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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.SsmViewModel
import com.example.ui.components.SsmBottomNavBar
import com.example.ui.theme.SsmRoyalBlue
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessesScreen(
    viewModel: SsmViewModel,
    onBack: () -> Unit
) {
    val businesses by viewModel.businesses.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableStateOf(0) } // 0: Usaha Saya, 1: Simulasi Hasil & BEP
    var showAddDialog by remember { mutableStateOf(false) }

    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
        maximumFractionDigits = 0
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (selectedTab == 0) "Usaha Saya" else "Simulasi Hasil Panen",
                        color = SsmRoyalBlue,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = SsmRoyalBlue
                        )
                    }
                },
                actions = {
                    if (selectedTab == 0) {
                        IconButton(onClick = { showAddDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Tambah Usaha",
                                tint = SsmRoyalBlue,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            SsmBottomNavBar(
                currentRoute = "businesses",
                onTabSelected = { route -> onBack() }
            )
        },
        floatingActionButton = {
            if (selectedTab == 0) {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = SsmRoyalBlue,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("add_business_fab")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah Usaha")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF4F6FB))
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = SsmRoyalBlue
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            "Dashboard Usaha (${businesses.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            "Simulasi Hasil & BEP",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                )
            }

            if (selectedTab == 0) {
                // DASHBOARD USAHA VIEW
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(businesses) { biz ->
                        val estYield = "1.200 kg"
                        val estProfit = biz.initialCapital * 2.1

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                // Business Header Row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Circular thumbnail avatar
                                    Box(
                                        modifier = Modifier
                                            .size(52.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFFFEBEE)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = "🌶️", fontSize = 28.sp)
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = biz.name,
                                                fontSize = 17.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF1E293B)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            // Active Badge
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(Color(0xFFE8F5E9))
                                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = "Aktif",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF2E7D32)
                                                )
                                            }
                                        }

                                        Text(
                                            text = biz.category,
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // 2x2 Metrics Grid
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFFF8FAFC))
                                        .padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Row(modifier = Modifier.fillMaxWidth()) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("Luas Lahan / Skala", fontSize = 11.sp, color = Color.Gray)
                                            Text(
                                                biz.areaOrCapacity,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF1E293B)
                                            )
                                        }
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("Modal Awal", fontSize = 11.sp, color = Color.Gray)
                                            Text(
                                                currencyFormatter.format(biz.initialCapital),
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF1E293B)
                                            )
                                        }
                                    }
                                    Row(modifier = Modifier.fillMaxWidth()) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("Perkiraan Hasil", fontSize = 11.sp, color = Color.Gray)
                                            Text(
                                                estYield,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF1E293B)
                                            )
                                        }
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("Estimasi Keuntungan", fontSize = 11.sp, color = Color.Gray)
                                            Text(
                                                currencyFormatter.format(estProfit),
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF2E7D32)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // SIMULASI HASIL & BEP Button
                                Button(
                                    onClick = { selectedTab = 1 },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = SsmRoyalBlue)
                                ) {
                                    Text(
                                        text = "SIMULASI HASIL & BEP",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }

                    // Ringkasan Cepat Card
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Ringkasan Cepat",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E293B)
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Harga Cabai (Hari ini)", fontSize = 12.sp, color = Color.Gray)
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("Rp 28.000 / kg", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(Color(0xFFE8F5E9))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text("▲ Rp 37.500 / kg", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                                            }
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Cuaca Hari Ini", fontSize = 12.sp, color = Color.Gray)
                                        Text("Berawan, 28°C", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Rekomendasi Hari Ini", fontSize = 12.sp, color = Color.Gray)
                                        Text("Penyiraman secukupnya", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = SsmRoyalBlue)
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // SIMULASI HASIL PANEN & BEP VIEW
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header Selected Commodity Card
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFFFEBEE)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "🌶️", fontSize = 26.sp)
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column {
                                    Text("Cabai Merah", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                                    Text("Luas Lahan: 1.000 m²", fontSize = 12.sp, color = Color.Gray)
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            text = "Pilih Skenario",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                    }

                    // 3 Scenario Cards (OPTIMAL, NORMAL, BURUK)
                    item {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // OPTIMAL
                            ScenarioCard(
                                modifier = Modifier.weight(1f),
                                title = "OPTIMAL",
                                icon = Icons.Default.CheckCircle,
                                headerBg = Color(0xFF2E7D32),
                                borderBg = Color(0xFFE8F5E9),
                                yieldText = "1.500 kg",
                                incomeText = "Rp 42.000.000",
                                profitText = "Rp 25.000.000"
                            )

                            // NORMAL
                            ScenarioCard(
                                modifier = Modifier.weight(1f),
                                title = "NORMAL",
                                icon = Icons.Default.Info,
                                headerBg = SsmRoyalBlue,
                                borderBg = Color(0xFFE3F2FD),
                                yieldText = "1.200 kg",
                                incomeText = "Rp 33.600.000",
                                profitText = "Rp 17.000.000"
                            )

                            // BURUK
                            ScenarioCard(
                                modifier = Modifier.weight(1f),
                                title = "BURUK",
                                icon = Icons.Default.Warning,
                                headerBg = Color(0xFFD32F2F),
                                borderBg = Color(0xFFFFEBEE),
                                yieldText = "800 kg",
                                incomeText = "Rp 22.400.000",
                                profitText = "Rp 6.000.000"
                            )
                        }
                    }

                    // Break Even Point (BEP) Card
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Break Even Point (BEP)",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E293B)
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Harga BEP per kg", fontSize = 13.sp, color = Color.Gray)
                                    Text("Rp 12.500", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SsmRoyalBlue)
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Total Biaya Produksi", fontSize = 13.sp, color = Color.Gray)
                                    Text("Rp 15.000.000", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Highlight Callout Banner
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFFEBF3FF))
                                        .padding(12.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Shield,
                                            contentDescription = null,
                                            tint = SsmRoyalBlue,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = "BEP tercapai jika harga jual lebih dari Rp 12.500 / kg",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = SsmRoyalBlue
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Business Modal Dialog
    if (showAddDialog) {
        var name by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("PETANI") }
        var commodity by remember { mutableStateOf("") }
        var area by remember { mutableStateOf("") }
        var capital by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Tambah Usaha Baru", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nama Usaha (mis: Kebun Cabai Natar)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = commodity,
                        onValueChange = { commodity = it },
                        label = { Text("Komoditas / Produk (mis: Cabai Merah)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = area,
                        onValueChange = { area = it },
                        label = { Text("Skala / Luas (mis: 1.000 m²)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = capital,
                        onValueChange = { capital = it },
                        label = { Text("Modal Awal Rp (mis: 2000000)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotBlank() && commodity.isNotBlank()) {
                            val capDouble = capital.toDoubleOrNull() ?: 2000000.0
                            viewModel.addBusiness(
                                name = name,
                                category = category,
                                commodity = commodity,
                                capacity = area.ifBlank { "1.000 m²" },
                                capital = capDouble
                            )
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SsmRoyalBlue)
                ) {
                    Text("Simpan", color = Color.White)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showAddDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) {
                    Text("Batal", color = Color.White)
                }
            }
        )
    }
}

@Composable
fun ScenarioCard(
    modifier: Modifier = Modifier,
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    headerBg: Color,
    borderBg: Color,
    yieldText: String,
    incomeText: String,
    profitText: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(headerBg)
                    .padding(vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = title,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
            }

            Column(
                modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text("Hasil Panen", fontSize = 10.sp, color = Color.Gray)
                Text(yieldText, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                Text("Pendapatan", fontSize = 10.sp, color = Color.Gray)
                Text(incomeText, fontSize = 11.sp, fontWeight = FontWeight.Bold)

                Text("Keuntungan", fontSize = 10.sp, color = Color.Gray)
                Text(profitText, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = headerBg)
            }
        }
    }
}
