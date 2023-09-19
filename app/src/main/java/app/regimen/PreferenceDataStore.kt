package app.regimen

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

class PreferenceDataStore(context: Context) {

    private val Context.dataStore by preferencesDataStore(name = "SETTINGS")
    private var pref = context.dataStore

    companion object {
        var themeRadioPref = stringPreferencesKey("THEME_RADIO")
        var isDarkTheme = booleanPreferencesKey("IS_DARK_THEME")

        var passcodeSwitchPref = booleanPreferencesKey("PASSCODE_SWITCH")
        var passcodeDigits = intPreferencesKey("PASSCODE_DIGITS")
    }


    //Theme radio
    suspend fun setThemeRadio(themeRadio : String) {
        pref.edit {
            it[themeRadioPref] = themeRadio
        }
    }

    fun getThemeRadio() = pref.data.map {
        it[themeRadioPref]?:false
    }

    //Dark theme
    suspend fun setIsDarkTheme(darkTheme : Boolean) {
        pref.edit {
            it[isDarkTheme] = darkTheme
        }
    }

    fun getIsDarkTheme() = pref.data.map {
        it[isDarkTheme]?:false
    }

    //Passcode switch
    suspend fun setPasscodeSwitch(passcodeSwitch : Boolean) {
        pref.edit {
            it[passcodeSwitchPref] = passcodeSwitch
        }
    }

    fun getPasscodeSwitch() = pref.data.map {
        it[passcodeSwitchPref]?:false
    }

    //Passcode digits
    suspend fun setPasscodeDigits(passcode : Int) {
        pref.edit {
            it[passcodeDigits] = passcode
        }
    }

    fun getPasscodeDigits() = pref.data.map {
        it[passcodeDigits]?:false
    }

}