package app.regimen.screens

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavController
import app.regimen.AppBarState

@Composable
fun SettingsScreen(
    onComposing: (AppBarState) -> Unit,
    navController: NavController
) {
    // Dynamic toolbar
    LaunchedEffect(key1 = true) {
        onComposing(
            AppBarState(
                title = "Settings",
                subTitle = "Personalize your app.",
            )
        )
    }

    // Settings column
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {

        // Bring user to android notification page
        Text(
            text = "Notifications",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 24.dp)
        )
        Text(
            text = "Set how you want to be notified.",
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Normal
            ),
            modifier = Modifier.padding(start = 24.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        NotificationsButton()

        // Theme selection setting
        Text(
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .padding(start = 24.dp),
            text = "Visuals"
        )
        Text(
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Normal
            ),
            modifier = Modifier
                .padding(start = 24.dp),
            text = "Select app theme.",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        ThemeRadio()

        // Password settings
        Text(
            text = "Security",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 24.dp)
        )
        Text(
            text = "Choose how you want to secure your app.",
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Normal
            ),
            modifier = Modifier.padding(start = 24.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        PasswordSwitch()
        SetPasswordButton()

    }
}

@Composable
fun SetPasswordButton() {
    Button(
        onClick = {  },
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp),
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

@Composable
fun PasswordSwitch() {
    var passcodeChecked by remember { mutableStateOf(false) }

    Row(
        Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    )  {
        Text(
            text = "Enable Passcode",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 24.dp)
        )

        Spacer(modifier = Modifier.weight(1f)) // Keep switch on the right side of screen

        Switch(
            checked = passcodeChecked,
            onCheckedChange = { passcodeChecked = it },
            modifier = Modifier.padding(end = 24.dp)
        )
    }
}

@Composable
fun ThemeRadio() {
    val displayRadioOptions = listOf("Use device settings", "Dark mode", "Light mode")
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(displayRadioOptions[0]) }

    Column(Modifier.selectableGroup()) {
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
                    onClick = null,
                    modifier = Modifier.padding(start = 24.dp)
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 24.dp)
                )
            }
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
            .padding(start = 24.dp, end = 24.dp, top = 12.dp),
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

private fun openNotificationSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
    intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
    context.startActivity(intent)
}


