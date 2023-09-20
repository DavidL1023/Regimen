package app.regimen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import app.regimen.di.AppModule.providePreferenceDataStore
import app.regimen.ui.theme.RegimenTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // Preference DataStore singleton
    @Inject
    lateinit var dataStoreSingleton: PreferenceDataStore

    // App
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataStoreSingleton = providePreferenceDataStore(this)

        // Grab theme mode
        var isDarkTheme = false
        CoroutineScope(Dispatchers.IO).launch {
            dataStoreSingleton.getIsDarkTheme().collect {
                withContext(Dispatchers.Main) {
                    isDarkTheme = it

                    setContent {
                        RegimenTheme(isDarkTheme) {
                            Surface(
                                modifier = Modifier.fillMaxSize(),
                            ) {
                                MainScreen(dataStoreSingleton)
                            }
                        }
                    }

                }
            }
        }
    }

}