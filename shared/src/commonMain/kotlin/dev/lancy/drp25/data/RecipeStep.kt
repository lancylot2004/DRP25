package dev.lancy.drp25.data

import arrow.core.NonEmptyList
import kotlinx.serialization.Serializable

@Serializable
data class RecipeSection(
    val title: String,
    val steps: List<RecipeStep>
)

@Serializable
data class RecipeStep(val description: String)
