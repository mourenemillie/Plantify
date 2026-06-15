package com.example.plantify.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.plantify.R
import com.example.plantify.data.local.PlantDatabase
import com.example.plantify.data.repository.PlantRepository
import com.example.plantify.ui.theme.PlantifyMediumGreen
import com.example.plantify.ui.screens.*
import com.example.plantify.ui.viewmodel.HomeViewModel
import com.example.plantify.ui.viewmodel.LocationViewModel
import com.example.plantify.ui.viewmodel.ViewModelFactory

sealed class Screen(val route: String, val title: String, val iconRes: Int = 0) {
    object Splash : Screen("splash", "Splash")
    object Home : Screen("home", "Home", R.drawable.ic_nav_home)
    object Catalog : Screen("catalog", "Catalog", R.drawable.ic_nav_catalog)
    object Schedule : Screen("schedule", "Schedule", R.drawable.ic_nav_schedule)
    object Growth : Screen("growth", "Growth", R.drawable.ic_nav_growth)
    object Profile : Screen("profile", "Profile", R.drawable.ic_nav_profile)
    object AddPlant : Screen("add_plant?plantId={plantId}", "Add Plant") {
        fun createRoute(plantId: Int? = null) = if (plantId != null) "add_plant?plantId=$plantId" else "add_plant"
    }
    object AddPlantType : Screen("add_plant_type", "Add Plant Type")
    object PlantDetail : Screen("plant_detail/{plantId}", "Plant Detail") {
        fun createRoute(plantId: String) = "plant_detail/$plantId"
    }
    object GrowthProgress : Screen("growth_progress", "Growth Progress")
    object Alerts : Screen("alerts", "Alerts")
}

@Composable
fun PlantifyApp() {
    val context = LocalContext.current
    val database = remember { PlantDatabase.getDatabase(context) }
    val repository = remember { PlantRepository(database.plantDao()) }
    val viewModelFactory = remember { ViewModelFactory(repository) }

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Shared ViewModels
    val homeViewModel: HomeViewModel = viewModel(factory = viewModelFactory)
    val locationViewModel: LocationViewModel = viewModel(factory = viewModelFactory)

    // Feed live weather from LocationViewModel into HomeViewModel
    val weatherCondition by locationViewModel.weatherCondition.collectAsState()
    LaunchedEffect(weatherCondition) {
        weatherCondition?.let { homeViewModel.updateWeather(it) }
    }

    val bottomNavItems = listOf(
        Screen.Home,
        Screen.Catalog,
        Screen.Schedule,
        Screen.Growth,
        Screen.Profile
    )

    val showBottomBar = bottomNavItems.any { it.route == currentDestination?.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    bottomNavItems.forEach { screen ->
                        val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true

                        NavigationBarItem(
                            icon = {
                                Icon(
                                    painter = painterResource(id = screen.iconRes),
                                    contentDescription = screen.title,
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = screen.title,
                                    fontSize = 11.sp
                                )
                            },
                            selected = isSelected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.find