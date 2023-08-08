package app.regimen

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import app.regimen.screens.GroupsScreen
import app.regimen.screens.HomeScreen
import app.regimen.screens.PagesScreen
import app.regimen.screens.SettingsScreen

@Composable
fun BottomNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = BottomBarScreen.Home.route
    ) {
        composable(route = BottomBarScreen.Home.route) {
            HomeScreen()
        }
        composable(route = BottomBarScreen.Pages.route) {
            PagesScreen()
        }
        composable(route = BottomBarScreen.Groups.route) {
            GroupsScreen()
        }
        composable(route = BottomBarScreen.Settings.route) {
            SettingsScreen()
        }
    }
}