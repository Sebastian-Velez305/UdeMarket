package com.example.udemarket.features.marketplace.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.udemarket.core.ResultState
import com.example.udemarket.data.model.MarketplaceItem
import com.example.udemarket.data.repository.MarketplaceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ItemUpsertUiState(
    val item: MarketplaceItem? = null,
    val items: List<MarketplaceItem> = emptyList(),
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val errorMessage: String? = null
)

class ItemViewModel(private val repository: MarketplaceRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(ItemUpsertUiState())
    val uiState = _uiState.asStateFlow()

    init {
        fetchItems()
    }

    fun fetchItems() {
        viewModelScope.launch {
            repository.getMarketplaceItems().collect { result ->
                when (result) {
                    is ResultState.Loading -> _uiState.update { it.copy(isLoading = true) }
                    is ResultState.Success -> _uiState.update { it.copy(isLoading = false, items = result.data) }
                    is ResultState.Error -> _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
            }
        }
    }

    fun loadItem(itemId: String) {
        viewModelScope.launch {
            repository.getMarketplaceItem(itemId).collect { result ->
                when (result) {
                    is ResultState.Loading -> _uiState.update { it.copy(isLoading = true) }
                    is ResultState.Success -> _uiState.update { it.copy(isLoading = false, item = result.data) }
                    is ResultState.Error -> _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
            }
        }
    }

    fun deleteItem(itemId: String) {
        viewModelScope.launch {
            repository.deleteMarketplaceItem(itemId).collect { result ->
                when (result) {
                    is ResultState.Loading -> _uiState.update { it.copy(isLoading = true) }
                    is ResultState.Success -> _uiState.update { it.copy(isLoading = false) }
                    is ResultState.Error -> _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
            }
        }
    }

    fun saveProduct(
        itemId: String?,
        titulo: String,
        descripcion: String,
        precio: String,
        categoria: String,
        onSuccess: () -> Unit
    ) {
        val precioDouble = precio.replace(",", ".").toDoubleOrNull() ?: 0.0
        val product = MarketplaceItem(
            itemId = itemId ?: "",
            titulo = titulo,
            descripcion = descripcion,
            precio = precioDouble,
            categoria = categoria
        )

        viewModelScope.launch {
            repository.saveMarketplaceItem(product).collect { result ->
                when (result) {
                    is ResultState.Loading -> _uiState.update { it.copy(isLoading = true) }
                    is ResultState.Success -> {
                        _uiState.update { it.copy(isLoading = false, isSaved = true) }
                        onSuccess()
                    }
                    is ResultState.Error -> _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
            }
        }
    }
}
