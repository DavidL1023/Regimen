package app.regimen

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

class PreferenceDataStore(context: Context) {

    val Context.dataStore by preferencesDataStore(name = "SETTINGS")
    var pref = context.dataStore

    companion object {
        var isDarkThemePref = booleanPreferencesKey("IS_DARK_THEME")
        var hasPasscodePref = booleanPreferencesKey("HAS_PASSCODE")
        var passcodeValuePref = intPreferencesKey("PASSCODE_VALUE")
    }


    //Theme preference getter setter
    suspend fun setTheme(isDarkTheme : Boolean) {
        pref.edit {
            it[isDarkThemePref] = isDarkTheme
        }
    }

    fun getTheme() = pref.data.map {
        it[isDarkThemePref]?:false
    }

    //Passcode switch getter setter


    //Passcode value getter setter

}