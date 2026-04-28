package com.example.plantify.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.plantify.ui.screens.*

sealed class Screen(val route: String, val title: String) {
    object Splash : Screen("splash", "Splash")
    object Home : Screen("home", "Home")
    object Catalog : Screen("catalog", "Catalog")
    object Schedule : Screen("schedule", "Schedule")
    object Alerts : Screen("alerts", "Alerts")
    object Profile : Screen("profile", "Profile")
    object AddPlant : Screen("add_plant", "Add Plant")
    object GrowthProgress : Screen("growth_progress", "Growth Progress")
}

val items = listOf(
    Screen.Home,
    Screen.Catalog,
    Screen.Schedule,
    Screen.Alerts,
    Screen.Profile
)

@Composable
fun PlantifyApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) {
            SplashScreen(onTimeout = {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            })
        }
        composable(Screen.Home.route) {
            MainScaffold(navController) {
                HomeScreen(onPlantClick = { navController.navigate(Screen.GrowthProgress.route) })
            }
        }
        composable(Screen.Catalog.route) {
            MainScaffold(navController) {
                CatalogScreen(
                    onAddPlantClick = { navController.navigate(Screen.AddPlant.route) }
                )
            }
        }
        composable(Screen.Schedule.route) {
            MainScaffold(navController) {
                ScheduleScreen()
            }
        }
        composable(Screen.Alerts.route) {
            MainScaffold(navController) {
                AlertsScreen()
            }
        }
        composable(Screen.Profile.route) {
            MainScaffold(navController) {
                ProfileScreen()
            }
        }
        composable(Screen.AddPlant.route) {
            AddPlantScreen(
                onBackClick = { navController.popBackStack() },
                onUseAiSchedule = { navController.popBackStack() },
                onCustomizeManually = { navController.popBackStack() }
            )
        }
        composable(Screen.GrowthProgress.route) {
            GrowthProgressScreen(onBackClick = { navController.popBackStack() })
        }
    }
}

@Composable
fun MainScaffold(
    navController: androidx.navigation.NavHostController,
    content: @Composable () -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = {
                            val icon = when (screen) {
                                Screen.Home -> Icons.Default.Home
                                Screen.Catalog -> Icons.AutoMirrored.Filled.List
                                Screen.Schedule -> Icons.Default.DateRange
                                Screen.Alerts -> Icons.Default.Notifications
                                Screen.Profile -> Icons.Default.Person
                                else -> Icons.Default.Home
                            }
                            Icon(icon, contentDescription = null)
                        },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        androidx.compose.foundation.layout.Box(modifier = Modifier.padding(innerPadding)) {
            content()
        }
    }
}
