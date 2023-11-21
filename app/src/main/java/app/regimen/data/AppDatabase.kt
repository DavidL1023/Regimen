package app.regimen.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Group::class, SingleTimeReminder::class, RecurringReminder::class, Habit::class, Page::class], exportSchema = false, version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun getGroupDao(): GroupDao
    abstract fun getSingleTimeReminderDao(): SingleTimeReminderDao
    abstract fun getRecurringReminderDao(): RecurringReminderDao
    abstract fun getHabitDao(): HabitDao
    abstract fun getPageDao(): PageDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, "item_database")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}