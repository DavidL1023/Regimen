package app.regimen.di

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferenceDataStore @Inject constructor(@ApplicationContext private val context: Context) {

    companion object {
        private val Context.dataStore by preferencesDataStore(name = "SETTINGS")

        var themeRadioPref = stringPreferencesKey("THEME_RADIO")
        var isDarkTheme = booleanPreferencesKey("IS_DARK_THEME")

        var passcodeSwitchPref = booleanPreferencesKey("PASSCODE_SWITCH")
        var passcodeDigits = intPreferencesKey("PASSCODE_DIGITS")
    }

    private var pref = context.dataStore


    //Theme radio
    suspend fun setThemeRadio(themeRadio : String) {
        pref.edit {
            it[themeRadioPref] = themeRadio
        }
    }

    fun getThemeRadio() = pref.data.map {
        it[themeRadioPref]?:"Light mode"
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
        it[passcodeDigits]?:-1
    }

}