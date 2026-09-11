package com.example.udemarket

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
import com.example.udemarket.data.repository.ChatRepository
import com.example.udemarket.data.repository.MarketplaceRepository
import com.example.udemarket.features.auth.presentation.login.LoginScreen
import com.example.udemarket.features.auth.presentation.login.LoginViewModel
import com.example.udemarket.features.auth.presentation.register.RegisterScreen
import com.example.udemarket.features.auth.presentation.register.RegisterViewModel
import com.example.udemarket.features.chat.presentation.detail.ChatDetailScreen
import com.example.udemarket.features.chat.presentation.detail.ChatDetailViewModel
import com.example.udemarket.features.chat.presentation.inbox.ChatInboxScreen
import com.example.udemarket.features.chat.presentation.inbox.ChatInboxViewModel
import com.example.udemarket.features.food.presentation.FoodScreenContainer
import com.example.udemarket.features.marketplace.presentation.*
import com.example.udemarket.features.profile.presentation.ProfileScreen
import com.example.udemarket.features.profile.presentation.ProfileViewModel
import com.example.udemarket.ui.features.gastos.GastosScreen
import com.example.udemarket.ui.theme.UdeMarketTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        
        val authRepository = AuthRepositoryImpl(auth, db)
        val marketplaceRepository = MarketplaceRepository(db)
        val chatRepository = ChatRepository(db)

        setContent {
            UdeMarketTheme {
                UdeMarketApp(auth, authRepository, marketplaceRepository, chatRepository)
            }
        }
    }
}

@Composable
fun UdeMarketApp(
    auth: FirebaseAuth,
    authRepository: AuthRepositoryImpl,
    marketplaceRepository: MarketplaceRepository,
    chatRepository: ChatRepository
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val scope = rememberCoroutineScope()

    val currentUserId = auth.currentUser?.uid ?: ""

    val startDestination = remember {
        if (auth.currentUser != null) Screen.FoodList.route else Screen.Login.route
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Black,
        bottomBar = {
            val mainScreens = listOf(
                Screen.FoodList.route, 
                Screen.MarketplaceItems.route, 
                Screen.ChatInbox.route, 
                Screen.Gastos.route, 
                Screen.Profile.route
            )
            
            if (currentRoute in mainScreens) {
                NavigationBar(containerColor = Color(0xFF0F001A), tonalElevation = 8.dp) {
                    val items = listOf(
                        Triple(Screen.FoodList, "Comida", Icons.Default.Fastfood),
                        Triple(Screen.MarketplaceItems, "Mercado", Icons.Default.Storefront),
                        Triple(Screen.ChatInbox, "Chat", Icons.Default.ChatBubble),
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
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
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
            // --- AUTH ---
            composable(Screen.Login.route) {
                LoginScreen(
                    viewModel = viewModel(factory = createFactory { LoginViewModel(authRepository) }),
                    onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                    onLoginSuccess = { navController.navigate(Screen.FoodList.route) { popUpTo(0) { inclusive = true } } }
                )
            }
            composable(Screen.Register.route) {
                RegisterScreen(
                    viewModel = viewModel(factory = createFactory { RegisterViewModel(authRepository) }),
                    onNavigateBack = { navController.popBackStack() },
                    onRegisterSuccess = { navController.navigate(Screen.FoodList.route) { popUpTo(0) { inclusive = true } } }
                )
            }

            // --- MARKETPLACE ---
            composable(Screen.FoodList.route) { FoodScreenContainer() }
            
            composable(Screen.MarketplaceItems.route) {
                MarketplaceScreenContainer(
                    viewModel = viewModel(factory = createFactory { MarketplaceViewModel(marketplaceRepository, authRepository) }),
                    currentUserId = currentUserId,
                    onEditClick = { id -> navController.navigate(Screen.ItemUpsert.createRoute(id)) },
                    onContactClick = { product ->
                        scope.launch {
                            try {
                                val conversationId = chatRepository.getOrCreateConversation(
                                    myId = currentUserId,
                                    otherId = product.sellerId,
                                    productId = product.id,
                                    productName = product.name
                                )
                                navController.navigate(Screen.ChatDetail.createRoute(conversationId))
                            } catch (e: Exception) {
                                // Fallback o mensaje si intenta contactarse a sí mismo
                                // La lógica del repositorio ya bloquea esto lanzando una excepción
                            }
                        }
                    },
                    onAddProductClick = { navController.navigate(Screen.ItemUpsert.createRoute(null)) }
                )
            }
            
            composable(
                route = Screen.ItemUpsert.route,
                arguments = listOf(navArgument("itemId") { type = NavType.StringType })
            ) { backStackEntry ->
                val itemId = backStackEntry.arguments?.getString("itemId")?.let { if (it == "new") null else it }
                ItemUpsertScreen(
                    viewModel = viewModel(factory = createFactory { ItemViewModel(marketplaceRepository) }),
                    itemId = itemId,
                    onBack = { navController.popBackStack() }
                )
            }

            // --- CHAT ---
            composable(Screen.ChatInbox.route) {
                ChatInboxScreen(
                    viewModel = viewModel(factory = createFactory { ChatInboxViewModel(chatRepository, authRepository) }),
                    onConversationClick = { id -> navController.navigate(Screen.ChatDetail.createRoute(id)) }
                )
            }
            composable(
                route = Screen.ChatDetail.route,
                arguments = listOf(navArgument("conversationId") { type = NavType.StringType })
            ) { backStackEntry ->
                val conversationId = backStackEntry.arguments?.getString("conversationId") ?: ""
                ChatDetailScreen(
                    viewModel = viewModel(factory = createFactory { ChatDetailViewModel(conversationId, chatRepository, authRepository) }),
                    onBack = { navController.popBackStack() }
                )
            }

            // --- OTROS ---
            composable(Screen.Gastos.route) { GastosScreen() }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    viewModel = viewModel(factory = createFactory { ProfileViewModel(authRepository) }),
                    onLogoutSuccess = { navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } } }
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
