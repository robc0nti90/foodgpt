package com.foodielogs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FoodieLogsApp()
        }
    }
}

private val BrandGreen = Color(0xFF0E5A3A)
private val BrandGreenDark = Color(0xFF0A4A31)
private val BrandGold = Color(0xFFD89B4A)
private val SoftGray = Color(0xFFF3F3F3)
private val TextGray = Color(0xFF6E6E6E)

@Composable
fun FoodieLogsApp() {
    MaterialTheme {
        val navController = rememberNavController()
        val restaurants = remember { mutableStateListOf(*sampleRestaurants.toTypedArray()) }
        val selectedRestaurant = remember { mutableStateOf(restaurants.first()) }
        val selectedMenuItem = remember { mutableStateOf(restaurants.first().menuItems.first()) }

        NavHost(
            navController = navController,
            startDestination = "home"
        ) {
            composable("home") {
                HomeScreen(
                    restaurants = restaurants,
                    onAddRestaurant = { navController.navigate("add_restaurant") },
                    onOpenAccount = { navController.navigate("account") },
                    onOpenRestaurant = {
                        selectedRestaurant.value = it
                        navController.navigate("restaurant_main")
                    },
                    onFavoriteToggle = { restaurant ->
                        val index = restaurants.indexOfFirst { it.id == restaurant.id }
                        if (index >= 0) {
                            restaurants[index] = restaurant.copy(isFavorite = !restaurant.isFavorite)
                        }
                    }
                )
            }
            composable("account") {
                AccountSettingsScreen(onBack = { navController.popBackStack() })
            }
            composable("add_restaurant") {
                AddRestaurantScreen(
                    onBack = { navController.popBackStack() },
                    onSubmit = { name, location, review, rating, price ->
                        val nextId = (restaurants.maxOfOrNull { it.id } ?: 0) + 1
                        restaurants.add(
                            Restaurant(
                                id = nextId,
                                name = name,
                                location = location,
                                review = review,
                                longReview = review,
                                rating = rating,
                                price = price,
                                menuItems = emptyList(),
                                isFavorite = false
                            )
                        )
                        navController.popBackStack()
                    }
                )
            }
            composable("restaurant_main") {
                RestaurantMainScreen(
                    restaurant = selectedRestaurant.value,
                    onBack = { navController.popBackStack() },
                    onOpenAbout = { navController.navigate("restaurant_about") },
                    onAddMenuItem = { navController.navigate("add_menu_item") },
                    onFavoriteRestaurant = {
                        val index = restaurants.indexOfFirst { it.id == selectedRestaurant.value.id }
                        if (index >= 0) {
                            val updated = selectedRestaurant.value.copy(isFavorite = !selectedRestaurant.value.isFavorite)
                            selectedRestaurant.value = updated
                            restaurants[index] = updated
                        }
                    },
                    onFavoriteMenuItem = { menuItem ->
                        val updatedItems = selectedRestaurant.value.menuItems.map {
                            if (it.id == menuItem.id) it.copy(isFavorite = !it.isFavorite) else it
                        }
                        selectedRestaurant.value = selectedRestaurant.value.copy(menuItems = updatedItems)
                    },
                    onOpenMenuItem = {
                        selectedMenuItem.value = it
                        navController.navigate("menu_item_about")
                    }
                )
            }
            composable("restaurant_about") {
                RestaurantAboutScreen(
                    restaurant = selectedRestaurant.value,
                    onBack = { navController.popBackStack() },
                    onEdit = { navController.navigate("edit_restaurant") },
                    onFavorite = {
                        val index = restaurants.indexOfFirst { it.id == selectedRestaurant.value.id }
                        if (index >= 0) {
                            val updated = selectedRestaurant.value.copy(isFavorite = !selectedRestaurant.value.isFavorite)
                            selectedRestaurant.value = updated
                            restaurants[index] = updated
                        }
                    }
                )
            }
            composable("edit_restaurant") {
                EditRestaurantScreen(
                    restaurant = selectedRestaurant.value,
                    onBack = { navController.popBackStack() },
                    onSubmit = { navController.popBackStack() }
                )
            }
            composable("add_menu_item") {
                AddMenuItemScreen(
                    onBack = { navController.popBackStack() },
                    onSubmit = { name, review, rating ->
                        val currentRestaurant = selectedRestaurant.value
                        val nextId = (currentRestaurant.menuItems.maxOfOrNull { it.id } ?: 0) + 1
                        val newItem = MenuItem(
                            id = nextId,
                            name = name,
                            review = review,
                            rating = rating,
                            isFavorite = false
                        )
                        val updatedRestaurant = currentRestaurant.copy(
                            menuItems = currentRestaurant.menuItems + newItem
                        )
                        selectedRestaurant.value = updatedRestaurant
                        val index = restaurants.indexOfFirst { it.id == currentRestaurant.id }
                        if (index >= 0) {
                            restaurants[index] = updatedRestaurant
                        }
                        navController.popBackStack()
                    }
                )
            }
            composable("menu_item_about") {
                MenuItemAboutScreen(
                    item = selectedMenuItem.value,
                    onBack = { navController.popBackStack() },
                    onEdit = { navController.navigate("edit_menu_item") },
                    onFavorite = {
                        selectedMenuItem.value = selectedMenuItem.value.copy(isFavorite = !selectedMenuItem.value.isFavorite)
                    }
                )
            }
            composable("edit_menu_item") {
                EditMenuItemScreen(
                    item = selectedMenuItem.value,
                    onBack = { navController.popBackStack() },
                    onSubmit = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
fun TopBar(title: String, onBack: (() -> Unit)? = null, trailing: @Composable (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBack != null) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
        }
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )
        if (trailing != null) {
            trailing()
        }
    }
}

@Composable
fun HomeScreen(
    restaurants: List<Restaurant>,
    onAddRestaurant: () -> Unit,
    onOpenAccount: () -> Unit,
    onOpenRestaurant: (Restaurant) -> Unit,
    onFavoriteToggle: (Restaurant) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var filterExpanded by remember { mutableStateOf(false) }

    val sortedRestaurants = restaurants.sortedWith(
        compareByDescending<Restaurant> { it.isFavorite }.thenByDescending { it.rating }
    )

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("FOODIE", fontWeight = FontWeight.Bold, fontSize = 22.sp)
                    Text("LOGS", fontSize = 18.sp, color = TextGray)
                }
                IconButton(onClick = onOpenAccount) {
                    Icon(Icons.Default.Person, contentDescription = "Account")
                }
            }

            SearchBar(
                value = query,
                placeholder = "Search Your Restaurants",
                onValueChange = { query = it },
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { filterExpanded = !filterExpanded }
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text("FILTERS", color = BrandGreen, fontWeight = FontWeight.SemiBold)
                Icon(
                    if (filterExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Toggle Filters",
                    tint = BrandGreen
                )
            }
            if (filterExpanded) {
                FilterRow(
                    items = listOf("Category", "Price", "Features", "Location"),
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                items(sortedRestaurants.filter { it.name.contains(query, ignoreCase = true) }) { restaurant ->
                    RestaurantCard(restaurant = restaurant, onOpen = { onOpenRestaurant(restaurant) }, onFavorite = {
                        onFavoriteToggle(restaurant)
                    })
                }
            }

            Button(
                onClick = onAddRestaurant,
                colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("ADD RESTAURANT", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun FilterRow(items: List<String>, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        items.forEach { label ->
            Box(
                modifier = Modifier
                    .border(1.dp, BrandGreen, RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(label, fontSize = 12.sp, color = BrandGreen)
            }
        }
    }
}

@Composable
fun RestaurantCard(restaurant: Restaurant, onOpen: () -> Unit, onFavorite: () -> Unit) {
    Surface(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth(),
        color = Color.White,
        tonalElevation = 2.dp,
        shadowElevation = 2.dp,
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onFavorite) {
                    Icon(
                        if (restaurant.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (restaurant.isFavorite) Color(0xFFB5161C) else TextGray
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(restaurant.name, fontWeight = FontWeight.Bold)
                    Text(restaurant.location, fontSize = 12.sp, color = TextGray)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = "Rating", tint = BrandGold, modifier = Modifier.size(16.dp))
                        Text("${restaurant.rating}", fontSize = 12.sp)
                    }
                    Text("${restaurant.menuItems.size} Items", fontSize = 11.sp, color = TextGray)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                restaurant.review,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontSize = 13.sp
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onOpen) {
                    Icon(Icons.Default.ArrowForward, contentDescription = "Open", tint = BrandGreen)
                }
            }
        }
    }
}

@Composable
fun AccountSettingsScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopBar(title = "Account Settings", onBack = onBack)
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text("Import & Export", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Import or export your Foodie Logs data as JSON files.",
                color = TextGray,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("IMPORT DATA", color = Color.White)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("EXPORT DATA", color = Color.White)
            }
        }
    }
}

@Composable
fun AddRestaurantScreen(
    onBack: () -> Unit,
    onSubmit: (String, String, String, Double, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var review by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(0.0) }
    var price by remember { mutableStateOf("") }
    val selectedFeatures = remember { mutableStateListOf<String>() }
    val selectedCategories = remember { mutableStateListOf<String>() }
    val requiredFilled = name.isNotBlank() && location.isNotBlank() && rating > 0

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 72.dp)
                .verticalScroll(rememberScrollState())
        ) {
            TopBar(title = "Add a Restaurant", onBack = onBack)
            RequiredNote()
            FormField(label = "NAME*", value = name, onValueChange = { name = it })
            FormField(label = "LOCATION*", value = location, onValueChange = { location = it })
            FormField(label = "REVIEW AND NOTES", value = review, onValueChange = { review = it })
            RatingPicker(label = "RATING*", rating = rating, onRatingChange = { rating = it })
            PriceSelector(price = price, onSelect = { price = it })
            MultiSelectChips(
                label = "FEATURES:",
                options = sampleFeatures,
                selected = selectedFeatures
            )
            MultiSelectChips(
                label = "CATEGORIES:",
                options = sampleCategories,
                selected = selectedCategories
            )
        }
        SubmitBar(
            enabled = requiredFilled,
            onSubmit = { onSubmit(name.trim(), location.trim(), review.trim(), rating, price) }
        )
    }
}

@Composable
fun RestaurantMainScreen(
    restaurant: Restaurant,
    onBack: () -> Unit,
    onOpenAbout: () -> Unit,
    onAddMenuItem: () -> Unit,
    onFavoriteRestaurant: () -> Unit,
    onFavoriteMenuItem: (MenuItem) -> Unit,
    onOpenMenuItem: (MenuItem) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var sortExpanded by remember { mutableStateOf(false) }

    val sortedItems = restaurant.menuItems.sortedWith(
        compareByDescending<MenuItem> { it.isFavorite }.thenByDescending { it.rating }
    )

    Column(modifier = Modifier.fillMaxSize()) {
        TopBar(
            title = restaurant.name,
            onBack = onBack,
            trailing = {
                IconButton(onClick = onFavoriteRestaurant) {
                    Icon(
                        if (restaurant.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite restaurant",
                        tint = if (restaurant.isFavorite) Color(0xFFB5161C) else TextGray
                    )
                }
            }
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            ToggleChip(text = "MENU", selected = true)
            Spacer(modifier = Modifier.width(12.dp))
            ToggleChip(text = "ABOUT", selected = false, onClick = onOpenAbout)
        }
        SearchBar(
            value = query,
            placeholder = "Search Menu Items",
            onValueChange = { query = it },
            modifier = Modifier.padding(20.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { sortExpanded = !sortExpanded }
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text("SORT BY FAVORITES", color = BrandGreen, fontWeight = FontWeight.SemiBold)
            Icon(
                if (sortExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = "Toggle sort",
                tint = BrandGreen
            )
        }
        if (sortExpanded) {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                listOf(
                    "Favorites (default)",
                    "Rating high to low",
                    "Rating low to high",
                    "Alphabetical A-Z",
                    "Alphabetical Z-A"
                ).forEach { option ->
                    Text(option, fontSize = 12.sp, color = TextGray, modifier = Modifier.padding(vertical = 2.dp))
                }
            }
        }
        LazyColumn(modifier = Modifier.weight(1f).padding(horizontal = 16.dp)) {
            items(sortedItems.filter { it.name.contains(query, ignoreCase = true) }) { item ->
                MenuItemCard(item = item, onOpen = { onOpenMenuItem(item) }, onFavorite = {
                    onFavoriteMenuItem(item)
                })
            }
        }
        Button(
            onClick = onAddMenuItem,
            colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("ADD MENU ITEM", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun RestaurantAboutScreen(restaurant: Restaurant, onBack: () -> Unit, onEdit: () -> Unit, onFavorite: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopBar(
            title = restaurant.name,
            onBack = onBack,
            trailing = {
                IconButton(onClick = onFavorite) {
                    Icon(
                        if (restaurant.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (restaurant.isFavorite) Color(0xFFB5161C) else TextGray
                    )
                }
            }
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            ToggleChip(text = "MENU", selected = false, onClick = onBack)
            Spacer(modifier = Modifier.width(12.dp))
            ToggleChip(text = "ABOUT", selected = true)
        }
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = "Rating", tint = BrandGold, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("${restaurant.rating}", fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.width(12.dp))
                Text(restaurant.location, color = TextGray)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .background(BrandGreen, RoundedCornerShape(20.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(restaurant.price, color = Color.White, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text("REVIEW AND NOTES", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(restaurant.longReview, color = TextGray, fontSize = 13.sp, lineHeight = 18.sp)
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = BrandGreen)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Edit Restaurant", color = BrandGreen)
            }
        }
    }
}

@Composable
fun EditRestaurantScreen(restaurant: Restaurant, onBack: () -> Unit, onSubmit: () -> Unit) {
    var name by remember { mutableStateOf(restaurant.name) }
    var location by remember { mutableStateOf(restaurant.location) }
    var review by remember { mutableStateOf(restaurant.review) }
    var rating by remember { mutableStateOf(restaurant.rating) }
    val hasChanges = name != restaurant.name || location != restaurant.location || review != restaurant.review || rating != restaurant.rating

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 72.dp)
                .verticalScroll(rememberScrollState())
        ) {
            TopBar(title = "Edit Restaurant", onBack = onBack)
            RequiredNote()
            FormField(label = "NAME*", value = name, onValueChange = { name = it })
            FormField(label = "LOCATION*", value = location, onValueChange = { location = it })
            FormField(label = "REVIEW AND NOTES", value = review, onValueChange = { review = it })
            RatingPicker(label = "RATING*", rating = rating, onRatingChange = { rating = it })
        }
        SubmitBar(enabled = hasChanges, onSubmit = onSubmit)
    }
}

@Composable
fun AddMenuItemScreen(onBack: () -> Unit, onSubmit: (String, String, Double) -> Unit) {
    var name by remember { mutableStateOf("") }
    var review by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(0.0) }
    val requiredFilled = name.isNotBlank() && rating > 0

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 72.dp)
                .verticalScroll(rememberScrollState())
        ) {
            TopBar(title = "Add Menu Item", onBack = onBack)
            RequiredNote()
            FormField(label = "NAME*", value = name, onValueChange = { name = it })
            RatingPicker(label = "RATING*", rating = rating, onRatingChange = { rating = it })
            FormField(label = "REVIEW AND NOTES", value = review, onValueChange = { review = it })
        }
        SubmitBar(
            enabled = requiredFilled,
            onSubmit = { onSubmit(name.trim(), review.trim(), rating) }
        )
    }
}

@Composable
fun MenuItemAboutScreen(item: MenuItem, onBack: () -> Unit, onEdit: () -> Unit, onFavorite: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopBar(
            title = item.name,
            onBack = onBack,
            trailing = {
                IconButton(onClick = onFavorite) {
                    Icon(
                        if (item.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (item.isFavorite) Color(0xFFB5161C) else TextGray
                    )
                }
            }
        )
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = "Rating", tint = BrandGold)
                Spacer(modifier = Modifier.width(6.dp))
                Text("${item.rating}", fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text("REVIEW AND NOTES", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(item.review, color = TextGray, fontSize = 13.sp, lineHeight = 18.sp)
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = BrandGreen)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Edit Menu Item", color = BrandGreen)
            }
        }
    }
}

@Composable
fun EditMenuItemScreen(item: MenuItem, onBack: () -> Unit, onSubmit: () -> Unit) {
    var name by remember { mutableStateOf(item.name) }
    var review by remember { mutableStateOf(item.review) }
    var rating by remember { mutableStateOf(item.rating) }
    val hasChanges = name != item.name || review != item.review || rating != item.rating

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 72.dp)
                .verticalScroll(rememberScrollState())
        ) {
            TopBar(title = "Edit Menu Item", onBack = onBack)
            RequiredNote()
            FormField(label = "NAME*", value = name, onValueChange = { name = it })
            RatingPicker(label = "RATING*", rating = rating, onRatingChange = { rating = it })
            FormField(label = "REVIEW AND NOTES", value = review, onValueChange = { review = it })
        }
        SubmitBar(enabled = hasChanges, onSubmit = onSubmit)
    }
}

@Composable
fun RequiredNote() {
    Text(
        "* required",
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
        color = Color(0xFFB5161C),
        fontSize = 12.sp
    )
}

@Composable
fun FormField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
        Text(label, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(fontSize = 14.sp)
        )
    }
}

@Composable
fun RatingPicker(label: String, rating: Double, onRatingChange: (Double) -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
        Text(label, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
        Row(verticalAlignment = Alignment.CenterVertically) {
            for (i in 1..5) {
                val isFilled = rating >= i * 2
                IconButton(onClick = { onRatingChange(i * 2.0) }) {
                    Icon(
                        if (isFilled) Icons.Default.Star else Icons.Outlined.StarBorder,
                        contentDescription = "Star",
                        tint = BrandGold
                    )
                }
            }
            Text("/10", fontSize = 12.sp, color = TextGray)
        }
        Text("Half stars supported in final build", fontSize = 11.sp, color = TextGray)
    }
}

@Composable
fun PriceSelector(price: String, onSelect: (String) -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
        Text("PRICE:", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("$", "$$", "$$$", "$$$$").forEach { option ->
                val selected = price == option
                Box(
                    modifier = Modifier
                        .border(1.dp, BrandGreen, RoundedCornerShape(20.dp))
                        .background(if (selected) BrandGreen else Color.Transparent, RoundedCornerShape(20.dp))
                        .clickable { onSelect(option) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(option, color = if (selected) Color.White else BrandGreen, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun MultiSelectChips(label: String, options: List<String>, selected: MutableList<String>) {
    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
        Text(label, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            options.take(3).forEach { option ->
                Chip(option, selected.contains(option)) {
                    if (selected.contains(option)) selected.remove(option) else selected.add(option)
                }
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            options.drop(3).take(3).forEach { option ->
                Chip(option, selected.contains(option)) {
                    if (selected.contains(option)) selected.remove(option) else selected.add(option)
                }
            }
        }
    }
}

@Composable
fun Chip(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .border(1.dp, BrandGreen, RoundedCornerShape(20.dp))
            .background(if (selected) BrandGreen else Color.Transparent, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(label, color = if (selected) Color.White else BrandGreen, fontSize = 12.sp)
    }
}

@Composable
fun SearchBar(value: String, placeholder: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text(placeholder, fontSize = 12.sp) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") }
    )
}

@Composable
fun SubmitBar(enabled: Boolean, onSubmit: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Bottom) {
        Button(
            onClick = onSubmit,
            enabled = enabled,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (enabled) BrandGreen else Color(0xFFBDBDBD)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("SUBMIT", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ToggleChip(text: String, selected: Boolean, onClick: (() -> Unit)? = null) {
    Box(
        modifier = Modifier
            .border(1.dp, BrandGreen, RoundedCornerShape(20.dp))
            .background(if (selected) BrandGreen else Color.Transparent, RoundedCornerShape(20.dp))
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(horizontal = 18.dp, vertical = 8.dp)
    ) {
        Text(text, color = if (selected) Color.White else BrandGreen, fontSize = 12.sp)
    }
}

@Composable
fun MenuItemCard(item: MenuItem, onOpen: () -> Unit, onFavorite: () -> Unit) {
    Surface(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth(),
        color = Color.White,
        tonalElevation = 2.dp,
        shadowElevation = 2.dp,
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onFavorite) {
                    Icon(
                        if (item.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (item.isFavorite) Color(0xFFB5161C) else TextGray
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(item.name, fontWeight = FontWeight.Bold)
                    Text(
                        item.review,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 12.sp,
                        color = TextGray
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = "Rating", tint = BrandGold, modifier = Modifier.size(16.dp))
                        Text("${item.rating}", fontSize = 12.sp)
                    }
                    IconButton(onClick = onOpen) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "Open", tint = BrandGreen)
                    }
                }
            }
        }
    }
}

private data class Restaurant(
    val id: Int,
    val name: String,
    val location: String,
    val review: String,
    val longReview: String,
    val rating: Double,
    val price: String,
    val menuItems: List<MenuItem>,
    val isFavorite: Boolean
)

private data class MenuItem(
    val id: Int,
    val name: String,
    val review: String,
    val rating: Double,
    val isFavorite: Boolean
)

private val sampleFeatures = listOf("Bar", "Delivery", "Dine In", "Outdoor", "Take Out", "Brunch")
private val sampleCategories = listOf("American", "Italian", "Mexican", "Asian", "Seafood", "Cafe")

private val sampleRestaurants = listOf(
    Restaurant(
        id = 1,
        name = "Burger King",
        location = "San Francisco, CA",
        review = "Restaurant review from user limited to two lines of their review...",
        longReview = "Restaurant description from user Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus ultricies elit eget imperdiet consectetur. Nam blandit mi eget orci tempus vehicula.\n\nLorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse et vestibulum purus. Fusce euismod metus tortor, in pellentesque quam consequat in.",
        rating = 4.5,
        price = "$$",
        menuItems = listOf(
            MenuItem(1, "Whopper", "Restaurant review from user limited to two lines of their review...", 4.5, true),
            MenuItem(2, "French Fries", "Restaurant review from user limited to two lines of their review...", 4.5, false),
            MenuItem(3, "Chicken Sandwich", "Restaurant review from user limited to two lines of their review...", 4.0, false)
        ),
        isFavorite = true
    ),
    Restaurant(
        id = 2,
        name = "McDonalds",
        location = "San Francisco, CA",
        review = "Restaurant review from user limited to two lines of their review...",
        longReview = "Restaurant description from user Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
        rating = 4.5,
        price = "$",
        menuItems = listOf(
            MenuItem(4, "Big Mac", "Restaurant review from user limited to two lines of their review...", 4.0, false)
        ),
        isFavorite = false
    ),
    Restaurant(
        id = 3,
        name = "Taco Bell",
        location = "San Francisco, CA",
        review = "Restaurant review from user limited to two lines of their review...",
        longReview = "Restaurant description from user Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
        rating = 4.0,
        price = "$$",
        menuItems = listOf(
            MenuItem(5, "Crunchwrap", "Restaurant review from user limited to two lines of their review...", 4.0, false)
        ),
        isFavorite = false
    )
)
