package com.example.udemarket

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.udemarket.core.navigation.Screen
import com.example.udemarket.data.repository.AuthRepositoryImpl
import com.example.udemarket.data.repository.FoodRepositoryImpl
import com.example.udemarket.data.repository.MarketplaceRepositoryImpl
import com.example.udemarket.features.auth.presentation.login.LoginScreen
import com.example.udemarket.features.auth.presentation.login.LoginViewModel
import com.example.udemarket.features.auth.presentation.register.RegisterScreen
import com.example.udemarket.features.auth.presentation.register.RegisterViewModel
import com.example.udemarket.features.food.presentation.FoodScreenContainer
import com.example.udemarket.features.food.presentation.FoodViewModel
import com.example.udemarket.features.marketplace.presentation.MarketplaceScreenContainer
import com.example.udemarket.features.marketplace.presentation.MarketplaceViewModel
import com.example.udemarket.ui.theme.UdeMarketTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        val storage = FirebaseStorage.getInstance()

        val authRepository = AuthRepositoryImpl(auth, db)
        val foodRepository = FoodRepositoryImpl(db)
        val marketplaceRepository = MarketplaceRepositoryImpl(db, storage)

        setContent {
            UdeMarketTheme {
                UdeMarketApp(auth, authRepository, foodRepository, marketplaceRepository)
            }
        }
    }
}

@Composable
fun UdeMarketApp(
    auth: FirebaseAuth,
    authRepository: AuthRepositoryImpl,
    foodRepository: FoodRepositoryImpl,
    marketplaceRepository: MarketplaceRepositoryImpl
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // PERSISTENCIA: Si ya hay sesión, inicia en FoodList, si no en Login
    val startDestination = remember {
        if (auth.currentUser != null) Screen.FoodList.route else Screen.Login.route
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            // Solo mostramos la barra si no estamos en Login o Registro
            if (currentRoute != Screen.Login.route && currentRoute != Screen.Register.route) {
                NavigationBar(containerColor = Color(0xFF0F001A)) {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Fastfood, null) },
                        label = { Text("Comida") },
                        selected = currentRoute == Screen.FoodList.route,
                        onClick = { navController.navigate(Screen.FoodList.route) }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Storefront, null) },
                        label = { Text("Mercado") },
                        selected = currentRoute == Screen.MarketplaceItems.route,
                        onClick = { navController.navigate(Screen.MarketplaceItems.route) }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.ExitToApp, null, tint = Color.Red) },
                        label = { Text("Salir", color = Color.Red) },
                        selected = false,
                        onClick = {
                            auth.signOut()
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    viewModel = viewModel(factory = createFactory { LoginViewModel(authRepository) }),
                    onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                    onLoginSuccess = {
                        navController.navigate(Screen.FoodList.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Register.route) {
                RegisterScreen(
                    viewModel = viewModel(factory = createFactory { RegisterViewModel(authRepository) }),
                    onNavigateBack = { navController.popBackStack() },
                    onRegisterSuccess = {
                        navController.navigate(Screen.FoodList.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.FoodList.route) {
                FoodScreenContainer(viewModel = viewModel(factory = createFactory { FoodViewModel(foodRepository) }))
            }
            composable(Screen.MarketplaceItems.route) {
                MarketplaceScreenContainer(viewModel = viewModel(factory = createFactory { MarketplaceViewModel(marketplaceRepository) }))
            }
        }
    }
}

inline fun <reified T : ViewModel> createFactory(crossinline creator: () -> T): ViewModelProvider.Factory {
    return object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return creator() as T
        }
    }
}