package dev.lancy.drp25.data

import dev.lancy.drp25.ui.shared.NavTarget
import io.realm.kotlin.types.RealmObject
import kotlinx.serialization.Serializable

@Serializable
data class Recipe(
    /**
     * The name of the recipe.
     */
    val name: String,
    /**
     * Time taken to cook the recipe, in minutes.
     */
    val cookingTime: Int,
    /**
     * Time taken to clean up after cooking the recipe, in minutes.
     */
    val cleanupTime: Int? = null,
    /**
     * The number of portions the recipe serves.
     */
    val portions: Int,
    /**
     * User-rating of the recipe, from 0.0 to 5.0.
     */
    val rating: Float,
    /**
     * The vertical format image for this recipe.
     *
     * TODO: Specify format.
     */
    val cardImage: String,
    /**
     * The horizontal format image for this recipe.
     */
    val smallImage: String,
    /**
     * The video tutorial for this recipe.
     */
    val video: String? = null,
): NavTarget, RealmObject
