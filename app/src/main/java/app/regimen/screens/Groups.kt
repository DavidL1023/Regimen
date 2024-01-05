package app.regimen.screens

import android.graphics.drawable.Icon
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.Left
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.Right
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.regimen.ColorsEnum
import app.regimen.DynamicScaffoldState
import app.regimen.IconsEnum
import app.regimen.NoGroups
import app.regimen.NoPages
import app.regimen.data.Group
import app.regimen.data.Habit
import app.regimen.data.Page
import app.regimen.data.RecurringReminder
import app.regimen.data.Reminder
import app.regimen.data.SingleTimeReminder
import app.regimen.fadingEdge
import app.regimen.formatLocalDateTime
import app.regimen.groupDao
import app.regimen.habitDao
import app.regimen.pageDao
import app.regimen.recurringReminderDao
import app.regimen.singleTimeReminderDao
import app.regimen.validateGroupSelection
import app.regimen.validateTitleAndDescription
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

// Used to filter by group
lateinit var setSelectedGroupId: (Int) -> Unit
lateinit var getSelectedGroupId: () -> Int

// Used to know when lazy lists have first index visible
lateinit var setListFirstVisible: (Boolean) -> Unit
lateinit var setStaggeredListFirstVisible: (Boolean) -> Unit

// Boolean for sheet visibility
lateinit var setSheetVisibilityGroups: (Boolean) -> Unit

// Used to edit the content displayed when bottom sheet is visible
object SheetContentGroups {
    var sheetContent: @Composable () -> Unit = { CreateGroup() }
}

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

    // Show sheet
    var sheetVisibility by remember { mutableStateOf(false) }

    // Set functions to modify show sheet
    setSheetVisibilityGroups = { sheetVisibility = it }

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
            bottomSheetBoxContent = { SheetContentGroups.sheetContent() },
            showBottomSheet = sheetVisibility,
            sheetDropdownDismissed = { setSheetVisibilityGroups(false) },
            showBottomSheetFabClicked = {
                SheetContentGroups.sheetContent = { CreateGroup() }
                sheetVisibility = true
            }
        )
    )

    // Groups column
    Column {

        // Group main content
        val groupList by groupDao.getAllGroups().collectAsState(initial = emptyList())
        if (groupList.isEmpty()) {
            NoGroups()
        } else {
            GroupTabs()
        }

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
        onClick = { isClicked = !isClicked }
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
    val sortedReminders = combinedReminders
        .sortedBy { it.localDateTime }

    LazyColumn(
        state = lazyListState,
        modifier = Modifier.fillMaxWidth()
    ){

        item {
            // Display group list
            Column {
                val isButtonClicked = toggleableTextButton()
                ExpandableGroupList(isButtonClicked)

                Spacer(modifier = Modifier.height(14.dp))
            }
        }

        items(sortedReminders) { reminder ->
            if (reminder.groupId == selectedGroupId) { // Filtering sorted reminders causes crash so do this instead?
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

                val group = groupDao.getGroup(reminder.groupId).collectAsState(null).value

                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    ReminderCard(
                        type = reminderType,
                        title = reminder.title,
                        timeDisplay = formatLocalDateTime(reminder.localDateTime, format),
                        displayGroup = false,
                        onClick = {
                            if (group != null) {
                                reminderOnClickViewGroup(reminder, group)
                            }
                        },
                        onLongPress = {
                            when (reminder) {
                                is Habit -> {
                                    habitOnClickEditGroup(reminder)
                                }

                                is RecurringReminder -> {
                                    recurringOnClickEditGroup(reminder)
                                }

                                is SingleTimeReminder -> {
                                    singleTimeOnClickEditGroup(reminder)
                                }
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
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
    val filteredSortedPages = pagesState
        .filter { it.groupId == selectedGroupId }
        .sortedBy { it.dateTimeModified }

    LazyVerticalStaggeredGrid(
        state = lazyStaggeredGridState,
        columns = StaggeredGridCells.Fixed(2),
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalItemSpacing = 8.dp
    ) {

        item(span = StaggeredGridItemSpan.FullLine) {
            // Display group list
            Column {
                val isButtonClicked = toggleableTextButton()
                ExpandableGroupList(isButtonClicked)

                Spacer(modifier = Modifier.height(6.dp))
            }
        }

        itemsIndexed(filteredSortedPages) { index, page ->
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

            val group = groupDao.getGroup(page.groupId).collectAsState(null).value

            Box(modifier = Modifier
                .padding( // Add padding depending on the side its on
                    start = if (index % 2 == 0) 16.dp else 0.dp,
                    end = if (index % 2 != 0) 16.dp else 0.dp
                )) {
                PageCard(
                    header = page.title,
                    body = page.body,
                    timeDisplay = timeDisplay,
                    displayGroup = false,
                    onClick = {
                        if (group != null) {
                            pageOnClickViewGroup(page, group)
                        }
                    },
                    onLongPress = { pageOnClickEditGroup(page) }
                )
            }

        }


        item(span = StaggeredGridItemSpan.FullLine) {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

}

// Item for horizontal list of group selection
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GroupItem(name: String, iconId: Int, colorId: Int, selected: Boolean,
              onSelectedChange: () -> Unit, onLongPress: () -> Unit
) {

    val textStyle = if (selected) {
        MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
    } else {
        MaterialTheme.typography.bodyMedium
    }

    Box(
        modifier = Modifier
            .size(animateDpAsState(if (selected) 150.dp else 140.dp).value)
            .clip(RoundedCornerShape(16.dp)) // allows ripple to match shape
            .background(
                color = if (selected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(16.dp)
            )
            .combinedClickable(
                onClick = { onSelectedChange() },
                onLongClick = { onLongPress() }
            )
    ) {
        val imageVector = IconsEnum.iconFromIntValue(iconId)
        val color = ColorsEnum.colorFromIntValue(colorId)

        if (imageVector != null && color != null) {
            Icon(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 26.dp, end = 26.dp, bottom = 16.dp),
                imageVector = imageVector,
                tint = color,
                contentDescription = null
            )
        }

        Text(
            text = name,
            style = textStyle,
            modifier = Modifier
                .padding(10.dp)
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

    val leftRightFade = Brush.horizontalGradient(0f to Color.Transparent, 0.02f to Color.Red, 0.98f to Color.Red, 1f to Color.Transparent)

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
                iconId = group.icon,
                colorId = group.color,
                selected = isSelected,
                onSelectedChange = {
                    if (isVisible) {
                        setSelectedGroupId( if (isSelected) -1 else group.id )
                    }
                },
                onLongPress = {
                    SheetContentGroups.sheetContent = {
                        CreateGroup(setTitle = group.title, setDescription = group.description,
                            setColor = group.color, setIcon = group.icon, updateMode = true,
                            updateModeId = group.id)
                    }

                    setSheetVisibilityGroups(true)
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
fun CreateGroup(setTitle: String = "", setDescription: String = "", setColor: Int = 0,
                setIcon: Int = 0, updateMode: Boolean = false, updateModeId: Int = -1) {
    Column (
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val context = LocalContext.current

        var title by remember { mutableStateOf(setTitle) }
        var description by remember { mutableStateOf(setDescription) }
        var color by remember { mutableIntStateOf(setColor)}
        var icon by remember { mutableIntStateOf(setIcon) }

        val maxTitleChar = 100
        val maxDescriptionChar = 10000

        // Explanation
        CreateTopExplanation(header = if(updateMode) "Edit group" else "Create group",
            subtitle = "Groups are to organize your reminders and pages in one location.")

        // Title and description
        CreateTitleAndDescription(
            title = title,
            description = description,
            setTitle = { title = it },
            setDescription = { description = it}
        )

        // Color picker
        ColorPicker( color = color, setColor = { color = it } )

        // Icon picker
        IconPicker(icon = icon, setIcon = { icon = it })

        // Save button
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                // Validate data
                val errorCodeOne = validateTitleAndDescription(title, description, maxTitleChar, maxDescriptionChar,
                    context = context)

                if (errorCodeOne==0) {
                    CoroutineScope(Dispatchers.IO).launch {
                        if (updateMode) {
                            val updatedGroup = Group(
                                id = updateModeId,
                                title = title,
                                description = description,
                                color = color,
                                icon = icon
                            )

                            groupDao.update(updatedGroup)
                        } else {
                            groupDao.insert(
                                Group(
                                    title = title,
                                    description = description,
                                    color = color,
                                    icon = icon
                                )
                            )
                        }

                    }
                    setSheetVisibilityGroups(false)
                }
            }
        ) {
            Text(
                text = if(updateMode) "Save" else "Create"
            )
        }

        // Delete button
        if (updateMode) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    modifier = Modifier.width(200.dp),
                    onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            val groupDeleteById = Group(id = updateModeId)

                            groupDao.delete(groupDeleteById)
                        }
                        // TODO delete all associated pages and reminders
                        setSheetVisibilityGroups(false)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(text = "Delete")
                }
            }
        }

        Spacer(modifier = Modifier.padding(vertical = 12.dp))
    }
}

@Composable
fun ColorPicker(color: Int, setColor: (Int) -> Unit) {
    Column {
        Text(
            text = "Select Color",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 3.dp)
        )

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(ColorsEnum.values()) { customColor ->
                val isSelected = color == customColor.intValue
                val alpha = animateFloatAsState(if (isSelected) 1f else 0.4f).value
                val roundedShape = animateDpAsState(if (isSelected) 50.dp else 10.dp).value

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(roundedShape))
                        .size(50.dp)
                        .background(
                            color = customColor
                                .toComposeColor()
                                .copy(alpha = alpha),
                            shape = RoundedCornerShape(roundedShape)
                        )
                        .clickable {
                            setColor(customColor.intValue)
                        }
                )
            }
        }
    }
}

@Composable
fun IconPicker(icon: Int, setIcon: (Int) -> Unit) {
    Column {
        Text(
            text = "Select Icon",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 3.dp)
        )

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(IconsEnum.values()) { customIcon ->
                val isSelected = icon == customIcon.intValue
                val alpha = animateFloatAsState(if (isSelected) 1f else 0.4f).value
                val roundedShape = animateDpAsState(if (isSelected) 50.dp else 10.dp).value

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(roundedShape))
                        .size(50.dp)
                        .background(
                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = alpha),
                            shape = RoundedCornerShape(roundedShape)
                        )
                        .clickable {
                            setIcon(customIcon.intValue)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        modifier = Modifier
                            .size(35.dp)
                            .alpha(alpha),
                        imageVector = customIcon.toComposeIcon(),
                        contentDescription = null
                    )
                }
            }
        }
    }
}

fun reminderOnClickViewGroup(reminder: Reminder, group: Group) {
    val type: String = when (reminder) {
        is Habit -> { "Habit" }
        is RecurringReminder -> { "Recurring" }
        is SingleTimeReminder -> { "Single Time" }

        else -> {"Unknown"}
    }

    SheetContentGroups.sheetContent = {
        ViewReminder(
            type = type,
            title = reminder.title,
            description = reminder.description,
            localDateTime = reminder.localDateTime,
            groupTitle = group.title,
            groupIconId = group.icon,
            groupColorId = group.color
        )
    }

    setSheetVisibilityGroups(true)
}

fun habitOnClickEditGroup(habit: Habit) {
    val dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
    val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")

    val date = habit.localDateTime.format(dateFormatter)
    val time = habit.localDateTime.format(timeFormatter)

    SheetContentGroups.sheetContent = {
        CreateHabit(
            setTitle = habit.title,
            setDescription = habit.description,
            setDate = date,
            setTime = time,
            setGroupId = habit.groupId,
            setSpecificTimeEnabled = habit.specificTimeEnabled,
            setCustomPeriodEnabled = habit.customPeriodEnabled,
            setRecurringPeriod = habit.recurringPeriod.toString(),
            setRecurringDay = habit.recurringDay,
            updateMode = true,
            updateModeId = habit.id
        )
    }

    setSheetVisibilityGroups(true)
}

fun recurringOnClickEditGroup(recurringReminder: RecurringReminder) {
    val dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
    val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")

    val date = recurringReminder.localDateTime.format(dateFormatter)
    val time = recurringReminder.localDateTime.format(timeFormatter)

    SheetContentGroups.sheetContent = {
        CreateRecurring(
            setTitle = recurringReminder.title,
            setDescription = recurringReminder.description,
            setDate = date,
            setTime = time,
            setGroupId = recurringReminder.groupId,
            setSpecificTimeEnabled = recurringReminder.specificTimeEnabled,
            setCustomPeriodEnabled = recurringReminder.customPeriodEnabled,
            setRecurringPeriod = recurringReminder.recurringPeriod.toString(),
            setRecurringDay = recurringReminder.recurringDay,
            updateMode = true,
            updateModeId = recurringReminder.id
        )
    }

    setSheetVisibilityGroups(true)
}

fun singleTimeOnClickEditGroup(singleTimeReminder: SingleTimeReminder) {
    val dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
    val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")

    val date = singleTimeReminder.localDateTime.format(dateFormatter)
    val time = singleTimeReminder.localDateTime.format(timeFormatter)

    SheetContentGroups.sheetContent = {
        CreateSingleTime(
            setTitle = singleTimeReminder.title,
            setDescription = singleTimeReminder.description,
            setDate = date,
            setTime = time,
            setGroupId = singleTimeReminder.groupId,
            setSpecificTimeEnabled = singleTimeReminder.specificTimeEnabled,
            updateMode = true,
            updateModeId = singleTimeReminder.id
        )
    }

    setSheetVisibilityGroups(true)
}

fun pageOnClickViewGroup(page: Page, group: Group) {
    SheetContentGroups.sheetContent = {
        ViewPage(
            title = page.title,
            description = page.body,
            localDateTime = page.dateTimeModified,
            groupTitle = group.title,
            groupIconId = group.icon,
            groupColorId = group.color
        )
    }

    setSheetVisibilityGroups(true)
}

fun pageOnClickEditGroup(page: Page) {
    SheetContentGroups.sheetContent = {
        CreatePage(
            setTitle = page.title,
            setDescription = page.body,
            setGroupId = page.groupId,
            updateMode = true,
            updateModeId = page.id
        )
    }

    setSheetVisibilityGroups(true)
}