package dev.lancy.drp25.utilities

import dev.lancy.drp25.data.FilterValues
import dev.lancy.drp25.data.Recipe
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.PostgrestFilterDSL
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.postgrest.query.request.SelectRequestBuilder
import kotlinx.serialization.Serializable
import kotlin.time.Duration.Companion.seconds

val client: SupabaseClient = createSupabaseClient(
    supabaseUrl = "https://zepduojefkyzoreleeoi.supabase.co",
    supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InplcGR1b2plZmt5em9yZWxlZW9pIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDg5NzA5OTMsImV4cCI6MjA2NDU0Njk5M30.bXfAH98IEQzoHm3pprtPtSnoB_fcGsF2MW3raoDHz3M",
) {
    install(Postgrest)

    requestTimeout = 1.seconds
}

val applyRecipeFilters: @PostgrestFilterDSL (SelectRequestBuilder.(filters: FilterValues?) -> Unit) = { filters ->
    if (filters != null) {
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
}

// Fetch all recipes in the database
suspend fun fetchRecipes(filters: FilterValues? = null): List<Recipe> = runCatching {
    client
        .from("recipes")
        .select { applyRecipeFilters(filters) }
        .decodeList<Recipe>()
}.fold(
    onSuccess = ::identity,
    onFailure = { error ->
        println("Failed to fetch recipes: ${error.message}")
        emptyList<Recipe>()
    },
)

suspend fun isSavedRecipe(recipe: Recipe): Boolean = runCatching {
    client
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
data class RecipeID(
    val recipe_id: Int,
)

suspend fun setSaved(recipe: Recipe, saved: Boolean): Boolean = runCatching {
    when (saved) {
        true -> client.from("saved_recipes").insert(RecipeID(recipe.id.toInt()))
        false -> client.from("saved_recipes").delete { filter { eq("recipe_id", recipe.id) } }
    }
}.fold(
    onSuccess = { true },
    onFailure = { error ->
        println("Failed to set saved recipe: ${error.message}")
        false
    },
)
