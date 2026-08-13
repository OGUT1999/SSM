package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.data.local.OfferEntity
import com.example.engine.CostBreakdown
import com.example.engine.FairPriceRecommendation
import com.example.engine.NegotiationAssistant
import com.example.ui.SsmViewModel
import com.example.ui.components.SsmBottomNavBar
import com.example.ui.theme.SsmRoyalBlue
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesScreen(
    viewModel: SsmViewModel,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Stok & Fair Price, 1: Tawaran Masuk & Nego

    val inventoryList by viewModel.inventory.collectAsStateWithLifecycle()
    val offersList by viewModel.offers.collectAsStateWithLifecycle()
    val partnersList by viewModel.partners.collectAsStateWithLifecycle()

    val producerStocks = inventoryList.filter { it.category == "HASIL_PANEN" || it.currentStock > 20 }
    val pendingOffers = offersList.filter { it.status != "REJECTED" }

    var selectedExplanation by remember { mutableStateOf<FairPriceRecommendation?>(null) }
    var selectedOfferForNego by remember { mutableStateOf<OfferEntity?>(null) }
    var selectedOfferForBuyers by remember { mutableStateOf<OfferEntity?>(null) }
    var showEditStockDialog by remember { mutableStateOf<InventoryEntity?>(null) }

    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
        maximumFractionDigits = 0
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "SAYA PUNYA — Stok & Penjualan",
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
                        screenName = "sales",
                        viewModel = viewModel,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            SsmBottomNavBar(
                currentRoute = "sales",
                onTabSelected = { route -> onBack() }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF4F6FB))
        ) {
            // Sub-Tab Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { selectedTab = 0 },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedTab == 0) SsmRoyalBlue else Color(0xFFE2E8F0),
                        contentColor = if (selectedTab == 0) Color.White else Color(0xFF475569)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("📦 Stok & Fair Price", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { selectedTab = 1 },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedTab == 1) SsmRoyalBlue else Color(0xFFE2E8F0),
                        contentColor = if (selectedTab == 1) Color.White else Color(0xFF475569)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("📩 Tawaran (${pendingOffers.size})", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (selectedTab == 0) {
                    // STOK & FAIR PRICE TAB
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFEBF3FF))
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(SsmRoyalBlue),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Inventory2,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "SSM Fair Price & Auto Inventory",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = SsmRoyalBlue
                                    )
                                    Text(
                                        text = "Harga dihitung dari biaya produksi + acuan pasar. Bebas tentukan atau ubah harga kapan saja.",
                                        fontSize = 12.sp,
                                        color = Color(0xFF334155)
                                    )
                                }
                            }
                        }
                    }

                    items(producerStocks) { stockItem ->
                        val fairRec = viewModel.getFairPriceRecommendation(
                            commodity = stockItem.itemName,
                            grade = stockItem.qualityGrade,
                            marketPrice = stockItem.unitPrice
                        )

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
                                            text = stockItem.itemName,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color(0xFF1E293B)
                                        )
                                        Text(
                                            text = "Stok: ${stockItem.currentStock.toInt()} ${stockItem.unit} • ${stockItem.qualityGrade}",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SsmRoyalBlue
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                when (stockItem.statusText) {
                                                    "HABIS" -> Color(0xFFFFEBEE)
                                                    "TERBATAS" -> Color(0xFFFFF8E1)
                                                    else -> Color(0xFFE8F5E9)
                                                }
                                            )
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = stockItem.statusText,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = when (stockItem.statusText) {
                                                "HABIS" -> Color(0xFFC62828)
                                                "TERBATAS" -> Color(0xFFF57F17)
                                                else -> Color(0xFF2E7D32)
                                            }
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = "💡 Rekomendasi Harga Wajar SSM:",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF475569)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // 3 Scenario Price Cards
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Safe
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color(0xFFE8F5E9))
                                            .border(1.dp, Color(0xFFA5D6A7), RoundedCornerShape(10.dp))
                                            .padding(8.dp)
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("🟢 UNTUNG AMAN", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                                            Text(currencyFormatter.format(fairRec.safeProfitPrice), fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                                        }
                                    }

                                    // Normal
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color(0xFFE3F2FD))
                                            .border(1.dp, Color(0xFF90CAF9), RoundedCornerShape(10.dp))
                                            .padding(8.dp)
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("🔵 UNTUNG NORMAL", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1565C0))
                                            Text(currencyFormatter.format(fairRec.normalProfitPrice), fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                                        }
                                    }

                                    // High
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color(0xFFF3E5F5))
                                            .border(1.dp, Color(0xFFCE93D8), RoundedCornerShape(10.dp))
                                            .padding(8.dp)
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("🟣 UNTUNG TINGGI", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6A1B9A))
                                            Text(currencyFormatter.format(fairRec.highProfitPrice), fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        modifier = Modifier.clickable { selectedExplanation = fairRec },
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.HelpOutline,
                                            contentDescription = null,
                                            tint = SsmRoyalBlue,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Kenapa harga ini?",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SsmRoyalBlue
                                        )
                                    }

                                    Button(
                                        onClick = { showEditStockDialog = stockItem },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = SsmRoyalBlue)
                                    ) {
                                        Text("Edit Stok / Harga", fontSize = 12.sp, color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // TAWARAN MASUK & NEGO TAB
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1))
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Sms,
                                    contentDescription = null,
                                    tint = Color(0xFFF57F17)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Asisten Negosiasi SSM",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = Color(0xFFB78103)
                                    )
                                    Text(
                                        text = "AI membantu mengevaluasi tawaran pembeli & menyusun draft pesan balasan balasan secara adil.",
                                        fontSize = 12.sp,
                                        color = Color(0xFF424242)
                                    )
                                }
                            }
                        }
                    }

                    if (pendingOffers.isEmpty()) {
                        item {
                            Text(
                                text = "Belum ada penawaran masuk dari pembeli.",
                                modifier = Modifier.padding(20.dp),
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                    }

                    items(pendingOffers) { offer ->
                        val eval = NegotiationAssistant.evaluateBuyerOffer(
                            offeredPrice = offer.buyerOfferedPricePerUnit,
                            sellerTargetPrice = offer.sellerTargetPricePerUnit,
                            costPerUnit = 18000.0,
                            normalFairPrice = 24000.0
                        )

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (offer.isWarningLowOffer) Color(0xFFFFF5F5) else Color.White
                            ),
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
                                            text = offer.buyerName,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1E293B)
                                        )
                                        Text(
                                            text = "Komoditas: ${offer.commodityName} (${offer.quantity.toInt()} ${offer.unit})",
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (offer.status == "COMPLETED") Color(0xFFE8F5E9) else Color(0xFFE3F2FD))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = offer.status,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (offer.status == "COMPLETED") Color(0xFF2E7D32) else Color(0xFF1565C0)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("Tawaran Pembeli:", fontSize = 11.sp, color = Color.Gray)
                                        Text(
                                            currencyFormatter.format(offer.buyerOfferedPricePerUnit) + "/${offer.unit}",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (offer.isWarningLowOffer) Color(0xFFC62828) else Color(0xFF2E7D32)
                                        )
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("Target Penjual:", fontSize = 11.sp, color = Color.Gray)
                                        Text(
                                            currencyFormatter.format(offer.sellerTargetPricePerUnit) + "/${offer.unit}",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF334155)
                                        )
                                    }
                                }

                                // Warning if low offer
                                if (offer.isWarningLowOffer) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color(0xFFFFEBEE))
                                            .padding(10.dp)
                                    ) {
                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Warning,
                                                    contentDescription = null,
                                                    tint = Color(0xFFC62828),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = eval.warningTitle,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFFC62828)
                                                )
                                            }
                                            Text(
                                                text = eval.warningMessage,
                                                fontSize = 11.sp,
                                                color = Color(0xFF424242)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                if (offer.status != "COMPLETED") {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = { viewModel.acceptOffer(offer.id) },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(10.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                                        ) {
                                            Text("Terima & Jual", fontSize = 11.sp, color = Color.White)
                                        }

                                        OutlinedButton(
                                            onClick = { selectedOfferForNego = offer },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Mic,
                                                    contentDescription = null,
                                                    tint = SsmRoyalBlue,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Bantu Nego", fontSize = 11.sp, color = SsmRoyalBlue)
                                            }
                                        }

                                        OutlinedButton(
                                            onClick = { selectedOfferForBuyers = offer },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Text("Cari Lain", fontSize = 11.sp, color = Color(0xFF475569))
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

    // Explanation Dialog
    if (selectedExplanation != null) {
        val rec = selectedExplanation!!
        AlertDialog(
            onDismissRequest = { selectedExplanation = null },
            title = { Text("Kenapa Harga Ini? (${rec.commodity})", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("• Rincian Sumber: ${rec.primarySourceType.label}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("• ${rec.officialReferenceText}", fontSize = 12.sp, color = Color(0xFF1565C0))
                    Text("• ${rec.explanation}", fontSize = 12.sp, color = Color(0xFF334155))
                    Text("• Kepastian Data: ${rec.confidence.icon} ${rec.confidence.label}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            },
            confirmButton = {
                Button(
                    onClick = { selectedExplanation = null },
                    colors = ButtonDefaults.buttonColors(containerColor = SsmRoyalBlue)
                ) {
                    Text("Tutup", color = Color.White)
                }
            }
        )
    }

    // AI Nego Draft Dialog
    if (selectedOfferForNego != null) {
        val offer = selectedOfferForNego!!
        var counterPriceText by remember { mutableStateOf("${(offer.sellerTargetPricePerUnit * 0.95).toInt()}") }
        var draftMessageText by remember {
            mutableStateOf(
                NegotiationAssistant.generateNegoDraft(
                    sellerName = offer.sellerName,
                    buyerName = offer.buyerName,
                    commodity = offer.commodityName,
                    quantity = offer.quantity,
                    unit = offer.unit,
                    offeredPrice = offer.buyerOfferedPricePerUnit,
                    targetPrice = offer.sellerTargetPricePerUnit,
                    counterPrice = (offer.sellerTargetPricePerUnit * 0.95)
                )
            )
        }

        AlertDialog(
            onDismissRequest = { selectedOfferForNego = null },
            title = { Text("🎤 Draft Pesan Negosiasi AI", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Anda dapat meninjau dan mengubah pesan ini sebelum dikirim ke pembeli:", fontSize = 12.sp, color = Color.Gray)

                    OutlinedTextField(
                        value = counterPriceText,
                        onValueChange = {
                            counterPriceText = it
                            val counterVal = it.toDoubleOrNull() ?: offer.sellerTargetPricePerUnit
                            draftMessageText = NegotiationAssistant.generateNegoDraft(
                                sellerName = offer.sellerName,
                                buyerName = offer.buyerName,
                                commodity = offer.commodityName,
                                quantity = offer.quantity,
                                unit = offer.unit,
                                offeredPrice = offer.buyerOfferedPricePerUnit,
                                targetPrice = offer.sellerTargetPricePerUnit,
                                counterPrice = counterVal
                            )
                        },
                        label = { Text("Harga Penawaran Balik (Rp/${offer.unit})") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = draftMessageText,
                        onValueChange = { draftMessageText = it },
                        label = { Text("Pesan Negosiasi") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 4
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val cPrice = counterPriceText.toDoubleOrNull() ?: offer.sellerTargetPricePerUnit
                        viewModel.submitCounterOffer(offer.id, cPrice, draftMessageText)
                        selectedOfferForNego = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SsmRoyalBlue)
                ) {
                    Text("Kirim Tawaran Balik", color = Color.White)
                }
            },
            dismissButton = {
                Button(
                    onClick = { selectedOfferForNego = null },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) {
                    Text("Batal", color = Color.White)
                }
            }
        )
    }

    // Alternative Buyers Ranking Dialog
    if (selectedOfferForBuyers != null) {
        val offer = selectedOfferForBuyers!!
        val rankedBuyers = NegotiationAssistant.calculateNetValueBuyers(partnersList, offer.sellerTargetPricePerUnit, offer.quantity)

        AlertDialog(
            onDismissRequest = { selectedOfferForBuyers = null },
            title = { Text("Alternatif Pembeli Terdekat (Net Value)", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Diurutkan dari Nilai Bersih (Net Value) tertinggi:", fontSize = 12.sp, color = Color.Gray)

                    rankedBuyers.take(3).forEach { buyer ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC))
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(buyer.buyerName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Tawaran: ${currencyFormatter.format(buyer.offeredPricePerUnit)} • Jarak: ${buyer.distanceKm} km", fontSize = 11.sp)
                                Text("Net Bersih: ${currencyFormatter.format(buyer.netValuePerUnit)}/${offer.unit}", fontWeight = FontWeight.ExtraBold, color = SsmRoyalBlue, fontSize = 12.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { selectedOfferForBuyers = null },
                    colors = ButtonDefaults.buttonColors(containerColor = SsmRoyalBlue)
                ) {
                    Text("Tutup", color = Color.White)
                }
            }
        )
    }

    // Edit Stock Dialog
    if (showEditStockDialog != null) {
        val item = showEditStockDialog!!
        var newStockText by remember { mutableStateOf("${item.currentStock.toInt()}") }

        AlertDialog(
            onDismissRequest = { showEditStockDialog = null },
            title = { Text("Atur Jumlah Stok (${item.itemName})", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newStockText,
                        onValueChange = { newStockText = it },
                        label = { Text("Jumlah Stok (${item.unit})") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val valStock = newStockText.toDoubleOrNull() ?: item.currentStock
                        viewModel.updateStockQuantity(item.id, valStock, "Pembaruan Manual oleh Petani")
                        showEditStockDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SsmRoyalBlue)
                ) {
                    Text("Simpan Stok", color = Color.White)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showEditStockDialog = null },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) {
                    Text("Batal", color = Color.White)
                }
            }
        )
    }
}
