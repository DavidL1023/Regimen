package app.regimen.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "groups")
data class Group(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var title: String,
    var description: String,
    var color: Int, //Color is assigned as integer to be decoded later
    var icon: Int //Same with icon
)

interface Reminder {
    val title: String
    val groupId: Int
    val specificTimeEnabled: Boolean
    val localDateTime: LocalDateTime
    val description: String
}

@Entity(tableName = "single time reminders")
data class SingleTimeReminder(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    override var title: String,
    override var groupId: Int,
    override var specificTimeEnabled: Boolean,
    override var localDateTime: LocalDateTime,
    override var description: String
) : Reminder

@Entity(tableName = "recurring reminders")
data class RecurringReminder(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    override var title: String,
    override var groupId: Int,
    override var specificTimeEnabled: Boolean,
    override var localDateTime: LocalDateTime,
    override var description: String,

    var customPeriodEnabled: Boolean,
    var recurringPeriod: Int,
    var recurringDay: String,
) : Reminder

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    override var title: String,
    override var groupId: Int,
    override var specificTimeEnabled: Boolean,
    override var localDateTime: LocalDateTime,
    override var description: String,

    var customPeriodEnabled: Boolean,
    var recurringPeriod: Int,
    var recurringDay: String,
    var streakActive: Int,
    var streakHighest: Int
) : Reminder

@Entity(tableName = "pages")
data class Page(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var title: String,
    var groupId: Int,
    var body: String,
    var dateTimeModified: LocalDateTime,
    val dateTimeCreated: LocalDateTime
)