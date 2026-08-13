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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AroundMeScreen(
    viewModel: SsmViewModel,
    onBack: () -> Unit
) {
    val partners by viewModel.partners.collectAsStateWithLifecycle()
    var selectedFilter by remember { mutableStateOf("Semua") }

    val filterCategories = listOf("Semua", "Supplier", "Pembeli", "Pasar", "Jasa")

    val filteredPartners = when (selectedFilter) {
        "Supplier" -> partners.filter { it.type.contains("Supplier", ignoreCase = true) }
        "Pembeli" -> partners.filter { it.type.contains("Pembeli", ignoreCase = true) || it.type.contains("Pengepul", ignoreCase = true) }
        "Pasar" -> partners.filter { it.type.contains("Pasar", ignoreCase = true) }
        "Jasa" -> partners.filter { it.type.contains("Jasa", ignoreCase = true) || it.type.contains("Koperasi", ignoreCase = true) }
        else -> partners
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Pasar & Lokasi",
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            SsmBottomNavBar(
                currentRoute = "around_me",
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
            // Filter Chips Row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filterCategories) { cat ->
                    val isSelected = selectedFilter == cat
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) SsmRoyalBlue else Color(0xFFF1F5F9))
                            .clickable { selectedFilter = cat }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = cat,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else Color(0xFF475569)
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Interactive Map Canvas Area
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE2ECE9)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            // Map Roads Canvas Simulation
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceEvenly
                            ) {
                                repeat(5) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(8.dp)
                                            .background(Color.White.copy(alpha = 0.6f))
                                    )
                                }
                            }

                            // Interactive Pins
                            Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                                LocationPinMarker(
                                    title = "Toko Tani",
                                    subtitle = "Supplier 1,2 km",
                                    pinColor = Color(0xFF2E7D32),
                                    modifier = Modifier.align(Alignment.TopStart)
                                )
                                LocationPinMarker(
                                    title = "Pak Joko",
                                    subtitle = "Pembeli 2,4 km",
                                    pinColor = SsmRoyalBlue,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                                LocationPinMarker(
                                    title = "Pasar Induk",
                                    subtitle = "Pasar 3,1 km",
                                    pinColor = Color(0xFFE65100),
                                    modifier = Modifier.align(Alignment.BottomEnd)
                                )
                            }
                        }
                    }
                }

                // Partner Location List Items
                items(filteredPartners) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val iconColor = when {
                                item.type.contains("Supplier", true) -> Color(0xFF2E7D32)
                                item.type.contains("Pembeli", true) || item.type.contains("Pengepul", true) -> SsmRoyalBlue
                                else -> Color(0xFFE65100)
                            }

                            val iconBg = when {
                                item.type.contains("Supplier", true) -> Color(0xFFE8F5E9)
                                item.type.contains("Pembeli", true) || item.type.contains("Pengepul", true) -> Color(0xFFE3F2FD)
                                else -> Color(0xFFFFF3E0)
                            }

                            // Left Icon
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(iconBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (item.type.contains("Supplier", true)) Icons.Default.ShoppingBag else Icons.Default.Storefront,
                                    contentDescription = null,
                                    tint = iconColor,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E293B)
                                )
                                Text(
                                    text = "${item.type} • ${item.commoditiesOrItems}",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    text = "${item.distanceKm} km",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = iconColor
                                )
                            }

                            // Phone Call Action Button
                            IconButton(
                                onClick = { /* Initiate Call */ },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF1F5F9))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = "Hubungi",
                                    tint = SsmRoyalBlue,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                // Lihat Semua di Peta Footer Link Button
                item {
                    Button(
                        onClick = { /* Full map view */ },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Lihat Semua di Peta",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = SsmRoyalBlue
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = SsmRoyalBlue,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LocationPinMarker(
    title: String,
    subtitle: String,
    pinColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = pinColor)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Column {
                Text(text = title, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(text = subtitle, fontSize = 8.sp, color = Color.White.copy(alpha = 0.85f))
            }
        }
    }
}
