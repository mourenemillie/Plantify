package com.example.plantify.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
    object Location : Screen("location/{plantName}/{idKebun}", "Location") {
        fun createRoute(plantName: String, idKebun: Int) = "location/$plantName/$idKebun"
    }
}

@Composable
fun PlantifyApp() {
    val context = LocalContext.current
    val database = remember { PlantDatabase.getDatabase(context) }
    val repository = remember { PlantRepository(database.plantDao(), com.example.plantify.data.remote.AiService()) }
    val viewModelFactory = remember { ViewModelFactory(repository) }

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val homeViewModel: HomeViewModel = viewModel(factory = viewModelFactory)

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
                    onPlantClick = {
                        navController.navigate(Screen.GrowthProgress.route)
                    },
                    onNotificationClick = {
                        navController.navigate(Screen.Alerts.route)
                    }
                )
            }
            composable(Screen.Catalog.route) {
                CatalogScreen(
                    viewModel = viewModel(factory = viewModelFactory),
                    onAddNewTypeClick = {
                        navController.navigate(Screen.AddPlantType.route)
                    },
                    onAddPlantClick = { id ->
                        navController.navigate(Screen.AddPlant.createRoute(id))
                    },
                    onPlantClick = { plantId -> navController.navigate(Screen.PlantDetail.createRoute(plantId)) }
                )
            }
            composable(Screen.Schedule.route) {
                ScheduleScreen(
                    viewModel = viewModel(factory = viewModelFactory)
                )
            }
            composable(Screen.Growth.route) {
                GrowthProgressScreen(
                    viewModel = viewModel(factory = viewModelFactory)
                )
            }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    viewModel = viewModel(factory = viewModelFactory),
                    onNotificationClick = {
                        navController.navigate(Screen.Alerts.route)
                    }
                )
            }
            composable(
                route = Screen.AddPlant.route,
                arguments = listOf(navArgument("plantId") {
                    type = NavType.IntType
                    defaultValue = 0
                })
            ) { backStackEntry ->
                val plantId = backStackEntry.arguments?.getInt("plantId") ?: 0
                AddPlantScreen(
                    viewModel = viewModel(factory = viewModelFactory),
                    preSelectedPlantId = plantId,
                    onSuccess = { navController.navigateUp() }
                )
            }
            composable(Screen.AddPlantType.route) {
                AddPlantTypeScreen(
                    viewModel = viewModel(factory = viewModelFactory),
                    onBackClick = { navController.navigateUp() },
                    onSuccess = { navController.navigateUp() }
                )
            }
            composable(
                route = Screen.PlantDetail.route,
                arguments = listOf(navArgument("plantId") { type = NavType.StringType })
            ) { backStackEntry ->
                val plantId = backStackEntry.arguments?.getString("plantId") ?: ""
                PlantDetailScreen(
                    plantId = plantId,
                    viewModel = viewModel(factory = viewModelFactory),
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(Screen.GrowthProgress.route) {
                GrowthProgressScreen(
                    viewModel = viewModel(factory = viewModelFactory)
                )
            }
            composable(Screen.Alerts.route) {
                AlertsScreen(
                    viewModel = viewModel(factory = viewModelFactory),
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(
                route = Screen.Location.route,
                arguments = listOf(
                    navArgument("plantName") { type = NavType.StringType },
                    navArgument("idKebun") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val plantName = backStackEntry.arguments?.getString("plantName") ?: ""
                val idKebun = backStackEntry.arguments?.getInt("idKebun") ?: 0
                LocationScreen(
                    viewModel = viewModel(factory = viewModelFactory),
                    plantName = plantName,
                    idKebun = idKebun,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}