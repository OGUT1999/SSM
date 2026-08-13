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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
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
import com.example.data.local.InventoryEntity
import com.example.ui.SsmViewModel
import com.example.ui.components.SsmBottomNavBar
import com.example.ui.theme.SsmRoyalBlue
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProcurementScreen(
    viewModel: SsmViewModel,
    onBack: () -> Unit
) {
    val inventory by viewModel.inventory.collectAsStateWithLifecycle()
    val publicInventory by viewModel.publicInventory.collectAsStateWithLifecycle()
    val partners by viewModel.partners.collectAsStateWithLifecycle()
    val suppliers = partners.filter { it.type.contains("Supplier", true) || it.type == "SUPPLIER" }

    var selectedTab by remember { mutableStateOf(0) } // 0: Kebutuhan Stok, 1: Stok Publik Produsen, 2: Supplier
    var showAddInventoryDialog by remember { mutableStateOf(false) }
    var selectedPublicItemForOffer by remember { mutableStateOf<InventoryEntity?>(null) }

    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
        maximumFractionDigits = 0
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "SAYA BUTUH — Pengadaan & Penawaran",
                        color = SsmRoyalBlue,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 17.sp
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
                    com.example.ui.components.VoiceHelpButton(
                        screenName = "procurement",
                        viewModel = viewModel,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            SsmBottomNavBar(
                currentRoute = "procurement",
                onTabSelected = { route -> onBack() }
            )
        },
        floatingActionButton = {
            if (selectedTab == 0) {
                FloatingActionButton(
                    onClick = { showAddInventoryDialog = true },
                    containerColor = Color(0xFF2E7D32),
                    contentColor = Color.White,
                    modifier = Modifier.testTag("add_inventory_fab")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah Kebutuhan")
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
                    text = { Text("Stok Saya", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Stok Petani/Produsen", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Cari Supplier", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                )
            }

            when (selectedTab) {
                0 -> {
                    // STOK SAYA
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(inventory) { item ->
                            val isLow = item.currentStock <= item.minThreshold

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(44.dp)
                                                .clip(CircleShape)
                                                .background(if (isLow) Color(0xFFFFEBEE) else Color(0xFFE8F5E9)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ShoppingBag,
                                                contentDescription = null,
                                                tint = if (isLow) Color(0xFFD32F2F) else Color(0xFF2E7D32)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = item.itemName,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF1E293B)
                                            )
                                            Text(
                                                text = "Kategori: ${item.category}",
                                                fontSize = 12.sp,
                                                color = Color.Gray
                                            )
                                        }

                                        if (isLow) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(Color(0xFFFFEBEE))
                                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Text(
                                                    text = "Stok Menipis!",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFFD32F2F)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text("Sisa Stok", fontSize = 11.sp, color = Color.Gray)
                                            Text(
                                                "${item.currentStock.toInt()} ${item.unit}",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isLow) Color(0xFFD32F2F) else Color(0xFF1E293B)
                                            )
                                        }
                                        Column {
                                            Text("Estimasi Harga", fontSize = 11.sp, color = Color.Gray)
                                            Text(
                                                "${currencyFormatter.format(item.unitPrice)} / ${item.unit}",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = SsmRoyalBlue
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // STOK PUBLIK PETANI (BUYER OFFERS)
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(publicInventory) { pubItem ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = pubItem.itemName,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF1E293B)
                                            )
                                            Text(
                                                text = "Wilayah: ${pubItem.locationRegion} • Grade: ${pubItem.qualityGrade}",
                                                fontSize = 12.sp,
                                                color = Color.Gray
                                            )
                                        }

                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color(0xFFE8F5E9))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = "🟢 TERSEDIA (${pubItem.currentStock.toInt()} ${pubItem.unit})",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF2E7D32)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text("Harga Penjual:", fontSize = 11.sp, color = Color.Gray)
                                            Text(
                                                currencyFormatter.format(pubItem.unitPrice) + " / ${pubItem.unit}",
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = SsmRoyalBlue
                                            )
                                        }

                                        Button(
                                            onClick = { selectedPublicItemForOffer = pubItem },
                                            shape = RoundedCornerShape(10.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = SsmRoyalBlue)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Gavel,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("Buat Penawaran", fontSize = 12.sp, color = Color.White)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                2 -> {
                    // CARI SUPPLIER TERDEKAT
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(suppliers) { sup ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = sup.name,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF1E293B)
                                            )
                                            Text(
                                                text = sup.commoditiesOrItems,
                                                fontSize = 12.sp,
                                                color = Color.Gray
                                            )
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Star,
                                                contentDescription = null,
                                                tint = Color(0xFFFFB300),
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "${sup.rating}",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${sup.address} (${sup.distanceKm} km)",
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )

                                        Button(
                                            onClick = { /* Contact supplier */ },
                                            shape = RoundedCornerShape(10.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Call,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("Pesan Stok", fontSize = 12.sp, color = Color.White)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Buyer Submit Offer Dialog
    if (selectedPublicItemForOffer != null) {
        val pubItem = selectedPublicItemForOffer!!
        var buyerNameText by remember { mutableStateOf("Pak H. Supri (Tengkulak)") }
        var offerQtyText by remember { mutableStateOf("${(pubItem.currentStock * 0.4).toInt()}") }
        var offerPriceText by remember { mutableStateOf("${(pubItem.unitPrice * 0.9).toInt()}") }
        var shippingText by remember { mutableStateOf("50000") }
        var noteText by remember { mutableStateOf("Saya tawar di harga ini untuk pengambilan besok pagi ya Pak.") }

        AlertDialog(
            onDismissRequest = { selectedPublicItemForOffer = null },
            title = { Text("🤝 Buat Penawaran Harga (${pubItem.itemName})", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = buyerNameText,
                        onValueChange = { buyerNameText = it },
                        label = { Text("Nama Pembeli / Tengkulak") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = offerQtyText,
                        onValueChange = { offerQtyText = it },
                        label = { Text("Jumlah Beli (${pubItem.unit})") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = offerPriceText,
                        onValueChange = { offerPriceText = it },
                        label = { Text("Harga Penawaran Rp/${pubItem.unit}") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = shippingText,
                        onValueChange = { shippingText = it },
                        label = { Text("Estimasi Ongkir / Transport Rp") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = noteText,
                        onValueChange = { noteText = it },
                        label = { Text("Pesan Penawaran") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val qtyVal = offerQtyText.toDoubleOrNull() ?: 100.0
                        val priceVal = offerPriceText.toDoubleOrNull() ?: pubItem.unitPrice
                        val shipVal = shippingText.toDoubleOrNull() ?: 50000.0

                        viewModel.createBuyerOffer(
                            inventoryId = pubItem.id,
                            sellerName = "Petani SSM",
                            buyerName = buyerNameText,
                            buyerDistrict = pubItem.locationRegion,
                            commodityName = pubItem.itemName,
                            quantity = qtyVal,
                            unit = pubItem.unit,
                            offeredPrice = priceVal,
                            targetPrice = pubItem.unitPrice,
                            shippingCost = shipVal,
                            message = noteText
                        )
                        selectedPublicItemForOffer = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SsmRoyalBlue)
                ) {
                    Text("Kirim Penawaran", color = Color.White)
                }
            },
            dismissButton = {
                Button(
                    onClick = { selectedPublicItemForOffer = null },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) {
                    Text("Batal", color = Color.White)
                }
            }
        )
    }

    if (showAddInventoryDialog) {
        var itemName by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("Pupuk & Obat") }
        var qty by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddInventoryDialog = false },
            title = { Text("Tambah Kebutuhan Stok", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = itemName,
                        onValueChange = { itemName = it },
                        label = { Text("Nama Barang (mis: Pupuk NPK 16-16-16)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Kategori (mis: Pupuk / Pakan / Bibit)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = qty,
                        onValueChange = { qty = it },
                        label = { Text("Jumlah Dibutuhkan (mis: 10 Sak)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (itemName.isNotBlank()) {
                            val qtyDouble = qty.toDoubleOrNull() ?: 10.0
                            viewModel.addInventory(
                                businessId = 1L,
                                name = itemName,
                                category = category,
                                stock = qtyDouble,
                                unit = "Sak",
                                minThresh = 2.0,
                                price = 175000.0
                            )
                            showAddInventoryDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Text("Simpan", color = Color.White)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showAddInventoryDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) {
                    Text("Batal", color = Color.White)
                }
            }
        )
    }
}
