package ru.dartx.core.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

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
    val extId: Int,
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

data class RecipeData(
    val recipeCore: RecipeCore? = null,
    val errorMessage: String? = null,
    val throwable: Throwable? = null,
)

data class RecipeState(
    val recipe: RecipeCore? = null,
    val errorMessage: String? = null,
    val throwable: Throwable? = null,
    val isLoading: Boolean = true,
)

@Entity("recipes")
data class Recipe(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val extId: Int,
    val name: String,
    val category: String,
    val area: String,
    val instruction: String,
    val thumbnail: String,
    val tags: String,
    val sourceUrl: String,
    val youTubeUrl: String
)

@Entity("ingredients")
data class Ingredient(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val recipeId: Int,
    val extId: Int,
    val ingredient: String,
    val quantity: String,
    val unitOfMeasure: String
)