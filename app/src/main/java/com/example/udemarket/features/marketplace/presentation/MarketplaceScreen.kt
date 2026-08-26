package com.example.udemarket.features.marketplace.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.udemarket.ui.theme.UdeMarketTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketplaceScreen(
    uiState: MarketplaceUiState = MarketplaceUiState(),
    onCategorySelected: (String) -> Unit = {},
    onProductClick: (Int) -> Unit = {},
    onAddProductClick: () -> Unit = {}
) {
    val neonPurple = MaterialTheme.colorScheme.primary
    val deepPurple = Color(0xFF0F001A)

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(Color.Black, deepPurple, Color.Black)
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "MARKETPLACE",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp
                        )
                    )
                },
                actions = {
                    IconButton(onClick = { /* Ir al carrito o mis ventas */ }) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddProductClick,
                containerColor = neonPurple,
                contentColor = Color.Black,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Vender artículo")
            }
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush)
                .padding(paddingValues)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Buscador Glassmorphism
                SearchBar(
                    modifier = Modifier.padding(16.dp),
                    neonPurple = neonPurple
                )

                // Categorías en fila
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    items(uiState.categories) { category ->
                        CategoryChip(
                            name = category,
                            isSelected = category == uiState.selectedCategory,
                            neonPurple = neonPurple,
                            onClick = { onCategorySelected(category) }
                        )
                    }
                }

                // Grid de Productos
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.products) { product ->
                        ProductItem(
                            product = product,
                            neonPurple = neonPurple,
                            onClick = { onProductClick(product.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MarketplaceScreenContainer(
    viewModel: MarketplaceViewModel = viewModel(),
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
fun SearchBar(modifier: Modifier = Modifier, neonPurple: Color) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        color = Color.White.copy(alpha = 0.05f),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Search, contentDescription = null, tint = neonPurple)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                "Buscar en el Marketplace...",
                color = Color.White.copy(alpha = 0.4f),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun CategoryChip(
    name: String,
    isSelected: Boolean,
    neonPurple: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        color = if (isSelected) neonPurple else Color.White.copy(alpha = 0.05f),
        shape = RoundedCornerShape(50.dp),
        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Text(
            text = name,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = if (isSelected) Color.Black else Color.White,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
        )
    }
}

@Composable
fun ProductItem(
    product: Product,
    neonPurple: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = Color.White.copy(alpha = 0.05f),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Column {
            // Placeholder de imagen
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(neonPurple.copy(alpha = 0.05f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = product.name.take(1),
                    style = MaterialTheme.typography.displaySmall.copy(
                        color = neonPurple,
                        fontWeight = FontWeight.Black
                    )
                )
            }
            
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = product.price,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = neonPurple,
                        fontWeight = FontWeight.ExtraBold
                    )
                )
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Vendedor: ${product.sellerName}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Preview(showBackground = true, device = "id:pixel_7")
@Composable
fun MarketplaceScreenPreview() {
    UdeMarketTheme {
        MarketplaceScreen()
    }
}
