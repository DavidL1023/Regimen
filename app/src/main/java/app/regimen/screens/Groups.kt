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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Filter
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import app.regimen.DynamicScaffoldState

@Composable
fun GroupsScreen(
    onComposing: (DynamicScaffoldState) -> Unit,
    navController: NavController
) {
    // Dynamic toolbar
    LaunchedEffect(key1 = true) {
        onComposing(
            DynamicScaffoldState(
                toolbarActions = {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = null
                        )
                    }
                },
                fabOnClick = {

                }
            )
        )
    }

    // Groups column
    Column (
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // Tab selector (includes group list)
        GroupTab()

    }
}

@Composable
fun GroupTab () {
    var state by remember { mutableIntStateOf(0) }
    val titles = listOf("Reminders", "Pages")
    val icons = listOf(Icons.Default.CalendarMonth, Icons.Default.Description)

    Column (
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TabRow(
            selectedTabIndex = state,
            indicator = { tabPositions ->
                if (state < tabPositions.size) {
                    TabRowDefaults.PrimaryIndicator(
                        modifier = Modifier
                            .tabIndicatorOffset(tabPositions[state]),
                        shape = RoundedCornerShape(
                            topStart = 3.dp,
                            topEnd = 3.dp,
                            bottomEnd = 0.dp,
                            bottomStart = 0.dp,
                        ),
                    )
                }
            }
        ) {
            titles.forEachIndexed { index, title ->
                Tab(
                    selected = state == index,
                    onClick = { state = index },
                    text = { Text(
                        text = title,
                        color = if (state == index)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.secondary
                        ) },
                    icon = { Icon(
                        imageVector = icons[index],
                        contentDescription = null,
                        tint = if (state == index)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(22.dp)
                    ) }
                )
            }
        }

        // Display group list
        GroupList()

        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = "Text tab ${state + 1} selected",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun GroupItem(name: String, selected: Boolean, onSelectedChange: () -> Unit) {
    val textStyle = if (selected) {
        MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
    } else {
        MaterialTheme.typography.bodyLarge
    }

    ListItem(
        modifier = Modifier
            .clickable { onSelectedChange() }
            .padding(horizontal = 12.dp),
        headlineContent = { Text("Group $name", style = textStyle) },
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
            .height(170.dp)
    ) {
        item {
            Text(
                text = "Select group:",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier
                    .padding(start = 24.dp)
            )
        }

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
