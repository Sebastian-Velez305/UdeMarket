package com.example.udemarket

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.udemarket.core.navigation.Screen
import com.example.udemarket.data.repository.AuthRepositoryImpl
import com.example.udemarket.data.repository.MarketplaceRepository
import com.example.udemarket.features.auth.presentation.login.LoginScreen
import com.example.udemarket.features.auth.presentation.login.LoginViewModel
import com.example.udemarket.features.auth.presentation.register.RegisterScreen
import com.example.udemarket.features.auth.presentation.register.RegisterViewModel
import com.example.udemarket.features.food.presentation.FoodScreenContainer
import com.example.udemarket.features.marketplace.presentation.ItemUpsertScreen
import com.example.udemarket.features.marketplace.presentation.ItemViewModel
import com.example.udemarket.features.marketplace.presentation.MarketplaceScreenContainer
import com.example.udemarket.features.marketplace.presentation.MarketplaceViewModel
import com.example.udemarket.features.profile.presentation.ProfileScreen
import com.example.udemarket.features.profile.presentation.ProfileViewModel
import com.example.udemarket.ui.features.gastos.GastosScreen
import com.example.udemarket.ui.theme.UdeMarketTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Payments

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inicialización de servicios de Firebase
        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        
        // Inicialización de Repositorios (Capa de Datos)
        val authRepository = AuthRepositoryImpl(auth, db)
        val marketplaceRepository = MarketplaceRepository(db)

        setContent {
            UdeMarketTheme {
                UdeMarketApp(auth, authRepository, marketplaceRepository)
            }
        }
    }
}

@Composable
fun UdeMarketApp(
    auth: FirebaseAuth,
    authRepository: AuthRepositoryImpl,
    marketplaceRepository: MarketplaceRepository
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // PERSISTENCIA DE SESIÓN: Decisión de arranque
    val startDestination = remember {
        if (auth.currentUser != null) Screen.FoodList.route else Screen.Login.route
    }

    Scaffold(
        containerColor = Color.Black, // Fondo base oscuro para el tema Neon
        bottomBar = {
            // LISTA DE RUTAS QUE MUESTRAN LA BARRA INFERIOR
            val mainScreens = listOf(
                Screen.FoodList.route, 
                Screen.MarketplaceItems.route, 
                Screen.Gastos.route, 
                Screen.Profile.route
            )
            
            if (currentRoute in mainScreens) {
                NavigationBar(
                    containerColor = Color(0xFF0F001A),
                    tonalElevation = 8.dp
                ) {
                    val items = listOf(
                        Triple(Screen.FoodList, "Comida", Icons.Default.Fastfood),
                        Triple(Screen.MarketplaceItems, "Mercado", Icons.Default.Storefront),
                        Triple(Screen.Gastos, "Gastos", Icons.Default.Payments),
                        Triple(Screen.Profile, "Perfil", Icons.Default.AccountCircle)
                    )

                    items.forEach { (screen, label, icon) ->
                        NavigationBarItem(
                            icon = { Icon(icon, contentDescription = label) },
                            label = { Text(label) },
                            selected = currentRoute == screen.route,
                            onClick = {
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        // Patrón Senior: Evita acumular copias de la misma pantalla
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = Color.Gray,
                                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            // --- BLOQUE 1: AUTENTICACIÓN ---
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

            // --- BLOQUE 2: MARKETPLACE ---
            composable(Screen.MarketplaceItems.route) {
                MarketplaceScreenContainer(
                    viewModel = viewModel(factory = createFactory { MarketplaceViewModel(marketplaceRepository) }),
                    onProductClick = { id -> 
                        navController.navigate(Screen.ItemUpsert.createRoute(id.toString())) 
                    },
                    onAddProductClick = { 
                        navController.navigate(Screen.ItemUpsert.createRoute(null)) 
                    }
                )
            }
            
            composable(
                route = Screen.ItemUpsert.route,
                arguments = listOf(navArgument("itemId") { type = NavType.StringType })
            ) { backStackEntry ->
                val itemId = backStackEntry.arguments?.getString("itemId")?.let { 
                    if (it == "new") null else it 
                }
                ItemUpsertScreen(
                    viewModel = viewModel(factory = createFactory { ItemViewModel(marketplaceRepository) }),
                    itemId = itemId,
                    onBack = { navController.popBackStack() }
                )
            }

            // --- BLOQUE 3: UTILIDADES & OTROS ---
            composable(Screen.FoodList.route) { FoodScreenContainer() }
            
            composable(Screen.Gastos.route) { GastosScreen() }
            
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

/**
 * ViewModel Factory Genérico: Permite inyectar repositorios de forma limpia.
 */
inline fun <reified T : ViewModel> createFactory(crossinline creator: () -> T): ViewModelProvider.Factory {
    return object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return creator() as T
        }
    }
}
