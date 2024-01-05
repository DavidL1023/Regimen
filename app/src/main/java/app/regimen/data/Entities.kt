package app.regimen.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "groups")
data class Group(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    var title: String = "",
    var description: String = "",
    var color: Int = 0, //Color is assigned as integer to be decoded later
    var icon: Int = 0 //Same with icon
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

    override var title: String = "",
    override var groupId: Int = 0,
    override var specificTimeEnabled: Boolean = false,
    override var localDateTime: LocalDateTime = LocalDateTime.now(),
    override var description: String = ""
) : Reminder

@Entity(tableName = "recurring reminders")
data class RecurringReminder(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    override var title: String = "",
    override var groupId: Int = 0,
    override var specificTimeEnabled: Boolean = false,
    override var localDateTime: LocalDateTime = LocalDateTime.now(),
    override var description: String = "",

    var customPeriodEnabled: Boolean = false,
    var recurringPeriod: Int = 0,
    var recurringDay: String = ""
) : Reminder

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    override var title: String = "",
    override var groupId: Int = 0,
    override var specificTimeEnabled: Boolean = false,
    override var localDateTime: LocalDateTime = LocalDateTime.now(),
    override var description: String = "",

    var customPeriodEnabled: Boolean = false,
    var recurringPeriod: Int = 0,
    var recurringDay: String = "",
    var streakActive: Int = 0,
    var streakHighest: Int = 0
) : Reminder

@Entity(tableName = "pages")
data class Page(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    var title: String = "",
    var groupId: Int = 0,
    var body: String = "",
    var dateTimeModified: LocalDateTime = LocalDateTime.now(),
    val dateTimeCreated: LocalDateTime = LocalDateTime.now()
)