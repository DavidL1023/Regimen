package app.regimen.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "groups")
data class Group(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val title: String,
    val color: Int, //Color is assigned as integer to be decoded later
    val icon: Int //Same with icon
)

data class CommonReminderProperties(
    val title: String,
    val group: Int,
    val specificTimeEnabled: Boolean,
    val localDateTime: LocalDateTime,
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
    val group: Int,
    val body: String, //todo probably going to be different for rich text
    val dateTimeCreated: LocalDateTime,
    val dateTimeLastEdited: LocalDateTime
)