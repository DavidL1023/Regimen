package app.regimen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.ChildFriendly
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

// Define an enum class for colors
enum class ColorsEnum(val color: Color, val intValue: Int) {
    Red(Color(0xFFC93C20), 0),
    Green(Color(0xFF57A639), 1),
    Blue(Color(0xFF3B83BD), 2),
    Yellow(Color(0xFFFFFF00), 3),
    Purple(Color(0xFF800080), 4),
    Cyan(Color(0xFF00FFFF), 5),
    Magenta(Color(0xFFFF00FF), 6),
    Orange(Color(0xFFFFA500), 7),
    Pink(Color(0xFFFFC0CB), 8),
    Brown(Color(0xFF8E402A), 9),
    Teal(Color(0xFF008080), 10),
    TerraBrown(Color(0xFF4E3B31), 11),
    Violet(Color(0xFF924E7D), 12),
    Olive(Color(0xFF808000), 13),
    SlateGray(Color(0xFF708090), 14);

    fun toComposeColor(): Color {
        return color
    }

    companion object {
        fun colorFromIntValue(value: Int): Color? {
            return ColorsEnum.values().firstOrNull { it.intValue == value }?.color
        }
    }
}

// Define an enum class for colors
enum class IconsEnum(val icon: ImageVector, val intValue: Int) {
    Person(Icons.Default.Person, 0),
    Shopping(Icons.Default.ShoppingBag, 1),
    Water(Icons.Default.WaterDrop, 2),
    Pill(Icons.Default.Medication, 3),
    Book(Icons.Default.Class, 4),
    Dumbbell(Icons.Default.FitnessCenter, 5),
    Plane(Icons.Default.Flight, 6),
    Music(Icons.Default.MusicNote, 7),
    PawPrint(Icons.Default.Pets, 8),
    LightBulb(Icons.Default.Lightbulb, 9),
    Car(Icons.Default.DirectionsCar, 10),
    Plant(Icons.Default.Favorite, 11),
    Stroller(Icons.Default.ChildFriendly, 12),
    Work(Icons.Default.Work, 13),
    Birthday(Icons.Default.Cake, 14);

    fun toComposeIcon(): ImageVector {
        return icon
    }

    companion object {
        fun iconFromIntValue(value: Int): ImageVector? {
            return values().firstOrNull { it.intValue == value }?.icon
        }
    }
}