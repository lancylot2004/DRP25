package dev.lancy.drp25.ui.main.me

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.IconButton
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.LeafNode
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.CircleUserRound
import com.composables.icons.lucide.Settings
import dev.lancy.drp25.ui.RootNode
import dev.lancy.drp25.ui.main.MainNode
import dev.lancy.drp25.ui.shared.NavConsumer
import dev.lancy.drp25.ui.shared.NavConsumerImpl
import dev.lancy.drp25.utilities.ColourScheme
import dev.lancy.drp25.utilities.rememberUserNameManager
import dev.lancy.drp25.utilities.rememberRecipesCookedManager
import dev.lancy.drp25.utilities.rememberSavedPreferencesManager
import dev.lancy.drp25.utilities.rememberCreatedRecipesManager
import dev.lancy.drp25.utilities.rememberPreferenceState

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.plus
import kotlinx.datetime.minus
import kotlinx.datetime.Month
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.isoDayNumber

import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import com.composables.icons.lucide.ChevronLeft
import com.composables.icons.lucide.ChevronRight

import dev.lancy.drp25.utilities.rememberDailyCookingActivityManager
import kotlinx.coroutines.launch

class MeNode(
    nodeContext: NodeContext,
    parent: MainNode
) : LeafNode(nodeContext),
    NavConsumer<MainNode.MainTarget, MainNode> by NavConsumerImpl(parent) {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content(modifier: Modifier) {
        val scope = rememberCoroutineScope()

        val userNameManager = rememberUserNameManager()
        val recipesCookedManager = rememberRecipesCookedManager()
        val savedPreferencesManager = rememberSavedPreferencesManager()
        val createdRecipesManager = rememberCreatedRecipesManager()
        val dailyCookingActivityManager = rememberDailyCookingActivityManager()

        val (userName, setUserName) = rememberPreferenceState(userNameManager)
        val (recipesCooked, setRecipesCooked) = rememberPreferenceState(recipesCookedManager)
        val (savedPreferences, setSavedPreferences) = rememberPreferenceState(savedPreferencesManager)
        val (createdRecipes, setCreatedRecipes) = rememberPreferenceState(createdRecipesManager)
        val dailyCookingMap by dailyCookingActivityManager.state.collectAsState()

        var showNameEditDialog by remember { mutableStateOf(false) }
        var tempUserName by remember { mutableStateOf(userName) }

        var currentCalendarDisplayDate by remember { mutableStateOf(Clock.System.todayIn(TimeZone.currentSystemDefault())) }

        Box(
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            IconButton(
                onClick = {
                    scope.launch {
                        this@MeNode
                            .navParent
                            .superNavigate<RootNode.RootTarget>(RootNode.RootTarget.Settings)
                    }
                },
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = Lucide.Settings,
                    contentDescription = "Settings",
                    tint = Color.White
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .padding(top = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF444444)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Lucide.CircleUserRound,
                        contentDescription = "Profile",
                        tint = Color.White,
                        modifier = Modifier.size(64.dp)
                    )
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    text = userName,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { showNameEditDialog = true }
                )

                Spacer(Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    StatItem("Cooked", recipesCooked.toString())
                    StatItem("Saved", savedPreferences.toString())
                    StatItem("Created", createdRecipes.toString())
                }

                Spacer(Modifier.height(32.dp))

                Divider(
                    color = ColourScheme.outlineVariant,
                    thickness = 2.dp,
                    modifier = Modifier
                        .padding(horizontal = 32.dp)
                        .fillMaxWidth()
                )

                Spacer(Modifier.height(32.dp))

                Text(
                    text = "Cooking Activity",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                )

                CookingCalendar(
                    currentDisplayDate = currentCalendarDisplayDate,
                    dailyCookingMap = dailyCookingMap,
                    onMonthChange = { newDate -> currentCalendarDisplayDate = newDate },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))
            }

            // User Name Edit Dialog
            if (showNameEditDialog) {
                AlertDialog(
                    onDismissRequest = { showNameEditDialog = false },
                    title = { Text("Edit User Name") },
                    text = {
                        OutlinedTextField(
                            value = tempUserName,
                            onValueChange = { tempUserName = it },
                            label = { Text("User Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                setUserName(tempUserName)
                                showNameEditDialog = false
                            }
                        ) {
                            Text("Save")
                        }
                    },
                    dismissButton = {
                        Button(onClick = { showNameEditDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(text = label, color = Color.LightGray, fontSize = 13.sp)
    }
}

@Composable
private fun CookingCalendar(
    currentDisplayDate: LocalDate,
    dailyCookingMap: Map<String, Int>,
    onMonthChange: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentMonthName = currentDisplayDate.month.name.lowercase().replaceFirstChar { it.uppercase() }
    val currentMonthYear = "$currentMonthName ${currentDisplayDate.year}"

    val colorForZeroActivity = Color(0xFF424242)
    val colorForOneRecipe = Color(0xFF6CC644)
    val colorForTwoPlusRecipes = Color(0xFF44AA44)

    Column(
        modifier = modifier
            .padding(16.dp)
    ) {
        // Month Navigation Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onMonthChange(currentDisplayDate.minus(1, DateTimeUnit.MONTH)) }) {
                Icon(Lucide.ChevronLeft, contentDescription = "Previous Month", tint = Color.White)
            }
            Text(
                text = currentMonthYear,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { onMonthChange(currentDisplayDate.plus(1, DateTimeUnit.MONTH)) }) {
                Icon(Lucide.ChevronRight, contentDescription = "Next Month", tint = Color.White)
            }
        }
        Spacer(Modifier.height(8.dp))

        // Weekday Headers (Mon, Tue, etc.)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach { day ->
                Text(day, color = Color.LightGray, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }
        Spacer(Modifier.height(4.dp))

        // Calendar Grid
        val firstDayOfMonth = LocalDate(currentDisplayDate.year, currentDisplayDate.month, 1)
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())

        val startOffset = (firstDayOfMonth.dayOfWeek.isoDayNumber - DayOfWeek.MONDAY.isoDayNumber + 7) % 7

        val daysInMonth = when (currentDisplayDate.month) {
            Month.JANUARY -> 31
            Month.FEBRUARY -> if (isLeapYear(currentDisplayDate.year)) 29 else 28
            Month.MARCH -> 31
            Month.APRIL -> 30
            Month.MAY -> 31
            Month.JUNE -> 30
            Month.JULY -> 31
            Month.AUGUST -> 31
            Month.SEPTEMBER -> 30
            Month.OCTOBER -> 31
            Month.NOVEMBER -> 30
            Month.DECEMBER -> 31
            else -> 30
        }

        val calendarDays = mutableListOf<LocalDate?>()
        repeat(startOffset) { calendarDays.add(null) }
        for (dayNum in 1..daysInMonth) {
            calendarDays.add(LocalDate(currentDisplayDate.year, currentDisplayDate.month, dayNum))
        }
        val remainingCells = (7 - (calendarDays.size % 7)) % 7
        repeat(remainingCells) { calendarDays.add(null) }

        Column(modifier = Modifier.fillMaxWidth()) {
            calendarDays.chunked(7).forEach { week ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    week.forEach { date ->
                        val isCurrentMonthDay = date != null && date.month == currentDisplayDate.month && date.year == currentDisplayDate.year
                        val isToday = date == today

                        val cookCount = if (isCurrentMonthDay && date != null) {
                            dailyCookingMap[date.toString()] ?: 0
                        } else {
                            0
                        }

                        val cellColor = when {
                            !isCurrentMonthDay -> Color.Transparent
                            cookCount == 0 -> colorForZeroActivity
                            cookCount == 1 -> colorForOneRecipe
                            else -> colorForTwoPlusRecipes
                        }

                        Box(
                            modifier = Modifier
                                .size(32.dp) // Adjusted from 36.dp to 32.dp for even smaller size
                                .clip(RoundedCornerShape(6.dp)) // Slightly smaller corner radius for consistency
                                .background(cellColor)
                                .then(
                                    if (isToday) {
                                        Modifier.border(2.dp, Color(0xFFBBBBBB), RoundedCornerShape(6.dp))
                                    } else {
                                        Modifier
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isCurrentMonthDay && date != null) {
                                Text(
                                    text = date.dayOfMonth.toString(),
                                    fontSize = 12.sp, // Adjusted from 14.sp to 12.sp for smaller date text
                                    color = if (isToday) Color.Black else Color.White,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
                Spacer(Modifier.height(3.dp)) // Adjusted from 4.dp to 3.dp for slightly less space between weeks
            }
        }
    }
}

// Helper function to check if a year is a leap year
private fun isLeapYear(year: Int): Boolean {
    return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
}