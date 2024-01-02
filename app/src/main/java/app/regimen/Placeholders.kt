package app.regimen

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

// Composables used to fill empty space and tell user first thing to do

@Composable
fun MissingGroups() {
    Text("First create a group")
}

@Composable
fun NoGroups() {
    Text("You have no groups")
}

@Composable
fun NoReminders() {
    Text("You have nothing to complete")
}

@Composable
fun NoPages() {
    Text("You have nothing written")
}