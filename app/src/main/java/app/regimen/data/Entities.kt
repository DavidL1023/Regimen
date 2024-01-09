package app.regimen.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "groups")
data class Group(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String = "",
    val description: String = "",
    val color: Int = 0, //Color is assigned as integer to be decoded later
    val icon: Int = 0 //Same with icon
)

interface Reminder {
    val id: Int
    val title: String
    val groupId: Int
    val specificTimeEnabled: Boolean
    val localDateTime: LocalDateTime
    val description: String
}

@Entity(tableName = "single time reminders")
data class SingleTimeReminder(
    @PrimaryKey(autoGenerate = true)
    override val id: Int = 0,

    override val title: String = "",
    override val groupId: Int = 0,
    override val specificTimeEnabled: Boolean = false,
    override val localDateTime: LocalDateTime = LocalDateTime.now(),
    override val description: String = ""
) : Reminder

@Entity(tableName = "recurring reminders")
data class RecurringReminder(
    @PrimaryKey(autoGenerate = true)
    override val id: Int = 0,

    override val title: String = "",
    override val groupId: Int = 0,
    override val specificTimeEnabled: Boolean = false,
    override val localDateTime: LocalDateTime = LocalDateTime.now(),
    override val description: String = "",

    val customPeriodEnabled: Boolean = false,
    val recurringPeriod: Int = 0,
    val recurringDay: String = ""
) : Reminder

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true)
    override val id: Int = 0,

    override val title: String = "",
    override val groupId: Int = 0,
    override val specificTimeEnabled: Boolean = false,
    override val localDateTime: LocalDateTime = LocalDateTime.now(),
    override val description: String = "",

    val customPeriodEnabled: Boolean = false,
    val recurringPeriod: Int = 0,
    val recurringDay: String = "",
    val streakActive: Int = 0,
    val streakHighest: Int = 0
) : Reminder

@Entity(tableName = "pages")
data class Page(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String = "",
    val groupId: Int = 0,
    val body: String = "",
    val dateTimeModified: LocalDateTime = LocalDateTime.now(),
    val dateTimeCreated: LocalDateTime = LocalDateTime.now()
)