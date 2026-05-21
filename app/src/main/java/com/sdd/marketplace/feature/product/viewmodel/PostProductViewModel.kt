package com.sdd.marketplace.feature.product.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sdd.marketplace.domain.model.Product
import com.sdd.marketplace.domain.repository.AuthRepository
import com.sdd.marketplace.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.net.URLEncoder
import javax.inject.Inject

data class LocationSuggestion(
    val displayName: String,
    val shortName: String,
    val lat: Double,
    val lng: Double
)

data class PostProductUiState(
    val step: Int = 1,
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val brand: String = "",
    val condition: String = "",
    val price: String = "",
    val discountPrice: String = "",
    val stockQuantity: Int = 1,
    val tags: List<String> = emptyList(),
    val selectedImages: List<Uri> = emptyList(),
    val location: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val deliveryOptions: List<String> = emptyList(),
    val returnPolicy: String = "",
    val isNegotiable: Boolean = false,
    val isNew: Boolean = true,
    val isBoosted: Boolean = false,
    val isLoading: Boolean = false,
    val isUploading: Boolean = false,
    val uploadProgress: Float = 0f,
    val error: String? = null,
    val isSuccess: Boolean = false,
    val locationSuggestions: List<LocationSuggestion> = emptyList(),
    val isSearchingLocation: Boolean = false,
    val showLocationSheet: Boolean = false,
    val showDeliverySheet: Boolean = false,
    val showReturnPolicySheet: Boolean = false,
    val showPreview: Boolean = false
)

sealed class PostProductEvent {
    object PostSuccess : PostProductEvent()
    data class ShowError(val message: String) : PostProductEvent()
}

@HiltViewModel
class PostProductViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val authRepository: AuthRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(PostProductUiState())
    val uiState: StateFlow<PostProductUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<PostProductEvent>()
    val events: SharedFlow<PostProductEvent> = _events.asSharedFlow()

    fun updateTitle(title: String) = _uiState.update { it.copy(title = title) }
    fun updateDescription(desc: String) = _uiState.update { it.copy(description = desc) }
    fun updateCategory(cat: String) = _uiState.update { it.copy(category = cat) }
    fun updateBrand(brand: String) = _uiState.update { it.copy(brand = brand) }
    fun updateCondition(cond: String) = _uiState.update { it.copy(condition = cond) }
    fun updatePrice(price: String) = _uiState.update { it.copy(price = price.filter { c -> c.isDigit() || c == '.' }) }
    fun updateDiscountPrice(p: String) = _uiState.update { it.copy(discountPrice = p.filter { c -> c.isDigit() || c == '.' }) }
    fun updateStock(qty: Int) = _uiState.update { it.copy(stockQuantity = qty.coerceAtLeast(1)) }
    fun updateReturnPolicy(policy: String) = _uiState.update { it.copy(returnPolicy = policy) }
    fun toggleNegotiable() = _uiState.update { it.copy(isNegotiable = !it.isNegotiable) }
    fun toggleNew() = _uiState.update { it.copy(isNew = !it.isNew) }
    fun toggleBoosted() = _uiState.update { it.copy(isBoosted = !it.isBoosted) }

    fun addImage(uri: Uri) {
        val current = _uiState.value.selectedImages.toMutableList()
        if (current.size < 10) { current.add(uri); _uiState.update { it.copy(selectedImages = current) } }
    }

    fun removeImage(uri: Uri) =
        _uiState.update { it.copy(selectedImages = it.selectedImages.filter { img -> img != uri }) }

    fun addTag(tag: String) {
        val tags = _uiState.value.tags.toMutableList()
        if (tags.size < 10 && tag.isNotBlank() && !tags.contains(tag)) tags.add(tag.trim())
        _uiState.update { it.copy(tags = tags) }
    }

    fun removeTag(tag: String) = _uiState.update { it.copy(tags = it.tags.filter { t -> t != tag }) }

    fun toggleDeliveryOption(option: String) {
        val options = _uiState.value.deliveryOptions.toMutableList()
        if (options.contains(option)) options.remove(option) else options.add(option)
        _uiState.update { it.copy(deliveryOptions = options) }
    }

    fun showLocationSheet() = _uiState.update { it.copy(showLocationSheet = true) }
    fun hideLocationSheet() = _uiState.update { it.copy(showLocationSheet = false, locationSuggestions = emptyList()) }
    fun showDeliverySheet() = _uiState.update { it.copy(showDeliverySheet = true) }
    fun hideDeliverySheet() = _uiState.update { it.copy(showDeliverySheet = false) }
    fun showReturnPolicySheet() = _uiState.update { it.copy(showReturnPolicySheet = true) }
    fun hideReturnPolicySheet() = _uiState.update { it.copy(showReturnPolicySheet = false) }
    fun togglePreview() = _uiState.update { it.copy(showPreview = !it.showPreview) }

    fun selectLocation(suggestion: LocationSuggestion) {
        _uiState.update {
            it.copy(
                location = suggestion.shortName,
                latitude = suggestion.lat,
                longitude = suggestion.lng,
                showLocationSheet = false,
                locationSuggestions = emptyList()
            )
        }
    }

    fun searchLocation(query: String) {
        if (query.length < 2) { _uiState.update { it.copy(locationSuggestions = emptyList()) }; return }
        viewModelScope.launch {
            _uiState.update { it.copy(isSearchingLocation = true) }
            try {
                val suggestions = withContext(Dispatchers.IO) { fetchLocationSuggestions(query) }
                _uiState.update { it.copy(locationSuggestions = suggestions, isSearchingLocation = false) }
            } catch (e: Exception) {
                Timber.e(e, "Location search failed")
                _uiState.update { it.copy(isSearchingLocation = false) }
            }
        }
    }

    private fun fetchLocationSuggestions(query: String): List<LocationSuggestion> {
        val encodedQuery = URLEncoder.encode(query, "UTF-8")
        val url = java.net.URL("https://nominatim.openstreetmap.org/search?q=$encodedQuery&format=json&limit=8&addressdetails=1")
        val connection = url.openConnection() as java.net.HttpURLConnection
        connection.setRequestProperty("User-Agent", "SddMarketplace/1.0")
        connection.connectTimeout = 5000
        connection.readTimeout = 5000
        return try {
            val response = connection.inputStream.bufferedReader().readText()
            val jsonArray = Json.parseToJsonElement(response).jsonArray
            jsonArray.mapNotNull { element ->
                try {
                    val obj = element.jsonObject
                    val displayName = obj["display_name"]?.jsonPrimitive?.content ?: return@mapNotNull null
                    val lat = obj["lat"]?.jsonPrimitive?.content?.toDouble() ?: return@mapNotNull null
                    val lng = obj["lon"]?.jsonPrimitive?.content?.toDouble() ?: return@mapNotNull null
                    val address = obj["address"]?.jsonObject
                    val shortName = buildShortName(address, displayName)
                    LocationSuggestion(displayName = displayName, shortName = shortName, lat = lat, lng = lng)
                } catch (e: Exception) { null }
            }
        } finally {
            connection.disconnect()
        }
    }

    private fun buildShortName(address: kotlinx.serialization.json.JsonObject?, displayName: String): String {
        if (address == null) return displayName.split(",").take(2).joinToString(", ").trim()
        val parts = mutableListOf<String>()
        listOf("suburb", "city_district", "city", "town", "village", "county", "state").forEach { key ->
            address[key]?.jsonPrimitive?.content?.let { if (it.isNotBlank()) parts.add(it) }
        }
        address["country"]?.jsonPrimitive?.content?.let { parts.add(it) }
        return if (parts.size >= 2) parts.take(3).joinToString(", ")
        else displayName.split(",").take(3).joinToString(", ").trim()
    }

    fun nextStep() {
        val state = _uiState.value
        val error = when (state.step) {
            1 -> when {
                state.title.isBlank() -> "Please enter a product title"
                state.category.isBlank() -> "Please select a category"
                state.condition.isBlank() -> "Please select condition"
                state.price.isBlank() || state.price.toDoubleOrNull() == null || (state.price.toDoubleOrNull() ?: 0.0) <= 0 -> "Please enter a valid price"
                state.description.isBlank() -> "Please enter a product description"
                else -> null
            }
            2 -> null
            else -> null
        }
        if (error != null) { _uiState.update { it.copy(error = error) } } else {
            _uiState.update { it.copy(step = it.step + 1, error = null) }
        }
    }

    fun prevStep() = _uiState.update { it.copy(step = maxOf(1, it.step - 1), error = null) }

    fun submitProduct() = viewModelScope.launch {
        val state = _uiState.value
        val userId = authRepository.getCurrentUserId() ?: run {
            _events.emit(PostProductEvent.ShowError("Please sign in to post a product"))
            return@launch
        }

        if (state.selectedImages.isEmpty()) {
            _uiState.update { it.copy(error = "Please add at least one photo") }; return@launch
        }

        _uiState.update { it.copy(isLoading = true, isUploading = true, error = null, uploadProgress = 0f) }

        val imagePaths = state.selectedImages.mapIndexedNotNull { idx, uri ->
            _uiState.update { it.copy(uploadProgress = idx.toFloat() / state.selectedImages.size) }
            uriToFile(uri)?.absolutePath
        }

        val product = Product(
            id = "", title = state.title.trim(), description = state.description.trim(),
            price = state.price.toDoubleOrNull() ?: 0.0,
            discountPrice = state.discountPrice.toDoubleOrNull()?.takeIf { it > 0 },
            currency = "INR", category = state.category, brand = state.brand.ifBlank { null },
            condition = state.condition, stockQuantity = state.stockQuantity,
            images = emptyList(), tags = state.tags, attributes = emptyMap(),
            sellerId = userId, seller = null, location = state.location.ifBlank { null },
            latitude = state.latitude, longitude = state.longitude,
            deliveryOptions = state.deliveryOptions, returnPolicy = state.returnPolicy.ifBlank { null },
            isNegotiable = state.isNegotiable, isFeatured = false,
            isBoosted = state.isBoosted, isNew = state.isNew, isSold = false,
            viewCount = 0, favoriteCount = 0, rating = 0.0, reviewCount = 0,
            createdAt = "", updatedAt = ""
        )

        productRepository.createProduct(product, imagePaths)
            .onSuccess {
                _uiState.update { s -> s.copy(isSuccess = true, uploadProgress = 1f) }
                _events.emit(PostProductEvent.PostSuccess)
            }
            .onFailure {
                _uiState.update { s -> s.copy(error = it.message ?: "Failed to post product") }
                _events.emit(PostProductEvent.ShowError(it.message ?: "Failed to post product"))
            }
        _uiState.update { it.copy(isLoading = false, isUploading = false) }
    }

    private fun uriToFile(uri: Uri): File? = try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val tempFile = File.createTempFile("upload_", ".jpg", context.cacheDir)
        FileOutputStream(tempFile).use { output -> inputStream.copyTo(output) }
        inputStream.close()
        tempFile
    } catch (e: Exception) { null }
}
