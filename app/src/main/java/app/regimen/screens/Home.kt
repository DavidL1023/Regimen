package app.regimen.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EventRepeat
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Keyboard
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.text.isDigitsOnly
import app.regimen.DynamicScaffoldState
import app.regimen.RemindMeRow
import app.regimen.data.Habit
import app.regimen.data.HabitDao
import app.regimen.data.Page
import app.regimen.data.PageDao
import app.regimen.data.RecurringReminder
import app.regimen.data.RecurringReminderDao
import app.regimen.data.SingleTimeReminder
import app.regimen.data.SingleTimeReminderDao
import app.regimen.fadingEdge
import app.regimen.formatLocalDateTime
import app.regimen.groupDao
import app.regimen.habitDao
import app.regimen.recurringReminderDao
import app.regimen.singleTimeReminderDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

// Used to filter by chip dates
lateinit var setSelectedChipIndex: (Int) -> Unit
lateinit var getSelectedChipIndex: () -> Int

@Composable
fun HomeScreen(
    onComposing: (DynamicScaffoldState) -> Unit
) {
    // The selected date to filter by
    var selectedChipIndex by remember { mutableIntStateOf(-1) }

    // Set the functions to manipulate selectedChipIndex
    setSelectedChipIndex = { value -> selectedChipIndex = value }
    getSelectedChipIndex = { selectedChipIndex }

    // Used to hide on scroll
    val lazyListState = rememberLazyListState()
    val hiddenOnScroll by remember(lazyListState) {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex == 0
        }
    }

    // Bottom sheet toggle and item clicked
    var showBottomSheet by remember { mutableStateOf(false) }
    var fabBoxItemClicked by remember { mutableStateOf("") }

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
                    onFabItemClick = { showBottomSheet = !showBottomSheet },
                    callback = { index -> fabBoxItemClicked = index }
                )
            },
            expandableFab = true,
            lazyListStateVisible = hiddenOnScroll,
            fabBoxContextBottomSheetVisible = showBottomSheet,
            fabBoxContextDropdownDismissed = { showBottomSheet = !showBottomSheet },
            bottomSheetBoxContent = {
                when (fabBoxItemClicked) {
                    "Habit" -> CreateHabit()
                    "Recurring" -> CreateRecurring()
                    "Single Time" -> CreateSingleTime()
                }
            }
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
fun HomeScreenFabBox(isExpanded: Boolean, onFabItemClick: () -> Unit, callback: (String) -> Unit) {
    Column {
        RemindMeRow(
            icon = Icons.Filled.SelfImprovement,
            text = "Habit",
            isExpanded = isExpanded,
            onClick = {
                onFabItemClick()
                callback("Habit")
            },
            enterDelay = 500
        )

        RemindMeRow(
            icon = Icons.Filled.EventRepeat,
            text = "Recurring",
            isExpanded = isExpanded,
            onClick = {
                onFabItemClick()
                callback("Recurring")
            },
            enterDelay = 350
        )

        RemindMeRow(
            icon = Icons.Filled.CalendarMonth,
            text = "Single Time",
            isExpanded = isExpanded,
            onClick = {
                onFabItemClick()
                callback("Single Time")
            },
            enterDelay = 200
        )
    }
}

// Column for the reminder cards
@Composable
fun LazyReminderColumn(lazyListState: LazyListState) {
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
        verticalArrangement = Arrangement.spacedBy(12.dp),
        content = {
            item {
                Spacer(modifier = Modifier.height(0.5.dp))
            }

            items(sortedReminders) { reminder ->
                val format: String = if(reminder.localDateTime.toLocalTime() == LocalTime.MIDNIGHT) {
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

                if (group != null) {
                    ReminderCard(
                        type = reminderType,
                        title = reminder.title,
                        timeDisplay = formatLocalDateTime(reminder.localDateTime, format),
                        groupDisplay = group.title
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    )
}

// A reminder card
@Composable
fun ReminderCard(type: String, title: String, timeDisplay: String, groupDisplay: String, displayGroup: Boolean = true) {

    Card(
        onClick = { /* Do something */ },
        modifier = Modifier
            .heightIn(min = 110.dp, max = 150.dp)
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = MaterialTheme.shapes.medium)
    ) {
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
                text = type,
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                modifier = Modifier
                    .padding(top = 6.dp, bottom = 8.dp),
                text = title,
                style = MaterialTheme.typography.titleLarge
            )

            Row (
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.CalendarMonth,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )

                Text(
                    text = timeDisplay, //16 Feb · 11:00 PM
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (displayGroup) {
                Spacer(modifier = Modifier.padding(6.dp))

                Row (
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier
                            .alpha(0.80f)
                            .width(16.dp),
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = null
                    )

                    Text(
                        modifier = Modifier.alpha(0.80f),
                        text = groupDisplay,
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
    val leftRightFade = Brush.horizontalGradient(0f to Color.Transparent, 0.03f to Color.Red, 0.97f to Color.Red, 1f to Color.Transparent)
    val selectedChipIndex = getSelectedChipIndex()

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
            val isSelected = index == selectedChipIndex
            val selectedIndex = if (isSelected) -1 else index
            VerticalChip(
                isSelected = isSelected,
                onClick = {
                    setSelectedChipIndex(selectedIndex)
                },
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
    val options = listOf("All", "Repeating", "Single Time")
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

// Converts string dates and time to localDateTimes
fun toLocalDateTime(date: String, time: String, specificTimeEnabled: Boolean): LocalDateTime {
    val formatter: DateTimeFormatter
    val dateTimeString: String
    val localDateTime: LocalDateTime
    if (specificTimeEnabled) {
        dateTimeString = "$date $time"
        formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a")
        localDateTime = LocalDateTime.parse(dateTimeString, formatter)
    }else {
        dateTimeString = date
        formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
        val localDate = LocalDate.parse(dateTimeString, formatter)
        val beginningOfDay = LocalTime.MIDNIGHT
        localDateTime = LocalDateTime.of(localDate, beginningOfDay)
    }

    return localDateTime
}

// Fab click content
@Composable
fun CreateHabit() {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("")}
    var time by remember { mutableStateOf("")}
    var groupId by remember { mutableIntStateOf(-1) }
    var specificTimeEnabled by remember { mutableStateOf(false) }
    var customPeriodEnabled by remember { mutableStateOf(false) }
    var recurringPeriod by remember { mutableStateOf("1") }
    var recurringDay by remember { mutableStateOf("Mondays") }

    Column (
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Explanation
        CreateTopExplanation(header = "New habit", subtitle = "Recurring reminder that also keeps track of streak and highest streak.")

        // Enter title and description
        CreateTitleAndDescription(
            title = title,
            description = description,
            setTitle = { title = it },
            setDescription = { description = it }
        )

        // Time, Date, and checkbox
        Column {
            // Specific time checkbox
            LabelledCheckBox(
                checked = specificTimeEnabled,
                onCheckedChange = { specificTimeEnabled = it },
                label = "Specific time"
            )

            // Date and time
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    CreateDatePickerDialog(date = date, setDate = { date = it })
                }
                Box(modifier = Modifier.weight(1f)) {
                    CreateTimePickerDialog(time = time, setTime = { time = it }, enabled = specificTimeEnabled)
                }
            }

        }

        // Recurring period
        CreateRecurringSelector(
            recurringPeriod = recurringPeriod,
            setRecurringPeriod = { recurringPeriod = it },
            recurringDay = recurringDay,
            setRecurringDay = { recurringDay = it },
            setCustomPeriodEnabled = { customPeriodEnabled = it }
        )

        // Select group
        CreateGroupSelector( setGroup = { groupId = it } )

        // Save button
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                val localDateTime = toLocalDateTime(date, time, specificTimeEnabled)

                CoroutineScope(Dispatchers.IO).launch {
                    habitDao.insert(
                        Habit(
                            title = title,
                            groupId = groupId,
                            specificTimeEnabled = specificTimeEnabled,
                            localDateTime = localDateTime,
                            description = description,
                            customProgressActive = 0.0f,
                            customProgressGoal = 0.0f,
                            customUnit = "",

                            customPeriodEnabled = customPeriodEnabled,
                            recurringPeriod = recurringPeriod.toInt(),
                            recurringDay = recurringDay,
                            paused = false,
                            streakActive = 0,
                            streakHighest = 0
                        )
                    )
                }

            }
        ) {
            Text(
                text = "Save",
            )
        }

        Spacer(modifier = Modifier.padding(vertical = 8.dp))
    }

}

@Composable
fun CreateRecurring() {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("")}
    var time by remember { mutableStateOf("")}
    var groupId by remember { mutableIntStateOf(-1) }
    var specificTimeEnabled by remember { mutableStateOf(false) }
    var customPeriodEnabled by remember { mutableStateOf(false) }
    var recurringPeriod by remember { mutableStateOf("1") }
    var recurringDay by remember { mutableStateOf("Mondays") }

    Column (
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Explanation
        CreateTopExplanation(header = "New recurring", subtitle = "Recurring reminder that will continue to reapply after completion, can be paused to continue at a later date.")

        // Enter title and description
        CreateTitleAndDescription(
            title = title,
            description = description,
            setTitle = { title = it },
            setDescription = { description = it }
        )

        // Time, Date, and checkbox
        Column {
            // Specific time checkbox
            LabelledCheckBox(
                checked = specificTimeEnabled,
                onCheckedChange = { specificTimeEnabled = it },
                label = "Specific time"
            )

            // Date and time
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    CreateDatePickerDialog(date = date, setDate = { date = it })
                }
                Box(modifier = Modifier.weight(1f)) {
                    CreateTimePickerDialog(time = time, setTime = { time = it }, enabled = specificTimeEnabled)
                }
            }

        }

        // Recurring period
        CreateRecurringSelector(
            recurringPeriod = recurringPeriod,
            setRecurringPeriod = { recurringPeriod = it },
            recurringDay = recurringDay,
            setRecurringDay = { recurringDay = it },
            setCustomPeriodEnabled = { customPeriodEnabled = it }
        )

        // Select group
        CreateGroupSelector( setGroup = { groupId = it } )

        // Save button
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                val localDateTime = toLocalDateTime(date, time, specificTimeEnabled)

                CoroutineScope(Dispatchers.IO).launch {
                    recurringReminderDao.insert(
                        RecurringReminder(
                            title = title,
                            groupId = groupId,
                            specificTimeEnabled = specificTimeEnabled,
                            localDateTime = localDateTime,
                            description = description,
                            customProgressActive = 0.0f,
                            customProgressGoal = 0.0f,
                            customUnit = "",

                            customPeriodEnabled = customPeriodEnabled,
                            recurringPeriod = recurringPeriod.toInt(),
                            recurringDay = recurringDay,
                            paused = false
                        )
                    )
                }

            }
        ) {
            Text(
                text = "Save",
            )
        }

        Spacer(modifier = Modifier.padding(vertical = 8.dp))
    }
}

@Composable
fun CreateSingleTime() {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var specificTimeEnabled by remember { mutableStateOf(false) }
    var groupId by remember { mutableIntStateOf(-1) }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }

    Column (
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Explanation
        CreateTopExplanation(header = "New reminder", subtitle = "Reminder that only happens once and will be deleted when complete.")

        // Enter title and description
        CreateTitleAndDescription(
            title = title,
            description = description,
            setTitle = { title = it },
            setDescription = { description = it }
        )

        // Time, Date, and checkbox
        Column {
            // Specific time checkbox
            LabelledCheckBox(
                checked = specificTimeEnabled,
                onCheckedChange = { specificTimeEnabled = it },
                label = "Specific time"
            )

            // Date and time
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    CreateDatePickerDialog(date = date, setDate = { date = it })
                }
                Box(modifier = Modifier.weight(1f)) {
                    CreateTimePickerDialog(time = time, setTime = { time = it }, enabled = specificTimeEnabled)
                }
            }

        }

        // Select group
        CreateGroupSelector( setGroup = { groupId = it } )

        // Save button
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                val localDateTime = toLocalDateTime(date, time, specificTimeEnabled)

                CoroutineScope(Dispatchers.IO).launch {
                    singleTimeReminderDao.insert(
                        SingleTimeReminder(
                            title = title,
                            groupId = groupId,
                            specificTimeEnabled = specificTimeEnabled,
                            localDateTime = localDateTime,
                            description = description,
                            customProgressActive = 0.0f,
                            customProgressGoal = 0.0f,
                            customUnit = ""
                        )
                    )
                }

            }
        ) {
            Text(
                text = "Save",
            )
        }

        Spacer(modifier = Modifier.padding(vertical = 12.dp))
    }
}

@Composable
fun CreateGroupSelector(setGroup: (Int) -> Unit) {
    val groupList = groupDao.getAllGroups().collectAsState(initial = emptyList())
    val selectedGroupId = remember { mutableIntStateOf(-1) }

    Column {
        Text(
            text = "Select group",
            style = MaterialTheme.typography.bodyMedium
        )

        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(groupList.value) { group ->
                val isSelected = group.id == selectedGroupId.intValue

                FilterChip(
                    selected = isSelected,
                    onClick = {
                        selectedGroupId.intValue = group.id
                        setGroup(selectedGroupId.intValue)
                    },
                    label = { Text(group.title) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = null
                        )
                    }
                )

            }
        }

    }
}

@Composable
fun CreateTopExplanation(header: String, subtitle: String) {
    Column {
        Text(
            text = header,
            style = MaterialTheme.typography.headlineLarge
        )
        Text(
            modifier = Modifier.alpha(0.6f),
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun CreateTitleAndDescription(
    title: String,
    description: String,
    setTitle: (String) -> Unit,
    setDescription: (String) -> Unit
) {
    // Enter title
    Column {
        Text(
            text = "Title",
            style = MaterialTheme.typography.bodyMedium
        )
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = title,
            onValueChange = { setTitle(it) },
            label = { Text("Enter title") },
            singleLine = true
        )
    }

    // Enter description
    Column {
        Text(
            text = "Description",
            style = MaterialTheme.typography.bodyMedium
        )
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = description,
            onValueChange = { setDescription(it) },
            label = { Text("Enter description") },
            minLines = 3,
            maxLines = 5
        )
    }
}

// Recurring area
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRecurringSelector(
    recurringPeriod: String,
    setRecurringPeriod: (String) -> Unit,
    recurringDay: String,
    setRecurringDay: (String) -> Unit,
    setCustomPeriodEnabled: (Boolean) -> Unit
) {
    var customRecurringEnabled by remember { mutableStateOf(false) }
    val daysOfWeek = listOf("Mondays", "Tuesdays", "Wednesdays", "Thursdays", "Fridays", "Saturdays", "Sundays")
    val selectedChipIndex = remember { mutableIntStateOf(daysOfWeek.indexOf(recurringDay)) }

    Column {
        Text(
            text = "Recurring period",
            style = MaterialTheme.typography.bodyMedium
        )

        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(daysOfWeek.size) { index ->
                val isSelected = index == selectedChipIndex.intValue

                FilterChip(
                    selected = isSelected,
                    onClick = {
                        selectedChipIndex.intValue = index
                        setRecurringDay(daysOfWeek[index])
                    },
                    label = { Text(daysOfWeek[index]) },
                    enabled = !customRecurringEnabled
                )
            }
        }

        LabelledCheckBox(
            checked = customRecurringEnabled,
            onCheckedChange = {
                customRecurringEnabled = it
                setCustomPeriodEnabled(it)
            },
            label = "Custom period"
        )

        OutlinedTextField(
            value = recurringPeriod,
            onValueChange = { if (it.isDigitsOnly()) setRecurringPeriod(it) },
            label = { Text("Days between reminder") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            enabled = customRecurringEnabled,
            singleLine = true
        )

    }

}

// Date picker
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDatePickerDialog(
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    val selectedDate = datePickerState.selectedDateMillis?.let {
        convertMillisToDate(it)
    } ?: ""

    DatePickerDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            OutlinedButton(onClick = {
                onDateSelected(selectedDate)
                onDismiss()
            }

            ) {
                Text(text = "OK")
            }
        },
        dismissButton = {
            Button(onClick = {
                onDismiss()
            }) {
                Text(text = "Cancel")
            }
        }
    ) {
        DatePicker(
            state = datePickerState
        )
    }
}
@Composable
fun CreateDatePickerDialog(
    date: String,
    setDate: (String) -> Unit,
    enabled: Boolean = true
) {
    var showDatePicker by remember {
        mutableStateOf(false)
    }

    ReadonlyTextField(
        value = date,
        onValueChange = { /* not used */ },
        onClick = { showDatePicker = true },
        label = { Text("Select date") },
        trailingIcon = {
            Icon(
                imageVector = Icons.Filled.CalendarMonth,
                contentDescription = null
            )
        },
        enabled = enabled
    )

    if (showDatePicker) {
        MyDatePickerDialog(
            onDateSelected = { setDate(it) },
            onDismiss = { showDatePicker = false }
        )
    }
}

// Time to string format
private fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.US)
    formatter.timeZone = TimeZone.getTimeZone("UTC") // Set the time zone to UTC
    return formatter.format(Date(millis))
}

// Time picker
@Composable
fun MyTimePickerDialog(
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    contentPicker: @Composable () -> Unit,
    contentInput: @Composable () -> Unit,
) {
    var pickerEnabled by remember {
        mutableStateOf(true)
    }
    Dialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        ),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surface
                )
                .animateContentSize()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = "Select time",
                    style = MaterialTheme.typography.labelMedium
                )
                if (pickerEnabled) {
                    contentPicker()
                } else {
                    contentInput()
                }
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    IconButton(onClick = { pickerEnabled = !pickerEnabled }) {
                        Icon(
                            imageVector = if(!pickerEnabled) Icons.Outlined.AccessTime else Icons.Outlined.Keyboard,
                            contentDescription = null
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Button(
                        onClick = onCancel
                    ) { Text("Cancel") }
                    Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                    OutlinedButton(
                        onClick = onConfirm
                    ) { Text("OK") }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTimePickerDialog (
    time: String,
    setTime: (String) -> Unit,
    enabled: Boolean = true
) {
    var showTimePicker by remember { mutableStateOf(false) }
    val state = rememberTimePickerState()
    val formatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }

    ReadonlyTextField(
        value = time,
        onValueChange = { /* not used */ },
        onClick = { showTimePicker = true },
        label = { Text("Select time") },
        trailingIcon = {
            Icon(
                imageVector = Icons.Filled.AccessTime,
                contentDescription = null
            )
        },
        enabled = enabled
    )

    if (showTimePicker) {
        MyTimePickerDialog(
            onCancel = { showTimePicker = false },
            onConfirm = {
                val cal = Calendar.getInstance()
                cal.set(Calendar.HOUR_OF_DAY, state.hour)
                cal.set(Calendar.MINUTE, state.minute)
                cal.isLenient = false
                setTime(formatter.format(cal.time))
                showTimePicker = false
            },
            contentPicker = {TimePicker(state = state)},
            contentInput = {TimeInput(state = state)}
        )
    }
}

// Text field that does not consume click so that you can have custom onCLick
@Composable
fun ReadonlyTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    trailingIcon: @Composable () -> Unit,
    enabled: Boolean = true
) {

    Box {
        OutlinedTextField(
            modifier = modifier,
            value = value,
            onValueChange = onValueChange,
            label = label,
            trailingIcon = trailingIcon,
            enabled = enabled
        )

        if (enabled) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .alpha(0f)
                    .clickable(onClick = onClick),
            )
        }
    }
}

// Checkbox with label
@Composable
fun LabelledCheckBox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit),
    label: String,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .clickable(
                indication = rememberRipple(color = MaterialTheme.colorScheme.primary),
                interactionSource = remember { MutableInteractionSource() },
                onClick = { onCheckedChange(!checked) }
            )
            .requiredHeight(ButtonDefaults.MinHeight)
            .padding(4.dp)
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = null
        )

        Spacer(Modifier.size(6.dp))

        Text(
            text = label,
        )
    }
}