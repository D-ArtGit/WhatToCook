package ru.dartx.core.navigation

import kotlinx.serialization.Serializable

@Serializable
object RecipesList

@Serializable
data class Recipe(
    val id: Int,
    val extId: Int,
    val imageUrl: String,
)

@Serializable
data class IngredientsRecalc(
    val id: Int,
    val extId: Int,
)