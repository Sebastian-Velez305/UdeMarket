package com.example.udemarket.features.food.presentation

data class FoodSpot(
    val id: Int,
    val name: String,
    val category: String,
    val rating: Double,
    val deliveryTime: String,
    val imageUrl: String = ""
)

data class FoodUiState(
    val categories: List<String> = listOf("Almuerzos", "Snacks", "Café", "Jugos", "Postres"),
    val selectedCategory: String = "Almuerzos",
    val featuredSpots: List<FoodSpot> = listOf(
        FoodSpot(1, "Doña Consuelo", "Almuerzos", 4.8, "15-20 min"),
        FoodSpot(2, "Snack UdeA", "Snacks", 4.5, "5-10 min"),
        FoodSpot(3, "Café Central", "Café", 4.9, "5 min")
    )
)
