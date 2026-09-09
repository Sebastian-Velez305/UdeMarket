package com.example.udemarket.features.marketplace.presentation

data class Product(
    val id: Int = 0,
    val name: String = "",
    val price: String = "",
    val category: String = "",
    val sellerName: String = "",
    val imageUrl: String? = null
)

data class MarketplaceUiState(
    val products: List<Product> = emptyList(),
    val categories: List<String> = listOf("Todo", "Tecnología", "Ropa", "Libros", "Otros"),
    val selectedCategory: String = "Todo",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
