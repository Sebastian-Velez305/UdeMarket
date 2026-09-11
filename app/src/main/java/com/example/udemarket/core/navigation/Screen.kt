package com.example.udemarket.core.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object FoodList : Screen("food_list")
    object MarketplaceItems : Screen("marketplace_items")
    object ChatInbox : Screen("chat_inbox")
    object Profile : Screen("profile")
    object Gastos : Screen("gastos")

    // Ruta para el chat individual con ID de conversación
    object ChatDetail : Screen("chat_detail/{conversationId}") {
        fun createRoute(conversationId: String) = "chat_detail/$conversationId"
    }

    // Ruta para crear/editar con parámetro opcional de ID
    object ItemUpsert : Screen("item_upsert/{itemId}") {
        fun createRoute(itemId: String? = null) = "item_upsert/${itemId ?: "new"}"
    }
}
