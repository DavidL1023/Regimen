package app.regimen.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EventRepeat
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.Whatshot
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
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
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
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.text.isDigitsOnly
import app.regimen.ColorsEnum
import app.regimen.DynamicScaffoldState
import app.regimen.IconsEnum
import app.regimen.NoReminders
import app.regimen.RemindMeRow
import app.regimen.ReminderForList
import app.regimen.data.Habit
import app.regimen.data.RecurringReminder
import app.regimen.data.Reminder
import app.regimen.data.SingleTimeReminder
import app.regimen.fadingEdge
import app.regimen.formatLocalDateTime
import app.regimen.groupDao
import app.regimen.habitDao
import app.regimen.raiseSheet
import app.regimen.recurringReminderDao
import app.regimen.setSheetVisibility
import app.regimen.shortenText
import app.regimen.singleTimeReminderDao
import app.regimen.validateCustomPeriod
import app.regimen.validateGroupSelection
import app.regimen.validateTimeAndDate
import app.regimen.validateTitleAndDescription
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

// Used to filter by chip dates
lateinit var setSelectedChipDate: (LocalDate?) -> Unit
lateinit var getSelectedChipDate: () -> LocalDate?

lateinit var setSelectedChipIndex: (Int) -> Unit
lateinit var getSelectedChipIndex: () -> Int

// Used to filter by types
lateinit var setSelectedSegmentText: (String) -> Unit
lateinit var getSelectedSegmentText: () -> String

@Composable
fun HomeScreen(
    onComposing: (DynamicScaffoldState) -> Unit
) {

    // The selected date / index to filter by
    var selectedChipDate by remember { mutableStateOf<LocalDate?>(null) }
    var selectedChipIndex by remember { mutableIntStateOf(-1) }

    // Set the functions to manipulate selectedChipIndex and date
    setSelectedChipDate = { selectedChipDate = it }
    getSelectedChipDate = { selectedChipDate }

    setSelectedChipIndex = { selectedChipIndex = it }
    getSelectedChipIndex = { selectedChipIndex }

    // Selected segment to filter by
    var selectedSegmentText by remember { mutableStateOf("All") }

    // Set the functions to manipulate selected segment
    setSelectedSegmentText = { selectedSegmentText = it }
    getSelectedSegmentText = { selectedSegmentText }

    // Used to hide on scroll
    val lazyListState = rememberLazyListState()
    val listFirstVisible by remember(lazyListState) {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex == 0
        }
    }

    // Dynamic toolbar
    onComposing(
        DynamicScaffoldState(
            toolbarTitle = "Home",
            toolbarSubtitle = "Manage your life.",
            toolbarActions = {
                // If you want toolbar action
            },
            fabBoxContent = { isExpanded ->
                HomeScreenFabBox(
                    isExpanded = isExpanded
                )
            },
            expandableFab = true,
            lazyListStateVisible = listFirstVisible
        )
    )

    // Home column
    Column {

        // Horizontal scroll for calendar filter
        AnimatedVisibility(
            visible = listFirstVisible
        ) {
            CalendarFilterChips()
        }

        // Filter by reminder type
        CategoryFilterSegmented()

        // Home main content of reminders
        val singleRemindersState by singleTimeReminderDao.getAllSingleTimeReminders().collectAsState(initial = emptyList())
        val recurringRemindersState by recurringReminderDao.getAllRecurringReminders().collectAsState(initial = emptyList())
        val habitsState by habitDao.getAllHabits().collectAsState(initial = emptyList())

        val combinedReminders = (singleRemindersState + recurringRemindersState + habitsState)

        if (combinedReminders.isEmpty()) {
            NoReminders()
        } else {
            LazyReminderColumn(lazyListState, combinedReminders)
        }

    }

}

// Expandable box for when fab is clicked on home screen
@Composable
fun HomeScreenFabBox(isExpanded: Boolean) {
    Column {
        RemindMeRow(
            icon = Icons.Filled.SelfImprovement,
            text = "Habit",
            isExpanded = isExpanded,
            onClick = {
                raiseSheet {
                    CreateHabit()
                }
            },
            enterDelay = 500
        )

        RemindMeRow(
            icon = Icons.Filled.EventRepeat,
            text = "Recurring",
            isExpanded = isExpanded,
            onClick = {
                raiseSheet {
                    CreateRecurring()
                }
            },
            enterDelay = 350
        )

        RemindMeRow(
            icon = Icons.Filled.Today,
            text = "Single Time",
            isExpanded = isExpanded,
            onClick = {
                raiseSheet {
                    CreateSingleTime()
                }
            },
            enterDelay = 200
        )
    }
}

// Get next occurrence of day depending on day as string format
fun getNextOccurrenceOfDay(localDateTime: LocalDateTime, dayString: String): LocalDateTime {
    val dayOfWeek = when (dayString) {
        "Mondays" -> DayOfWeek.MONDAY
        "Tuesdays" -> DayOfWeek.TUESDAY
        "Wednesdays" -> DayOfWeek.WEDNESDAY
        "Thursdays" -> DayOfWeek.THURSDAY
        "Fridays" -> DayOfWeek.FRIDAY
        "Saturdays" -> DayOfWeek.SATURDAY
        "Sundays" -> DayOfWeek.SUNDAY
        else -> throw IllegalArgumentException("Invalid day of week")
    }
    return localDateTime.with(TemporalAdjusters.next(dayOfWeek))
}

// Always generates unique key from reminders which may share id numbers in Room
fun getUniqueKey(reminder: Reminder): String {
    val key = when (reminder) {
        is SingleTimeReminder -> "${reminder.id}_SingleTimeReminder"
        is RecurringReminder -> "${reminder.id}_RecurringReminder"
        is Habit -> "${reminder.id}_Habit"
        else -> "Unknown_${reminder.id}"
    }

    return key
}

// Column for the reminder cards
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyReminderColumn(lazyListState: LazyListState, combinedReminders: List<Reminder>) {
    val sortedFilteredReminders = combinedReminders
        .filter { reminder ->
            // Determine the reminder type
            val reminderType = when (reminder) {
                is SingleTimeReminder -> "Single Time"
                is RecurringReminder -> "Recurring"
                is Habit -> "Habit"
                else -> "Unknown"
            }

            // Get the reminder date
            val reminderDate = reminder.localDateTime.toLocalDate()

            // Check if it passes the segment filter
            val passesSegmentFilter = when (getSelectedSegmentText()) {
                "All" -> true
                "Repeating" -> reminderType == "Habit" || reminderType == "Recurring"
                "Single Time" -> reminderType == "Single Time"
                else -> true
            }

            // Check if it passes the chip date filter
            val chipDate = getSelectedChipDate()
            val passesChipDateFilter = chipDate?.let { reminderDate == it } ?: true

            // Include in the filtered list if it passes both filters
            passesSegmentFilter && passesChipDateFilter
        }
        .sortedBy { it.localDateTime }

    if (sortedFilteredReminders.isEmpty()) {
        NoReminders()
    }

    LazyColumn(
        state = lazyListState,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {

        item(key = "spacerBegin") {
            Spacer(modifier = Modifier.height(1.dp))
        }

        items(sortedFilteredReminders, key = { reminder -> getUniqueKey(reminder) }) { reminder ->

            Box(modifier = Modifier.animateItemPlacement()) {
                ReminderForList(reminder, true)
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        item(key = "spacerEnd") {
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

// A reminder card
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ReminderCard(type: String, title: String, timeDisplay: String, groupTitle: String = "",
                 groupIconId: Int = -1, groupColorId: Int = -1, displayGroup: Boolean = true,
                 onClick: () -> Unit, onLongPress: () -> Unit, late: Boolean = false, due: Boolean = false,
                 habitMode: Boolean = false, habitCurrentStreak: Int = 0, completeOnClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .heightIn(min = 110.dp)
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = MaterialTheme.shapes.medium)
            .combinedClickable(
                onClick = { onClick() },
                onLongClick = { onLongPress() }
            )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
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

                if (habitMode) {
                    Row( // Habit streak visual
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Whatshot,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )

                        Text(
                            text = "$habitCurrentStreak",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }

            // Title and below
            Text(
                modifier = Modifier
                    .padding(top = 6.dp, bottom = 8.dp),
                text = shortenText(title, 38),
                style = MaterialTheme.typography.titleLarge
            )

            Row (
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val coloredByDate = if (late) MaterialTheme.colorScheme.error
                            else if (due) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                Icon(
                    imageVector = Icons.Outlined.CalendarMonth,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = coloredByDate
                )

                Text(
                    text = timeDisplay, //16 Feb · 11:00 PM
                    style = MaterialTheme.typography.bodyMedium,
                    color = coloredByDate
                )
            }

            if (displayGroup) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                ) {

                    DisplayGroup(
                        groupName = groupTitle,
                        iconId = groupIconId,
                        colorId = groupColorId
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
                        IconButton(
                            onClick = { completeOnClick() }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null
                            )
                        }
                    }
                }

            }

        }
    }
}



// Row of calendar filter chips, shows the current date to 13 days after it for filtering
@Composable
private fun CalendarFilterChips() {
    val leftRightFade = Brush.horizontalGradient(0f to Color.Transparent, 0.03f to Color.Red, 0.97f to Color.Red, 1f to Color.Transparent)
    val currentDate = LocalDate.now()

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
            val isSelected = index == getSelectedChipIndex()
            val selectedIndex = if (isSelected) -1 else index

            //Get date with index
            val dateAddedWithIndex = currentDate.plusDays(index.toLong())

            VerticalChip(
                isSelected = isSelected,
                onClick = {
                    setSelectedChipIndex(selectedIndex)
                    val chipDate = if (selectedIndex != -1) dateAddedWithIndex else null
                    setSelectedChipDate(chipDate)
                },
                topText = dateAddedWithIndex.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                bottomText = "${dateAddedWithIndex.dayOfMonth}"
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
            .height(animateDpAsState(if (isSelected) 62.dp else 52.dp, label = "").value),
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

// Display group with icon and color
@Composable
fun DisplayGroup(groupName: String, iconId: Int, colorId: Int) {
    Row (
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val imageVector = IconsEnum.iconFromIntValue(iconId)
        val color = ColorsEnum.colorFromIntValue(colorId)

        if (imageVector != null && color != null) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.width(24.dp),
                    imageVector = imageVector,
                    tint = color,
                    contentDescription = null
                )
            }
        }

        Text(
            text = groupName,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

// Segmented filter button for categories
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryFilterSegmented() {
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
                onClick = { setSelectedSegmentText(label) },
                selected = label == getSelectedSegmentText(),
                icon = {
                    SegmentedButtonDefaults.Icon(
                        active = label == getSelectedSegmentText(),
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
fun CreateHabit(setTitle: String = "", setDescription: String = "", setDate: String = "",
                setTime: String = "", setGroupId: Int = -1, setSpecificTimeEnabled: Boolean = false,
                setCustomPeriodEnabled: Boolean = false, setRecurringPeriod: String = "1",
                setRecurringDay: String = "Mondays", updateMode: Boolean = false, updateModeId: Int = -1,
                streakActive: Int = 0, streakHighest: Int = 0) {
    val context = LocalContext.current

    var title by remember { mutableStateOf(setTitle) }
    var description by remember { mutableStateOf(setDescription) }
    var date by remember { mutableStateOf(setDate)}
    var time by remember { mutableStateOf(setTime)}
    var groupId by remember { mutableIntStateOf(setGroupId) }
    var specificTimeEnabled by remember { mutableStateOf(setSpecificTimeEnabled) }
    var customPeriodEnabled by remember { mutableStateOf(setCustomPeriodEnabled) }
    var recurringPeriod by remember { mutableStateOf(setRecurringPeriod) }
    var recurringDay by remember { mutableStateOf(setRecurringDay) }

    val maxTitleChar = 100
    val maxDescriptionChar = 10000

    Column (
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Explanation
        CreateTopExplanation(header = if(updateMode) "Edit Habit" else "New Habit",
            subtitle = "Recurring reminder that also keeps track of streak and highest streak.")

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
            customPeriodEnabled = customPeriodEnabled,
            setCustomPeriodEnabled = { customPeriodEnabled = it }
        )

        // Select group
        CreateGroupSelector(
            groupId = groupId,
            setGroup = { groupId = it }
        )

        // Save button
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                // Validate data
                val errorCodeOne = validateTitleAndDescription(title, description, maxTitleChar, maxDescriptionChar,
                    context = context)
                val errorCodeTwo = validateGroupSelection(groupId, context = context)
                val errorCodeThree = validateTimeAndDate(date, time, specificTimeEnabled, context = context)
                val errorCodeFour = validateCustomPeriod(recurringPeriod, customPeriodEnabled, context = context)

                if (errorCodeOne==0 && errorCodeTwo==0 && errorCodeThree==0 && errorCodeFour==0) {
                    val localDateTime = toLocalDateTime(date, time, specificTimeEnabled)
                    CoroutineScope(Dispatchers.IO).launch {
                        if (updateMode) {
                            val updatedHabit = Habit(
                                id = updateModeId,
                                title = title,
                                groupId = groupId,
                                specificTimeEnabled = specificTimeEnabled,
                                localDateTime = localDateTime,
                                description = description,

                                customPeriodEnabled = customPeriodEnabled,
                                recurringPeriod = recurringPeriod.toInt(),
                                recurringDay = recurringDay,
                                streakActive = streakActive,
                                streakHighest = streakHighest
                            )

                            habitDao.update(updatedHabit)
                        } else {
                            habitDao.insert(
                                Habit(
                                    title = title,
                                    groupId = groupId,
                                    specificTimeEnabled = specificTimeEnabled,
                                    localDateTime = localDateTime,
                                    description = description,

                                    customPeriodEnabled = customPeriodEnabled,
                                    recurringPeriod = recurringPeriod.toInt(),
                                    recurringDay = recurringDay,
                                    streakActive = streakActive,
                                    streakHighest = streakHighest
                                )
                            )
                        }

                    }
                    // Dismiss the sheet after checks
                    setSheetVisibility(false)
                }
            }
        ) {
            Text(
                text = if(updateMode) "Save" else "Create",
            )
        }

        // Delete button
        if (updateMode) {
            var deleteConfirm by remember { mutableStateOf(false) }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    modifier = Modifier.width(200.dp),
                    onClick = {
                        if (deleteConfirm) {
                            CoroutineScope(Dispatchers.IO).launch {
                                val habitDeleteById = Habit(id = updateModeId)

                                habitDao.delete(habitDeleteById)
                            }

                            setSheetVisibility(false)
                        } else {
                            deleteConfirm = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(text = if (deleteConfirm) "Confirm Delete" else "Delete")
                }
            }
        }

        Spacer(modifier = Modifier.padding(vertical = 8.dp))
    }

}

@Composable
fun CreateRecurring(setTitle: String = "", setDescription: String = "", setDate: String = "",
                    setTime: String = "", setGroupId: Int = -1, setSpecificTimeEnabled: Boolean = false,
                    setCustomPeriodEnabled: Boolean = false, setRecurringPeriod: String = "1",
                    setRecurringDay: String = "Mondays", updateMode: Boolean = false, updateModeId: Int = -1) {
    val context = LocalContext.current

    var title by remember { mutableStateOf(setTitle) }
    var description by remember { mutableStateOf(setDescription) }
    var date by remember { mutableStateOf(setDate)}
    var time by remember { mutableStateOf(setTime)}
    var groupId by remember { mutableIntStateOf(setGroupId) }
    var specificTimeEnabled by remember { mutableStateOf(setSpecificTimeEnabled) }
    var customPeriodEnabled by remember { mutableStateOf(setCustomPeriodEnabled) }
    var recurringPeriod by remember { mutableStateOf(setRecurringPeriod) }
    var recurringDay by remember { mutableStateOf(setRecurringDay) }

    val maxTitleChar = 100
    val maxDescriptionChar = 10000

    Column (
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Explanation
        CreateTopExplanation(header = if(updateMode) "Edit Recurring" else "New Recurring",
            subtitle = "Recurring reminder that will continue to reapply after completion.")

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
            customPeriodEnabled = customPeriodEnabled,
            setCustomPeriodEnabled = { customPeriodEnabled = it }
        )

        // Select group
        CreateGroupSelector(
            groupId = groupId,
            setGroup = { groupId = it }
        )

        // Save button
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                // Validate data
                val errorCodeOne = validateTitleAndDescription(title, description, maxTitleChar, maxDescriptionChar,
                    context = context)
                val errorCodeTwo = validateGroupSelection(groupId, context = context)
                val errorCodeThree = validateTimeAndDate(date, time, specificTimeEnabled, context = context)
                val errorCodeFour = validateCustomPeriod(recurringPeriod, customPeriodEnabled, context = context)

                if (errorCodeOne==0 && errorCodeTwo==0 && errorCodeThree==0 && errorCodeFour==0) {
                    val localDateTime = toLocalDateTime(date, time, specificTimeEnabled)
                    CoroutineScope(Dispatchers.IO).launch {
                        if (updateMode) {
                            val updatedRecurringReminder = RecurringReminder(
                                id = updateModeId,
                                title = title,
                                groupId = groupId,
                                specificTimeEnabled = specificTimeEnabled,
                                localDateTime = localDateTime,
                                description = description,

                                customPeriodEnabled = customPeriodEnabled,
                                recurringPeriod = recurringPeriod.toInt(),
                                recurringDay = recurringDay
                            )

                            recurringReminderDao.update(updatedRecurringReminder)
                        } else {
                            recurringReminderDao.insert(
                                RecurringReminder(
                                    title = title,
                                    groupId = groupId,
                                    specificTimeEnabled = specificTimeEnabled,
                                    localDateTime = localDateTime,
                                    description = description,

                                    customPeriodEnabled = customPeriodEnabled,
                                    recurringPeriod = recurringPeriod.toInt(),
                                    recurringDay = recurringDay
                                )
                            )
                        }

                    }
                    // Dismiss the sheet after checks
                    setSheetVisibility(false)
                }
            }
        ) {
            Text(
                text = if(updateMode) "Save" else "Create",
            )
        }

        // Delete button
        if (updateMode) {
            var deleteConfirm by remember { mutableStateOf(false) }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    modifier = Modifier.width(200.dp),
                    onClick = {
                        if (deleteConfirm) {
                            CoroutineScope(Dispatchers.IO).launch {
                                val recurringDeleteById = RecurringReminder(id = updateModeId)

                                recurringReminderDao.delete(recurringDeleteById)
                            }
                            setSheetVisibility(false)
                        } else {
                            deleteConfirm = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(text = if (deleteConfirm) "Confirm Delete" else "Delete")
                }
            }
        }

        Spacer(modifier = Modifier.padding(vertical = 8.dp))
    }
}

@Composable
fun CreateSingleTime(setTitle: String = "", setDescription: String = "", setDate: String = "",
                     setTime: String = "", setGroupId: Int = -1, setSpecificTimeEnabled: Boolean = false,
                     updateMode: Boolean = false, updateModeId: Int = -1) {
    val context = LocalContext.current

    var title by remember { mutableStateOf(setTitle) }
    var description by remember { mutableStateOf(setDescription) }
    var specificTimeEnabled by remember { mutableStateOf(setSpecificTimeEnabled) }
    var groupId by remember { mutableIntStateOf(setGroupId) }
    var date by remember { mutableStateOf(setDate) }
    var time by remember { mutableStateOf(setTime) }

    val maxTitleChar = 100
    val maxDescriptionChar = 10000

    Column (
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Explanation
        CreateTopExplanation(header = if(updateMode) "Edit Reminder" else "New Reminder",
            subtitle = "Reminder that only happens once and will be deleted when complete.")

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
        CreateGroupSelector(
            groupId = groupId,
            setGroup = { groupId = it }
        )

        // Save button
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                // Validate data
                val errorCodeOne = validateTitleAndDescription(title, description, maxTitleChar, maxDescriptionChar,
                    context = context)
                val errorCodeTwo = validateGroupSelection(groupId, context = context)
                val errorCodeThree = validateTimeAndDate(date, time, specificTimeEnabled, context = context)

                if (errorCodeOne==0 && errorCodeTwo==0 && errorCodeThree==0) {
                    val localDateTime = toLocalDateTime(date, time, specificTimeEnabled)
                    CoroutineScope(Dispatchers.IO).launch {
                        if (updateMode) {
                            val updatedReminder = SingleTimeReminder(
                                id = updateModeId,
                                title = title,
                                groupId = groupId,
                                specificTimeEnabled = specificTimeEnabled,
                                localDateTime = localDateTime,
                                description = description
                            )

                            singleTimeReminderDao.update(updatedReminder)
                        } else {
                            singleTimeReminderDao.insert(
                                SingleTimeReminder(
                                    title = title,
                                    groupId = groupId,
                                    specificTimeEnabled = specificTimeEnabled,
                                    localDateTime = localDateTime,
                                    description = description
                                )
                            )
                        }

                    }
                    // Dismiss the sheet after checks
                    setSheetVisibility(false)
                }
            }
        ) {
            Text(
                text = if(updateMode) "Save" else "Create",
            )
        }

        // Delete button
        if (updateMode) {
            var deleteConfirm by remember { mutableStateOf(false) }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    modifier = Modifier.width(200.dp),
                    onClick = {
                        if (deleteConfirm) {
                            CoroutineScope(Dispatchers.IO).launch {
                                val reminderDeleteById = SingleTimeReminder(id = updateModeId)

                                singleTimeReminderDao.delete(reminderDeleteById)
                            }
                            setSheetVisibility(false)
                        } else {
                            deleteConfirm = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(text = if (deleteConfirm) "Confirm Delete" else "Delete")
                }
            }
        }

        Spacer(modifier = Modifier.padding(vertical = 12.dp))
    }
}

@Composable
fun CreateGroupSelector(groupId: Int, setGroup: (Int) -> Unit) {
    val groupList by groupDao.getAllGroups().collectAsState(initial = emptyList())
    val leftRightFade = Brush.horizontalGradient(0f to Color.Transparent, 0.01f to Color.Red, 0.99f to Color.Red, 1f to Color.Transparent)

    Column {
        Text(
            text = "Select group",
            style = MaterialTheme.typography.bodyMedium
        )

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fadingEdge(leftRightFade)) {

            item {
                Spacer(modifier = Modifier.width(1.dp))
            }

            items(groupList) { group ->
                val isSelected = group.id == groupId
                val imageVector = IconsEnum.iconFromIntValue(group.icon)
                val color = ColorsEnum.colorFromIntValue(group.color)

                if (imageVector != null && color != null) {
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            setGroup(group.id)
                        },
                        label = { Text(group.title) },
                        leadingIcon = {
                            Box(
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = imageVector,
                                    tint = color,
                                    contentDescription = null
                                )
                            }

                        }
                    )
                }

            }

            item {
                Spacer(modifier = Modifier.width(1.dp))
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
            maxLines = 8
        )
    }
}

// Recurring area
@Composable
fun CreateRecurringSelector(
    recurringPeriod: String,
    setRecurringPeriod: (String) -> Unit,
    recurringDay: String,
    setRecurringDay: (String) -> Unit,
    customPeriodEnabled: Boolean,
    setCustomPeriodEnabled: (Boolean) -> Unit
) {
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
                    enabled = !customPeriodEnabled
                )
            }
        }

        LabelledCheckBox(
            checked = customPeriodEnabled,
            onCheckedChange = { setCustomPeriodEnabled(it) },
            label = "Custom period"
        )

        OutlinedTextField(
            value = recurringPeriod,
            onValueChange = { if (it.isDigitsOnly()) setRecurringPeriod(it) },
            label = { Text("Days between reminder") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            enabled = customPeriodEnabled,
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
    val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
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
                    ) {
                        Text("Cancel")
                    }

                    Spacer(modifier = Modifier.padding(horizontal = 4.dp))

                    OutlinedButton(
                        onClick = onConfirm
                    ) {
                        Text("OK")
                    }
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

@Composable
fun ViewReminder(type: String, title: String, description: String, localDateTime: LocalDateTime,
                        groupTitle: String, groupIconId: Int, groupColorId: Int,
                 habitMode: Boolean = false, habitCurrentStreak: Int = 0, habitHighestStreak: Int = 0
) {

    val format: String =
        if (localDateTime.toLocalTime() == LocalTime.MIDNIGHT) {
            "d MMM"
        } else {
            "d MMM · h:mm a"
        }

    Column (
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
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

                if (habitMode) {
                    Column {

                        Row( // Highest habit streak visual
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.LocalFireDepartment,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )

                            Text(
                                text = "Highest: $habitHighestStreak days",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }

                        Row( // Habit streak visual
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Whatshot,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )

                            Text(
                                text = "Current: $habitCurrentStreak days",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }

                    }

                }
            }

            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.CalendarMonth,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )

                Text(
                    text = "Next alarm: " + formatLocalDateTime(localDateTime, format),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            DisplayGroup(groupName = groupTitle, iconId = groupIconId, colorId = groupColorId)
        }

        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.padding(vertical = 12.dp))
    }
}