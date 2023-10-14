package app.regimen.data

import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieve of entities from a given data source.
 */
interface GroupRepository {
    fun getAllGroupsStream(): Flow<List<Group>>
    fun getGroupStream(id: Int): Flow<Group?>
    suspend fun insertGroup(group: Group)
    suspend fun deleteGroup(group: Group)
    suspend fun updateGroup(group: Group)
}

interface SingleTimeReminderRepository {
    fun getAllSingleTimeRemindersStream(): Flow<List<SingleTimeReminder>>
    fun getSingleTimeReminderStream(id: Int): Flow<SingleTimeReminder?>
    suspend fun insertSingleTimeReminder(reminder: SingleTimeReminder)
    suspend fun deleteSingleTimeReminder(reminder: SingleTimeReminder)
    suspend fun updateSingleTimeReminder(reminder: SingleTimeReminder)
}

interface RecurringReminderRepository {
    fun getAllRecurringRemindersStream(): Flow<List<RecurringReminder>>
    fun getRecurringReminderStream(id: Int): Flow<RecurringReminder?>
    suspend fun insertRecurringReminder(reminder: RecurringReminder)
    suspend fun deleteRecurringReminder(reminder: RecurringReminder)
    suspend fun updateRecurringReminder(reminder: RecurringReminder)
}

interface HabitRepository {
    fun getAllHabitsStream(): Flow<List<Habit>>
    fun getHabitStream(id: Int): Flow<Habit?>
    suspend fun insertHabit(habit: Habit)
    suspend fun deleteHabit(habit: Habit)
    suspend fun updateHabit(habit: Habit)
}

interface PageRepository {
    fun getAllPagesStream(): Flow<List<Page>>
    fun getPageStream(id: Int): Flow<Page?>
    suspend fun insertPage(page: Page)
    suspend fun deletePage(page: Page)
    suspend fun updatePage(page: Page)
}
