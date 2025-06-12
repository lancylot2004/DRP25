package dev.lancy.drp25.ui.main.search

import dev.lancy.drp25.data.*
import dev.lancy.drp25.utilities.fetchRecipes

suspend fun searchRecipes(query: String): List<Recipe> {
    val filters = parseQueryToFilters(query)
    return fetchRecipes(filters)
}

fun parseQueryToFilters(query: String): FilterValues {
    val q = query.lowercase()

    // Time parsing
    val underRegex = Regex("""(?:under|less than|<)\s*(\d+)\s*(minutes|min|mins)?""")
    val overRegex = Regex("""(?:over|more than|greater than|>)\s*(\d+)\s*(minutes|min|mins)?""")
    val betweenRegex = Regex("""(\d+)\s*(?:-|to|–)\s*(\d+)\s*(minutes|min|mins)?""")
    var timeRange = FilterRanges.TIME_DEFAULT
    when {
        betweenRegex.containsMatchIn(q) -> {
            val match = betweenRegex.find(q)!!
            val start = match.groupValues[1].toFloatOrNull() ?: FilterRanges.TIME_DEFAULT.start
            val end = match.groupValues[2].toFloatOrNull() ?: FilterRanges.TIME_DEFAULT.endInclusive
            timeRange = start..end
        }
        underRegex.containsMatchIn(q) -> {
            val match = underRegex.find(q)!!
            val end = match.groupValues[1].toFloatOrNull() ?: FilterRanges.TIME_DEFAULT.endInclusive
            timeRange = FilterRanges.TIME_RANGE.start..end
        }
        overRegex.containsMatchIn(q) -> {
            val match = overRegex.find(q)!!
            val start = match.groupValues[1].toFloatOrNull() ?: FilterRanges.TIME_DEFAULT.start
            timeRange = start..FilterRanges.TIME_RANGE.endInclusive
        }
    }

    // Rating parsing
    val ratingRegex = Regex("""(?:at least|minimum|over|more than|>=|>|)\s*(\d(?:\.\d)?)\s*(?:stars?)""")
    var rating = 3.0f
    ratingRegex.find(q)?.let {
        rating = it.groupValues[1].toFloatOrNull() ?: rating
    }

    // Calories, protein, fat, carbs parsing
    fun parseRange(q: String, key: String, default: ClosedFloatingPointRange<Float>): ClosedFloatingPointRange<Float> {
        val under = Regex("""(?:under|less than|<)\s*(\d+)\s*(?:$key)?""")
        val over = Regex("""(?:over|more than|greater than|>)\s*(\d+)\s*(?:$key)?""")
        val between = Regex("""(\d+)\s*(?:-|to|–)\s*(\d+)\s*(?:$key)?""")
        return when {
            between.containsMatchIn(q) -> {
                val m = between.find(q)!!
                val s = m.groupValues[1].toFloatOrNull() ?: default.start
                val e = m.groupValues[2].toFloatOrNull() ?: default.endInclusive
                s..e
            }
            under.containsMatchIn(q) -> {
                val m = under.find(q)!!
                val e = m.groupValues[1].toFloatOrNull() ?: default.endInclusive
                default.start..e
            }
            over.containsMatchIn(q) -> {
                val m = over.find(q)!!
                val s = m.groupValues[1].toFloatOrNull() ?: default.start
                s..default.endInclusive
            }
            else -> default
        }
    }
    val calorieRange = parseRange(q, "calories?", FilterRanges.CALORIE_DEFAULT)
    val proteinRange = parseRange(q, "g protein", FilterRanges.PROTEIN_DEFAULT)
    val fatRange = parseRange(q, "g fat", FilterRanges.FAT_DEFAULT)
    val carbsRange = parseRange(q, "g carbs?", FilterRanges.CARBS_DEFAULT)

    // Diets
    val diets = Diet.entries.filter { d ->
        val name = d.name.lowercase().replace("_", " ")
        name in q || d.toString().lowercase() in q
    }.toSet()

    // Meal Types
    val mealTypes = MealType.entries.filter { m ->
        val name = m.name.lowercase().replace("_", " ")
        name in q || m.toString().lowercase() in q
    }.toSet()

    // Cuisines
    val cuisines = Cuisine.entries.filter { c ->
        val name = c.name.lowercase().replace("_", " ")
        name in q || c.toString().lowercase() in q
    }.toSet()

    // Included/Avoided Ingredients
    val words = q.split(Regex("""\W+"""))
    val includedIngredients = Ingredients.entries.filter { ing ->
        words.any { it == ing.name.lowercase() || it == ing.displayName.lowercase() }
    }.toSet()
    val avoidedIngredients = Ingredients.entries.filter { ing ->
        Regex("""(?:no|without|avoid)\s+${Regex.escape(ing.name.lowercase())}""").containsMatchIn(q) ||
                Regex("""(?:no|without|avoid)\s+${Regex.escape(ing.displayName.lowercase())}""").containsMatchIn(q)
    }.toSet()

    return FilterValues(
        timeRange = timeRange,
        rating = rating,
        selectedMealTypes = mealTypes,
        selectedCuisines = cuisines,
        selectedDiets = diets,
        includedIngredients = includedIngredients,
        avoidedIngredients = avoidedIngredients,
        calorieRange = calorieRange,
        proteinRange = proteinRange,
        fatRange = fatRange,
        carbsRange = carbsRange
    )
}