package dev.lancy.drp25.utilities

import dev.lancy.drp25.data.Cuisine
import dev.lancy.drp25.data.Diet
import dev.lancy.drp25.data.FilterRanges
import dev.lancy.drp25.data.FilterValues
import dev.lancy.drp25.data.MealType
import dev.lancy.drp25.data.Recipe
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.PostgrestFilterDSL
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.postgrest.query.request.SelectRequestBuilder
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline
import kotlin.time.Duration.Companion.seconds

internal expect fun createHttpClient(): HttpClient

// Converts a set to a compatible IN list: (A,B,C)
fun <T> Set<T>.toInList(): String = joinToString(prefix = "(", postfix = ")", separator = ",") { it.toString() }

object Client {
    private val supabaseClient: SupabaseClient = createSupabaseClient(SUPABASE_URL, SUPABASE_KEY) {
        install(Postgrest)

        requestTimeout = 1.seconds
    }

    private val httpClient = createHttpClient()

    private const val SUPABASE_URL = "https://zepduojefkyzoreleeoi.supabase.co"
    private const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZS" +
        "IsInJlZiI6InplcGR1b2plZmt5em9yZWxlZW9pIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDg5NzA5OTMsImV4cC" +
        "I6MjA2NDU0Njk5M30.bXfAH98IEQzoHm3pprtPtSnoB_fcGsF2MW3raoDHz3M"

    @PostgrestFilterDSL
    fun SelectRequestBuilder.applyRecipeFilters(filters: FilterValues?) {
        if (filters == null) return

        filter {
            // Apply time range filter
            filter("cookingTime", FilterOperator.IN, filters.timeRange.toIntString())

            // Apply minimum rating filter
            filter("rating", FilterOperator.GTE, filters.rating)

            // Apply calorie range filter
            filter("calories", FilterOperator.IN, filters.calorieRange.toIntString())

            // Apply macro filters (protein, fat, carbs) - accessing JSON fields
            filter("macros->protein", FilterOperator.IN, filters.proteinRange.toIntString())
            filter("macros->fat", FilterOperator.IN, filters.fatRange.toIntString())
            filter("macros->carbs", FilterOperator.IN, filters.carbsRange.toIntString())

            // Apply meal type filters
            if (filters.selectedMealTypes.isNotEmpty()) {
                filter(
                    "mealType",
                    FilterOperator.IN,
                    filters.selectedMealTypes.map { it.name }.toSet().toInList()
                )
            }

            // Cuisines
            if (filters.selectedCuisines.isNotEmpty()) {
                filter(
                    "cuisine",
                    FilterOperator.IN,
                    filters.selectedCuisines.map { it.name }.toSet().toInList()
                )
            }

            // Diets
            if (filters.selectedDiets.isNotEmpty()) {
                filter(
                    "diet",
                    FilterOperator.IN,
                    filters.selectedDiets.map { it.name }.toSet().toInList()
                )
            }
        }
    }

    // / Fetch all recipes in the database
    suspend fun fetchRecipes(filters: FilterValues? = null): List<Recipe> = runCatching {
        supabaseClient
            .from("recipes_dup")
            .select { applyRecipeFilters(filters) }
            .decodeList<Recipe>()
    }.fold(
        onSuccess = ::identity,
        onFailure = { error ->
            println("Failed to fetch recipes: ${error.message}")
            emptyList<Recipe>()
        },
    )

    // / Fetch saved recipes in the database
    suspend fun fetchSavedRecipes(): List<Recipe> {
        val savedRecipeIds = runCatching {
            supabaseClient
                .from("saved_recipes")
                .select()
                .decodeList<RecipeID>()
        }.fold(
            onSuccess = ::identity,
            onFailure = { error ->
                println("Failed to fetch saved recipes: ${error.message}")
                emptyList<RecipeID>()
            },
        ).map { it.recipe_id.toString() }

        return fetchRecipes().filter { savedRecipeIds.contains(it.id) }
    }

    // / Returns whether a recipe is saved.
    suspend fun isSavedRecipe(recipe: Recipe): Boolean = runCatching {
        supabaseClient
            .from("saved_recipes")
            .select { filter { filter("recipe_id", FilterOperator.EQ, recipe.id) } }
            .decodeSingleOrNull<Unit>() != null
    }.fold(
        onSuccess = ::identity,
        onFailure = { error ->
            println("Failed to check saved recipe: ${error.message}")
            false
        },
    )

    @JvmInline
    @Serializable
    private value class RecipeID(
        val recipe_id: Int,
    )

    // / Sets a recipe as saved or unsaved.
    suspend fun setSaved(recipe: Recipe, saved: Boolean): Boolean = runCatching {
        when (saved) {
            true -> supabaseClient.from("saved_recipes").insert(RecipeID(recipe.id.toInt()))
            false ->
                supabaseClient
                    .from("saved_recipes")
                    .delete { filter { eq("recipe_id", recipe.id) } }
        }
    }.fold(
        onSuccess = { true },
        onFailure = { error ->
            println("Failed to set saved recipe: ${error.message}")
            false
        },
    )

    // / Fetch product details from Open Food Facts API using barcode
    suspend fun fetchProduct(barcode: String) {
        coroutineScope {
            println(barcode)
            val result = httpClient.get("https://world.openfoodfacts.org/api/v2/product/$barcode").body<String>()
            println(result)
        }
    }

    suspend fun searchRecipes(query: String): List<Recipe> {
        val (filters, reducedQuery) = parseQueryToFilters(query)
        val queriedRecipes = fetchRecipes(filters)
        val filtered = searchIngredients(reducedQuery, queriedRecipes)
        return filtered.ifEmpty { queriedRecipes }
    }

    private fun parseQueryToFilters(query: String): Pair<FilterValues, String> {
        var q = query.lowercase()

        // Time parsing
        val underRegex = Regex("""(?:under|less than|<)\s*(\d+)\s*(minutes|min|mins)""")
        val overRegex = Regex("""(?:over|more than|greater than|>)\s*(\d+)\s*(minutes|min|mins)""")
        val betweenRegex = Regex("""(\d+)\s*(?:-|to|–)\s*(\d+)\s*(minutes|min|mins)""")
        var timeRange = FilterRanges.TIME_RANGE
        when {
            betweenRegex.containsMatchIn(q) -> {
                val match = betweenRegex.find(q)!!
                val start = match.groupValues[1].toFloatOrNull() ?: FilterRanges.TIME_RANGE.start
                val end = match.groupValues[2].toFloatOrNull() ?: FilterRanges.TIME_RANGE.endInclusive
                timeRange = start..end
                q = q.replace(match.value, " ")
            }
            underRegex.containsMatchIn(q) -> {
                val match = underRegex.find(q)!!
                val end = match.groupValues[1].toFloatOrNull() ?: FilterRanges.TIME_RANGE.endInclusive
                timeRange = FilterRanges.TIME_RANGE.start..end
                q = q.replace(match.value, " ")
            }
            overRegex.containsMatchIn(q) -> {
                val match = overRegex.find(q)!!
                val start = match.groupValues[1].toFloatOrNull() ?: FilterRanges.TIME_RANGE.start
                timeRange = start..FilterRanges.TIME_RANGE.endInclusive
                q = q.replace(match.value, " ")
            }
        }

        // Rating parsing
        val ratingRegex = Regex("""(?:at least|minimum|over|more than|>=|>|)\s*(\d(?:\.\d)?)\s*stars?""")
        var rating = 3.0f
        ratingRegex.find(q)?.let {
            rating = it.groupValues[1].toFloatOrNull() ?: rating
            q = q.replace(it.value, " ")
        }

        fun parseRange(q: String, key: String, default: ClosedFloatingPointRange<Float>): Pair<ClosedFloatingPointRange<Float>, String> {
            val under = Regex("""(?:under|less than|<)\s*(\d+)\s*(g?\s*$key)""")
            val over = Regex("""(?:over|more than|greater than|>)\s*(\d+)\s*(g?\s*$key)""")
            val between = Regex("""(\d+)\s*(?:-|to|–)\s*(\d+)\s*(g?\s*$key)""")
            return when {
                between.containsMatchIn(q) -> {
                    val m = between.find(q)!!
                    val s = m.groupValues[1].toFloatOrNull() ?: default.start
                    val e = m.groupValues[2].toFloatOrNull() ?: default.endInclusive
                    val newQuery = q.replace(m.value, " ")
                    Pair(s..e, newQuery)
                }
                under.containsMatchIn(q) -> {
                    val m = under.find(q)!!
                    val e = m.groupValues[1].toFloatOrNull() ?: default.endInclusive
                    val newQuery = q.replace(m.value, " ")
                    Pair(default.start..e, newQuery)
                }
                over.containsMatchIn(q) -> {
                    val m = over.find(q)!!
                    val s = m.groupValues[1].toFloatOrNull() ?: default.start
                    val newQuery = q.replace(m.value, " ")
                    Pair(s..default.endInclusive, newQuery)
                }
                else -> Pair(default, q)
            }
        }

        val (calorieRange, q1) = parseRange(q, "cal(?:ories|s)?", FilterRanges.CALORIE_RANGE)
        val (proteinRange, q2) = parseRange(q1, "protein", FilterRanges.PROTEIN_RANGE)
        val (fatRange, q3) = parseRange(q2, "fat", FilterRanges.FAT_RANGE)
        val (carbsRange, q4) = parseRange(q3, "carbs?", FilterRanges.CARBS_RANGE)
        q = q4

        // Diets
        val diets = Diet.entries
            .filter { diet ->
                val found = q.contains(diet.name.lowercase())
                if (found) q = q.replace(diet.name.lowercase(), " ")
                found
            }.toSet()

        // Meal Types
        val mealTypes = MealType.entries
            .filter { mealType ->
                val found = q.contains(mealType.name.lowercase())
                if (found) q = q.replace(mealType.name.lowercase(), " ")
                found
            }.toSet()

        // Cuisines
        val cuisines = Cuisine.entries
            .filter { cuisine ->
                val found = q.contains(cuisine.name.lowercase())
                if (found) q = q.replace(cuisine.name.lowercase(), " ")
                found
            }.toSet()

        return FilterValues(
            timeRange = timeRange,
            calorieRange = calorieRange,
            proteinRange = proteinRange,
            fatRange = fatRange,
            carbsRange = carbsRange,
            rating = rating,
            selectedMealTypes = mealTypes,
            selectedCuisines = cuisines,
            selectedDiets = diets
        ) to q.trim()
    }

    private fun levenshtein(str1: String, str2: String): Int {
        val d = Array(str1.length + 1) { IntArray(str2.length + 1) }

        for (i in 0..str1.length) d[i][0] = i
        for (j in 0..str2.length) d[0][j] = j

        for (i in 1..str1.length) {
            for (j in 1..str2.length) {
                val cost = if (str1[i - 1] == str2[j - 1]) 0 else 1
                d[i][j] = minOf(d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1] + cost)
            }
        }

        return d[str1.length][str2.length]
    }

    private fun fuzzyMatch(queryToken: String, recipeToken: String, threshold: Int = 1): Boolean {
        if (queryToken.length <= 3 || recipeToken.length <= 3) {
            return queryToken == recipeToken // require exact match for short words
        }
        if (queryToken == recipeToken) return true
        if (recipeToken.contains(queryToken) || queryToken.contains(recipeToken)) return true
        if (kotlin.math.abs(queryToken.length - recipeToken.length) > 2) return false
        return levenshtein(queryToken, recipeToken) <= threshold
    }

    private fun tokenizeQuery(query: String): List<String> {
        val regex = Regex("""(?:no |not |-)?\w+""")
        return regex.findAll(query.lowercase())
            .map { it.value.trim() }
            .filter { it.isNotEmpty() }
            .toList()
    }

    private fun searchIngredients(query: String, recipes: List<Recipe>, threshold: Int = 0): List<Recipe> {
        val tokens = tokenizeQuery(query)
        val positiveTokens = tokens.filterNot { it.startsWith("-") || it.startsWith("no ") || it.startsWith("not ") }
        val negativeTokens = tokens.filter { it.startsWith("-") || it.startsWith("no ") || it.startsWith("not ") }
            .map { it.removePrefix("-").removePrefix("no ").removePrefix("not ") }

        return recipes.filter { recipe ->
            val hasNegative = negativeTokens.any { token ->
                recipe.name.lowercase().split("\\s+".toRegex()).any { word ->
                    fuzzyMatch(token, word, threshold)
                } || recipe.ingredients.any { ingredient ->
                    ingredient.name.lowercase().split("\\s+".toRegex()).any { word ->
                        fuzzyMatch(token, word, threshold)
                    }
                }
            }
            if (hasNegative) return@filter false

            if (positiveTokens.isEmpty()) return@filter true

            positiveTokens.any { token ->
                recipe.name.lowercase().split("\\s+".toRegex()).any { word ->
                    fuzzyMatch(token, word, threshold)
                } || recipe.ingredients.any { ingredient ->
                    ingredient.name.lowercase().split("\\s+".toRegex()).any { word ->
                        fuzzyMatch(token, word, threshold)
                    }
                }
            }
        }
    }
}


