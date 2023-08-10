package app.regimen.screens

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Button
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavController
import app.regimen.AppBarState

@Composable
fun SettingsScreen(
    onComposing: (AppBarState) -> Unit,
    navController: NavController
) {
    // Theme mode radio
    val displayRadioOptions = listOf("Use device settings", "Dark mode", "Light mode")
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(displayRadioOptions[0]) }

    // Passcode checked
    var passcodeChecked by remember { mutableStateOf(true) }

    // Dynamic toolbar
    LaunchedEffect(key1 = true) {
        onComposing(
            AppBarState(
                title = "Settings",
            )
        )
    }


    // Settings column
    Column {

        // Bring user to android notification page
        Text(
            text = "Set Notifications",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(start = 16.dp)
        )

        NotificationsButton()

        // Theme selection setting
        Column(Modifier.selectableGroup()) {
            Text(
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                text = "Select Theme"
            )

            displayRadioOptions.forEach { text ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .selectable(
                            selected = (text == selectedOption),
                            onClick = { onOptionSelected(text) },
                            role = Role.RadioButton
                        )
                        .padding(horizontal = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (text == selectedOption),
                        onClick = null
                    )
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        }

        // Password settings
        Text(
            text = "Configure Security",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(start = 16.dp)
        )
        // Enable or disable password
        Row(
            Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        )  {
            Text(
                text = "Enable password",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(start = 16.dp)
            )

            Spacer(modifier = Modifier.weight(1f)) // Keep switch on the right side of screen

            Switch(
                checked = passcodeChecked,
                onCheckedChange = { passcodeChecked = it },
                modifier = Modifier.padding(end = 16.dp)
            )
        }

    }
}

@Composable
fun NotificationsButton() {
    val context = LocalContext.current
    Button(
        onClick = { openNotificationSettings(context) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Text(
            text = "Notifications",
        )
    }
}

private fun openNotificationSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
    intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
    context.startActivity(intent)
}


