package app.regimen

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import app.regimen.data.Group
import app.regimen.data.Habit
import app.regimen.data.Page
import app.regimen.data.RecurringReminder
import app.regimen.data.Reminder
import app.regimen.data.SingleTimeReminder
import app.regimen.screens.CreateGroup
import app.regimen.screens.CreateHabit
import app.regimen.screens.CreatePage
import app.regimen.screens.CreateRecurring
import app.regimen.screens.CreateSingleTime
import app.regimen.screens.GroupItem
import app.regimen.screens.PageCard
import app.regimen.screens.ReminderCard
import app.regimen.screens.ViewPage
import app.regimen.screens.ViewReminder
import app.regimen.screens.getNextOccurrenceOfDay
import app.regimen.screens.setSelectedGroupId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

// On click actions for items
@Composable
fun ReminderOnClickView(reminder: Reminder, group: Group) {
    val type: String = when (reminder) {
        is Habit -> { "Habit" }
        is RecurringReminder -> { "Recurring" }
        is SingleTimeReminder -> { "Single Time" }

        else -> {"Unknown"}
    }

    var habitMode = false
    var habitCurrentStreak = 0
    var habitHighestStreak = 0
    if (reminder is Habit) {
        habitMode = true
        habitCurrentStreak = reminder.streakActive
        habitHighestStreak = reminder.streakHighest
    }

    ViewReminder(
        type = type,
        title = reminder.title,
        description = reminder.description,
        localDateTime = reminder.localDateTime,
        groupTitle = group.title,
        groupIconId = group.icon,
        groupColorId = group.color,
        habitMode = habitMode,
        habitCurrentStreak = habitCurrentStreak,
        habitHighestStreak = habitHighestStreak
    )


}

@Composable
fun HabitOnClickEdit(habit: Habit) {
    val dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
    val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")

    val date = habit.localDateTime.format(dateFormatter)
    val time = habit.localDateTime.format(timeFormatter)

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
        updateModeId = habit.id,
        streakActive = habit.streakActive,
        streakHighest = habit.streakHighest
    )

}

@Composable

fun RecurringOnClickEdit(recurringReminder: RecurringReminder) {
    val dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
    val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")

    val date = recurringReminder.localDateTime.format(dateFormatter)
    val time = recurringReminder.localDateTime.format(timeFormatter)

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

@Composable
fun SingleTimeOnClickEdit(singleTimeReminder: SingleTimeReminder) {
    val dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
    val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")

    val date = singleTimeReminder.localDateTime.format(dateFormatter)
    val time = singleTimeReminder.localDateTime.format(timeFormatter)

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

@Composable
fun PageOnClickView(page: Page, group: Group) {
    ViewPage(
        title = page.title,
        description = page.body,
        localDateTime = page.dateTimeModified,
        groupTitle = group.title,
        groupIconId = group.icon,
        groupColorId = group.color
    )

}

@Composable
fun PageOnClickEdit(page: Page) {
    CreatePage(
        setTitle = page.title,
        setDescription = page.body,
        setGroupId = page.groupId,
        updateMode = true,
        updateModeId = page.id
    )

}

// Display cards in a list
@Composable
fun ReminderForList(reminder: Reminder, displayGroup: Boolean) {
    val group = groupDao.getGroup(reminder.groupId).collectAsState(null).value
    if (group != null) {

        val context = LocalContext.current

        val currentDate = LocalDate.now()
        val reminderDate = reminder.localDateTime.toLocalDate()

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

        var habitMode = false
        var habitCurrentStreak = 0
        if (reminder is Habit) {
            habitMode = true
            habitCurrentStreak = reminder.streakActive
        }

        ReminderCard(
            type = reminderType,
            title = reminder.title,
            timeDisplay = formatLocalDateTime(reminder.localDateTime, format),
            groupTitle = group.title,
            groupIconId = group.icon,
            groupColorId = group.color,
            onClick = {
                raiseSheet { ReminderOnClickView( reminder, group) }
            },
            onLongPress = {
                when (reminder) {
                    is Habit -> {
                        raiseSheet { HabitOnClickEdit(reminder) }
                    }

                    is RecurringReminder -> {
                        raiseSheet { RecurringOnClickEdit(reminder) }
                    }

                    is SingleTimeReminder -> {
                        raiseSheet { SingleTimeOnClickEdit(reminder) }
                    }
                }
            },
            late = reminderDate.isBefore(currentDate),
            due = reminderDate.isEqual(currentDate),
            habitMode = habitMode,
            habitCurrentStreak = habitCurrentStreak,
            completeOnClick = {
                when (reminder) {
                    is Habit -> { // Increment based on increment type, increase streak
                        val localDateTime =
                            if (reminder.customPeriodEnabled) {
                                reminder.localDateTime
                                    .plusDays(reminder.recurringPeriod.toLong())
                            } else {
                                getNextOccurrenceOfDay(
                                    reminder.localDateTime, reminder.recurringDay
                                )
                            }

                        // Check if it was late
                        var streakActive = reminder.streakActive
                        var streakHighest = reminder.streakHighest

                        val dateTime: LocalDateTime = reminder.localDateTime
                        val now: LocalDateTime = LocalDateTime.now()
                        val late = dateTime.isBefore(now)
                        if (late) {
                            streakActive = 1
                        } else {
                            streakActive += 1
                            if (streakActive > streakHighest) {
                                streakHighest = streakActive
                            }
                        }

                        val updatedReminder = reminder.copy(localDateTime = localDateTime,
                            streakActive = streakActive, streakHighest = streakHighest)

                        CoroutineScope(Dispatchers.IO).launch {
                            habitDao.update(updatedReminder)
                        }

                        Toast.makeText(
                            context,
                            "Habit completed!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is RecurringReminder -> { // Increment based on increment type
                        val localDateTime =
                            if (reminder.customPeriodEnabled) {
                                reminder.localDateTime
                                    .plusDays(reminder.recurringPeriod.toLong())
                            } else {
                                getNextOccurrenceOfDay(
                                    reminder.localDateTime, reminder.recurringDay
                                )
                            }

                        val updatedReminder = reminder.copy(localDateTime = localDateTime)

                        CoroutineScope(Dispatchers.IO).launch {
                            recurringReminderDao.update(updatedReminder)
                        }

                        Toast.makeText(
                            context,
                            "Recurring reminder completed!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is SingleTimeReminder -> { // Delete it
                        CoroutineScope(Dispatchers.IO).launch {
                            singleTimeReminderDao.delete(reminder)
                        }

                        Toast.makeText(
                            context,
                            "Reminder completed!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            },
            displayGroup = displayGroup
        )

    }

}

@Composable
fun PageForList(page: Page, displayGroup: Boolean) {
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

    if (group != null) {

        PageCard(
            header = page.title,
            body = page.body,
            timeDisplay = timeDisplay,
            groupTitle = group.title,
            groupIconId = group.icon,
            groupColorId = group.color,
            onClick = {
                raiseSheet { PageOnClickView(page, group) }
            },
            onLongPress = {
                raiseSheet { PageOnClickEdit(page) }
            },
            displayGroup = displayGroup
        )

    }
}

@Composable
fun GroupForList(group: Group, isSelected: Boolean, active: Boolean) {
    GroupItem(
        name = group.title,
        iconId = group.icon,
        colorId = group.color,
        selected = isSelected,
        onSelectedChange = {
            if (active) {
                setSelectedGroupId(if (isSelected) -1 else group.id)
            }
        },
        onLongPress = {
            if (active) {
                raiseSheet {
                    CreateGroup(
                        setTitle = group.title, setDescription = group.description,
                        setColor = group.color, setIcon = group.icon, updateMode = true,
                        updateModeId = group.id
                    )
                }
            }
        }
    )
}