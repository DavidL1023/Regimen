package app.regimen.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(tableName = "groups")
data class Group(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val title: String,
    //val color: Color, //todo these cant be stored in database, find workaround
    //val icon: ImageVector
)

data class CommonReminderProperties(
    val title: String,
    //val group: Group,
    val specificTimeEnabled: Boolean,
    //val localDate: LocalDate,
    //val localDateTime: LocalDateTime,
    val description: String,
    val customProgressActive: Float,
    val customProgressGoal: Float,
    val customUnit: String
)

@Entity(tableName = "single time reminders")
data class SingleTimeReminder(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @Embedded val commonProperties: CommonReminderProperties
)

@Entity(tableName = "recurring reminders")
data class RecurringReminder(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @Embedded val commonProperties: CommonReminderProperties,
    val customPeriodEnabled: Boolean,
    val recurringPeriod: Int,
    val recurringDay: String,
    val paused: Boolean
)

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @Embedded val commonProperties: CommonReminderProperties,
    val customPeriodEnabled: Boolean,
    val recurringPeriod: Int,
    val recurringDay: String,
    val paused: Boolean,
    val streakActive: Int,
    val streakHighest: Int
)

@Entity(tableName = "pages")
data class Page(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val title: String,
    //val group: Group,
    val body: String, //todo probably going to be different for rich text
    //val dateTimeCreated: LocalDateTime,
    //val dateTimeLastEdited: LocalDateTime
)