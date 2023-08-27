package app.regimen.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Filter
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import app.regimen.AppBarState

@Composable
fun PagesScreen(
    onComposing: (AppBarState) -> Unit,
    navController: NavController
) {
    // Dynamic toolbar
    LaunchedEffect(key1 = true) {
        onComposing(
            AppBarState(
                title = "Pages",
                subTitle = "Store your thoughts.",
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

    // Pages column
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // Search bar for pages
        PageSearchBar()

        // Page Cards using a responsive grid
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalItemSpacing = 8.dp,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            content = {
                items(5) { index ->
                    PageCard()
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PageCard() {
    Card(
        onClick = { /* Do something */ },
        modifier = Modifier
            .heightIn(min = 70.dp, max = 200.dp)
            .fillMaxWidth()
    ) {
        Column {
            Text(text = "Date")
            Text(text = "Header")
            Text(text = "Body")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PageSearchBar() {
    var text by remember { mutableStateOf("")}
    var active by remember { mutableStateOf(false)}

    DockedSearchBar(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        query = text,
        onQueryChange = {
            text = it
        },
        onSearch = {
            active = false
        },
        active = active,
        onActiveChange = {
            active = it
        },
        placeholder = {
            Text(text = "Search pages")
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null
            )
        },
        trailingIcon = {
            if(active) {
                Icon(
                    modifier = Modifier.clickable {
                        if(text.isNotEmpty()) {
                            text = ""
                        } else {
                            active = false
                        }
                        text = ""
                    },
                    imageVector = Icons.Default.Close,
                    contentDescription = null
                )
            }
        }
    ) {

    }
}