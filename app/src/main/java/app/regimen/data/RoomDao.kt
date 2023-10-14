package app.regimen.data

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import app.regimen.data.Group
import app.regimen.data.Habit
import app.regimen.data.Page
import app.regimen.data.RecurringReminder
import app.regimen.data.SingleTimeReminder
import kotlinx.coroutines.flow.Flow

interface GroupDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(group: Group)

    @Update
    suspend fun update(group: Group)

    @Delete
    suspend fun delete(group: Group)

    @Query("SELECT * from groups WHERE id = :id")
    fun getGroup(id: Int): Flow<Group>

    @Query("SELECT * from groups")
    fun getAllGroups(): Flow<List<Group>>
}

interface SingleTimeReminderDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(reminder: SingleTimeReminder)

    @Update
    suspend fun update(reminder: SingleTimeReminder)

    @Delete
    suspend fun delete(reminder: SingleTimeReminder)

    @Query("SELECT * FROM `single time reminders` WHERE id = :id")
    fun getSingleTimeReminder(id: Int): Flow<SingleTimeReminder>

    @Query("SELECT * FROM `single time reminders`")
    fun getAllSingleTimeReminders(): Flow<List<SingleTimeReminder>>

}

interface RecurringReminderDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(reminder: RecurringReminder)

    @Update
    suspend fun update(reminder: RecurringReminder)

    @Delete
    suspend fun delete(reminder: RecurringReminder)

    @Query("SELECT * FROM `recurring reminders` WHERE id = :id")
    fun getRecurringReminder(id: Int): Flow<RecurringReminder>

    @Query("SELECT * FROM `recurring reminders`")
    fun getAllRecurringReminders(): Flow<List<RecurringReminder>>
}

interface HabitDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(habit: Habit)

    @Update
    suspend fun update(habit: Habit)

    @Delete
    suspend fun delete(habit: Habit)

    @Query("SELECT * FROM habits WHERE id = :id")
    fun getHabit(id: Int): Flow<Habit>

    @Query("SELECT * FROM habits")
    fun getAllHabits(): Flow<List<Habit>>
}

interface PageDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(page: Page)

    @Update
    suspend fun update(page: Page)

    @Delete
    suspend fun delete(page: Page)

    @Query("SELECT * FROM pages WHERE id = :id")
    fun getPage(id: Int): Flow<Page>

    @Query("SELECT * FROM pages")
    fun getAllPages(): Flow<List<Page>>
}