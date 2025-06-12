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
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
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

        runBlocking { fetchProduct("5000169631515") }

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
}
