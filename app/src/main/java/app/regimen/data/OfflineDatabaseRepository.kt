package app.regimen.data

import kotlinx.coroutines.flow.Flow

class OfflineGroupRepository(private val groupDao: GroupDao) : GroupRepository {
    override fun getAllGroupsStream(): Flow<List<Group>> = groupDao.getAllGroups()

    override fun getGroupStream(id: Int): Flow<Group?> = groupDao.getGroup(id)

    override suspend fun insertGroup(group: Group) = groupDao.insert(group)

    override suspend fun deleteGroup(group: Group) = groupDao.delete(group)

    override suspend fun updateGroup(group: Group) = groupDao.update(group)
}

class OfflineSingleTimeReminderRepository(private val singleTimeReminderDao: SingleTimeReminderDao) :
    SingleTimeReminderRepository {

    override fun getAllSingleTimeRemindersStream(): Flow<List<SingleTimeReminder>> =
        singleTimeReminderDao.getAllSingleTimeReminders()

    override fun getSingleTimeReminderStream(id: Int): Flow<SingleTimeReminder?> =
        singleTimeReminderDao.getSingleTimeReminder(id)

    override suspend fun insertSingleTimeReminder(reminder: SingleTimeReminder) =
        singleTimeReminderDao.insert(reminder)

    override suspend fun deleteSingleTimeReminder(reminder: SingleTimeReminder) =
        singleTimeReminderDao.delete(reminder)

    override suspend fun updateSingleTimeReminder(reminder: SingleTimeReminder) =
        singleTimeReminderDao.update(reminder)
}

class OfflineRecurringReminderRepository(private val recurringReminderDao: RecurringReminderDao) :
    RecurringReminderRepository {

    override fun getAllRecurringRemindersStream(): Flow<List<RecurringReminder>> =
        recurringReminderDao.getAllRecurringReminders()

    override fun getRecurringReminderStream(id: Int): Flow<RecurringReminder?> =
        recurringReminderDao.getRecurringReminder(id)

    override suspend fun insertRecurringReminder(reminder: RecurringReminder) =
        recurringReminderDao.insert(reminder)

    override suspend fun deleteRecurringReminder(reminder: RecurringReminder) =
        recurringReminderDao.delete(reminder)

    override suspend fun updateRecurringReminder(reminder: RecurringReminder) =
        recurringReminderDao.update(reminder)
}

class OfflineHabitRepository(private val habitDao: HabitDao) : HabitRepository {

    override fun getAllHabitsStream(): Flow<List<Habit>> = habitDao.getAllHabits()

    override fun getHabitStream(id: Int): Flow<Habit?> = habitDao.getHabit(id)

    override suspend fun insertHabit(habit: Habit) = habitDao.insert(habit)

    override suspend fun deleteHabit(habit: Habit) = habitDao.delete(habit)

    override suspend fun updateHabit(habit: Habit) = habitDao.update(habit)
}

class OfflinePageRepository(private val pageDao: PageDao) : PageRepository {

    override fun getAllPagesStream(): Flow<List<Page>> = pageDao.getAllPages()

    override fun getPageStream(id: Int): Flow<Page?> = pageDao.getPage(id)

    override suspend fun insertPage(page: Page) = pageDao.insert(page)

    override suspend fun deletePage(page: Page) = pageDao.delete(page)

    override suspend fun updatePage(page: Page) = pageDao.update(page)
}
