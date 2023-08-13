package app.regimen

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.NotificationAdd
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import app.regimen.screens.GroupsScreen
import app.regimen.screens.HomeScreen
import app.regimen.screens.PagesScreen
import app.regimen.screens.SettingsScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    // Used for dynamic topAppBar
    var appBarState by remember {
        mutableStateOf(AppBarState())
    }

    // Bottom nav bar destination tracker
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = { BottomBar(navController = navController) },
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(text = appBarState.title)
                },
                actions = {
                    appBarState.actions?.invoke(this)
                }
            )
        },
        floatingActionButton = {
            if (currentDestination != null) {
                if (currentDestination.route != BottomBarScreen.Settings.route) {
                    FloatingActionButton(
                        onClick = {
                            // Should be empty
                        },
                        shape = MaterialTheme.shapes.small.copy(CornerSize(percent = 50))
                    ) {
                        val fabIcon = getFabIconForDestination(currentDestination)
                        val onClickAction = getFabOnClickActionForDestination(currentDestination)

                        FloatingActionButton(onClick = onClickAction) {
                            Icon(imageVector = fabIcon, contentDescription = null)
                        }
                    }
                }
            }
        }

    ) {contentPadding ->

        // Bottom nav bar navigation
        NavHost(
            navController = navController,
            startDestination = BottomBarScreen.Home.route,
            modifier = Modifier.padding(
                contentPadding
            )
        ) {
            composable(BottomBarScreen.Home.route) {
                HomeScreen(
                    onComposing = {
                        appBarState = it
                    },
                    navController = navController
                )
            }
            composable(BottomBarScreen.Pages.route) {
                PagesScreen(
                    onComposing = {
                        appBarState = it
                    },
                    navController = navController
                )
            }
            composable(BottomBarScreen.Groups.route) {
                GroupsScreen(
                    onComposing = {
                        appBarState = it
                    },
                    navController = navController
                )
            }
            composable(BottomBarScreen.Settings.route) {
                SettingsScreen(
                    onComposing = {
                        appBarState = it
                    },
                    navController = navController
                )
            }
        }

    }
}

// Bottom nav bar
@Composable
fun BottomBar(navController: NavHostController) {
    val screens = listOf(
        BottomBarScreen.Home,
        BottomBarScreen.Pages,
        BottomBarScreen.Groups,
        BottomBarScreen.Settings,
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar() {
        screens.forEach { screen ->
            AddItem(screen = screen, currentDestination = currentDestination, navController = navController)
        }
    }
}

@Composable
fun RowScope.AddItem(
    screen: BottomBarScreen,
    currentDestination: NavDestination?,
    navController: NavHostController
) {
    val isSelected = currentDestination?.hierarchy?.any {
        it.route == screen.route
    } == true

    NavigationBarItem(
        label = {
            Text(text = screen.title)
        },
        icon = {
            val icon = if (isSelected) {
                screen.filledIcon
            } else {
                screen.icon
            }

            Icon(
                imageVector = icon,
                contentDescription = null
            )
        },
        selected = isSelected,
        onClick = {
            navController.navigate(screen.route) {
                popUpTo(navController.graph.findStartDestination().id)
                launchSingleTop = true
            }
        }
    )
}

// Dynamic top app bar
data class AppBarState(
    val title: String = "",
    val actions: (@Composable RowScope.() -> Unit)? = null
)

// Fab button functionality
// Define a function to get the appropriate FAB icon based on the destination:
private fun getFabIconForDestination(destination: NavDestination?): ImageVector {
    return when (destination?.route) {
        BottomBarScreen.Home.route -> Icons.Default.NotificationAdd
        BottomBarScreen.Pages.route -> Icons.Default.Edit
        BottomBarScreen.Groups.route -> Icons.Default.CreateNewFolder
        else -> Icons.Filled.Add // Default icon
    }
}

// Define a function to get the appropriate FAB onClick action based on the destination:
private fun getFabOnClickActionForDestination(destination: NavDestination?): () -> Unit {
    return when (destination?.route) {
        BottomBarScreen.Home.route -> {
            // Add your desired onClick action for the Home destination
            {  }
        }
        BottomBarScreen.Pages.route -> {
            // Add your desired onClick action for the Pages destination
            {  }
        }
        BottomBarScreen.Groups.route -> {
            // Add your desired onClick action for the Groups destination
            {  }
        }
        else -> {
            // Default onClick action
            { }
        }
    }
}