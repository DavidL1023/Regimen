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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EventRepeat
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import app.regimen.DynamicScaffoldState
import app.regimen.RemindMeRow
import app.regimen.fadingEdge
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
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
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("")}
    var time by remember { mutableStateOf("")}
    var recurringPeriod by remember { mutableIntStateOf(1) }

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

        // Date
        Row (horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Date",
                    style = MaterialTheme.typography.bodyMedium
                )
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Select date") },
                    readOnly = true,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.CalendarMonth,
                            contentDescription = null
                        )
                    }
                )
            }

            // Time
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Time",
                    style = MaterialTheme.typography.bodyMedium
                )
                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    label = { Text("Select time") },
                    readOnly = true,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.AccessTime,
                            contentDescription = null
                        )
                    }
                )
            }
        }

        // Recurring period
        CreateRecurringNumberWheel(
            recurringPeriod = recurringPeriod,
            subtractRecurringPeriod = { recurringPeriod-=1 },
            addRecurringPeriod = { recurringPeriod+=1 }
        )

        // Select group
        CreateGroupSelector()

        // Save button
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { /*TODO*/ }
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
    var recurringPeriod by remember { mutableIntStateOf(1) }

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

        Row (horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Date",
                    style = MaterialTheme.typography.bodyMedium
                )
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Select date") },
                    readOnly = true,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.CalendarMonth,
                            contentDescription = null
                        )
                    }
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Time",
                    style = MaterialTheme.typography.bodyMedium
                )
                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    label = { Text("Select time") },
                    readOnly = true,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.AccessTime,
                            contentDescription = null
                        )
                    }
                )
            }
        }

        // Recurring period
        CreateRecurringNumberWheel(
            recurringPeriod = recurringPeriod,
            subtractRecurringPeriod = { recurringPeriod-=1 },
            addRecurringPeriod = { recurringPeriod+=1 }
        )

        // Select group
        CreateGroupSelector()

        // Save button
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { /*TODO*/ }
        ) {
            Text(
                text = "Save",
            )
        }

        Spacer(modifier = Modifier.padding(vertical = 8.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateSingleTime() {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var specificTimeEnabled by remember { mutableStateOf(false) }
    var date by remember { mutableStateOf("")}
    var time by remember { mutableStateOf("")}

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
        CreateGroupSelector()

        // Save button
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { /*TODO*/ }
        ) {
            Text(
                text = "Save",
            )
        }

        Spacer(modifier = Modifier.padding(vertical = 12.dp))
    }
}

@Composable
fun CreateGroupSelector() {
    Column {
        Text(
            text = "Select group",
            style = MaterialTheme.typography.bodyMedium
        )

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
            minLines = 3
        )
    }
}

@Composable
fun CreateRecurringNumberWheel(
    recurringPeriod: Int,
    subtractRecurringPeriod: () -> Unit,
    addRecurringPeriod: () -> Unit
) {
    Text(
        text = "Recurring period",
        style = MaterialTheme.typography.bodyMedium
    )
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        IconButton(
            modifier = Modifier.size(40.dp),
            onClick = { subtractRecurringPeriod() },
            enabled = recurringPeriod > 1
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBackIosNew,
                contentDescription = null
            )
        }

        Text(
            text = recurringPeriod.toString() + if (recurringPeriod==1) " day" else " days",
            style = MaterialTheme.typography.bodyLarge
        )

        IconButton(
            modifier = Modifier
                .size(40.dp)
                .rotate(180f),
            onClick = { addRecurringPeriod() },
            enabled = recurringPeriod < 365
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBackIosNew,
                contentDescription = null
            )
        }
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
        onValueChange = {/* not used */ },
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
        onValueChange = {/* not used */ },
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