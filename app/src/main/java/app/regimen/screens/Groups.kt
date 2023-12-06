package app.regimen.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.Left
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.Right
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.regimen.DynamicScaffoldState
import app.regimen.data.AppDatabase
import app.regimen.data.Group
import app.regimen.data.GroupDao
import app.regimen.data.Habit
import app.regimen.data.RecurringReminder
import app.regimen.data.SingleTimeReminder
import app.regimen.fadingEdge
import app.regimen.formatLocalDateTime
import app.regimen.groupDao
import app.regimen.habitDao
import app.regimen.pageDao
import app.regimen.recurringReminderDao
import app.regimen.singleTimeReminderDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.LocalTime

// Used to filter by group
lateinit var setSelectedGroupId: (Int) -> Unit
lateinit var getSelectedGroupId: () -> Int

// Used to know when lazy lists have first index visible
lateinit var setListFirstVisible: (Boolean) -> Unit
lateinit var setStaggeredListFirstVisible: (Boolean) -> Unit

@Composable
fun GroupsScreen(
    onComposing: (DynamicScaffoldState) -> Unit
) {
    // The selected group to filter by
    var selectedGroupId by remember { mutableIntStateOf(1) }

    // Set the functions to manipulate selectedGroupId
    setSelectedGroupId = { selectedGroupId = it }
    getSelectedGroupId = { selectedGroupId }

    // Bool to know if first index is visible for hiding fab
    var listFirstVisible by remember { mutableStateOf(true) }
    var staggeredListFirstVisible by remember { mutableStateOf(true) }

    // Set functions to manipulate first index booleans
    setListFirstVisible = { listFirstVisible = it }
    setStaggeredListFirstVisible = { staggeredListFirstVisible = it }

    // Dynamic toolbar
    onComposing(
        DynamicScaffoldState(
            toolbarTitle = "Groups",
            toolbarSubtitle = "Organize your data.",
            toolbarActions = {
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = null
                    )
                }
            },
            lazyListStateVisible = listFirstVisible,
            lazyStaggeredGridStateVisible = staggeredListFirstVisible,
            bottomSheetBoxContent = { CreateGroup() }
        )
    )

    // Groups column
    Column {

        // Tab selector (includes group list)
        GroupTabs()

    }
}

// The tabs for filtering
enum class GroupTabsEnum {
    Reminders,
    Pages
}

@Composable
fun GroupTabs() {
    var state by remember { mutableStateOf(GroupTabsEnum.Reminders) }
    val titles = GroupTabsEnum.values().toList()
    val icons = listOf(Icons.Default.CalendarMonth, Icons.Default.Description)

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TabRow(
            selectedTabIndex = state.ordinal,
            indicator = { tabPositions ->
                if (state.ordinal < tabPositions.size) {
                    TabRowDefaults.PrimaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[state.ordinal]),
                        shape = RoundedCornerShape(
                            topStart = 3.dp,
                            topEnd = 3.dp,
                            bottomEnd = 0.dp,
                            bottomStart = 0.dp,
                        ),
                        width = animateDpAsState(if (state == GroupTabsEnum.Reminders) 80.dp else 50.dp).value
                    )
                }
            },
            divider = { HorizontalDivider(
                modifier = Modifier
                    .alpha(0.8f)
                    .padding(horizontal = 24.dp)
            ) }
        ) {
            titles.forEachIndexed { index, title ->
                Tab(
                    modifier = Modifier.clip(RoundedCornerShape(24.dp)),
                    selected = state == title,
                    onClick = { state = title },
                    text = { Text(text = title.name) },
                    icon = {
                        Icon(
                            imageVector = icons[index],
                            contentDescription = null,
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    unselectedContentColor = MaterialTheme.colorScheme.secondary
                )
            }
        }

        // Display tab content with animation between them
        AnimatedTabContent(state)
    }
}

// Tab content with animation between them
@Composable
fun AnimatedTabContent(state: GroupTabsEnum) {
    AnimatedContent(
        targetState = state,
        content = { selectedTab ->
            when (selectedTab) {
                GroupTabsEnum.Reminders -> RemindersGroupTab()
                GroupTabsEnum.Pages -> PagesGroupTab()
            }
        },
        transitionSpec = {
            slideIntoContainer(
                animationSpec = tween(300),
                towards = when (state) {
                    GroupTabsEnum.Reminders -> Right
                    GroupTabsEnum.Pages -> Left
                }
            ).togetherWith(
                slideOutOfContainer(
                    animationSpec = tween(300),
                    towards = when (state) {
                        GroupTabsEnum.Reminders -> Right
                        GroupTabsEnum.Pages -> Left
                    }
                )
            )
        }
    )
}

// Return clicked boolean to enable / disable
@Composable
fun toggleableTextButton(): Boolean {
    var isClicked by remember { mutableStateOf(false) }

    TextButton(
        modifier = Modifier
            .padding(start = 12.dp),
        onClick = {
            isClicked = !isClicked

        }
    ) {
        Icon(
            imageVector = Icons.Default.FilterList,
            contentDescription = null,
        )
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

// Tab content for reminders
@Composable
fun RemindersGroupTab() {
    val lazyListState = rememberLazyListState()
    val listFirstVisible by remember(lazyListState) {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex == 0
        }
    }
    setListFirstVisible(listFirstVisible)
    setStaggeredListFirstVisible(false)

    val selectedGroupId = getSelectedGroupId()

    val singleRemindersState by singleTimeReminderDao.getAllSingleTimeReminders().collectAsState(initial = emptyList())
    val recurringRemindersState by recurringReminderDao.getAllRecurringReminders().collectAsState(initial = emptyList())
    val habitsState by habitDao.getAllHabits().collectAsState(initial = emptyList())

    val combinedReminders = (singleRemindersState + recurringRemindersState + habitsState)
    val sortedReminders = combinedReminders.sortedBy { it.localDateTime }

    LazyColumn(
        state = lazyListState,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        content = {

            item {
                // Display group list
                Column {
                    val isButtonClicked = toggleableTextButton()
                    ExpandableGroupList(isButtonClicked)
                }
            }

            items(sortedReminders) { reminder ->
                if (reminder.groupId == selectedGroupId) { // Only show if contains selected group
                    val format: String =
                        if (reminder.localDateTime.toLocalTime() == LocalTime.MIDNIGHT) {
                            "d MMM"
                        } else {
                            "d MMM · h:mm a"
                        }

                    val reminderType = when (reminder) {
                        is SingleTimeReminder -> "Single Time"
                        is RecurringReminder -> "Recurring"
                        is Habit -> "Habit"
                        else -> "Unknown"
                    }

                    ReminderCard(
                        type = reminderType,
                        title = reminder.title,
                        timeDisplay = formatLocalDateTime(reminder.localDateTime, format),
                        groupDisplay = "",
                        displayGroup = false
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    )
}

// Tab content for pages
@Composable
fun PagesGroupTab() {
    val lazyStaggeredGridState = rememberLazyStaggeredGridState()
    val staggeredListFirstVisible by remember(lazyStaggeredGridState) {
        derivedStateOf {
            lazyStaggeredGridState.firstVisibleItemIndex == 0
        }
    }
    setStaggeredListFirstVisible(staggeredListFirstVisible)
    setListFirstVisible(false)

    val selectedGroupId = getSelectedGroupId()

    val pagesState by pageDao.getAllPages().collectAsState(initial = emptyList())
    val sortedPages = pagesState.sortedBy { it.dateTimeModified }

    LazyVerticalStaggeredGrid(
        state = lazyStaggeredGridState,
        columns = StaggeredGridCells.Fixed(2),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalItemSpacing = 8.dp,
        content = {

            item(span = StaggeredGridItemSpan.FullLine) {
                // Display group list
                Column {
                    val isButtonClicked = toggleableTextButton()
                    ExpandableGroupList(isButtonClicked)
                }
            }

            items(sortedPages) { page ->
                if (page.groupId == selectedGroupId) { // Only show if contains selected group
                    val timeDisplay: String
                    val dateTimeModified = page.dateTimeModified
                    val currentTime = LocalDateTime.now()

                    timeDisplay =
                        if (dateTimeModified.toLocalDate() == currentTime.toLocalDate()) { // Same day
                            formatLocalDateTime(dateTimeModified, "'Today at' h:mm a")
                        } else if (dateTimeModified.year != currentTime.year) { // Different year
                            formatLocalDateTime(dateTimeModified, "MMM dd, yyyy")
                        } else { // Same year different day
                            formatLocalDateTime(dateTimeModified, "EEEE, MMM dd")
                        }

                    PageCard(
                        header = page.title,
                        body = page.body,
                        timeDisplay = timeDisplay,
                        groupDisplay = "",
                        displayGroup = false
                    )
                }
            }

            item(span = StaggeredGridItemSpan.FullLine) {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    )
}

// Item for horizontal list of group selection
@Composable
fun GroupItem(name: String, selected: Boolean, onSelectedChange: () -> Unit) {

    val textStyle = if (selected) {
        MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
    } else {
        MaterialTheme.typography.bodyLarge
    }

    Box(
        modifier = Modifier
            .size(animateDpAsState(if (selected) 130.dp else 120.dp).value)
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
                .padding(start = 26.dp, end = 26.dp, bottom = 16.dp)
        )

        Text(
            text = name,
            style = textStyle,
            modifier = Modifier
                .padding(12.dp)
                .align(Alignment.BottomCenter)
        )
    }
}

// Expandable group row
@Composable
fun ExpandableGroupList(isVisible: Boolean) {

    val selectedGroupId = getSelectedGroupId()
    val targetHeight = if (isVisible) 130.dp else 0.dp
    val animatedHeight by animateDpAsState(targetValue = targetHeight, animationSpec = spring(dampingRatio = 3f))

    val leftRightFade = Brush.horizontalGradient(0f to Color.Transparent, 0.01f to Color.Red, 0.99f to Color.Red, 1f to Color.Transparent)

    // Retrieve the list of groups from the DAO
    val groupsState by groupDao.getAllGroups().collectAsState(initial = emptyList())

    LazyRow(
        modifier = Modifier
            .height(animatedHeight)
            .fadingEdge(leftRightFade),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        item {
            Spacer(modifier = Modifier.width(8.dp))
        }

        items(groupsState) { group ->

            val isSelected = selectedGroupId == group.id
            GroupItem(
                name = group.title,
                selected = isSelected,
                onSelectedChange = {
                    if (isVisible) {
                        setSelectedGroupId( if (isSelected) -1 else group.id )
                    }
                }
            )
        }

        item {
            Spacer(modifier = Modifier.width(8.dp))
        }

    }
}

// Fab click content
@Composable
fun CreateGroup() {
    Column (
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        var title by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        var color by remember { mutableIntStateOf(0)}

        // Explanation
        CreateTopExplanation(header = "Create group", subtitle = "Groups are to organize your reminders and pages in one location.")

        // Title and description
        CreateTitleAndDescription(
            title = title,
            description = description,
            setTitle = { title = it },
            setDescription = { description = it}
        )

        // Color picker
        ColorPicker( setColor = { color = it } )

        // Save button
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    groupDao.insert(
                        Group(
                            title=title,
                            description=description,
                            color=color,
                            icon=0)
                    )
                }
            }
        ) {
            Text(
                text = "Save"
            )
        }

        Spacer(modifier = Modifier.padding(vertical = 12.dp))
    }
}

// Define an enum class for your colors
enum class CustomColor(val color: Color, val intValue: Int) { //TODO move this and image ints to another class
    Red(Color(0xFFC93C20), 0),
    Green(Color(0xFF57A639), 1),
    Blue(Color(0xFF3B83BD), 2),
    Yellow(Color(0xFFFFFF00), 3),
    Purple(Color(0xFF800080), 4),
    Cyan(Color(0xFF00FFFF), 5),
    Magenta(Color(0xFFFF00FF), 6),
    Orange(Color(0xFFFFA500), 7),
    Pink(Color(0xFFFFC0CB), 8),
    Brown(Color(0xFF8E402A), 9),
    Teal(Color(0xFF008080), 10),
    TerraBrown(Color(0xFF4E3B31), 11),
    Violet(Color(0xFF924E7D), 12),
    Olive(Color(0xFF808000), 13),
    SlateGray(Color(0xFF708090), 14);

    fun toComposeColor(): Color {
        return color
    }

}

@Composable
fun ColorPicker(setColor: (Int) -> Unit) {
    var selectedColor by remember { mutableStateOf(CustomColor.Red) }

    Column {
        Text(
            text = "Select Color",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 3.dp)
        )

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            itemsIndexed(CustomColor.values()) { index, customColor ->
                val composeColor = customColor.toComposeColor()
                val isSelected = selectedColor == customColor
                val alpha = animateFloatAsState(if (isSelected) 1f else 0.5f).value
                val roundedShape = animateDpAsState(if (isSelected) 20.dp else 10.dp).value

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(roundedShape))
                        .size(50.dp)
                        .background(
                            color = composeColor.copy(alpha = alpha),
                            shape = RoundedCornerShape(roundedShape)
                        )
                        .clickable {
                            selectedColor = customColor
                            setColor(customColor.intValue)
                        }
                )
            }
        }
    }
}
