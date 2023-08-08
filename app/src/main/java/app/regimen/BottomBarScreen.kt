package app.regimen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomBarScreen(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val filledIcon: ImageVector
) {
    object Home : BottomBarScreen(
        route = "home",
        title = "Home",
        icon = Icons.Outlined.Home,
        filledIcon = Icons.Filled.Home
    )

    object Pages : BottomBarScreen(
        route = "pages",
        title = "Pages",
        icon = Icons.Outlined.Description,
        filledIcon = Icons.Filled.Description
    )

    object Groups : BottomBarScreen(
        route = "groups",
        title = "Groups",
        icon = Icons.Outlined.Folder,
        filledIcon = Icons.Filled.Folder
    )

    object Settings : BottomBarScreen(
        route = "settings",
        title = "Settings",
        icon = Icons.Outlined.Settings,
        filledIcon = Icons.Filled.Settings
    )
}