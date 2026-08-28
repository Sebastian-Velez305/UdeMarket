package com.example.udemarket

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.udemarket.core.navigation.Screen
import com.example.udemarket.features.auth.presentation.login.LoginScreen
import com.example.udemarket.features.auth.presentation.register.RegisterScreen
import com.example.udemarket.features.marketplace.presentation.ItemListScreen
import com.example.udemarket.features.marketplace.presentation.ItemUpsertScreen
import com.example.udemarket.features.marketplace.presentation.ItemViewModel
import com.example.udemarket.ui.theme.UdeMarketTheme

class MainActivity : ComponentActivity() {
    private val itemViewModel: ItemViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UdeMarketTheme {
                UdeMarketApp(itemViewModel = itemViewModel)
            }
        }
    }
}

@Composable
fun UdeMarketApp(itemViewModel: ItemViewModel) {
    val navController = rememberNavController()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Login.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    onNavigateToRegister = {
                        navController.navigate(Screen.Register.route)
                    }
                )
            }
            composable(Screen.Register.route) {
                RegisterScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
            // Vista principal de productos (Marketplace)
            composable(Screen.MarketplaceItems.route) {
                ItemListScreen(
                    viewModel = itemViewModel,
                    onAddItem = {
                        navController.navigate(Screen.ItemUpsert.createRoute(null))
                    },
                    onEditItem = { itemId ->
                        navController.navigate(Screen.ItemUpsert.createRoute(itemId))
                    }
                )
            }
            // Pantalla para Crear / Editar productos
            composable(
                route = Screen.ItemUpsert.route,
                arguments = listOf(
                    navArgument("itemId") {
                        type = NavType.StringType
                        defaultValue = "new"
                    }
                )
            ) { backStackEntry ->
                val itemIdArg = backStackEntry.arguments?.getString("itemId")
                val itemId = if (itemIdArg == "new") null else itemIdArg

                ItemUpsertScreen(
                    viewModel = itemViewModel,
                    itemId = itemId,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
