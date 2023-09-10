package app.regimen.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HeartBroken
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import app.regimen.DynamicScaffoldState
import app.regimen.fadingEdge

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
        Column{
            val isButtonClicked  = toggleableTextButton()
            ExpandableGroupList(isButtonClicked)
        }

        // Display tab content
        if (state == 0) {
            RemindersGroupTab()
        } else {
            PagesGroupTab()
        }
    }
}

// Return clicked boolean to enable / disable
@Composable
fun toggleableTextButton() : Boolean {
    var isClicked by remember { mutableStateOf(false) }

    TextButton(
        modifier = Modifier
            .padding(start = 8.dp),
        onClick = {
            isClicked = !isClicked

        }
    ) {
        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        Text(
            text = "Select Group",
            style = MaterialTheme.typography.titleSmall
        )
        Icon(
            modifier = Modifier
                .rotate(animateFloatAsState(if (isClicked) 180f else 0f).value)
                .size(ButtonDefaults.IconSize),
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = null,
        )
    }

    return isClicked
}


@Composable
fun RemindersGroupTab() {
    LazyColumn(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        content = {

            items(5) { index ->
                ReminderCard(false)
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    )
}

@Composable
fun PagesGroupTab() {
    LazyColumn(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        content = {

            items(5) { index ->
                PageCard(false)
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    )
}

@Composable
fun GroupItem(name: String, selected: Boolean, onSelectedChange: () -> Unit) {

    val textStyle = if (selected) {
        MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
    } else {
        MaterialTheme.typography.bodyLarge
    }

    Box(
        modifier = Modifier
            .size(140.dp)
            .clip(RoundedCornerShape(16.dp)) // allows ripple to match shape
            .clickable { onSelectedChange() }
            .background(
                color = if (selected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp)
        )

        Text(
            text = "Group $name",
            style = textStyle,
            modifier = Modifier
                .padding(12.dp)
                .align(Alignment.BottomCenter)
        )
    }
}



@Composable
fun ExpandableGroupList(isVisible: Boolean) {
    var selectedItem by remember { mutableIntStateOf(0) }

    val targetHeight = if (isVisible) 140.dp else 0.dp
    val animatedHeight by animateDpAsState(targetValue = targetHeight)

    val leftRightFade = Brush.horizontalGradient(0f to Color.Transparent, 0.03f to Color.Red, 0.97f to Color.Red, 1f to Color.Transparent)

    LazyRow(
        modifier = Modifier
            .height(animatedHeight)
            .fadingEdge(leftRightFade),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        item {
            Spacer(modifier = Modifier.width(16.dp))
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

        item {
            Spacer(modifier = Modifier.width(16.dp))
        }

    }
}
