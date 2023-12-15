package app.regimen.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import app.regimen.DynamicScaffoldState
import app.regimen.data.Page
import app.regimen.formatLocalDateTime
import app.regimen.groupDao
import app.regimen.pageDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// Used to sort by selection
lateinit var setSelectedOptionText: (String) -> Unit
lateinit var getSelectedOptionText: () -> String

// Boolean for sheet visibility
lateinit var setSheetVisibilityPages: (Boolean) -> Unit

// Used to edit the content displayed when bottom sheet is visible
object SheetContentPages {
    var sheetContent: @Composable () -> Unit = { CreatePage() }
}

@Composable
fun PagesScreen(
    onComposing: (DynamicScaffoldState) -> Unit
) {
    // Selected dropdown option to sort by
    var selectedOptionText by remember { mutableStateOf("Date edited") }

    // Set functions to modify option to sort by
    setSelectedOptionText = { selectedOptionText = it }
    getSelectedOptionText = { selectedOptionText }

    // Show sheet
    var sheetVisibility by remember { mutableStateOf(false) }

    // Set functions to modify show sheet
    setSheetVisibilityPages = { sheetVisibility = it }

    // Used to hide on scroll
    val lazyStaggeredGridState = rememberLazyStaggeredGridState()
    val staggeredListFirstVisible by remember(lazyStaggeredGridState) {
        derivedStateOf {
            lazyStaggeredGridState.firstVisibleItemIndex == 0
        }
    }

    // Dynamic toolbar
    onComposing(
        DynamicScaffoldState(
            toolbarTitle = "Pages",
            toolbarSubtitle = "Store your thoughts.",
            toolbarActions = {
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = null
                    )
                }
            },
            lazyStaggeredGridStateVisible = staggeredListFirstVisible,
            bottomSheetBoxContent = { SheetContentPages.sheetContent() },
            showBottomSheet = sheetVisibility,
            sheetDropdownDismissed = {
                setSheetVisibilityPages(false)

                //Set sheet content back to default empty state
                SheetContentPages.sheetContent = { CreatePage() }
            }
        )
    )


    // Pages column
    Column {

        // Search bar for pages
        AnimatedVisibility(
            visible = staggeredListFirstVisible,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            PageSearchBar()
        }

        // Sort button
        SortExpandable()

        // Page Cards using a responsive grid
        LazyPageGrid(lazyStaggeredGridState)

    }
}

// Grid of pages
@Composable
fun LazyPageGrid(lazyStaggeredGridState: LazyStaggeredGridState) {
    // Retrieve the list of groups from the DAO
    val pagesState by pageDao.getAllPages().collectAsState(initial = emptyList())

    val sortedPages = when ( getSelectedOptionText() ) {
        "Date edited" -> pagesState.sortedBy { it.dateTimeModified }
        "Date created" -> pagesState.sortedBy { it.dateTimeCreated }
        "Header" -> pagesState.sortedBy { it.title }
        else -> pagesState
    }

    LazyVerticalStaggeredGrid(
        state = lazyStaggeredGridState,
        columns = StaggeredGridCells.Fixed(2),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalItemSpacing = 8.dp,
        content = {

            item(span = StaggeredGridItemSpan.FullLine) {
                Spacer(modifier = Modifier.height(0.5.dp))
            }

            items(sortedPages) { page ->
                val timeDisplay: String
                val dateTimeModified = page.dateTimeModified
                val currentTime = LocalDateTime.now()

                timeDisplay = if (dateTimeModified.toLocalDate() == currentTime.toLocalDate()) { // Same day
                    formatLocalDateTime(dateTimeModified, "'Today at' h:mm a")
                } else if (dateTimeModified.year != currentTime.year) { // Different year
                    formatLocalDateTime(dateTimeModified, "MMM dd, yyyy")
                } else { // Same year different day
                    formatLocalDateTime(dateTimeModified, "EEEE, MMM dd")
                }

                val group = groupDao.getGroup(page.groupId).collectAsState(null).value

                if (group != null) {
                    PageCard(
                        header = page.title,
                        body = page.body,
                        timeDisplay = timeDisplay,
                        groupDisplay = group.title,
                        onClick = {
                            SheetContentPages.sheetContent = {
                                CreatePage(setTitle = page.title,
                                    setDescription = page.body,
                                    setGroupId = page.groupId,
                                    updateMode = true
                                )
                            }
                            setSheetVisibilityPages(true)
                        }
                    )
                }
            }

            item(span = StaggeredGridItemSpan.FullLine) {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    )
}

// The sort expandable selection menu
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortExpandable() {
    val options = listOf("Date edited", "Date created", "Header")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier
            .padding(start = 16.dp, bottom = 12.dp)
    ) {
        OutlinedTextField(
            modifier = Modifier.menuAnchor(),
            readOnly = true,
            value = getSelectedOptionText(),
            onValueChange = {},
            label = { Text(text = "Sort by") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Sort,
                    contentDescription = null
                )
            },
            trailingIcon = {
                Icon(
                    modifier = Modifier.rotate(animateFloatAsState(if (expanded) 180f else 0f).value),
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null
                )},
            shape = RoundedCornerShape(18.dp)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(text = selectionOption) },
                    onClick = {
                        setSelectedOptionText(selectionOption)
                        expanded = false
                    }
                )
            }
        }
    }

}

// A page card
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PageCard(header: String, body: String, timeDisplay: String, groupDisplay: String,
             displayGroup: Boolean = true,  onClick: () -> Unit) {
    Card(
        onClick = { onClick() },
        modifier = Modifier
            .heightIn(min = 80.dp, max = 200.dp)
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = MaterialTheme.shapes.medium)
    ) {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            Text(
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .alpha(0.5f),
                text = timeDisplay,
                style = MaterialTheme.typography.labelSmall
            )
            Text(
                text = header,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                style = MaterialTheme.typography.bodyMedium,
                text = body
            )

            if (displayGroup) {
                Spacer(modifier = Modifier.padding(2.dp))

                Row (
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier
                            .alpha(0.80f)
                            .width(16.dp),
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = null
                    )

                    Text(
                        modifier = Modifier
                            .alpha(0.80f)
                            .padding(top = 2.dp),
                        text = groupDisplay,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

            }
        }
    }
}

// Page search bar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PageSearchBar() {
    var text by remember { mutableStateOf("")}
    var active by remember { mutableStateOf(false)}

    DockedSearchBar(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 12.dp),
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
                contentDescription = null,
                modifier = Modifier
                    .size(animateDpAsState(if (active) 25.dp else 20.dp).value)
            )
        },
        trailingIcon = {
            if (active) {
                Icon(
                    modifier = Modifier.clickable {
                        if (text.isNotEmpty()) {
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

// Fab click content
@Composable
fun CreatePage(setTitle: String = "", setDescription: String = "", setGroupId: Int = -1,
               updateMode: Boolean = false) {
    var title by remember { mutableStateOf(setTitle) }
    var description by remember { mutableStateOf(setDescription) }
    var groupId by remember { mutableIntStateOf(setGroupId) }

    Column (
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Explanation
        CreateTopExplanation(header = if(updateMode) "Edit page" else "Create page",
            subtitle = "Keep pages for details and to store anything.")

        // Title and description
        CreateTitleAndDescription(
            title = title,
            description = description,
            setTitle = { title = it },
            setDescription = { description = it}
        )

        // Group
        CreateGroupSelector(
            group = groupId,
            setGroup = { groupId = it }
        )

        // Save button
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    val currentTime = LocalDateTime.now()
                    if (updateMode) {
                        pageDao.getPage(groupId).collect { page ->
                            page.title = title
                            page.groupId = groupId
                            page.body = description
                            page.dateTimeModified = currentTime
                            // We don't edit dateTimeCreated

                            pageDao.update(page)
                        }
                    } else {
                        pageDao.insert(
                            Page(
                                title = title,
                                groupId = groupId,
                                body = description,
                                dateTimeModified = currentTime,
                                dateTimeCreated = currentTime
                            )
                        )
                    }

                }
                // Dismiss the sheet after checks
                setSheetVisibilityPages(false)

            }
        ) {
            Text(
                text = if(updateMode) "Save" else "Create",
            )
        }

        Spacer(modifier = Modifier.padding(vertical = 12.dp))

    }
}

