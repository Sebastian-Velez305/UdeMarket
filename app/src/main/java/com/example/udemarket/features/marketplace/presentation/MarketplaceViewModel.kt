package com.example.udemarket.features.marketplace.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.udemarket.core.ResultState
import com.example.udemarket.data.repository.MarketplaceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MarketplaceViewModel(private val repository: MarketplaceRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(MarketplaceUiState())
    val uiState: StateFlow<MarketplaceUiState> = _uiState.asStateFlow()

    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            repository.getMarketplaceItems().collect { result ->
                when (result) {
                    is ResultState.Loading -> {
                        // Manejar carga
                    }
                    is ResultState.Success -> {
                        val products = result.data.map { item ->
                            Product(
                                id = item.itemId.hashCode(),
                                name = item.titulo,
                                price = "$${String.format("%,.0f", item.precio)}",
                                category = item.categoria,
                                sellerName = "Cargando...", // Se podría cruzar con el UID del vendedor
                                imageUrl = item.fotoUrl
                            )
                        }
                        _uiState.update { it.copy(products = products) }
                    }
                    is ResultState.Error -> {
                        // Manejar error
                    }
                }
            }
        }
    }

    fun onCategorySelected(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
    }
}
