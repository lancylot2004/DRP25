package dev.lancy.drp25.utilities

import dev.lancy.drp25.data.Cuisine
import dev.lancy.drp25.data.Diet
import dev.lancy.drp25.data.FilterRanges
import dev.lancy.drp25.data.FilterValues
import dev.lancy.drp25.data.Ingredients
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
        ).map { it.recipe_id }

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

    @Serializable
    private data class RecipeID(
        val recipe_id: String
    )

    // / Sets a recipe as saved or unsaved.
    suspend fun setSaved(recipe: Recipe, saved: Boolean): Boolean = runCatching {
        when (saved) {
            true -> supabaseClient.from("saved_recipes").insert(RecipeID(recipe.id))
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
        val filters = parseQueryToFilters(query)
        return fetchRecipes(filters)
    }

    fun parseQueryToFilters(query: String): FilterValues {
        val q = query.lowercase()

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
            }
            underRegex.containsMatchIn(q) -> {
                val match = underRegex.find(q)!!
                val end = match.groupValues[1].toFloatOrNull() ?: FilterRanges.TIME_RANGE.endInclusive
                timeRange = FilterRanges.TIME_RANGE.start..end
            }
            overRegex.containsMatchIn(q) -> {
                val match = overRegex.find(q)!!
                val start = match.groupValues[1].toFloatOrNull() ?: FilterRanges.TIME_RANGE.start
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
            val under = Regex("""(?:under|less than|<)\s*(\d+)\s*(g?\s*$key)""")
            val over = Regex("""(?:over|more than|greater than|>)\s*(\d+)\s*(g?\s*$key)""")
            val between = Regex("""(\d+)\s*(?:-|to|–)\s*(\d+)\s*(g?\s*$key)""")
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
        val calorieRange = parseRange(q, "cal(?:ories|s)?", FilterRanges.CALORIE_RANGE)
        val proteinRange = parseRange(q, "protein", FilterRanges.PROTEIN_RANGE)
        val fatRange = parseRange(q, "fat", FilterRanges.FAT_RANGE)
        val carbsRange = parseRange(q, "carbs?", FilterRanges.CARBS_RANGE)

        // Diets
        val diets = Diet.entries
            .filter { d ->
                val name = d.name.lowercase().replace("_", " ")
                name in q || d.toString().lowercase() in q
            }.toSet()

        // Meal Types
        val mealTypes = MealType.entries
            .filter { m ->
                val name = m.name.lowercase().replace("_", " ")
                name in q || m.toString().lowercase() in q
            }.toSet()

        // Cuisines
        val cuisines = Cuisine.entries
            .filter { c ->
                val name = c.name.lowercase().replace("_", " ")
                name in q || c.toString().lowercase() in q
            }.toSet()

        // Included/Avoided Ingredients
        val words = q.split(Regex("""\W+"""))
        val avoidedIngredients = Ingredients.entries
            .filter { ing ->
                Regex("""(?:no|without|avoid)\s+${Regex.escape(ing.name.lowercase())}""").containsMatchIn(q) ||
                    Regex("""(?:no|without|avoid)\s+${Regex.escape(ing.displayName.lowercase())}""").containsMatchIn(q)
            }.toSet()

        val includedIngredients = Ingredients.entries
            .filter { ing ->
                // Only include if not in avoided
                !avoidedIngredients.contains(ing) &&
                    words.any { it == ing.name.lowercase() || it == ing.displayName.lowercase() }
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
            selectedDiets = diets,
            includedIngredients = includedIngredients,
            avoidedIngredients = avoidedIngredients,
        )
    }

    // Extension function for Client to save new recipes
    suspend fun Client.saveNewRecipe(recipe: Recipe, savedRecipeIdsManager: PersistenceManager<Set<String>>): Boolean = runCatching {
        supabaseClient
            .from("recipes_dup")
            .insert(recipe)
    }.fold(
        onSuccess = {
            savedRecipeIdsManager.update {
                this + recipe.id
            }
            true
        },
        onFailure = { error ->
            println("Failed to save recipe: ${error.message}")
            false
        }
    )
}
