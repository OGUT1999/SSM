package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Store
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
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.CollectiveTradeEngine
import com.example.engine.CostBreakdown
import com.example.engine.ExportAssistant
import com.example.engine.PriceEngineExtensions
import com.example.engine.RiskSimulationEngine
import com.example.engine.StorageCostEngine
import com.example.engine.TransactionReceiptEngine
import com.example.engine.UniversalSectorEngine
import com.example.engine.ValueAddedProcessingEngine
import com.example.ui.SsmViewModel
import com.example.ui.components.VoiceHelpButton

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun UniversalToolsScreen(
    viewModel: SsmViewModel,
    onBack: () -> Unit
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf(
        "⚡ Simulasi Risiko",
        "🏭 Mentah vs Olahan",
        "🧊 Gudang & Storage",
        "🤝 Beli/Jual Bersama",
        "📄 Nota Digital",
        "🌐 Panduan Ekspor",
        "🏢 Sektor Usaha"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Alat Universal & Kalkulator SSM",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Analisis Risiko, BEP, Storage & Kolektif",
                            fontSize = 12.sp,
                            color = Color.DarkGray
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("tools_back_button")) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    VoiceHelpButton(
                        screenName = "tools",
                        viewModel = viewModel,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8FAFC))
        ) {
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                edgePadding = 12.dp,
                containerColor = Color.White,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            )
                        },
                        modifier = Modifier.defaultMinSize(minHeight = 48.dp)
                    )
                }
            }

            when (selectedTabIndex) {
                0 -> RiskSimulationSection()
                1 -> RawVsProcessedSection()
                2 -> StorageCostSection()
                3 -> CollectiveTradeSection()
                4 -> DigitalReceiptSection()
                5 -> ExportGuideSection()
                6 -> SectorFilterSection()
            }
        }
    }
}

// -----------------------------------------------------------------------------
// SECTION 1: SIMULASI RISIKO ("APA YANG TERJADI JIKA?")
// -----------------------------------------------------------------------------
@Composable
private fun RiskSimulationSection() {
    var baseCostInput by remember { mutableStateOf("10000000") }
    var baseYieldInput by remember { mutableStateOf("500") }
    var basePriceInput by remember { mutableStateOf("25000") }

    val costVal = baseCostInput.toDoubleOrNull() ?: 10000000.0
    val yieldVal = baseYieldInput.toDoubleOrNull() ?: 500.0
    val priceVal = basePriceInput.toDoubleOrNull() ?: 25000.0

    val breakdown = remember(costVal, yieldVal) {
        CostBreakdown(
            seedCost = costVal * 0.3,
            fertilizerCost = costVal * 0.3,
            laborCost = costVal * 0.4,
            totalProductionVolume = yieldVal,
            unit = "kg"
        )
    }

    val scenarioResults = remember(breakdown, priceVal) {
        RiskSimulationEngine.runRiskSimulation(breakdown, priceVal)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Assessment, contentDescription = null, tint = Color(0xFF1D4ED8))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Simulasi Risiko & Sensitivitas (Apa Yang Terjadi Jika?)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF1E3A8A)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Uji ketahanan modal usaha Anda jika harga pasar turun, panen berkurang, atau biaya operasional naik.",
                        fontSize = 13.sp,
                        color = Color(0xFF1E40AF)
                    )
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Parameter Dasar Usaha Anda:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = baseCostInput,
                            onValueChange = { baseCostInput = it },
                            label = { Text("Total Modal (Rp)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = baseYieldInput,
                            onValueChange = { baseYieldInput = it },
                            label = { Text("Total Hasil (KG)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = basePriceInput,
                        onValueChange = { basePriceInput = it },
                        label = { Text("Harga Jual Target (Rp/KG)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        items(scenarioResults) { res ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (res.simulatedProfitLoss >= 0) Color(0xFFF0FDF4) else Color(0xFFFEF2F2)
                ),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = if (res.simulatedProfitLoss >= 0) Color(0xFF86EFAC) else Color(0xFFFCA5A5)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = res.scenarioName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF1E293B)
                        )
                        Text(
                            text = res.statusText,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Harga: ${PriceEngineExtensions.formatRupiah(res.simulatedPricePerUnit)}/kg",
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Hasil: ${res.simulatedYieldVolume.toInt()} kg",
                            fontSize = 13.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Laba / Rugi Bersih:",
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp
                        )
                        Text(
                            text = PriceEngineExtensions.formatRupiah(res.simulatedProfitLoss),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            color = if (res.simulatedProfitLoss >= 0) Color(0xFF15803D) else Color(0xFFB91C1C)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "💡 ${res.riskAdvice}",
                        fontSize = 12.sp,
                        color = Color(0xFF334155),
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// SECTION 2: MENTAH VS OLAHAN
// -----------------------------------------------------------------------------
@Composable
private fun RawVsProcessedSection() {
    var rawName by remember { mutableStateOf("Singkong Segar") }
    var rawPriceInput by remember { mutableStateOf("2500") }
    var rawVolumeInput by remember { mutableStateOf("100") }

    var processedName by remember { mutableStateOf("Keripik Singkong") }
    var processedPriceInput by remember { mutableStateOf("12000") }
    var yieldRatioInput by remember { mutableStateOf("3.5") }
    var laborCostInput by remember { mutableStateOf("150000") }
    var gasCostInput by remember { mutableStateOf("50000") }

    val rawP = rawPriceInput.toDoubleOrNull() ?: 2500.0
    val rawV = rawVolumeInput.toDoubleOrNull() ?: 100.0
    val procP = processedPriceInput.toDoubleOrNull() ?: 12000.0
    val ratio = yieldRatioInput.toDoubleOrNull() ?: 3.5
    val labor = laborCostInput.toDoubleOrNull() ?: 150000.0
    val gas = gasCostInput.toDoubleOrNull() ?: 50000.0

    val comparison = remember(rawName, rawP, rawV, processedName, procP, ratio, labor, gas) {
        ValueAddedProcessingEngine.compareRawVsProcessed(
            rawName = rawName,
            rawPricePerUnit = rawP,
            rawVolumeKg = rawV,
            processedName = processedName,
            processedYieldPcsPerKg = ratio,
            processedPricePerPcs = procP,
            laborCost = labor,
            energyGasCost = gas
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7ED)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Store, contentDescription = null, tint = Color(0xFFC2410C))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Lebih Menguntungkan Dijual Mentah atau Diolah?",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF9A3412)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Bandingkan pendapatan bersih antara menjual komoditas segar vs mengolahnya menjadi produk bernilai tambah.",
                        fontSize = 13.sp,
                        color = Color(0xFFC2410C)
                    )
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(text = "1. Data Komoditas Mentah:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    OutlinedTextField(
                        value = rawName,
                        onValueChange = { rawName = it },
                        label = { Text("Nama Komoditas") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = rawPriceInput,
                            onValueChange = { rawPriceInput = it },
                            label = { Text("Harga Mentah (Rp/KG)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = rawVolumeInput,
                            onValueChange = { rawVolumeInput = it },
                            label = { Text("Volume (KG)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "2. Data Produk Olahan:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    OutlinedTextField(
                        value = processedName,
                        onValueChange = { processedName = it },
                        label = { Text("Nama Produk Olahan") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = processedPriceInput,
                            onValueChange = { processedPriceInput = it },
                            label = { Text("Harga Olahan (Rp/Pcs)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = yieldRatioInput,
                            onValueChange = { yieldRatioInput = it },
                            label = { Text("Hasil (Pcs per KG)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "3. Tambahan Biaya Pengolahan:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = laborCostInput,
                            onValueChange = { laborCostInput = it },
                            label = { Text("Upah Tenaga Kerja (Rp)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = gasCostInput,
                            onValueChange = { gasCostInput = it },
                            label = { Text("Gas / Bumbu (Rp)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (comparison.isProcessingMoreProfitable) Color(0xFFF0FDF4) else Color(0xFFEFF6FF)
                ),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.5.dp,
                    color = if (comparison.isProcessingMoreProfitable) Color(0xFF22C55E) else Color(0xFF3B82F6)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "📊 HASIL PERBANDINGAN:",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Pendapatan Mentah Segar:", fontSize = 13.sp)
                        Text(
                            text = PriceEngineExtensions.formatRupiah(comparison.rawTotalRevenue),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Pendapatan Bersih Olahan:", fontSize = 13.sp)
                        Text(
                            text = PriceEngineExtensions.formatRupiah(comparison.processedNetRevenue),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFF15803D)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = comparison.recommendationSummary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color(0xFF1E293B),
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// SECTION 3: BIAYA GUDANG & COLD STORAGE
// -----------------------------------------------------------------------------
@Composable
private fun StorageCostSection() {
    var storageType by remember { mutableStateOf("Cold Storage / Pendingin") }
    var volumeInput by remember { mutableStateOf("1000") }
    var daysInput by remember { mutableStateOf("7") }
    var priceInput by remember { mutableStateOf("30000") }

    val vol = volumeInput.toDoubleOrNull() ?: 1000.0
    val days = daysInput.toIntOrNull() ?: 7
    val price = priceInput.toDoubleOrNull() ?: 30000.0

    val analysis = remember(storageType, vol, days, price) {
        StorageCostEngine.calculateStorageImpact(storageType, vol, days, price)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Kitchen, contentDescription = null, tint = Color(0xFF15803D))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Kalkulator Biaya Gudang & Cold Storage",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF166534)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Hitung berapa sisa keuntungan bersih jika barang Anda mengendap di gudang atau cold storage selama N hari.",
                        fontSize = 13.sp,
                        color = Color(0xFF15803D)
                    )
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(text = "Tipe Penyimpanan:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Cold Storage / Pendingin", "Gudang Biasa", "Depo / Silo").forEach { type ->
                            Surface(
                                onClick = { storageType = type },
                                shape = RoundedCornerShape(12.dp),
                                color = if (storageType == type) MaterialTheme.colorScheme.primary else Color(0xFFF1F5F9),
                                modifier = Modifier.defaultMinSize(minHeight = 44.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = type,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (storageType == type) Color.White else Color.DarkGray
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = volumeInput,
                        onValueChange = { volumeInput = it },
                        label = { Text("Volume Barang (KG)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = daysInput,
                            onValueChange = { daysInput = it },
                            label = { Text("Lama Simpan (Hari)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = priceInput,
                            onValueChange = { priceInput = it },
                            label = { Text("Harga Barang (Rp/KG)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "🧊 RINCIAN BIAYA SEWA & DAMPAK:", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Tarif Sewa Gudang:", fontSize = 13.sp)
                        Text(
                            text = "Rp${analysis.costPerKgPerDay.toInt()}/kg/hari",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Total Biaya Storage (${analysis.storageDays} Hari):", fontSize = 13.sp)
                        Text(
                            text = PriceEngineExtensions.formatRupiah(analysis.totalStorageCost),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFFC2410C)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Sisa Nilai Bersih:", fontSize = 13.sp)
                        Text(
                            text = PriceEngineExtensions.formatRupiah(analysis.netValueAfterStorage),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            color = Color(0xFF15803D)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = analysis.warningText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color(0xFFB91C1C)
                    )
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// SECTION 4: BELI / JUAL BERSAMA (KOLEKTIF)
// -----------------------------------------------------------------------------
@Composable
private fun CollectiveTradeSection() {
    val buyGroups = remember { CollectiveTradeEngine.getSampleCollectiveBuyGroups() }
    val sellGroups = remember { CollectiveTradeEngine.getSampleCollectiveSellGroups() }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF5FF)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Group, contentDescription = null, tint = Color(0xFF7E22CE))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Beli Bersama & Jual Bersama (Grup Kolektif)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF6B21A8)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Gabungkan kebutuhan modal/pakan dengan sesama produsen untuk mendapatkan diskon grosir pabrik atau harga jual premium.",
                        fontSize = 13.sp,
                        color = Color(0xFF7E22CE)
                    )
                }
            }
        }

        item {
            Text(
                text = "🛒 GRUP BELI BERSAMA (PEMBELIAN KOLEKTIF):",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }

        items(buyGroups) { bg ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = bg.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(text = bg.itemNeeded, fontSize = 13.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Peserta: ${bg.currentParticipantsCount} Orang", fontSize = 12.sp)
                        Text(
                            text = "Terkumpul: ${bg.totalAccumulatedQuantity.toInt()} / ${bg.targetQuantityForDiscount.toInt()} ${bg.unit}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Hemat estimasi ${PriceEngineExtensions.formatRupiah(bg.totalSavingsEst)} jika bergabung!",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color(0xFF15803D)
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7E22CE)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 44.dp)
                    ) {
                        Text("GABUNG GRUP BELI BERSAMA", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "🎁 GRUP JUAL BERSAMA (PENJUALAN KOLEKTIF):",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }

        items(sellGroups) { sg ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = sg.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(text = sg.commodityName, fontSize = 13.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Produsen: ${sg.currentProducersCount} Orang", fontSize = 12.sp)
                        Text(
                            text = "Stok Gabungan: ${sg.totalAccumulatedStock.toInt()} / ${sg.targetVolumeForWholesaler.toInt()} ${sg.unit}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Bonus Omset Gabungan: +${PriceEngineExtensions.formatRupiah(sg.totalExtraRevenueEst)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color(0xFF15803D)
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF15803D)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 44.dp)
                    ) {
                        Text("GABUNG KONSOLIDASI PANEN", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// SECTION 5: NOTA DIGITAL & BUKTI TRANSAKSI (OFFLINE)
// -----------------------------------------------------------------------------
@Composable
private fun DigitalReceiptSection() {
    var businessName by remember { mutableStateOf("Kelompok Tani Makmur") }
    var buyerName by remember { mutableStateOf("Toko Sembako Abadi") }
    var itemName by remember { mutableStateOf("Cabai Merah Keriting Grade A") }
    var qtyInput by remember { mutableStateOf("200") }
    var priceInput by remember { mutableStateOf("24000") }
    var shippingInput by remember { mutableStateOf("150000") }

    var generatedReceipt by remember { mutableStateOf<com.example.engine.DigitalReceipt?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Receipt, contentDescription = null, tint = Color(0xFF334155))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Nota Transaksi Digital & Bukti Penjualan",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF0F172A)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Buat bukti nota resmi transaksi SSM secara langsung tanpa perlu internet.",
                        fontSize = 13.sp,
                        color = Color(0xFF334155)
                    )
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(text = "Rincian Transaksi Nota:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    OutlinedTextField(
                        value = businessName,
                        onValueChange = { businessName = it },
                        label = { Text("Nama Penjual / Usaha") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = buyerName,
                        onValueChange = { buyerName = it },
                        label = { Text("Nama Pembeli / Counterparty") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = itemName,
                        onValueChange = { itemName = it },
                        label = { Text("Nama Barang / Komoditas") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = qtyInput,
                            onValueChange = { qtyInput = it },
                            label = { Text("Jumlah (KG/Unit)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = priceInput,
                            onValueChange = { priceInput = it },
                            label = { Text("Harga (Rp/Unit)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = shippingInput,
                        onValueChange = { shippingInput = it },
                        label = { Text("Ongkos Kirim (Rp)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            val q = qtyInput.toDoubleOrNull() ?: 1.0
                            val p = priceInput.toDoubleOrNull() ?: 0.0
                            val s = shippingInput.toDoubleOrNull() ?: 0.0
                            generatedReceipt = TransactionReceiptEngine.generateReceipt(
                                businessName = businessName,
                                counterpartyName = buyerName,
                                itemName = itemName,
                                quantity = q,
                                unit = "kg",
                                pricePerUnit = p,
                                shippingCost = s
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 48.dp)
                    ) {
                        Text("BUAT NOTA DIGITAL REGISTRASI", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        generatedReceipt?.let { rec ->
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEFCE8)),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEAB308)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "🧾 NOTA RESMI SSM — ${rec.receiptNo}",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            color = Color(0xFF854D0E)
                        )
                        Text(text = "Tanggal: ${rec.dateText}", fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(text = "Penjual: ${rec.businessName}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(text = "Pembeli: ${rec.counterpartyName}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "${rec.itemName} (${rec.quantity.toInt()} ${rec.unit}):", fontSize = 13.sp)
                            Text(text = PriceEngineExtensions.formatRupiah(rec.subtotal), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Ongkos Kirim:", fontSize = 13.sp)
                            Text(text = PriceEngineExtensions.formatRupiah(rec.shippingCost), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "TOTAL PEMBAYARAN:", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                            Text(
                                text = PriceEngineExtensions.formatRupiah(rec.totalAmount),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = Color(0xFF15803D)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "⚠️ ${rec.antiFraudNotice}",
                            fontSize = 11.sp,
                            color = Color(0xFFB91C1C),
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// SECTION 6: PANDUAN EKSPOR
// -----------------------------------------------------------------------------
@Composable
private fun ExportGuideSection() {
    var commodity by remember { mutableStateOf("Kopi Arabika Organik") }
    var country by remember { mutableStateOf("Jepang / Uni Eropa") }

    val guide = remember(commodity, country) { ExportAssistant.getExportGuide(commodity, country) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFECFDF5)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Public, contentDescription = null, tint = Color(0xFF047857))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Asisten Ekspor & Pasar Internasional",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF065F46)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Panduan alur, syarat dokumen kepabeanan, dan standar kemasan ekspor komoditas Anda ke luar negeri.",
                        fontSize = 13.sp,
                        color = Color(0xFF047857)
                    )
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(text = "Tujuan Ekspor:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    OutlinedTextField(
                        value = commodity,
                        onValueChange = { commodity = it },
                        label = { Text("Komoditas / Produk") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = country,
                        onValueChange = { country = it },
                        label = { Text("Negara Tujuan") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        item {
            Text(text = "📋 CHECKLIST DOKUMEN WAJIB:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }

        items(guide.documentChecklist) { doc ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = if (doc.isRequired) Color(0xFF15803D) else Color.Gray,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = doc.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(text = doc.description, fontSize = 12.sp, color = Color.Gray)
                        Text(text = "Penerbit: ${doc.issuingAuthority}", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = "📌 Saran Logistik & Suhu:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(text = guide.logisticsTips, fontSize = 12.sp, color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = guide.disclaimer, fontSize = 11.sp, color = Color.Gray)
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// SECTION 7: SEKTOR USAHA & MODULER FILTER
// -----------------------------------------------------------------------------
@Composable
private fun SectorFilterSection() {
    val sectors = remember { UniversalSectorEngine.SECTORS }
    var selectedSector by remember { mutableStateOf(sectors.first()) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Store, contentDescription = null, tint = Color(0xFF166534))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Pengatur Sektor Usaha Modular (Req AJ & AK)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF14532D)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Pilih profesi/sektor usaha Anda. SSM akan menyaring tampilan agar aplikasi terasa sangat sederhana dan relevan.",
                        fontSize = 13.sp,
                        color = Color(0xFF166534)
                    )
                }
            }
        }

        item {
            Text(text = "PILIH SEKTOR USAHA ANDA:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }

        items(sectors) { sec ->
            val isSelected = selectedSector.id == sec.id
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) Color(0xFFDCFCE7) else Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) Color(0xFF16A34A) else Color.LightGray
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedSector = sec }
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = sec.icon, fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = sec.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = sec.description, fontSize = 12.sp, color = Color.Gray)
                    }
                    if (isSelected) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF16A34A))
                    }
                }
            }
        }
    }
}
