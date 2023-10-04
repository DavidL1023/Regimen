package app.regimen.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EventRepeat
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import app.regimen.DynamicScaffoldState
import app.regimen.RemindMeRow
import app.regimen.fadingEdge

@Composable
fun HomeScreen(
    onComposing: (DynamicScaffoldState) -> Unit
) {
    // Used to hide on scroll
    val lazyListState = rememberLazyListState()
    val hiddenOnScroll by remember(lazyListState) {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex == 0
        }
    }

    // Bottom sheet toggle
    var showBottomSheet by remember { mutableStateOf(false) }

    // Dynamic toolbar
    onComposing(
        DynamicScaffoldState(
            toolbarTitle = "Home",
            toolbarSubtitle = "Manage your life.",
            toolbarActions = {
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = null
                    )
                }
            },
            fabBoxContent = { isExpanded ->
                HomeScreenFabBox(
                    isExpanded = isExpanded,
                    onFabItemClick = { showBottomSheet = !showBottomSheet }
                )
            },
            expandableFab = true,
            lazyListStateVisible = hiddenOnScroll,
            fabBoxContextBottomSheetVisible = showBottomSheet,
            fabBoxContextDropdownDismissed = { showBottomSheet = !showBottomSheet },
            bottomSheetBoxContent = { }
        )
    )

    // Home column
    Column {

        // Horizontal scroll for calendar filter
        AnimatedVisibility(
            visible = hiddenOnScroll,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            CalendarFilterChips()
        }

        // Filter by reminder type
        CategoryFilterSegmented()

        // Reminder cards scrollable column
        LazyReminderColumn(lazyListState)
    }

}

// Expandable box for when fab is clicked on home screen
@Composable
fun HomeScreenFabBox(isExpanded: Boolean, onFabItemClick: () -> Unit) {
    Column {
        RemindMeRow(
            icon = Icons.Filled.SelfImprovement,
            text = "Habit",
            isExpanded = isExpanded,
            onClick = { onFabItemClick() },
            enterDelay = 500
        )

        RemindMeRow(
            icon = Icons.Filled.EventRepeat,
            text = "Recurring",
            isExpanded = isExpanded,
            onClick = { onFabItemClick() },
            enterDelay = 350
        )

        RemindMeRow(
            icon = Icons.Filled.CalendarMonth,
            text = "Single Time",
            isExpanded = isExpanded,
            onClick = { onFabItemClick() },
            enterDelay = 200
        )
    }
}

// Column for the reminder cards
@Composable
fun LazyReminderColumn(lazyListState: LazyListState) {
    LazyColumn(
        state = lazyListState,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        content = {
            item {
                Spacer(modifier = Modifier.height(0.5.dp))
            }

            items(5) { index ->
                ReminderCard(true)
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    )
}

// A reminder card
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderCard(displayGroup: Boolean) {
    val cardHeight = if (displayGroup) 160.dp else 120.dp

    Card(
        onClick = { /* Do something */ },
        modifier = Modifier
            .fillMaxWidth()
            .height(cardHeight)
            .shadow(elevation = 4.dp, shape = MaterialTheme.shapes.medium)
    ) {
        Box(Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.padding(12.dp)
            )  {

                Text(
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            CircleShape
                        ) // Oval background
                        .padding(8.dp), // Padding for the oval background
                    text = "Type",
                    style = MaterialTheme.typography.labelMedium
                )

                Text(
                    modifier = Modifier
                        .padding(top = 6.dp, bottom = 8.dp),
                    text = "Reminder",
                    style = MaterialTheme.typography.titleLarge
                )

                Row (
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CalendarMonth,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.padding(horizontal = 2.dp))
                    Text(
                        text = "16 Feb · 11:00 PM",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                if (displayGroup) {
                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        modifier = Modifier.alpha(0.85f),
                        text = "Group",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}


// Row of calendar filter chips
@Composable
private fun CalendarFilterChips() {
    val selectedChipIndex = remember { mutableIntStateOf(-1) }
    val leftRightFade = Brush.horizontalGradient(0f to Color.Transparent, 0.03f to Color.Red, 0.97f to Color.Red, 1f to Color.Transparent)

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .fadingEdge(leftRightFade)
            .padding(bottom = 12.dp)
    ) {

        item {
            Spacer(modifier = Modifier.width(10.dp))
        }

        items(14) { index ->
            val isSelected = index == selectedChipIndex.intValue
            val selectedIndex = if (isSelected) -1 else index
            VerticalChip(
                isSelected = isSelected,
                onClick = { selectedChipIndex.intValue = selectedIndex },
                topText = "Thu",
                bottomText = "${index + 1}"
            )
        }

        item {
            Spacer(modifier = Modifier.width(10.dp))
        }
    }
}

// A calendar chip
@Composable
private fun VerticalChip(
    isSelected: Boolean,
    onClick: () -> Unit,
    topText: String,
    bottomText: String
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp)) //allows ripple to match shape
            .clickable(
                onClick = onClick
            )
            .background(
                color = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(14.dp)
            )
            .padding(9.dp)
            .height(animateDpAsState(if (isSelected) 62.dp else 52.dp).value),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = topText,
                modifier = Modifier
                    .padding(horizontal = 2.dp)
                    .alpha(if (isSelected) 1f else 0.65f),
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = bottomText,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

// Segmented filter button for categories
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryFilterSegmented() {
    var selectedIndex by remember { mutableIntStateOf(0) }
    val options = listOf("All", "Recurring", "Single Time")
    val icons = listOf(
        null,
        Icons.Default.Cached,
        Icons.Default.Today
    )

    SingleChoiceSegmentedButtonRow (
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
    ) {
        options.forEachIndexed { index, label ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                onClick = { selectedIndex = index },
                selected = index == selectedIndex,
                icon = {
                    SegmentedButtonDefaults.Icon(
                        active = index == selectedIndex,
                        inactiveContent = {
                            icons[index]?.let {
                                Icon(
                                    imageVector = it,
                                    contentDescription = null,
                                    Modifier.size(SegmentedButtonDefaults.IconSize)
                                )
                            }
                        }
                    )

                },
            ) {
                Text(text = label)
            }
        }
    }

}

// Fab click content
@Composable
fun CreateHabit() {

}

@Composable
fun CreateRecurring() {

}

@Composable
fun CreateSingleTime() {

}
