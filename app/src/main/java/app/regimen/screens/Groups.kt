package app.regimen.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Filter
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import app.regimen.AppBarState

@Composable
fun GroupsScreen(
    onComposing: (AppBarState) -> Unit,
    navController: NavController
) {
    // Dynamic toolbar
    LaunchedEffect(key1 = true) {
        onComposing(
            AppBarState(
                title = "Groups",
                subTitle = "Organize your data.",
                actions = {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = null
                        )
                    }
                }
            )
        )
    }

    // Groups column
    Column (
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Select a group",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .padding(start = 24.dp)
        )

        // List of groups within lazy column
        GroupList()

    }
}

@Composable
fun GroupItem(name: String, selected: Boolean, onSelectedChange: () -> Unit) {
    ListItem(
        modifier = Modifier
            .clickable { onSelectedChange() }
            .padding(horizontal = 12.dp),
        headlineContent = { Text("Group $name") },
        trailingContent = {
            if (selected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                )
            }
        },
        leadingContent = {
            Icon(
                imageVector = Icons.Filled.Favorite,
                contentDescription = null,
            )
        }
    )
}

@Composable
fun GroupList() {
    var selectedItem by remember { mutableIntStateOf(0) }

    LazyColumn(
        modifier = Modifier
            .height(180.dp)
    ) {
        items(5) { index ->
            val isSelected = selectedItem == index
            GroupItem(
                name = index.toString(),
                selected = isSelected,
                onSelectedChange = {
                    selectedItem = if (isSelected) -1 else index
                }
            )
        }
    }
}
