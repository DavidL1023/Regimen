package app.regimen.data

import androidx.room.Dao
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

@Dao
interface GroupDao {
    @Insert
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

@Dao
interface SingleTimeReminderDao {
    @Insert
    suspend fun insert(reminder: SingleTimeReminder)

    @Update
    suspend fun update(reminder: SingleTimeReminder)

    @Delete
    suspend fun delete(reminder: SingleTimeReminder)

    @Query("DELETE FROM `single time reminders` WHERE groupId = :groupId")
    suspend fun deleteSingleTimeRemindersByGroupId(groupId: Int)

    @Query("SELECT * FROM `single time reminders` WHERE id = :id")
    fun getSingleTimeReminder(id: Int): Flow<SingleTimeReminder>

    @Query("SELECT * FROM `single time reminders`")
    fun getAllSingleTimeReminders(): Flow<List<SingleTimeReminder>>

}

@Dao
interface RecurringReminderDao {
    @Insert
    suspend fun insert(reminder: RecurringReminder)

    @Update
    suspend fun update(reminder: RecurringReminder)

    @Delete
    suspend fun delete(reminder: RecurringReminder)

    @Query("DELETE FROM `recurring reminders` WHERE groupId = :groupId")
    suspend fun deleteRecurringRemindersByGroupId(groupId: Int)

    @Query("SELECT * FROM `recurring reminders` WHERE id = :id")
    fun getRecurringReminder(id: Int): Flow<RecurringReminder>

    @Query("SELECT * FROM `recurring reminders`")
    fun getAllRecurringReminders(): Flow<List<RecurringReminder>>

}

@Dao
interface HabitDao {
    @Insert
    suspend fun insert(habit: Habit)

    @Update
    suspend fun update(habit: Habit)

    @Delete
    suspend fun delete(habit: Habit)

    @Query("DELETE FROM habits WHERE groupId = :groupId")
    suspend fun deleteHabitsByGroupId(groupId: Int)

    @Query("SELECT * FROM habits WHERE id = :id")
    fun getHabit(id: Int): Flow<Habit>

    @Query("SELECT * FROM habits")
    fun getAllHabits(): Flow<List<Habit>>

}

@Dao
interface PageDao {
    @Insert
    suspend fun insert(page: Page)

    @Update
    suspend fun update(page: Page)

    @Delete
    suspend fun delete(page: Page)

    @Query("DELETE FROM pages WHERE groupId = :groupId")
    suspend fun deletePagesByGroupId(groupId: Int)

    @Query("SELECT * FROM pages WHERE id = :id")
    fun getPage(id: Int): Flow<Page>

    @Query("SELECT * FROM pages")
    fun getAllPages(): Flow<List<Page>>

}