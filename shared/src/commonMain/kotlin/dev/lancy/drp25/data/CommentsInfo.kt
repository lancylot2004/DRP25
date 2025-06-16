package dev.lancy.drp25.data

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Comment(
    val id: String? = null,
    val recipe_id: String,
    val user_name: String,
    val comment_text: String,
    val created_at: Instant? = null,
    val parent_comment_id: String? = null,
    val rating: Int = 0,
)

@Serializable
data class RecipeRatingUpdate(
    val rating: Float,
    val numRatings: Int,
)
