package app.regimen.screens

import android.widget.ToggleButton
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.CallMerge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Filter
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.twotone.AirlineSeatLegroomNormal
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SingleChoiceSegmentedButtonRowScope
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import app.regimen.AppBarState
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment

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
                subTitle = "Manage your life.",
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
    Column (
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // Horizontal scroll for calendar filter
        CalendarFilterChips()

        // Filter by reminder type
        CategoryFilterSegmented()

        // Reminder cards
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = {
                items(5) { index ->
                    ReminderCard()
                }
            }
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderCard() {
    Card(
        onClick = { /* Do something */ },
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
    ) {
        Box(Modifier.fillMaxSize()) {
            Column {
                Text(text = "Reminder")
            }
        }

    }
}

@Composable
private fun CalendarFilterChips() {
    val selectedChipIndex = remember { mutableIntStateOf(0) }
    val state = rememberScrollState()

    Row(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .horizontalScroll(state),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        for (index in 0 until 14) {
            val isSelected = index == selectedChipIndex.intValue

            VerticalChip(
                isSelected = isSelected,
                onClick = { selectedChipIndex.intValue = index },
                topText = "Thu",
                bottomText = "${index+1}"
            )
        }
    }
}

@Composable
private fun VerticalChip(
    isSelected: Boolean,
    onClick: () -> Unit,
    topText: String,
    bottomText: String
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp)) //allows ripple to match shape
            .clickable(
                onClick = onClick
            )
            .background(
                color = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(9.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = topText,
                modifier = Modifier
                    .padding(horizontal = 2.dp)
                    .alpha(if (isSelected) 1f else 0.6f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                modifier = Modifier.alpha(if (isSelected) 1f else 0.6f),
                text = bottomText,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryFilterSegmented() {
    var selectedIndex by remember { mutableIntStateOf(0) }
    val options = listOf("All", "Recurring", "Single Time")
    val icons = listOf(
        null,
        Icons.Default.Cached,
        Icons.Default.Today
    )

    SingleChoiceSegmentedButtonRow (
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        options.forEachIndexed { index, label ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.shape(position = index, count = options.size),
                onClick = { selectedIndex = index },
                selected = index == selectedIndex,
                icon = {

                    SegmentedButtonDefaults.SegmentedButtonIcon(
                        active = index == selectedIndex,
                        inactiveContent = {
                            icons[index]?.let {
                                Icon(
                                    imageVector = it,
                                    contentDescription = null,
                                    Modifier.size(SegmentedButtonDefaults.IconSize)
                                )
                            }
                        }
                    )

                },
            ) {
                Text(label)
            }
        }
    }

}
