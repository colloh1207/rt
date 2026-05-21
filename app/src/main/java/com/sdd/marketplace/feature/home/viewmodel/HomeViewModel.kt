package com.sdd.marketplace.feature.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sdd.marketplace.domain.model.Category
import com.sdd.marketplace.domain.model.Product
import com.sdd.marketplace.domain.repository.FavoriteRepository
import com.sdd.marketplace.domain.repository.NotificationRepository
import com.sdd.marketplace.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val featuredProducts: List<Product> = emptyList(),
    val trendingProducts: List<Product> = emptyList(),
    val categories: List<Category> = emptyList(),
    val selectedCategory: String? = null,
    val searchQuery: String = "",
    val isRefreshing: Boolean = false,
    val unreadNotifications: Int = 0,
    val unreadMessages: Int = 0,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val favoriteRepository: FavoriteRepository,
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    private val _searchQuery = MutableStateFlow("")

    val products: Flow<PagingData<Product>> = combine(
        _selectedCategory, _searchQuery
    ) { category, query -> Pair(category, query) }
        .flatMapLatest { (category, query) ->
            productRepository.getProducts(category = category, searchQuery = query.ifBlank { null })
        }
        .cachedIn(viewModelScope)

    init {
        loadInitialData()
        observeNotifications()
    }

    private fun loadInitialData() = viewModelScope.launch {
        productRepository.getFeaturedProducts().collect { featured ->
            _uiState.update { it.copy(featuredProducts = featured) }
        }
    }

    private fun observeNotifications() = viewModelScope.launch {
        notificationRepository.getUnreadCount().collect { count ->
            _uiState.update { it.copy(unreadNotifications = count) }
        }
    }

    fun selectCategory(category: String?) {
        _selectedCategory.value = category
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun refreshProducts() = viewModelScope.launch {
        _uiState.update { it.copy(isRefreshing = true) }
        kotlinx.coroutines.delay(1000)
        _uiState.update { it.copy(isRefreshing = false) }
    }

    fun toggleFavorite(productId: String) = viewModelScope.launch {
        favoriteRepository.toggleFavorite(productId)
    }

    fun clearError() = _uiState.update { it.copy(error = null) }
}
