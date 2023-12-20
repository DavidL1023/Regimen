package app.regimen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.NotificationAdd
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import app.regimen.data.AppDatabase
import app.regimen.data.GroupDao
import app.regimen.data.HabitDao
import app.regimen.data.PageDao
import app.regimen.data.RecurringReminderDao
import app.regimen.data.SingleTimeReminderDao
import app.regimen.screens.GroupsScreen
import app.regimen.screens.HomeScreen
import app.regimen.screens.PagesScreen
import app.regimen.screens.SettingsScreen
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// Database Dao's
lateinit var groupDao: GroupDao
lateinit var singleTimeReminderDao: SingleTimeReminderDao
lateinit var recurringReminderDao: RecurringReminderDao
lateinit var habitDao: HabitDao
lateinit var pageDao: PageDao

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(db: AppDatabase) {
    // Used for dynamic scaffold
    var dynamicScaffoldState by remember {
        mutableStateOf(DynamicScaffoldState())
    }

    groupDao = db.getGroupDao()
    singleTimeReminderDao = db.getSingleTimeReminderDao()
    recurringReminderDao = db.getRecurringReminderDao()
    habitDao = db.getHabitDao()
    pageDao = db.getPageDao()

    // Bottom nav bar destination tracker
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Bottom sheet toggle
    var showBottomSheet by remember { mutableStateOf(false) }

    // Background focus dim
    var toggledFocusDim by remember { mutableStateOf(false) }

    // Falsify dim on navigate
    DisposableEffect(currentDestination?.route) {
        onDispose {
            toggledFocusDim = false
        }
    }

    Scaffold(
        bottomBar = { BottomBar(navController = navController) },
        topBar = {
            CustomTopAppBar(
                title = dynamicScaffoldState.toolbarTitle,
                subtitle = dynamicScaffoldState.toolbarSubtitle,
                showDivider = true,
                actions = {
                    dynamicScaffoldState.toolbarActions?.invoke(this)
                }

            )
        },
        floatingActionButton = {
            if (currentDestination?.route != BottomBarScreen.Settings.route) {
                AnimatedVisibility(
                    visible = dynamicScaffoldState.lazyListStateVisible == true || dynamicScaffoldState.lazyStaggeredGridStateVisible == true,
                    enter = scaleIn(),
                    exit = scaleOut()
                ) {
                    CustomFloatingActionButton(
                        expandable = dynamicScaffoldState.expandableFab,
                        onFabClick = {
                            if (dynamicScaffoldState.expandableFab) {
                                toggledFocusDim = !toggledFocusDim // Show/hide a dismissible dim on extendable fab click
                            } else {
                                dynamicScaffoldState.showBottomSheetFabClicked()
                            }
                        },
                        fabIcon = getFabIconForDestination(currentDestination),
                        fabBoxContent = { isExpanded ->
                            dynamicScaffoldState.fabBoxContent?.invoke(this, isExpanded)
                        },
                        toggledFocusDim = toggledFocusDim,
                        showBottomSheet = showBottomSheet,
                        onFabBoxContentClick = { toggledFocusDim = false }
                    )
                }
            }
        }
    ) {
        // Show bottom sheet controlled by screens
        showBottomSheet = dynamicScaffoldState.showBottomSheet

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { dynamicScaffoldState.sheetDropdownDismissed() }
            ) {
                val isImeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
                val isKeyboardOpen by rememberUpdatedState(isImeVisible)

                // Sheet content
                Column(modifier = Modifier.verticalScroll (rememberScrollState())){
                    Box(modifier = Modifier.fillMaxWidth()) {
                        dynamicScaffoldState.bottomSheetBoxContent.invoke(this)
                    }

                    if (isKeyboardOpen) {
                        Spacer(modifier = Modifier.padding(vertical = 100.dp))
                    }
                }
            }
        } else {
            dynamicScaffoldState.sheetDropdownDismissed()
        }

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
                    }
                )
            }
            composable(
                route = BottomBarScreen.Pages.route
            ) {
                PagesScreen(
                    onComposing = {
                        dynamicScaffoldState = it
                    }
                )
            }
            composable(
                route = BottomBarScreen.Groups.route
            ) {
                GroupsScreen(
                    onComposing = {
                        dynamicScaffoldState = it
                    }
                )
            }
            composable(
                route = BottomBarScreen.Settings.route
            ) {
                SettingsScreen(
                    onComposing = {
                        dynamicScaffoldState = it
                    }
                )
            }
        }

        // Background dim
        val interactionSource = remember { MutableInteractionSource() }
        Box(modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black.copy(alpha = animateFloatAsState(if (toggledFocusDim) 0.18f else 0f).value))
            .then(if (toggledFocusDim) Modifier.clickableWithoutRipple(interactionSource = interactionSource) {
                toggledFocusDim = false
            } else Modifier)
        )
    }
}

// Custom fab that allows for displaying extended content
@Composable
fun CustomFloatingActionButton(
    expandable: Boolean,
    onFabClick: () -> Unit,
    fabBoxContent: @Composable() (BoxScope.(Boolean) -> Unit),
    fabIcon: ImageVector,
    toggledFocusDim: Boolean,
    showBottomSheet: Boolean,
    onFabBoxContentClick: () -> Unit
) {

    var isExpanded by remember { mutableStateOf(false) }
    // Close the expanded fab if enableFocusDim changes to false
    if (!toggledFocusDim && isExpanded) {
        isExpanded = false
    }
    // Close the expanded fab if clicked and bottom sheet appears
    if (showBottomSheet) {
        isExpanded = false
        onFabBoxContentClick()
    }

    val fabSize = 68.dp
    val expandedFabWidth by animateDpAsState(
        targetValue = if (isExpanded) 200.dp else fabSize,
        animationSpec = spring(dampingRatio = 3f)
    )
    val expandedFabHeight by animateDpAsState(
        targetValue = if (isExpanded) 58.dp else fabSize,
        animationSpec = spring(dampingRatio = 4f)
    )

    Column {
        // ExpandedBox over the FAB
        Box(
            modifier = Modifier
                .offset(y = (25).dp)
                .size(
                    width = expandedFabWidth,
                    height = (animateDpAsState(
                        if (isExpanded) 200.dp else 0.dp,
                        animationSpec = spring(dampingRatio = 4f)
                    )).value
                )
                .background(
                    MaterialTheme.colorScheme.surfaceContainer,
                    shape = RoundedCornerShape(18.dp)
                )
        ) {

            // The content of the expanded box
            fabBoxContent(isExpanded)
        }

        FloatingActionButton(
            modifier = Modifier
                .width(expandedFabWidth)
                .height(expandedFabHeight),
            onClick = {
                onFabClick()
                if (expandable) {
                    isExpanded = !isExpanded
                }
            }
        ) {


            Icon(
                imageVector = fabIcon,
                contentDescription = null,
                modifier = Modifier
                    .size(26.dp)
                    .offset(
                        x = animateDpAsState(
                            if (isExpanded) -70.dp else 0.dp,
                            animationSpec = spring(dampingRatio = 3f)
                        ).value
                    )
            )

            Text(
                text = "Create Reminder",
                softWrap = false,
                modifier = Modifier
                    .offset(
                        x = animateDpAsState(
                            if (isExpanded) 10.dp else 50.dp,
                            animationSpec = spring(dampingRatio = 3f)
                        ).value
                    )
                    .alpha(
                        animateFloatAsState(
                            targetValue = if (isExpanded) 1f else 0f,
                            animationSpec = tween(
                                durationMillis = if (isExpanded) 400 else 100,
                                delayMillis = if (isExpanded) 100 else 0,
                                easing = EaseIn
                            )
                        ).value
                    )
            )
        }
    }
}

// A row for the Fab box
@Composable
fun RemindMeRow(icon: ImageVector, text: String, isExpanded: Boolean, onClick: () -> Unit, enterDelay: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
            .fillMaxWidth()
            .alpha(
                animateFloatAsState(
                    targetValue = if (isExpanded) 1f else 0f,
                    animationSpec = tween(
                        durationMillis = if (isExpanded) 400 else 100,
                        delayMillis = if (isExpanded) enterDelay else 0,
                        easing = EaseIn
                    )
                ).value
            )
    ) {
        Icon(
            modifier = Modifier.size(26.dp),
            imageVector = icon,
            contentDescription = null,
        )

        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            softWrap = false
        )
    }
}

// Custom top app bar that supports subtitle
@Composable
fun CustomTopAppBar(
    title: String,
    subtitle: String,
    showDivider: Boolean,
    actions: @Composable RowScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.padding(top = 16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.displaySmall
                )
                Text(
                    modifier = Modifier.alpha(0.6f),
                    text = subtitle,
                    style = MaterialTheme.typography.labelLarge
                )
            }

            // Place actions on the right side of the top app bar
            actions()
        }

        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier
                    .alpha(0.4f)
                    .padding(top = 10.dp)
            )
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
                    maxLines = 1
                )
            }

        }
    }
}

// Class to handle dynamic scaffold for each nav screen
data class DynamicScaffoldState(
    val toolbarTitle: String = "",
    val toolbarSubtitle: String = "",
    val toolbarActions: (@Composable RowScope.() -> Unit)? = null,
    val fabBoxContent: (@Composable BoxScope.(Boolean) -> Unit)? = null,
    val expandableFab: Boolean = false,
    val lazyListStateVisible: Boolean? = null,
    val lazyStaggeredGridStateVisible: Boolean? = null,
    val sheetDropdownDismissed: () -> Unit = {},
    val bottomSheetBoxContent: (@Composable BoxScope.() -> Unit) = {},
    val showBottomSheet: Boolean = false,
    val showBottomSheetFabClicked: () -> Unit = {}
)

// Fab button icon
private fun getFabIconForDestination(destination: NavDestination?): ImageVector {
    return when (destination?.route) {
        BottomBarScreen.Home.route -> Icons.Filled.NotificationAdd
        BottomBarScreen.Pages.route -> Icons.Filled.Edit
        BottomBarScreen.Groups.route -> Icons.Filled.CreateNewFolder
        else -> Icons.Filled.Add // Default icon
    }
}

// Add a fading edge to scrollable content
fun Modifier.fadingEdge(brush: Brush) = this
    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
    .drawWithContent {
        drawContent()
        drawRect(brush = brush, blendMode = BlendMode.DstIn)
    }


// Modifier to click without ripple effect
fun Modifier.clickableWithoutRipple(
    interactionSource: MutableInteractionSource,
    onClick: () -> Unit
) = this.then(
    Modifier.clickable(
        interactionSource = interactionSource,
        indication = null,
        onClick = { onClick() }
    )
)

// Format dates
fun formatLocalDateTime(localDateTime: LocalDateTime, pattern: String): String {
    val formatter = DateTimeFormatter.ofPattern(pattern)
    return localDateTime.format(formatter)
}