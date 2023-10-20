package app.regimen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import app.regimen.data.AppDatabase
import app.regimen.data.GroupDao
import app.regimen.data.HabitDao
import app.regimen.data.PageDao
import app.regimen.data.RecurringReminder
import app.regimen.data.RecurringReminderDao
import app.regimen.data.SingleTimeReminderDao
import app.regimen.di.AppModule.providePreferenceDataStore
import app.regimen.di.PreferenceDataStore
import app.regimen.ui.theme.RegimenTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // The database/room variables
    lateinit var db: AppDatabase
    lateinit var groupDao: GroupDao
    lateinit var singleTimeReminderDao: SingleTimeReminderDao
    lateinit var recurringReminderDao: RecurringReminderDao
    lateinit var habitDao: HabitDao
    lateinit var pageDao: PageDao

    // Preference DataStore singleton
    @Inject
    lateinit var dataStoreSingleton: PreferenceDataStore

    // App
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Prepare to use the database
        db = AppDatabase.getDatabase(this)
        groupDao = db.getGroupDao()
        singleTimeReminderDao = db.getSingleTimeReminderDao()
        recurringReminderDao = db.getRecurringReminderDao()
        habitDao = db.getHabitDao()
        pageDao = db.getPageDao()

        // Create dataStore
        dataStoreSingleton = providePreferenceDataStore(this)

        // Grab theme mode
        var isDarkTheme: Boolean
        CoroutineScope(Dispatchers.IO).launch {
            dataStoreSingleton.getIsDarkTheme().collect {
                withContext(Dispatchers.Main) {
                    isDarkTheme = it

                    setContent {
                        RegimenTheme(isDarkTheme) {
                            Surface(
                                modifier = Modifier.fillMaxSize(),
                            ) {
                                MainScreen()
                            }
                        }
                    }

                }
            }
        }
    }

}