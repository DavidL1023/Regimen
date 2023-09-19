package app.regimen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import app.regimen.ui.theme.RegimenTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    // App
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Preference DataStore singleton
        val dataStoreSingleton = PreferenceDataStore(this)

        // Grab theme mode
        var isDarkTheme = false
        CoroutineScope(Dispatchers.IO).launch {
            dataStoreSingleton.getTheme().collect {
                withContext(Dispatchers.Main) {
                    isDarkTheme = it
                }
            }
        }

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
