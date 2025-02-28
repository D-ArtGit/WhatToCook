package ru.dartx.core.dto

data class RecipeCore(
    val id: Int,
    val extId: Int,
    val name: String,
    val category: String,
    val area: String,
    val instruction: String,
    val thumbnail: String,
    val ingredients: List<IngredientCore> = listOf(),
    val tags: String,
    val sourceUrl: String,
    val youTubeUrl: String,
    val isSaved: Boolean = false
)

data class IngredientCore(
    val id: Int = 0,
    val recipeId: Int,
    val ingredient: String,
    val quantity: String,
    val unitOfMeasure: String
)

data class RecipeItem(
    val id: Int,
    val extId: Int,
    val name: String,
    val thumbnail: String,
    val ingredients: String,
    val isSaved: Boolean = false
)