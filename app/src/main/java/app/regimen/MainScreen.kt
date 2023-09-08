package app.regimen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.NotificationAdd
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
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
    // Used for dynamic scaffold
    var dynamicScaffoldState by remember {
        mutableStateOf(DynamicScaffoldState())
    }

    // Bottom nav bar destination tracker
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Define your text based on the current destination
    val titleText = when (currentDestination?.route) {
        BottomBarScreen.Home.route -> "Home"
        BottomBarScreen.Pages.route -> "Pages"
        BottomBarScreen.Groups.route -> "Groups"
        BottomBarScreen.Settings.route -> "Settings"
        else -> ""
    }

    val subtitleText = when (currentDestination?.route) {
        BottomBarScreen.Home.route -> "Manage your life."
        BottomBarScreen.Pages.route -> "Store your thoughts."
        BottomBarScreen.Groups.route -> "Organize your data."
        BottomBarScreen.Settings.route -> "Personalize your app."
        else -> ""
    }

    // Hide toolbar on scroll behavior
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())


    Scaffold(
        bottomBar = { BottomBar(navController = navController) },
        topBar = {
            LargeTopAppBar(
                title = {
                    Column() {
                        Text(
                            text = titleText,
                            style = MaterialTheme.typography.displaySmall)
                        Text(
                            modifier = Modifier.alpha(0.6f),
                            text = subtitleText,
                            style = MaterialTheme.typography.labelLarge)

                        HorizontalDivider(
                            modifier = Modifier
                                .alpha(0.4f)
                                .padding(top = 10.dp, end = 16.dp))

                    }
                },
                actions = {
                    dynamicScaffoldState.toolbarActions?.invoke(this)
                },
                scrollBehavior = scrollBehavior,
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
            )
        },
        floatingActionButton = {
            if (currentDestination?.route != BottomBarScreen.Settings.route) {
                val fabIcon = getFabIconForDestination(currentDestination)
                FloatingActionButton(
                    onClick = {
                        dynamicScaffoldState.fabOnClick?.invoke()
                    }
                ) {
                    Icon(imageVector = fabIcon, contentDescription = null)
                }
            }
        }

    ) {

        // MAIN NAV HOST
        NavHost(
            navController = navController,
            startDestination = BottomBarScreen.Home.route,
            modifier = Modifier.padding(it)
        ) {
            composable(
                route = BottomBarScreen.Home.route,
            ) {
                HomeScreen(
                    onComposing = {
                        dynamicScaffoldState = it
                    },
                    navController = navController
                )
            }
            composable(
                route = BottomBarScreen.Pages.route
            ) {
                PagesScreen(
                    onComposing = {
                        dynamicScaffoldState = it
                    },
                    navController = navController
                )
            }
            composable(
                route = BottomBarScreen.Groups.route
            ) {
                GroupsScreen(
                    onComposing = {
                        dynamicScaffoldState = it
                    },
                    navController = navController
                )
            }
            composable(
                route = BottomBarScreen.Settings.route
            ) {
                SettingsScreen(
                    onComposing = {
                        dynamicScaffoldState = it
                    },
                    navController = navController
                )
            }
        }

    }
}

// Generate the bottom nav bar
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

    CustomNavBar(screens = screens, currentDestination = currentDestination, navController = navController)
}


// A custom visual for bottom nav bar
@Composable
fun CustomNavBar(
    screens: List<BottomBarScreen>,
    currentDestination: NavDestination?,
    navController: NavHostController
) {
    Box(
        Modifier
            .shadow(5.dp)
            .background(color = MaterialTheme.colorScheme.surface)
            .height(64.dp)
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            for (screen in screens) {
                val isSelected = currentDestination?.hierarchy?.any {
                    it.route == screen.route
                } == true
                val animatedWeight by animateFloatAsState(targetValue = if (isSelected) 1.5f else 1f)
                Box(
                    modifier = Modifier.weight(animatedWeight),
                    contentAlignment = Alignment.Center,
                ) {
                    val interactionSource = remember { MutableInteractionSource() }
                    val onClick: () -> Unit = {
                        if (currentDestination?.route != screen.route) {
                            // Change screen only if it's not the current screen
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id)
                                launchSingleTop = true
                            }
                        }
                    }
                    CustomNavBarItem(
                        modifier = Modifier.clickable(
                            interactionSource = interactionSource,
                            indication = null // Get rid of ripple effect
                        ) {
                            onClick()
                        },
                        screen = screen,
                        isSelected = isSelected
                    )
                }
            }
        }
    }
}


// custom visual for the bottom nav bar icons
@Composable
fun FlipIcon(
    modifier: Modifier = Modifier,
    isActive: Boolean,
    activeIcon: ImageVector,
    inactiveIcon: ImageVector,
    contentDescription: String,
) {
    val animationRotation by animateFloatAsState(
        targetValue = if (isActive) 180f else 0f,
        animationSpec = spring(
            stiffness = Spring.StiffnessLow,
            dampingRatio = Spring.DampingRatioMediumBouncy
        )
    )
    Box(
        modifier = modifier
            .graphicsLayer { rotationY = animationRotation },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            rememberVectorPainter(image = if (animationRotation > 90f) activeIcon else inactiveIcon),
            contentDescription = contentDescription,
        )
    }
}

// A custom visual for bottom nav bar items
@Composable
private fun CustomNavBarItem(
    modifier: Modifier = Modifier,
    screen: BottomBarScreen,
    isSelected: Boolean
) {
    val animatedHeight by animateDpAsState(targetValue = if (isSelected) 36.dp else 26.dp)
    val animatedElevation by animateDpAsState(targetValue = if (isSelected) 15.dp else 0.dp)
    val animatedAlpha by animateFloatAsState(targetValue = if (isSelected) 1f else .5f)
    val animatedIconSize by animateDpAsState(
        targetValue = if (isSelected) 26.dp else 20.dp,
        animationSpec = spring(
            stiffness = Spring.StiffnessLow,
            dampingRatio = Spring.DampingRatioMediumBouncy
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier
                .height(animatedHeight)
                .shadow(
                    elevation = animatedElevation,
                    shape = RoundedCornerShape(20.dp)
                )
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(20.dp)
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            FlipIcon(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .fillMaxHeight()
                    .padding(start = 11.dp)
                    .alpha(animatedAlpha)
                    .size(animatedIconSize),
                isActive = isSelected,
                activeIcon = screen.filledIcon,
                inactiveIcon = screen.icon,
                contentDescription = screen.title
            )

            AnimatedVisibility(visible = isSelected) {
                Text(
                    text = screen.title,
                    modifier = Modifier.padding(start = 8.dp, end = 10.dp),
                    maxLines = 1,
                )
            }

        }
    }
}

// Class to handle dynamic scaffold for each nav screen
data class DynamicScaffoldState(
    val toolbarActions: (@Composable RowScope.() -> Unit)? = null,
    val fabOnClick: (() -> Unit)? = null
)

// Fab button icon
private fun getFabIconForDestination(destination: NavDestination?): ImageVector {
    return when (destination?.route) {
        BottomBarScreen.Home.route -> Icons.Default.NotificationAdd
        BottomBarScreen.Pages.route -> Icons.Default.Edit
        BottomBarScreen.Groups.route -> Icons.Default.CreateNewFolder
        else -> Icons.Filled.Add // Default icon
    }
}
