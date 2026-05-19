package com.example.plantify.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

sealed class Screen(val route: String, val title: String, val iconRes: Int = 0) {
    object Splash : Screen("splash", "Splash")
    object Home : Screen("home", "Home", R.drawable.ic_nav_home)
    object Catalog : Screen("catalog", "Catalog", R.drawable.ic_nav_catalog)
    object Schedule : Screen("schedule", "Schedule", R.drawable.ic_nav_schedule)
    object Growth : Screen("growth", "Growth", R.drawable.ic_nav_growth)
    object Profile : Screen("profile", "Profile", R.drawable.ic_nav_profile)
    object AddPlant : Screen("add_plant", "Add Plant")
    object PlantDetail : Screen("plant_detail/{plantId}", "Plant Detail") {
        fun createRoute(plantId: String) = "plant_detail/$plantId"
    }
    object GrowthProgress : Screen("growth_progress", "Growth Progress")
}

@Composable
fun PlantifyApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

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
                    containerColor = Color.White,
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
                                unselectedIconColor = Color(0xFF9CA3AF),
                                unselectedTextColor = Color(0xFF9CA3AF),
                                indicatorColor = Color.White
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
                    onPlantClick = {
                        // Langsung diarahkan ke rute GrowthProgress
                        navController.navigate(Screen.GrowthProgress.route)
                    }
                )
            }
            composable(Screen.Catalog.route) {
                CatalogScreen(
                    onAddPlantClick = { navController.navigate(Screen.AddPlant.route) },
                    onPlantClick = { plantId -> navController.navigate(Screen.PlantDetail.createRoute(plantId)) }
                )
            }
            composable(Screen.Schedule.route) {
                ScheduleScreen()
            }
            composable(Screen.Growth.route) {
                GrowthProgressScreen()
            }
            composable(Screen.Profile.route) {
                ProfileScreen()
            }
            composable(Screen.AddPlant.route) {
                AddPlantScreen()
            }
            composable(
                route = Screen.PlantDetail.route,
                arguments = listOf(navArgument("plantId") { type = NavType.StringType })
            ) { backStackEntry ->
                val plantId = backStackEntry.arguments?.getString("plantId") ?: ""
                PlantDetailScreen(
                    plantId = plantId,
                    onGrowthProgressClick = { navController.navigate(Screen.GrowthProgress.route) }
                )
            }
            composable(Screen.GrowthProgress.route) {
                GrowthProgressScreen()
            }
        }
    }
}