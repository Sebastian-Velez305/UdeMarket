package com.example.udemarket

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
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
import com.example.udemarket.features.auth.presentation.login.LoginScreen
import com.example.udemarket.features.auth.presentation.login.LoginViewModel
import com.example.udemarket.features.auth.presentation.register.RegisterScreen
import com.example.udemarket.features.auth.presentation.register.RegisterViewModel
import com.example.udemarket.features.food.presentation.FoodScreenContainer
import com.example.udemarket.features.marketplace.presentation.MarketplaceScreenContainer
import com.example.udemarket.features.profile.presentation.ProfileScreen
import com.example.udemarket.features.profile.presentation.ProfileViewModel
import com.example.udemarket.ui.theme.UdeMarketTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        val authRepository = AuthRepositoryImpl(auth, db)

        setContent {
            UdeMarketTheme {
                UdeMarketApp(auth, authRepository)
            }
        }
    }
}

@Composable
fun UdeMarketApp(
    auth: FirebaseAuth,
    authRepository: AuthRepositoryImpl
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val startDestination = remember {
        if (auth.currentUser != null) Screen.FoodList.route else Screen.Login.route
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (currentRoute != Screen.Login.route && currentRoute != Screen.Register.route) {
                NavigationBar(containerColor = Color(0xFF0F001A)) {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Fastfood, null) },
                        label = { Text("Comida") },
                        selected = currentRoute == Screen.FoodList.route,
                        onClick = { 
                            navController.navigate(Screen.FoodList.route) {
                                popUpTo(Screen.FoodList.route) { inclusive = true }
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Storefront, null) },
                        label = { Text("Mercado") },
                        selected = currentRoute == Screen.MarketplaceItems.route,
                        onClick = { navController.navigate(Screen.MarketplaceItems.route) }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.AccountCircle, null) },
                        label = { Text("Perfil") },
                        selected = currentRoute == Screen.Profile.route,
                        onClick = { navController.navigate(Screen.Profile.route) }
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
                FoodScreenContainer(viewModel = viewModel())
            }
            composable(Screen.MarketplaceItems.route) {
                MarketplaceScreenContainer(viewModel = viewModel())
            }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    viewModel = viewModel(factory = createFactory { ProfileViewModel(authRepository) }),
                    onLogoutSuccess = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
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
