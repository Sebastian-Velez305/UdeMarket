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
import java.util.Locale

class MarketplaceViewModel(private val repository: MarketplaceRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(MarketplaceUiState())
    val uiState: StateFlow<MarketplaceUiState> = _uiState.asStateFlow()

    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.getMarketplaceItems().collect { result ->
                when (result) {
                    is ResultState.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                    is ResultState.Success -> {
                        val products = result.data.map { item ->
                            Product(
                                id = item.itemId.hashCode(),
                                name = item.titulo,
                                price = "$${String.format(Locale.getDefault(), "%,.0f", item.precio)}",
                                category = item.categoria,
                                sellerName = "Vendedor UdeMarket", 
                                imageUrl = item.fotoUrl
                            )
                        }
                        _uiState.update { it.copy(products = products, isLoading = false, errorMessage = null) }
                    }
                    is ResultState.Error -> {
                        _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                    }
                }
            }
        }
    }

    fun onCategorySelected(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
        // Aquí se podría filtrar la lista de productos por categoría en el futuro
    }
}
