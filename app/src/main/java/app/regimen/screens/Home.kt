package app.regimen.screens

import android.widget.ToggleButton
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Filter
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.twotone.AirlineSeatLegroomNormal
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import app.regimen.AppBarState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onComposing: (AppBarState) -> Unit,
    navController: NavController
) {
    // Dynamic toolbar
    LaunchedEffect(key1 = true) {
        onComposing(
            AppBarState(
                title = "Home",
                actions = {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = null
                        )
                    }
                }
            )
        )
    }

    // Home column
    Column {

        // Horizontal scroll for calendar filter
        CalendarFilterChips()

        // Filter by reminder type
        FilterChips()

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CalendarFilterChips() {
    // Smoothly scroll 100px on first composition
    val state = rememberScrollState()
    LaunchedEffect(Unit) { state.animateScrollTo(100) }

    Row(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .horizontalScroll(state)
    ) {
        var selected by remember { mutableStateOf(false) }
        repeat(14) {

            ElevatedFilterChip(
                selected = selected,
                onClick = { /*TODO*/ },
                label = {
                    Text("Thu")
                    Text("12/$it")
                }
            )

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChips() {
    var allSelected by remember { mutableStateOf(true) }
    var recurringSelected by remember { mutableStateOf(false) }
    var singleTimeSelected by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        FilterChip(
            label = { Text(text = "         All         ") },
            selected = allSelected,
            onClick = {
                allSelected = true
                recurringSelected = false
                singleTimeSelected = false
            }
        )
        FilterChip(
            modifier = Modifier.padding(horizontal = 8.dp),
            label = { Text(text = "Recurring") },
            selected = recurringSelected,
            leadingIcon = {
                Icon(
                    Icons.Default.Cached,
                    contentDescription = null,
                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                )
            },
            onClick = {
                recurringSelected = true
                allSelected = false
                singleTimeSelected = false
            }
        )
        FilterChip(
            label = { Text(text = "Single Time") },
            selected = singleTimeSelected,
            leadingIcon = {
                Icon(
                    Icons.Filled.Today,
                    contentDescription = null,
                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                )
            },
            onClick = {
                singleTimeSelected = true
                allSelected = false
                recurringSelected = false
            }
        )
    }
}
