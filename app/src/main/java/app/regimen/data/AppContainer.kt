package app.regimen.data

import android.content.Context

interface AppContainer {
    val groupRepository: GroupRepository
    val singleTimeReminderRepository: SingleTimeReminderRepository
    val recurringReminderRepository: RecurringReminderRepository
    val habitRepository: HabitRepository
    val pageRepository: PageRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val groupRepository: GroupRepository by lazy {
        OfflineGroupRepository(AppDatabase.getDatabase(context).groupDao())
    }

    override val singleTimeReminderRepository: SingleTimeReminderRepository by lazy {
        OfflineSingleTimeReminderRepository(AppDatabase.getDatabase(context).singleTimeReminderDao())
    }

    override val recurringReminderRepository: RecurringReminderRepository by lazy {
        OfflineRecurringReminderRepository(AppDatabase.getDatabase(context).recurringReminderDao())
    }

    override val habitRepository: HabitRepository by lazy {
        OfflineHabitRepository(AppDatabase.getDatabase(context).habitDao())
    }

    override val pageRepository: PageRepository by lazy {
        OfflinePageRepository(AppDatabase.getDatabase(context).pageDao())
    }
}
