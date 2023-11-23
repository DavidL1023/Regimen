package app.regimen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import app.regimen.data.AppDatabase
import app.regimen.data.Group
import app.regimen.data.GroupDao
import app.regimen.data.HabitDao
import app.regimen.data.PageDao
import app.regimen.data.RecurringReminderDao
import app.regimen.data.SingleTimeReminderDao
import app.regimen.di.AppModule.providePreferenceDataStore
import app.regimen.di.PreferenceDataStore
import app.regimen.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // The database/room
    lateinit var db: AppDatabase

    // Preference DataStore singleton
    @Inject
    lateinit var dataStoreSingleton: PreferenceDataStore

    // App
    override fun onCreate(savedInstanceState: Bundle?) {
        // Handle the splash screen transition.
        installSplashScreen()

        super.onCreate(savedInstanceState)

        // Prepare to use the database
        db = AppDatabase.getDatabase(this)

        // Create dataStore
        dataStoreSingleton = providePreferenceDataStore(this)

        // Grab theme mode
        var isDarkTheme: Boolean
        CoroutineScope(Dispatchers.IO).launch {
            dataStoreSingleton.getIsDarkTheme().collect {
                withContext(Dispatchers.Main) {
                    isDarkTheme = it

                    setContent {
                        AppTheme(isDarkTheme) {
                            Surface(
                                modifier = Modifier.fillMaxSize(),
                            ) {
                                MainScreen(db)
                            }
                        }
                    }

                }
            }
        }
    }

}