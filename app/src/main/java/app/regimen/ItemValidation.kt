package app.regimen

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext


// Return int code and make toast depending on which errors
fun validateTitleAndDescription(
    title: String,
    description: String,
    titleMaxChar: Int,
    descriptionMaxChar: Int,
    context: Context
): Int {

    // Default no error
    var errorCode = 0

    // Check title length
    if (title.length > titleMaxChar) {
        errorCode = 1
        Toast.makeText(context, "Title is too long", Toast.LENGTH_SHORT).show()
    }
    // Check description length
    else if (description.length > descriptionMaxChar) {
        errorCode = 2
        Toast.makeText(context, "Description is too long", Toast.LENGTH_SHORT).show()
    }
    // Check if title is empty
    else if (title.isEmpty()) {
        errorCode = 1
        Toast.makeText(context, "Title cannot be empty", Toast.LENGTH_SHORT).show()
    }

    return errorCode
}

fun validateTimeAndDate(
    date: String,
    time: String,
    specificTimeEnabled: Boolean,
    context: Context
): Int {

    // Default no error
    var errorCode = 0

    // Check date
    if (date.isEmpty()) {
        errorCode = 1
        Toast.makeText(context, "Set a date", Toast.LENGTH_SHORT).show()
    }
    // Check time
    else if (specificTimeEnabled && time.isEmpty()) {
        errorCode = 2
        Toast.makeText(context, "Set a time", Toast.LENGTH_SHORT).show()
    }

    return errorCode
}

fun validateCustomPeriod(
    customPeriod: String,
    customPeriodEnabled: Boolean,
    context: Context
): Int {

    // Default no error
    var errorCode = 0

    if (!customPeriodEnabled) {
        return errorCode
    }

    // Check custom period
    if (customPeriod.isEmpty()) {
        errorCode = 1
        Toast.makeText(context, "Period cannot be empty", Toast.LENGTH_SHORT).show()
    } else if (customPeriod == "0") {
        errorCode = 1
        Toast.makeText(context, "Period cannot be 0", Toast.LENGTH_SHORT).show()
    } else if(customPeriod.toInt() > 365) {
        errorCode = 1
        Toast.makeText(context, "Period cannot pass 365", Toast.LENGTH_SHORT).show()
    }

    return errorCode
}

fun validateGroupSelection(
    groupId: Int,
    context: Context
): Int {

    // Default no error
    var errorCode = 0

    // Check group
    if (groupId == -1) {
        errorCode = 1
        Toast.makeText(context, "Select a group", Toast.LENGTH_SHORT).show()
    }

    return errorCode
}