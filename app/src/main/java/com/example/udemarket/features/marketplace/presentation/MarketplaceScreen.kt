package com.example.udemarket.features.marketplace.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketplaceScreen(
    uiState: MarketplaceUiState,
    onCategorySelected: (String) -> Unit = {},
    onProductClick: (Int) -> Unit = {},
    onAddProductClick: () -> Unit = {}
) {
    val neonPurple = MaterialTheme.colorScheme.primary
    val deepPurple = Color(0xFF0F001A)
    val backgroundBrush = Brush.verticalGradient(listOf(Color.Black, deepPurple, Color.Black))

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("MARKETPLACE", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black, letterSpacing = 2.sp)) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent, titleContentColor = Color.White)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddProductClick, containerColor = neonPurple, contentColor = Color.Black, shape = RoundedCornerShape(16.dp)) {
                Icon(Icons.Default.Add, contentDescription = "Vender")
            }
        },
        containerColor = Color.Transparent
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().background(backgroundBrush).padding(padding)) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = neonPurple)
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    SearchBar(modifier = Modifier.padding(16.dp), neonPurple = neonPurple)
                    
                    LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(uiState.categories) { category ->
                            CategoryChip(name = category, isSelected = category == uiState.selectedCategory, neonPurple = neonPurple, onClick = { onCategorySelected(category) })
                        }
                    }

                    LazyVerticalGrid(columns = GridCells.Fixed(2), contentPadding = PaddingValues(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        items(uiState.products) { product ->
                            ProductItem(product = product, neonPurple = neonPurple, onClick = { onProductClick(product.id) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MarketplaceScreenContainer(
    viewModel: MarketplaceViewModel, // Quitamos el "= viewModel()" para evitar el crash
    onProductClick: (Int) -> Unit = {},
    onAddProductClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    MarketplaceScreen(
        uiState = uiState,
        onCategorySelected = { viewModel.onCategorySelected(it) },
        onProductClick = onProductClick,
        onAddProductClick = onAddProductClick
    )
}

@Composable
fun SearchBar(modifier: Modifier, neonPurple: Color) {
    Surface(modifier = modifier.fillMaxWidth().height(52.dp), color = Color.White.copy(alpha = 0.05f), shape = RoundedCornerShape(16.dp), border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))) {
        Row(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Search, null, tint = neonPurple)
            Spacer(modifier = Modifier.width(12.dp))
            Text("Buscar...", color = Color.White.copy(alpha = 0.4f))
        }
    }
}

@Composable
fun CategoryChip(name: String, isSelected: Boolean, neonPurple: Color, onClick: () -> Unit) {
    Surface(modifier = Modifier.clickable(onClick = onClick), color = if (isSelected) neonPurple else Color.White.copy(alpha = 0.05f), shape = RoundedCornerShape(50.dp)) {
        Text(text = name, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), color = if (isSelected) Color.Black else Color.White, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ProductItem(product: Product, neonPurple: Color, onClick: () -> Unit) {
    Surface(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick), color = Color.White.copy(alpha = 0.05f), shape = RoundedCornerShape(20.dp)) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(120.dp).background(neonPurple.copy(alpha = 0.05f)), contentAlignment = Alignment.Center) {
                Text(product.name.take(1), color = neonPurple, fontWeight = FontWeight.Black, fontSize = 40.sp)
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(product.price, color = neonPurple, fontWeight = FontWeight.ExtraBold)
                Text(product.name, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}
