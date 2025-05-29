package dev.lancy.drp25.data

import arrow.core.NonEmptyList

data class RecipeSection(
    val title: String,
    val steps: NonEmptyList<RecipeStep>
)

data class RecipeStep(val description: String)
