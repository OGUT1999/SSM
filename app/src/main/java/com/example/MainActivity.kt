package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.SsmViewModel
import com.example.ui.screens.AccountSecurityScreen
import com.example.ui.screens.AroundMeScreen
import com.example.ui.screens.AuthOnboardingScreen
import com.example.ui.screens.BusinessesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OtomatisScreen
import com.example.ui.screens.ProcurementScreen
import com.example.ui.screens.RegionalPricesScreen
import com.example.ui.screens.SalesScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.UniversalToolsScreen
import com.example.ui.screens.VoiceAssistantScreen
import com.example.ui.screens.WeatherRiskScreen
import com.example.ui.theme.SsmTheme

class MainActivity : ComponentActivity() {

    private val viewModel: SsmViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SsmTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SsmAppNavigation(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun SsmAppNavigation(viewModel: SsmViewModel) {
    val navController = rememberNavController()

    fun navigateTo(route: String) {
        if (route == "home") {
            navController.popBackStack("home", inclusive = false)
        } else {
            navController.navigate(route) {
                popUpTo("home") { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                viewModel = viewModel,
                onNavigateToRoute = { route -> navigateTo(route) }
            )
        }
        composable("voice_assistant") {
            VoiceAssistantScreen(
                viewModel = viewModel,
                onBack = { navigateTo("home") }
            )
        }
        composable("businesses") {
            BusinessesScreen(
                viewModel = viewModel,
                onBack = { navigateTo("home") }
            )
        }
        composable("prices") {
            RegionalPricesScreen(
                viewModel = viewModel,
                onBack = { navigateTo("home") }
            )
        }
        composable("procurement") {
            ProcurementScreen(
                viewModel = viewModel,
                onBack = { navigateTo("home") }
            )
        }
        composable("sales") {
            SalesScreen(
                viewModel = viewModel,
                onBack = { navigateTo("home") }
            )
        }
        composable("around_me") {
            AroundMeScreen(
                viewModel = viewModel,
                onBack = { navigateTo("home") }
            )
        }
        composable("weather") {
            WeatherRiskScreen(
                viewModel = viewModel,
                onBack = { navigateTo("home") }
            )
        }
        composable("otomatis") {
            OtomatisScreen(
                viewModel = viewModel,
                onBack = { navigateTo("home") }
            )
        }
        composable("settings") {
            SettingsScreen(
                viewModel = viewModel,
                onBack = { navigateTo("home") }
            )
        }
        composable("tools") {
            UniversalToolsScreen(
                viewModel = viewModel,
                onBack = { navigateTo("home") }
            )
        }
        composable("account_security") {
            AccountSecurityScreen(
                viewModel = viewModel,
                onBack = { navigateTo("home") }
            )
        }
        composable("auth_onboarding") {
            AuthOnboardingScreen(
                viewModel = viewModel,
                onBack = { navigateTo("home") },
                onFinishOnboarding = { navigateTo("home") }
            )
        }
    }
}
