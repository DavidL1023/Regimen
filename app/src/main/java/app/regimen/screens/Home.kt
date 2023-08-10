package app.regimen.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Filter
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import app.regimen.AppBarState

@Composable
fun HomeScreen(
    onComposing: (AppBarState) -> Unit,
    navController: NavController
) {
    // Title states
    var titleState by remember { mutableIntStateOf(0) }
    val titles = listOf("All", "Recurring", "Single Time")

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

        // Tabs for filter
        Column {
            TabRow(selectedTabIndex = titleState) {
                titles.forEachIndexed { index, title ->
                    Tab(
                        selected = titleState == index,
                        onClick = { titleState = index },
                        text = { Text(text = title, maxLines = 2, overflow = TextOverflow.Ellipsis) }
                    )
                }
            }
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = "Text tab ${titleState + 1} selected",
                style = MaterialTheme.typography.bodyLarge
            )
        }


    }

}