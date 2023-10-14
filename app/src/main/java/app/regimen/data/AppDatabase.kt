package app.regimen.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Database class with a singleton Instance object.
 */
@Database(entities = [Group::class, SingleTimeReminder::class, RecurringReminder::class, Habit::class, Page::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun groupDao(): GroupDao
    abstract fun singleTimeReminderDao(): SingleTimeReminderDao
    abstract fun recurringReminderDao(): RecurringReminderDao
    abstract fun habitDao(): HabitDao
    abstract fun pageDao(): PageDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, "app_database")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}