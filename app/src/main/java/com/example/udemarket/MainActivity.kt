package com.example.udemarket

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.udemarket.core.navigation.Screen
import com.example.udemarket.data.repository.AuthRepositoryImpl
import com.example.udemarket.data.repository.FoodRepositoryImpl
import com.example.udemarket.features.auth.presentation.login.LoginScreen
import com.example.udemarket.features.auth.presentation.login.LoginViewModel
import com.example.udemarket.features.auth.presentation.register.RegisterScreen
import com.example.udemarket.features.food.presentation.FoodScreenContainer
import com.example.udemarket.features.food.presentation.FoodViewModel
import com.example.udemarket.ui.theme.UdeMarketTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Inicialización manual de dependencias (Simulando Inyección de Dependencias)
        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        val authRepository = AuthRepositoryImpl(auth, db)
        val foodRepository = FoodRepositoryImpl(db)

        setContent {
            UdeMarketTheme {
                UdeMarketApp(authRepository, foodRepository)
            }
        }
    }
}

@Composable
fun UdeMarketApp(authRepository: AuthRepositoryImpl, foodRepository: FoodRepositoryImpl) {
    val navController = rememberNavController()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Login.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Login.route) {
                val loginViewModel: LoginViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return LoginViewModel(authRepository) as T
                        }
                    }
                )
                LoginScreen(
                    viewModel = loginViewModel,
                    onNavigateToRegister = {
                        navController.navigate(Screen.Register.route)
                    },
                    onLoginSuccess = {
                        navController.navigate(Screen.FoodList.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
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

            composable(Screen.FoodList.route) {
                val foodViewModel: FoodViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return FoodViewModel(foodRepository) as T
                        }
                    }
                )
                FoodScreenContainer(
                    viewModel = foodViewModel,
                    onNavigateToDetail = { /* Navegar a detalle de tienda */ }
                )
            }
        }
    }
}
