package com.example.plantify.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.plantify.ui.theme.PlantifyMediumGreen

import com.example.plantify.ui.screens.HomeScreen
import com.example.plantify.ui.screens.SplashScreen
import com.example.plantify.ui.screens.CatalogScreen
import com.example.plantify.ui.screens.ScheduleScreen
import com.example.plantify.ui.screens.AlertsScreen
import com.example.plantify.ui.screens.ProfileScreen
import com.example.plantify.ui.screens.AddPlantScreen
import com.example.plantify.ui.screens.PlantDetailScreen
import com.example.plantify.ui.screens.GrowthProgressScreen
import com.example.plantify.ui.viewmodel.HomeViewModel
import com.example.plantify.ui.viewmodel.LocationViewModel
import com.example.plantify.ui.viewmodel.ProfileViewModel
import com.example.plantify.ui.viewmodel.ViewModelFactory
import androidx.compose.ui.platform.LocalContext

sealed class Screen(val route: String, val title: String, val iconRes: Int = 0) {
    object Splash : Screen("splash", "Splash")
    object Home : Screen("home", "Home", R.drawable.ic_nav_home)
    object Catalog : Screen("catalog", "Catalog", R.drawable.ic_nav_catalog)
    object Schedule : Screen("schedule", "Schedule", R.drawable.ic_nav_schedule)
    object Growth : Screen("growth", "Growth", R.drawable.ic_nav_growth)
    object Profile : Screen("profile", "Profile", R.drawable.ic_nav_profile)
    object AddPlant : Screen("add_plant?plantName={plantName}", "Add Plant") {
        fun createRoute(plantName: String) = "add_plant?plantName=$plantName"
    }
    object PlantDetail : Screen("plant_detail/{plantId}", "Plant Detail") {
        fun createRoute(plantId: String) = "plant_detail/$plantId"
    }
    object GrowthProgress : Screen("growth_progress", "Growth Progress")
    object Alerts : Screen("alerts", "Alerts")
}

@Composable
fun PlantifyApp(profileViewModel: ProfileViewModel = viewModel()) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val context = LocalContext.current
    val factory = ViewModelFactory(context)

    // Shared ViewModels
    val homeViewModel: HomeViewModel = viewModel(factory = factory)
    val locationViewModel: LocationViewModel = viewModel(factory = factory)

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
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = PlantifyMediumGreen,
                                selectedTextColor = PlantifyMediumGreen,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                indicatorColor = Color.Transparent
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(onTimeout = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                })
            }
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = homeViewModel,
                    onPlantClick = { plantId ->
                        navController.navigate(Screen.PlantDetail.createRoute(plantId))
                    },
                    onNotificationClick = {
                        navController.navigate(Screen.Alerts.route)
                    }
                )
            }
            composable(Screen.Catalog.route) {
                CatalogScreen(
                    onAddPlantClick = { plantName -> 
                        if (plantName != null) {
                            navController.navigate(Screen.AddPlant.createRoute(plantName))
                        } else {
                            navController.navigate("add_plant?plantName=")
                        }
                    },
                    onPlantClick = { plantId -> navController.navigate(Screen.PlantDetail.createRoute(plantId)) }
                )
            }
            composable(Screen.Schedule.route) {
                ScheduleScreen()
            }
            composable(Screen.Growth.route) {
                GrowthProgressScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    viewModel = profileViewModel,
                    onNotificationClick = {
                        navController.navigate(Screen.Alerts.route)
                    }
                )
            }
            composable(
                route = Screen.AddPlant.route,
                arguments = listOf(navArgument("plantName") { 
                    type = NavType.StringType
                    nullable = true
                })
            ) { backStackEntry ->
                val plantName = backStackEntry.arguments?.getString("plantName")
                AddPlantScreen(
                    plantName = plantName,
                    onBackClick = { navController.popBackStack() },
                    onPlantSaved = { navController.popBackStack() }
                )
            }
            composable(
                route = Screen.PlantDetail.route,
                arguments = listOf(navArgument("plantId") { type = NavType.StringType })
            ) { backStackEntry ->
                val plantId = backStackEntry.arguments?.getString("plantId") ?: ""
                PlantDetailScreen(
                    plantId = plantId,
                    onBackClick = { navController.popBackStack() },
                    onGrowthProgressClick = { navController.navigate(Screen.GrowthProgress.route) }
                )
            }
            composable(Screen.GrowthProgress.route) {
                GrowthProgressScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(Screen.Alerts.route) {
                AlertsScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}
