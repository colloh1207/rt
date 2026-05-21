package com.sdd.marketplace.feature.home.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.sdd.marketplace.core.navigation.Screen
import com.sdd.marketplace.core.ui.components.*
import com.sdd.marketplace.core.ui.theme.*
import com.sdd.marketplace.domain.model.Category
import com.sdd.marketplace.domain.model.Product
import com.sdd.marketplace.feature.home.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val products = viewModel.products.collectAsLazyPagingItems()
    val pullRefreshState = rememberPullToRefreshState()

    if (pullRefreshState.isRefreshing) {
        LaunchedEffect(Unit) { viewModel.refreshProducts(); pullRefreshState.endRefresh() }
    }

    Box(Modifier.fillMaxSize().background(SddLightPink)) {
        Column(Modifier.fillMaxSize()) {
            // Top Bar
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.ShoppingBag, "Logo", tint = SddPink, modifier = Modifier.size(28.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Marketplace", color = SddPink, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                    }
                },
                actions = {
                    BadgedBox(badge = {
                        if (uiState.unreadNotifications > 0) Badge { Text("${uiState.unreadNotifications}") }
                    }) {
                        IconButton(onClick = { navController.navigate(Screen.Notifications.route) }) {
                            Icon(Icons.Outlined.Notifications, "Notifications")
                        }
                    }
                    IconButton(onClick = { }) {
                        Icon(Icons.Outlined.ShoppingCart, "Cart")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SddLightPink)
            )

            LazyColumn(Modifier.fillMaxSize()) {
                // Search Bar
                item {
                    Row(
                        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = uiState.searchQuery,
                            onValueChange = { viewModel.onSearchQueryChanged(it) },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("Search for products, brands and more...") },
                            leadingIcon = { Icon(Icons.Filled.Search, "Search") },
                            singleLine = true,
                            shape = RoundedCornerShape(50.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = Color.White,
                                focusedContainerColor = Color.White,
                                focusedBorderColor = SddPink,
                                unfocusedBorderColor = Color.Transparent
                            )
                        )
                        Spacer(Modifier.width(8.dp))
                        Box(
                            Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(SddPink).clickable { },
                            contentAlignment = Alignment.Center
                        ) { Icon(Icons.Filled.Tune, "Filter", tint = Color.White) }
                    }
                }

                // Category Shortcuts
                item {
                    Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Shortcuts", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("See all", color = SddPink, fontWeight = FontWeight.Medium, modifier = Modifier.clickable { })
                        }
                        Spacer(Modifier.height(12.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            items(categories) { cat ->
                                CategoryItem(cat, uiState.selectedCategory == cat.id, onClick = {
                                    viewModel.selectCategory(if (uiState.selectedCategory == cat.id) null else cat.id)
                                })
                            }
                        }
                    }
                }

                // Featured Products
                if (uiState.featuredProducts.isNotEmpty()) {
                    item {
                        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Featured Products", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("See all", color = SddPink, fontWeight = FontWeight.Medium)
                        }
                    }
                    item {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(uiState.featuredProducts) { product ->
                                ProductCard(
                                    imageUrl = product.images.firstOrNull(),
                                    title = product.title,
                                    price = product.price,
                                    sellerName = product.seller?.fullName ?: "Seller",
                                    sellerAvatarUrl = product.seller?.avatarUrl,
                                    isVerified = product.seller?.isVerified == true,
                                    isFavorite = false,
                                    onFavoriteClick = { viewModel.toggleFavorite(product.id) },
                                    onClick = { navController.navigate(Screen.ProductDetail.createRoute(product.id)) },
                                    modifier = Modifier.width(160.dp)
                                )
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                }

                // All Products Grid
                item {
                    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("All Products", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }

                // Products as 2-column grid
                item {
                    when {
                        products.loadState.refresh is LoadState.Loading -> {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                modifier = Modifier.height(400.dp),
                                contentPadding = PaddingValues(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(6) { ShimmerProductCard(Modifier.fillMaxWidth()) }
                            }
                        }
                        products.loadState.refresh is LoadState.Error -> {
                            Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                                ErrorMessage("Failed to load products") { products.retry() }
                            }
                        }
                        products.itemCount == 0 -> {
                            Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                                EmptyState("No products found", "Try a different category or search")
                            }
                        }
                        else -> {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                modifier = Modifier.height(1000.dp),
                                contentPadding = PaddingValues(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(products.itemCount) { index ->
                                    val product = products[index]
                                    product?.let { p ->
                                        ProductCard(
                                            imageUrl = p.images.firstOrNull(),
                                            title = p.title,
                                            price = p.price,
                                            sellerName = p.seller?.fullName ?: "Seller",
                                            sellerAvatarUrl = p.seller?.avatarUrl,
                                            isVerified = p.seller?.isVerified == true,
                                            isFavorite = false,
                                            onFavoriteClick = { viewModel.toggleFavorite(p.id) },
                                            onClick = { navController.navigate(Screen.ProductDetail.createRoute(p.id)) },
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
        PullToRefreshContainer(state = pullRefreshState, modifier = Modifier.align(Alignment.TopCenter))
    }
}

@Composable
fun CategoryItem(category: Category, isSelected: Boolean, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier.size(56.dp).clip(CircleShape)
                .background(if (isSelected) SddPink else SddPink.copy(alpha = 0.1f))
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(getCategoryIcon(category.name), category.name,
                tint = if (isSelected) Color.White else SddPink, modifier = Modifier.size(24.dp))
        }
        Spacer(Modifier.height(4.dp))
        Text(category.name, fontSize = 11.sp, color = if (isSelected) SddPink else MaterialTheme.colorScheme.onBackground)
    }
}

private fun getCategoryIcon(name: String): ImageVector = when (name.lowercase()) {
    "popular" -> Icons.Outlined.Star
    "women" -> Icons.Outlined.Checkroom
    "men" -> Icons.Outlined.Person
    "home" -> Icons.Outlined.Home
    "beauty" -> Icons.Outlined.Favorite
    "electronics" -> Icons.Outlined.PhoneAndroid
    else -> Icons.Outlined.GridView
}

private val categories = listOf(
    Category("popular", "Popular", "star", null),
    Category("women", "Women", "dress", null),
    Category("men", "Men", "tshirt", null),
    Category("home", "Home", "home", null),
    Category("beauty", "Beauty", "heart", null),
    Category("electronics", "Electronics", "phone", null),
    Category("all", "All", "grid", null)
)
