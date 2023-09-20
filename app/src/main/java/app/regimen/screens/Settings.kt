package app.regimen.screens

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import app.regimen.DynamicScaffoldState
import app.regimen.PreferenceDataStore
import app.regimen.di.AppModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


// Preference data store
lateinit var dataStoreSingleton: PreferenceDataStore

@Composable
fun SettingsScreen(
    onComposing: (DynamicScaffoldState) -> Unit,
    dataStore: PreferenceDataStore
) {

    // Inject the singleton data store
    dataStoreSingleton = dataStore

    // Dynamic toolbar
        onComposing(
            DynamicScaffoldState(
                toolbarTitle = "Settings",
                toolbarSubtitle = "Personalize your app."
            )
        )

    // Settings column
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Notification Row
        NotificationRow()
        Text(
            text = "Set how you want to be notified.",
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Normal
            ),
            modifier = Modifier.padding(start = 16.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        NotificationsButton()

        // Theme Selection Row
        ThemeSelectionRow()
        Text(
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Normal
            ),
            modifier = Modifier.padding(start = 16.dp),
            text = "Select app theme.",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        ThemeSelectRadio()

        // Security Row
        SecurityRow()
        Text(
            text = "Choose how you want to secure your app.",
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Normal
            ),
            modifier = Modifier.padding(start = 16.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        PasswordSwitch()
        SetPasswordButton()
    }
}


// Text and icon row for notifications
@Composable
fun NotificationRow() {
    Row(
        modifier = Modifier.padding(start = 16.dp, top = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = null
        )

        Text(
            text = "Notifications",
            style = MaterialTheme.typography.titleMedium
        )
    }
}

// Text and icon row for theme
@Composable
fun ThemeSelectionRow() {
    Row(
        modifier = Modifier.padding(start = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Visibility,
            contentDescription = null
        )

        Text(
            style = MaterialTheme.typography.titleMedium,
            text = "Visuals"
        )
    }
}

// Text and icon row for security
@Composable
fun SecurityRow() {
    Row(
        modifier = Modifier.padding(start = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Security,
            contentDescription = null
        )

        Text(
            text = "Security",
            style = MaterialTheme.typography.titleMedium
        )
    }
}


// Button to set password
@Composable
fun SetPasswordButton() {
    Button(
        onClick = {  },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        enabled = false
    ) {
        Icon(
            Icons.Filled.Password,
            contentDescription = null,
            modifier = Modifier.size(ButtonDefaults.IconSize)
        )
        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        Text(
            text = "Set Passcode",
        )
    }
}

// Switch to enable password
@Composable
fun PasswordSwitch() {
    var passcodeChecked by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            dataStoreSingleton.getPasscodeSwitch().collect {
                passcodeChecked = it
            }
        }
    }

    Row(
        Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    )  {
        Text(
            text = "Enable Passcode",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 16.dp)
        )

        Spacer(modifier = Modifier.weight(1f)) // Keep switch on the right side of screen

        Switch(
            checked = passcodeChecked,
            onCheckedChange = { isChecked ->
                CoroutineScope(Dispatchers.IO).launch {
                    dataStoreSingleton.setPasscodeSwitch(isChecked)
                }
            },
            modifier = Modifier.padding(end = 16.dp)
        )
    }
}


// Radio selection for theme mode
@Composable
fun ThemeSelectRadio() {
    val displayRadioOptions = listOf("Use device settings", "Dark mode", "Light mode")
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(displayRadioOptions[0]) }

    val isSystemDarkTheme = isSystemInDarkTheme()

    Column(Modifier.selectableGroup()) {
        displayRadioOptions.forEach { text ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .selectable(
                        selected = (text == selectedOption),
                        onClick = {
                            onOptionSelected(text)

                            // Change app theme
                            when (text) {
                                "Use device settings" -> {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        dataStoreSingleton.setIsDarkTheme(isSystemDarkTheme)
                                    }
                                }
                                "Dark mode" -> {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        dataStoreSingleton.setIsDarkTheme(true)
                                    }
                                }

                                "Light mode" -> {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        dataStoreSingleton.setIsDarkTheme(false)
                                    }
                                }
                            }
                        },
                        role = Role.RadioButton
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (text == selectedOption),
                    onClick = null,
                    modifier = Modifier.padding(start = 16.dp)
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }
}

// Button to open android notifications
@Composable
fun NotificationsButton() {
    val context = LocalContext.current
    Button(
        onClick = { openNotificationSettings(context) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 8.dp),
    ) {
        Icon(
            Icons.Filled.Notifications,
            contentDescription = null,
            modifier = Modifier.size(ButtonDefaults.IconSize)
        )
        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        Text(
            text = "Notifications",
        )
    }
}

// Launch notifications function
private fun openNotificationSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
    intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
    context.startActivity(intent)
}

