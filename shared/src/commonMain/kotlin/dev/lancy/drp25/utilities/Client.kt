package dev.lancy.drp25.utilities

import dev.lancy.drp25.data.Comment
import dev.lancy.drp25.data.Cuisine
import dev.lancy.drp25.data.Diet
import dev.lancy.drp25.data.FilterRanges
import dev.lancy.drp25.data.FilterValues
import dev.lancy.drp25.data.MealType
import dev.lancy.drp25.data.Recipe
import dev.lancy.drp25.data.RecipeRatingUpdate
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.PostgrestFilterDSL
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.postgrest.query.request.SelectRequestBuilder
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.decodeOldRecord
import io.github.jan.supabase.realtime.decodeRecord
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.serialization.Serializable
import kotlin.time.Duration.Companion.seconds

internal expect fun createHttpClient(): HttpClient

// Converts a set to a compatible IN list: (A,B,C)
fun <T> Set<T>.toInList(): String = joinToString(prefix = "(", postfix = ")", separator = ",") { it.toString() }

object Client {
    private val supabaseClient: SupabaseClient = createSupabaseClient(SUPABASE_URL, SUPABASE_KEY) {
        install(Postgrest)
        install(Realtime)

        requestTimeout = 3.seconds
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

            // Apply meal type filter
            if (filters.selectedMealTypes.isNotEmpty()) {
                val mealTypeValues = filters.selectedMealTypes.joinToString(",") { it.name }
                filter("mealType", FilterOperator.IN, "($mealTypeValues)")
            }

            // Apply cuisine filter
            if (filters.selectedCuisines.isNotEmpty()) {
                val cuisineValues = filters.selectedCuisines.joinToString(",") { it.name }
                filter("cuisine", FilterOperator.IN, "($cuisineValues)")
            }

            // Apply diet filter with hierarchy logic
            if (filters.selectedDiets.isNotEmpty()) {
                val expandedDiets = mutableSetOf<String>()

                filters.selectedDiets.forEach { selectedDiet ->
                    when (selectedDiet) {
                        Diet.VEGETARIAN -> {
                            expandedDiets.add(Diet.VEGETARIAN.name)
                            expandedDiets.add(Diet.VEGAN.name)
                        }
                        Diet.DAIRY_FREE -> {
                            expandedDiets.add(Diet.DAIRY_FREE.name)
                            expandedDiets.add(Diet.VEGAN.name)
                        }
                        Diet.LOW_CARB -> {
                            expandedDiets.add(Diet.LOW_CARB.name)
                            expandedDiets.add(Diet.KETO.name)
                        }
                        else -> {
                            expandedDiets.add(selectedDiet.name)
                        }
                    }
                }

                val dietValues = expandedDiets.joinToString(",")
                filter("diet", FilterOperator.IN, "($dietValues)")
            }

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

    // / Fetch all recipes in the database
    suspend fun fetchAllRecipes(): List<Recipe> = runCatching {
        supabaseClient
            .from("recipes_dup")
            .select()
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

    @Serializable
    private data class RecipeID(
        val recipe_id: String,
    )

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

    private fun buildTokenIndex(recipes: List<Recipe>): Map<String, MutableSet<Recipe>> {
        val index = mutableMapOf<String, MutableSet<Recipe>>()
        recipes.forEach { recipe ->
            val tokens = (recipe.name.lowercase().split("\\s+".toRegex()) +
                    recipe.ingredients.flatMap { it.name.lowercase().split("\\s+".toRegex()) })
            tokens.forEach { token ->
                if (!index.containsKey(token)) {
                    index[token] = mutableSetOf()
                }
                index[token]!!.add(recipe)
            }
        }
        return index
    }

    private fun searchIngredients(query: String, recipes: List<Recipe>, threshold: Int = 0): List<Recipe> {
        val tokens = tokenizeQuery(query)
        val positiveTokens =
            tokens.filterNot { it.startsWith("-") || it.startsWith("no ") || it.startsWith("not ") }
        val negativeTokens =
            tokens.filter { it.startsWith("-") || it.startsWith("no ") || it.startsWith("not ") }
                .map { it.removePrefix("-").removePrefix("no ").removePrefix("not ") }
        val tokenIndex = buildTokenIndex(recipes)
        val negativeMatches = negativeTokens.flatMap { token ->
            tokenIndex[token] ?: emptySet()
        }.toSet()
        val positiveMatches = positiveTokens.flatMap { token ->
            tokenIndex[token] ?: emptySet()
        }.toSet()
        return recipes.filter { recipe ->
            if (recipe in negativeMatches) return@filter false
            if (positiveTokens.isEmpty()) return@filter true
            recipe in positiveMatches
        }
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
        },
    )

    // Submit a comment to Database
    suspend fun submitComment(
        recipeId: String,
        userName: String,
        commentText: String,
        parentCommentId: String? = null,
        rating: Int,
    ): Boolean = runCatching {
        val newComment = Comment(
            recipe_id = recipeId,
            user_name = userName,
            comment_text = commentText,
            parent_comment_id = parentCommentId,
            rating = rating,
        )
        supabaseClient.from("comments").insert(newComment)
        true
    }.getOrElse { error ->
        println("Failed to submit comment: ${error.message}")
        false
    }

    // Fetch all comments
    suspend fun fetchAllComments(): List<Comment> = runCatching {
        supabaseClient
            .from("comments")
            .select()
            .decodeList<Comment>()
    }.getOrElse { error ->
        emptyList()
    }

    // Fetch comments from recipes
    suspend fun fetchCommentsForRecipe(recipeId: String): List<Comment> = runCatching {
        supabaseClient
            .from("comments")
            .select {
                filter { eq("recipe_id", recipeId) }
                order("created_at", order = Order.ASCENDING) // Order by date, oldest first
            }.decodeList<Comment>()
    }.getOrElse { error ->
        println("Failed to fetch comments for recipe $recipeId: ${error.message}")
        emptyList()
    }

    // Update recipe rating
    suspend fun updateRecipeRating(recipeId: String, newRating: Float): Boolean = runCatching {
        // 1. Fetch the current recipe to get existing rating and numRatings
        val currentRecipe = supabaseClient
            .from("recipes_dup")
            .select {
                filter { eq("id", recipeId) }
                limit(1)
            }.decodeSingleOrNull<Recipe>()

        if (currentRecipe == null) {
            println("Recipe with ID $recipeId not found for rating update.")
            return false
        }

        // 2. Calculate the new average rating
        val currentTotalRating = currentRecipe.rating * currentRecipe.numRatings
        val newNumRatings = currentRecipe.numRatings + 1
        val updatedRating = (currentTotalRating + newRating) / newNumRatings

        // 3. Prepare the update object
        val ratingUpdate = RecipeRatingUpdate(
            rating = updatedRating,
            numRatings = newNumRatings,
        )

        // 4. Perform the update
        supabaseClient
            .from("recipes_dup")
            .update(ratingUpdate) {
                filter { eq("id", recipeId) }
            }
        true
    }.getOrElse { error ->
        println("Failed to update recipe rating for $recipeId: ${error.message}")
        false
    }

    // Fetch product details from Open Food Facts API using barcode
    suspend fun fetchProduct(barcode: String) {
        coroutineScope {
            println(barcode)
            val result = httpClient.get("https://world.openfoodfacts.org/api/v2/product/$barcode").body<String>()
            println(result)
        }
    }

    // --- REAL-TIME SUBSCRIPTION FUNCTIONS ---
    private val _allComments = MutableStateFlow<List<Comment>>(emptyList())
    val allComments: StateFlow<List<Comment>> = _allComments.asStateFlow()

    private val _allRecipes = MutableStateFlow<List<Recipe>>(emptyList())
    val allRecipes: StateFlow<List<Recipe>> = _allRecipes.asStateFlow()

    private var commentsSubscriptionActive = false
    private var recipesSubscriptionActive = false

    // Modified subscription functions that update StateFlows
    suspend fun subscribeToAllComments() {
        if (commentsSubscriptionActive) return // Prevent duplicate subscriptions
        commentsSubscriptionActive = true

        val commentsChannel = supabaseClient.channel("realtime:public:comments")
        val changes = commentsChannel.postgresChangeFlow<PostgresAction>(schema = "public") {
            table = "comments"
        }

        changes
            .onEach {
                when (it) {
                    is PostgresAction.Insert -> {
                        val newComment = it.decodeRecord<Comment>()
                        _allComments.value += newComment
                    }
                    is PostgresAction.Update -> {
                        val updatedComment = it.decodeRecord<Comment>()
                        _allComments.value = _allComments.value.map { comment ->
                            if (comment.id == updatedComment.id) updatedComment else comment
                        }
                    }
                    is PostgresAction.Delete -> {
                        val deletedComment = it.decodeOldRecord<Comment>()
                        _allComments.value = _allComments.value.filter { comment ->
                            comment.id != deletedComment.id
                        }
                    }
                    else -> println("Unknown comment action")
                }
            }.launchIn(CoroutineScope(Dispatchers.IO))

        commentsChannel.subscribe()

        // Initial load
        try {
            _allComments.value = fetchAllComments() // You'll need this function
        } catch (e: Exception) {
            println("Error loading initial comments: ${e.message}")
        }
    }

    suspend fun subscribeToAllRecipes() {
        if (recipesSubscriptionActive) return // Prevent duplicate subscriptions
        recipesSubscriptionActive = true

        val recipeChannel = supabaseClient.channel("realtime:public:recipes_dup")
        val changes = recipeChannel.postgresChangeFlow<PostgresAction>(schema = "public") {
            table = "recipes_dup"
        }

        changes
            .onEach {
                when (it) {
                    is PostgresAction.Insert -> {
                        val newRecipe = it.decodeRecord<Recipe>()
                        _allRecipes.value += newRecipe
                    }
                    is PostgresAction.Update -> {
                        val updatedRecipe = it.decodeRecord<Recipe>()
                        _allRecipes.value = _allRecipes.value.map { recipe ->
                            if (recipe.id == updatedRecipe.id) updatedRecipe else recipe
                        }
                    }
                    is PostgresAction.Delete -> {
                        val deletedRecipe = it.decodeOldRecord<Recipe>()
                        _allRecipes.value = _allRecipes.value.filter { recipe ->
                            recipe.id != deletedRecipe.id
                        }
                    }
                    else -> println("Unknown recipe action")
                }
            }.launchIn(CoroutineScope(Dispatchers.IO))

        recipeChannel.subscribe()

        // Initial load
        try {
            _allRecipes.value = fetchAllRecipes() // You'll need this function
        } catch (e: Exception) {
            println("Error loading initial recipes: ${e.message}")
        }
    }

    // Helper function to get comments for a specific recipe
    fun getCommentsForRecipe(recipeId: String): StateFlow<List<Comment>> = allComments
        .map { comments ->
            comments
                .filter { it.recipe_id == recipeId }
                .sortedWith(
                    compareBy<Comment> { it.parent_comment_id ?: it.id }
                        .thenBy { it.created_at },
                )
        }.stateIn(
            scope = CoroutineScope(Dispatchers.IO),
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList(),
        )

    // Helper function to get a specific recipe
    fun getRecipe(recipeId: String): StateFlow<Recipe?> = allRecipes
        .map { recipes ->
            recipes.find { it.id == recipeId }
        }.stateIn(
            scope = CoroutineScope(Dispatchers.IO),
            started = SharingStarted.WhileSubscribed(),
            initialValue = null,
        )

    private val _savedRecipeIds = MutableStateFlow<Set<String>>(emptySet())
    val savedRecipeIds: StateFlow<Set<String>> = _savedRecipeIds.asStateFlow()

    private var savedRecipesSubscriptionActive = false

    // Subscribe to saved recipes changes
    suspend fun subscribeToSavedRecipes() {
        if (savedRecipesSubscriptionActive) return
        savedRecipesSubscriptionActive = true

        val savedRecipesChannel = supabaseClient.channel("saved_recipes_updates")
        val changes = savedRecipesChannel.postgresChangeFlow<PostgresAction>(schema = "public.saved_recipes")

        changes
            .onEach {
                when (it) {
                    is PostgresAction.Insert -> {
                        val newSavedRecipe = it.decodeRecord<RecipeID>()
                        _savedRecipeIds.value += newSavedRecipe.recipe_id
                    }
                    is PostgresAction.Delete -> {
                        val deletedSavedRecipe = it.decodeOldRecord<RecipeID>()
                        _savedRecipeIds.value -= deletedSavedRecipe.recipe_id
                    }
                    else -> println("Unknown saved recipe action")
                }
            }.launchIn(CoroutineScope(Dispatchers.IO))

        savedRecipesChannel.subscribe()

        // Initial load
        try {
            val initialSavedRecipes = supabaseClient
                .from("saved_recipes")
                .select()
                .decodeList<RecipeID>()
            _savedRecipeIds.value = initialSavedRecipes.map { it.recipe_id }.toSet()
        } catch (e: Exception) {
            println("Error loading initial saved recipes: ${e.message}")
        }
    }

    // Get saved recipes in real-time
    val savedRecipes: StateFlow<List<Recipe>> = combine(
        allRecipes,
        savedRecipeIds,
    ) { recipes, savedIds ->
        recipes.filter { savedIds.contains(it.id) }
    }.stateIn(
        scope = CoroutineScope(Dispatchers.IO),
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList(),
    )

    // Function to check if a recipe is saved
    fun isRecipeSaved(recipeId: String): StateFlow<Boolean> = savedRecipeIds
        .map { it.contains(recipeId) }
        .stateIn(
            scope = CoroutineScope(Dispatchers.IO),
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false,
        )

    // Updated save/unsave functions for real-time
    suspend fun toggleSaveRecipe(recipeId: String): Boolean = if (_savedRecipeIds.value.contains(recipeId)) {
        unsaveRecipe(recipeId)
    } else {
        saveRecipe(recipeId)
    }

    suspend fun saveRecipe(recipeId: String): Boolean = runCatching {
        supabaseClient
            .from("saved_recipes")
            .insert(RecipeID(recipe_id = recipeId))
        true
    }.getOrElse { error ->
        println("Failed to save recipe: ${error.message}")
        false
    }

    suspend fun unsaveRecipe(recipeId: String): Boolean = runCatching {
        supabaseClient
            .from("saved_recipes")
            .delete {
                filter { eq("recipe_id", recipeId) }
            }
        true
    }.getOrElse { error ->
        println("Failed to unsave recipe: ${error.message}")
        false
    }

    // Helper function to get real-time updates for a specific recipe
    fun getRecipeFlow(recipeId: String): StateFlow<Recipe?> = allRecipes
        .map { recipes ->
            recipes.find { it.id == recipeId }
        }.stateIn(
            scope = CoroutineScope(Dispatchers.IO),
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null,
        )

    // Helper function to get comments for a specific recipe with proper sorting
    fun getCommentsForRecipeFlow(recipeId: String): StateFlow<List<Comment>> = allComments
        .map { comments ->
            comments
                .filter { it.recipe_id == recipeId }
                .sortedWith(
                    compareBy<Comment> { it.parent_comment_id ?: it.id }
                        .thenBy { it.created_at },
                )
        }.stateIn(
            scope = CoroutineScope(Dispatchers.IO),
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList(),
        )
}


