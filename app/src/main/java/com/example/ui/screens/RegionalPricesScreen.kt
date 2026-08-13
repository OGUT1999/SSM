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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.PriceReferenceEntity
import com.example.engine.FairPriceRecommendation
import com.example.ui.SsmViewModel
import com.example.ui.components.SsmBottomNavBar
import com.example.ui.theme.SsmRoyalBlue
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegionalPricesScreen(
    viewModel: SsmViewModel,
    onBack: () -> Unit
) {
    val prices by viewModel.prices.collectAsStateWithLifecycle()
    val priceReferences by viewModel.priceReferences.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var selectedExplanation by remember { mutableStateOf<FairPriceRecommendation?>(null) }

    val filteredPrices = prices.filter {
        it.commodityName.contains(searchQuery, ignoreCase = true) ||
                it.category.contains(searchQuery, ignoreCase = true)
    }

    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
        maximumFractionDigits = 0
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "HARGA PASAR — Acuan & Harga Wajar",
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
                        screenName = "prices",
                        viewModel = viewModel,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            SsmBottomNavBar(
                currentRoute = "prices",
                onTabSelected = { route -> onBack() }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF4F6FB))
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Cari komoditas (misal: Cabai, Sapi, Lele)...", fontSize = 13.sp) },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = SsmRoyalBlue) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Section 1: Acuan Resmi
                item {
                    Text(
                        text = "🏛️ Harga Acuan Resmi Pemerintah & Pasar",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                }

                items(priceReferences) { ref ->
                    val fairRec = viewModel.getFairPriceRecommendation(
                        commodity = ref.commodityName,
                        grade = ref.qualityGrade,
                        marketPrice = ref.wholesaleReferencePrice
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEBF3FF)),
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
                                        text = ref.commodityName,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SsmRoyalBlue
                                    )
                                    Text(
                                        text = "${ref.sourceTypeLabel} (${ref.officialReferenceType})",
                                        fontSize = 11.sp,
                                        color = Color(0xFF334155)
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFE8F5E9))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "🟢 Kepastian: ${ref.confidenceScore}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2E7D32)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Acuan Tingkat Produsen", fontSize = 11.sp, color = Color.Gray)
                                    Text(
                                        "${currencyFormatter.format(ref.producerReferencePrice)} / ${ref.unit}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF2E7D32)
                                    )
                                }

                                Column {
                                    Text("Rekomendasi Wajar Normal", fontSize = 11.sp, color = Color.Gray)
                                    Text(
                                        "${currencyFormatter.format(fairRec.normalProfitPrice)} / ${ref.unit}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = SsmRoyalBlue
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier
                                    .clickable { selectedExplanation = fairRec }
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.HelpOutline,
                                    contentDescription = null,
                                    tint = SsmRoyalBlue,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Kenapa harga ini? (Lihat sumber data & rincian)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SsmRoyalBlue
                                )
                            }
                        }
                    }
                }

                // Section 2: Regional Market Prices
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "📊 Monitoring Harga Pasar Lokal Terbaru",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                }

                items(filteredPrices) { item ->
                    val isUp = item.status.contains("NAIK", ignoreCase = true)

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
                                Icon(
                                    imageVector = Icons.Default.Tag,
                                    contentDescription = null,
                                    tint = SsmRoyalBlue,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.commodityName,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E293B)
                                    )
                                    Text(
                                        text = "${item.district} • Grade: ${item.qualityGrade}",
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (isUp) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                                        contentDescription = null,
                                        tint = if (isUp) Color(0xFF2E7D32) else Color(0xFFD32F2F),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = item.status,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isUp) Color(0xFF2E7D32) else Color(0xFFD32F2F)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Pengepul", fontSize = 11.sp, color = Color.Gray)
                                    Text(
                                        "${currencyFormatter.format(item.collectorPrice)} / kg",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SsmRoyalBlue
                                    )
                                }
                                Column {
                                    Text("Grosir", fontSize = 11.sp, color = Color.Gray)
                                    Text(
                                        "${currencyFormatter.format(item.wholesalePrice)} / kg",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Column {
                                    Text("Eceran / Penawaran Tertinggi Saat Ini", fontSize = 11.sp, color = Color.Gray)
                                    Text(
                                        "${currencyFormatter.format(item.consumerPrice)} / kg",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFFC2410C)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "🟢 Status: Data Terbaru (${item.dateText})",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E7D32)
                                )
                                Text(
                                    text = "Sumber: ${item.source}",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (selectedExplanation != null) {
        val rec = selectedExplanation!!
        AlertDialog(
            onDismissRequest = { selectedExplanation = null },
            title = { Text("Kenapa Harga Ini? (${rec.commodity})", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("• Sumber Utama: ${rec.primarySourceType.label}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("• Acuan Resmi: ${rec.officialReferenceText}", fontSize = 12.sp, color = Color(0xFF1565C0))
                    Text("• ${rec.explanation}", fontSize = 12.sp, color = Color(0xFF334155))
                    Text("• Tingkat Kepastian: ${rec.confidence.icon} ${rec.confidence.label}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
}
