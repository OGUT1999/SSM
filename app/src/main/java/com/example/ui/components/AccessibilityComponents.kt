package com.example.ui.components

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.SsmViewModel
import java.text.NumberFormat
import java.util.Locale

/**
 * Prominent Voice Help Button ("🎤 Bantu Saya") for screen guidance.
 */
@Composable
fun VoiceHelpButton(
    screenName: String,
    viewModel: SsmViewModel,
    modifier: Modifier = Modifier
) {
    var showHelpDialog by remember { mutableStateOf(false) }

    Surface(
        onClick = {
            showHelpDialog = true
            viewModel.speakPageHelp(screenName)
        },
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFFFFF3E0),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFB8C00)),
        modifier = modifier
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
            .semantics { contentDescription = "Tombol Bantuan Suara. Bicara dan dengarkan panduan halaman $screenName" }
            .testTag("voice_help_button_$screenName")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = null,
                tint = Color(0xFFE65100),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Bantu Saya",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color(0xFFE65100)
            )
        }
    }

    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.HelpOutline,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Panduan Halaman ${screenName.uppercase()}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = when (screenName.lowercase()) {
                            "home" -> "Di halaman Beranda, Anda dapat melihat ringkasan keuangan, tombol cepat Saya Punya dan Saya Butuh, tawaran pembeli masuk, serta asisten suara."
                            "procurement" -> "Halaman Saya Butuh & Pengadaan. Di sini Anda bisa meninjau bahan produksi serta mengirim penawaran ke pemasok/pengepul."
                            "sales" -> "Halaman Saya Punya & Stok Panen. Di sini Anda bisa mempublikasikan stok panen Anda dan menerima tawaran dari pembeli."
                            "prices" -> "Halaman Harga Pasar. Di sini Anda melihat daftar harga komoditas nasional dan regional beserta rekomendasi Fair Price SSM."
                            else -> "Di halaman ini Anda dapat mengelola transaksi usaha Anda secara aman dan transparan."
                        },
                        fontSize = 15.sp,
                        lineHeight = 22.sp
                    )

                    Button(
                        onClick = { viewModel.speakPageHelp(screenName) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Putar Suara Panduan",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "🔊 Putar Ulang Suara Panduan",
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showHelpDialog = false },
                    modifier = Modifier.defaultMinSize(minHeight = 48.dp)
                ) {
                    Text("Saya Mengerti")
                }
            }
        )
    }
}

/**
 * Universal Confirmation Dialog for financial & inventory actions (Req 54 & 55).
 * Clear, simple language without technical legal jargon.
 */
@Composable
fun UniversalConfirmationDialog(
    titleText: String,
    actionDescriptionText: String,
    itemDetailText: String,
    totalAmountText: String? = null,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    viewModel: SsmViewModel? = null
) {
    val fullSpeech = "$titleText. $actionDescriptionText. $itemDetailText. ${totalAmountText ?: ""}. Apakah Anda yakin?"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF2E7D32)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = titleText,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = actionDescriptionText,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = itemDetailText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF1B5E20)
                        )
                        if (!totalAmountText.isNull_or_blank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Total: $totalAmountText",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = Color(0xFF2E7D32)
                            )
                        }
                    }
                }

                if (viewModel != null) {
                    OutlinedButton(
                        onClick = { viewModel.speakText(fullSpeech) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 48.dp)
                    ) {
                        Icon(imageVector = Icons.Default.VolumeUp, contentDescription = "Dengarkan Konfirmasi")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("🔊 Dengarkan Konfirmasi")
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                modifier = Modifier.defaultMinSize(minHeight = 48.dp)
            ) {
                Text("YA, SAYA YAKIN", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.defaultMinSize(minHeight = 48.dp)
            ) {
                Text("BATALKAN", color = Color.Red, fontWeight = FontWeight.Bold)
            }
        }
    )
}

private fun String?.isNull_or_blank(): Boolean = this == null || this.isBlank()

/**
 * Preset Chips for Price, Quantity, or Messages (Req 43 & 51).
 * Allows full action without typing or speaking.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuickSelectPriceChips(
    prices: List<Double>,
    selectedPrice: Double,
    onSelectPrice: (Double) -> Unit,
    modifier: Modifier = Modifier
) {
    val format = remember { NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply { maximumFractionDigits = 0 } }

    Column(modifier = modifier) {
        Text(
            text = "⚡ Pilihan Harga Cepat (Tanpa Mengetik):",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(6.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            prices.forEach { price ->
                val isSelected = selectedPrice == price
                Surface(
                    onClick = { onSelectPrice(price) },
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFFF1F5F9),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray
                    ),
                    modifier = Modifier.defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = format.format(price),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = if (isSelected) Color.White else Color.DarkGray
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuickSelectQuantityChips(
    quantities: List<Double>,
    unit: String,
    selectedQuantity: Double,
    onSelectQuantity: (Double) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "⚡ Pilihan Jumlah Cepat:",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(6.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            quantities.forEach { qty ->
                val isSelected = selectedQuantity == qty
                Surface(
                    onClick = { onSelectQuantity(qty) },
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.secondary else Color(0xFFF1F5F9),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = if (isSelected) MaterialTheme.colorScheme.secondary else Color.LightGray
                    ),
                    modifier = Modifier.defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "${qty.toInt()} $unit",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = if (isSelected) Color.White else Color.DarkGray
                        )
                    }
                }
            }
        }
    }
}

/**
 * Accessible Big Button with visual icon, title, subtitle, andTalkBack accessibility label (Req 42, 44, 47, 48).
 */
@Composable
fun AccessibleBigButton(
    title: String,
    subtitle: String? = null,
    icon: ImageVector,
    iconTint: Color,
    backgroundColor: Color,
    onClick: () -> Unit,
    accessibilityLabel: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 56.dp)
            .clickable { onClick() }
            .semantics { contentDescription = accessibilityLabel },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.White, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 17.sp,
                    color = Color(0xFF1E293B)
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        fontSize = 13.sp,
                        color = Color(0xFF475569)
                    )
                }
            }
        }
    }
}
