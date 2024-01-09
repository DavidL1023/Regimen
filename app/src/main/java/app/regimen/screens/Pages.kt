package app.regimen.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.regimen.DynamicScaffoldState
import app.regimen.NoPages
import app.regimen.PageForList
import app.regimen.PageOnClickEdit
import app.regimen.PageOnClickView
import app.regimen.data.Page
import app.regimen.formatLocalDateTime
import app.regimen.groupDao
import app.regimen.pageDao
import app.regimen.raiseSheet
import app.regimen.setSheetVisibility
import app.regimen.shortenText
import app.regimen.validateGroupSelection
import app.regimen.validateTitleAndDescription
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime

// Used to sort by selection
lateinit var setSelectedOptionText: (String) -> Unit
lateinit var getSelectedOptionText: () -> String

@Composable
fun PagesScreen(
    onComposing: (DynamicScaffoldState) -> Unit
) {
    // Selected dropdown option to sort by
    var selectedOptionText by remember { mutableStateOf("Date edited") }

    // Set functions to modify option to sort by
    setSelectedOptionText = { selectedOptionText = it }
    getSelectedOptionText = { selectedOptionText }

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
                // If you want toolbar action
            },
            lazyStaggeredGridStateVisible = staggeredListFirstVisible,
            mainFabClicked = {
                raiseSheet {
                    CreatePage()
                }
            }
        )
    )


    // Pages column
    Column {

        // Search bar for pages
        AnimatedVisibility(
            visible = staggeredListFirstVisible
        ) {
            PageSearchBar()
        }

        // Sort button
        SortExpandable()

        // Pages main content of page cards
        val pageList by pageDao.getAllPages().collectAsState(initial = emptyList())
        if (pageList.isEmpty()) {
            NoPages()
        } else {
            LazyPageGrid(lazyStaggeredGridState)
        }

    }
}

// Grid of pages
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyPageGrid(lazyStaggeredGridState: LazyStaggeredGridState) {
    // Retrieve the list of groups from the DAO
    val pagesState by pageDao.getAllPages().collectAsState(initial = emptyList())

    val sortedPages = when ( getSelectedOptionText() ) {
        "Date edited" -> pagesState.sortedByDescending { it.dateTimeModified }
        "Date created" -> pagesState.sortedByDescending { it.dateTimeCreated }
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
        verticalItemSpacing = 8.dp
    ) {

        item(span = StaggeredGridItemSpan.FullLine, key = "spacerBegin") {
            Spacer(modifier = Modifier.height(0.5.dp))
        }

        items(sortedPages, key = { page -> page.id }) { page ->
            Box (modifier = Modifier.animateItemPlacement()) {
                PageForList(page, true)
            }
        }

        item(span = StaggeredGridItemSpan.FullLine, key = "spacerEnd") {
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
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
                    modifier = Modifier.rotate(animateFloatAsState(if (expanded) 180f else 0f,
                        label = ""
                    ).value),
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
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PageCard(header: String, body: String, timeDisplay: String, groupTitle: String = "",
             groupIconId: Int = -1, groupColorId: Int = -1, displayGroup: Boolean = true,
             onClick: () -> Unit, onLongPress: () -> Unit
) {

    Card(
        modifier = Modifier
            .heightIn(min = 80.dp)
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = MaterialTheme.shapes.medium)
            .combinedClickable(
                onClick = { onClick() },
                onLongClick = { onLongPress() }
            )
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
                text = shortenText(header, 24),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                style = MaterialTheme.typography.bodyMedium,
                text = shortenText(body, 64)
            )

            if (displayGroup) {
                Spacer(modifier = Modifier.padding(2.dp))

                DisplayGroup(groupName = groupTitle, iconId = groupIconId, colorId = groupColorId)

            }
        }
    }
}

// Page search bar
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PageSearchBar() {
    var text by remember { mutableStateOf("")}
    var active by remember { mutableStateOf(false)}

    val pagesState by pageDao.getAllPages().collectAsState(initial = emptyList())

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
                    .size(animateDpAsState(if (active) 25.dp else 20.dp, label = "").value)
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
        // Filter pages based on the search text
        val filteredPages = pagesState.filter { page ->
            page.title.contains(text, ignoreCase = true)
        }

        // Displaying filtered pages
        Column( modifier = Modifier.verticalScroll(rememberScrollState()) ) {

            Spacer(modifier = Modifier.height(6.dp))

            filteredPages.forEach { page ->
                val group = groupDao.getGroup(page.groupId).collectAsState(null).value

                Card(
                    modifier = Modifier
                        .heightIn(min = 80.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .shadow(elevation = 2.dp, shape = MaterialTheme.shapes.medium)
                        .combinedClickable(
                            onClick = {
                                if (group != null) {
                                    raiseSheet { PageOnClickView(page, group) }
                                }
                            },
                            onLongClick = {
                                raiseSheet { PageOnClickEdit(page) }
                            }
                        )
                ) {
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {

                        Text(text = shortenText(page.title, 40),
                            style = TextStyle(fontWeight = FontWeight.Bold)
                        )

                        Text(text = shortenText(page.body, 60),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(50.dp))

        }
    }
}

// Fab click content
@Composable
fun CreatePage(setTitle: String = "", setDescription: String = "", setGroupId: Int = -1,
               updateMode: Boolean = false, updateModeId: Int = -1) {
    val context = LocalContext.current

    var title by remember { mutableStateOf(setTitle) }
    var description by remember { mutableStateOf(setDescription) }
    var groupId by remember { mutableIntStateOf(setGroupId) }

    val maxTitleChar = 100
    val maxDescriptionChar = 100000

    var dateTimeCreated = LocalDateTime.now()
    if (updateMode) {
        val page = pageDao.getPage(updateModeId).collectAsState(null).value
        if (page != null) {
            dateTimeCreated = page.dateTimeCreated
        }
    }

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
            groupId = groupId,
            setGroup = { groupId = it }
        )

        // Save button
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                // Validate data
                val errorCodeOne = validateTitleAndDescription(title, description, maxTitleChar, maxDescriptionChar,
                    context = context)
                val errorCodeTwo = validateGroupSelection(groupId, context = context)

                if (errorCodeOne==0 && errorCodeTwo==0) {
                    CoroutineScope(Dispatchers.IO).launch {

                        val currentTime = LocalDateTime.now()
                        if (updateMode) {
                            val updatedPage = Page(
                                id = updateModeId,
                                title = title,
                                groupId = groupId,
                                body = description,
                                dateTimeModified = currentTime,
                                dateTimeCreated = dateTimeCreated // Database date time created
                            )

                            pageDao.update(updatedPage)
                        } else {
                            pageDao.insert(
                                Page(
                                    title = title,
                                    groupId = groupId,
                                    body = description,
                                    dateTimeModified = currentTime,
                                    dateTimeCreated = dateTimeCreated // Current date time
                                )
                            )
                        }

                    }
                    // Dismiss the sheet after checks
                    setSheetVisibility(false)
                }
            }
        ) {
            Text(
                text = if(updateMode) "Save" else "Create",
            )
        }

        // Delete button
        if (updateMode) {
            var deleteConfirm by remember { mutableStateOf(false) }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    modifier = Modifier.width(200.dp),
                    onClick = {
                        if (deleteConfirm) {
                            CoroutineScope(Dispatchers.IO).launch {
                                val pageDeleteById = Page(id = updateModeId)

                                pageDao.delete(pageDeleteById)
                            }

                            setSheetVisibility(false)
                        } else {
                            deleteConfirm = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(text = if (deleteConfirm) "Confirm Delete" else "Delete")
                }
            }
        }

        Spacer(modifier = Modifier.padding(vertical = 12.dp))

    }
}

@Composable
fun ViewPage(title: String, description: String, localDateTime: LocalDateTime, groupTitle: String,
             groupIconId: Int, groupColorId: Int
) {

    Column (
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val currentTime = LocalDateTime.now()

        val timeDisplay =
            if (localDateTime.toLocalDate() == currentTime.toLocalDate()) { // Same day
                formatLocalDateTime(localDateTime, "'Today at' h:mm a")
            } else if (localDateTime.year != currentTime.year) { // Different year
                formatLocalDateTime(localDateTime, "MMM dd, yyyy")
            } else { // Same year different day
                formatLocalDateTime(localDateTime, "EEEE, MMM dd")
            }

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        CircleShape
                    ) // Oval background
                    .padding(8.dp), // Padding for the oval background
                text = "Page",
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge
            )

            Row (
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.CalendarMonth,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )

                Text(
                    text = "Last edited: $timeDisplay",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            DisplayGroup(groupName = groupTitle, iconId = groupIconId, colorId = groupColorId)

        }

        Text(
            modifier = Modifier,
            text = description,
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.padding(vertical = 12.dp))
    }
}