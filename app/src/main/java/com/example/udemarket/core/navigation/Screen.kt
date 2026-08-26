package com.example.udemarket.core.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object FoodList : Screen("food_list")
    object MarketplaceItems : Screen("marketplace_items")
    object ChatInbox : Screen("chat_inbox")
    object Profile : Screen("profile")
}
