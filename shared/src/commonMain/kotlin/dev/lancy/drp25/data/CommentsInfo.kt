package dev.lancy.drp25.data

import kotlinx.serialization.Serializable
import kotlinx.datetime.Instant

@Serializable
data class Comment(
    val id: String? = null,
    val recipe_id: String,
    val user_name: String,
    val comment_text: String,
    val created_at: Instant? = null,
    val parent_comment_id: String? = null
)

@Serializable
data class RecipeRatingUpdate(
    val rating: Float,
    val numRatings: Int
)