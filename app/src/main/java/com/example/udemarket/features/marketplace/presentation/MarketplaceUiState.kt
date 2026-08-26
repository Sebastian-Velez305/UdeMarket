package com.example.udemarket.features.marketplace.presentation

data class Product(
    val id: Int,
    val name: String,
    val price: String,
    val category: String,
    val sellerName: String,
    val imageUrl: String = ""
)

data class MarketplaceUiState(
    val categories: List<String> = listOf("Todos", "Libros", "Tecnología", "Ropa", "Insumos"),
    val selectedCategory: String = "Todos",
    val products: List<Product> = listOf(
        Product(1, "Calculadora TI-Nspire", "$250.000", "Tecnología", "Juan Pérez"),
        Product(2, "Libro Cálculo Stewart", "$80.000", "Libros", "María López"),
        Product(3, "Bata de Laboratorio", "$45.000", "Insumos", "Carlos Ruiz"),
        Product(4, "Audífonos Sony", "$120.000", "Tecnología", "Ana Gómez")
    )
)
