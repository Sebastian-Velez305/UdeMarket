package com.example.udemarket.data.model

data class MarketplaceItem(
    val itemId: String = "",
    val titulo: String = "",
    val descripcion: String = "",
    val precio: Double = 0.0,
    val categoria: String = "",
    val vendedorId: String = "",
    val fotoUrl: String? = null,
    val fechaPublicacion: Long = System.currentTimeMillis()
)
