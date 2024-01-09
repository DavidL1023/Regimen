package app.regimen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

// Composables used to fill empty space and tell user first thing to do

@Composable
fun MissingGroups() {
    Box(contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 8.dp, bottom = 82.dp)) {
            Image(
                painter = painterResource(R.drawable.no_groups),
                contentDescription = null
            )
            Text(
                "Start by creating a group.",
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}

@Composable
fun NoGroups() {
    Box(contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(R.drawable.no_groups),
                contentDescription = null
            )
            Text(
                "Start by creating a group here.",
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}

@Composable
fun NoReminders() {
    Box(contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(R.drawable.no_reminders),
                contentDescription = null
            )
            Text(
                "Nothing to complete!",
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}

@Composable
fun NoPages() {
    Box(contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(R.drawable.no_pages),
                contentDescription = null
            )
            Text(
                "Nothing is written!",
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}