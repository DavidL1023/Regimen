package app.regimen.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "groups")
data class Group(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val color: Int, //Color is assigned as integer to be decoded later
    val icon: Int //Same with icon
)

interface Reminder {
    val title: String
    val groupId: Int
    val specificTimeEnabled: Boolean
    val localDateTime: LocalDateTime
    val description: String
    val customProgressActive: Float
    val customProgressGoal: Float
    val customUnit: String
}

@Entity(tableName = "single time reminders")
data class SingleTimeReminder(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    override val title: String,
    override val groupId: Int,
    override val specificTimeEnabled: Boolean,
    override val localDateTime: LocalDateTime,
    override val description: String,
    override val customProgressActive: Float,
    override val customProgressGoal: Float,
    override val customUnit: String
) : Reminder

@Entity(tableName = "recurring reminders")
data class RecurringReminder(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    override val title: String,
    override val groupId: Int,
    override val specificTimeEnabled: Boolean,
    override val localDateTime: LocalDateTime,
    override val description: String,
    override val customProgressActive: Float,
    override val customProgressGoal: Float,
    override val customUnit: String,

    val customPeriodEnabled: Boolean,
    val recurringPeriod: Int,
    val recurringDay: String,
    val paused: Boolean
) : Reminder

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    override val title: String,
    override val groupId: Int,
    override val specificTimeEnabled: Boolean,
    override val localDateTime: LocalDateTime,
    override val description: String,
    override val customProgressActive: Float,
    override val customProgressGoal: Float,
    override val customUnit: String,

    val customPeriodEnabled: Boolean,
    val recurringPeriod: Int,
    val recurringDay: String,
    val paused: Boolean,
    val streakActive: Int,
    val streakHighest: Int
) : Reminder

@Entity(tableName = "pages")
data class Page(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var title: String,
    var groupId: Int,
    var body: String, //todo probably going to be different for rich text
    var dateTimeModified: LocalDateTime,
    val dateTimeCreated: LocalDateTime
)