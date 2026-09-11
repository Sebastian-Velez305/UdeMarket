package com.example.udemarket.data.model

data class User(
    val uid: String = "",
    val nombre: String = "",
    val email: String = "",
    val carrera: String = "",
    val reputacion: Double = 5.0,
    val role: String = "USER" // Roles: USER, ADMIN
)
