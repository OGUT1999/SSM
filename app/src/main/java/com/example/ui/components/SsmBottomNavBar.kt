package com.example.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SsmRoyalBlue

enum class SsmTab(val route: String, val label: String) {
    HOME("home", "Beranda"),
    USAHA("businesses", "Usaha"),
    TRANSAKSI("sales", "Transaksi"),
    PESAN("voice_assistant", "Pesan"),
    AKUN("settings", "Akun")
}

@Composable
fun SsmBottomNavBar(
    currentRoute: String,
    onTabSelected: (String) -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        contentColor = SsmRoyalBlue
    ) {
        SsmTab.entries.forEach { tab ->
            val isSelected = currentRoute == tab.route ||
                    (tab == SsmTab.HOME && currentRoute == "home") ||
                    (tab == SsmTab.USAHA && (currentRoute == "businesses")) ||
                    (tab == SsmTab.TRANSAKSI && (currentRoute == "sales" || currentRoute == "procurement" || currentRoute == "prices")) ||
                    (tab == SsmTab.PESAN && currentRoute == "voice_assistant") ||
                    (tab == SsmTab.AKUN && currentRoute == "settings")

            val icon = when (tab) {
                SsmTab.HOME -> Icons.Default.Home
                SsmTab.USAHA -> Icons.Default.Storefront
                SsmTab.TRANSAKSI -> Icons.Default.Assignment
                SsmTab.PESAN -> Icons.Default.ChatBubble
                SsmTab.AKUN -> Icons.Default.Person
            }

            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab.route) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = tab.label
                    )
                },
                label = {
                    Text(
                        text = tab.label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = SsmRoyalBlue,
                    selectedTextColor = SsmRoyalBlue,
                    indicatorColor = SsmRoyalBlue.copy(alpha = 0.12f),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                ),
                modifier = Modifier.testTag("nav_tab_${tab.route}")
            )
        }
    }
}
