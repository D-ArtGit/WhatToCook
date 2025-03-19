package ru.dartx.local_db.mapper

import ru.dartx.core.dto.IngredientCore
import ru.dartx.core.dto.RecipeCore
import ru.dartx.core.dto.Ingredient
import ru.dartx.core.dto.Recipe
import javax.inject.Inject

class LocalDbEntityMapper @Inject constructor() {
    fun recipeFromDbToCore(recipe: Recipe, ingredients: List<Ingredient>): RecipeCore {

        return RecipeCore(
            id = recipe.id,
            extId = recipe.extId,
            name = recipe.name,
            category = recipe.category,
            area = recipe.area,
            instruction = recipe.instruction,
            thumbnail = recipe.thumbnail,
            ingredients = ingredients.map { ingredientFromDbToCore(it) },
            tags = recipe.tags ,
            sourceUrl = recipe.sourceUrl,
            youTubeUrl = recipe.youTubeUrl,
            isSaved = true
        )
    }

    fun recipeCoreToDb(recipeCore: RecipeCore): Recipe {
        return Recipe(
            id = recipeCore.id,
            extId = recipeCore.extId,
            name = recipeCore.name,
            category = recipeCore.category,
            area = recipeCore.area,
            instruction = recipeCore.instruction,
            thumbnail = recipeCore.thumbnail,
            tags = recipeCore.tags,
            sourceUrl = recipeCore.sourceUrl,
            youTubeUrl = recipeCore.youTubeUrl)
    }

    fun ingredientCoreToDb(ingredientCore: IngredientCore): Ingredient {
        return Ingredient(
            id = ingredientCore.id,
            recipeId = ingredientCore.recipeId,
            extId = ingredientCore.extId,
            ingredient = ingredientCore.ingredient,
            quantity = ingredientCore.quantity,
            unitOfMeasure = ingredientCore.unitOfMeasure,
            )
    }

    private fun ingredientFromDbToCore(ingredient: Ingredient) : IngredientCore {
        return IngredientCore(
            id = ingredient.id,
            recipeId = ingredient.recipeId,
            extId = ingredient.extId,
            ingredient = ingredient.ingredient,
            quantity = ingredient.quantity,
            unitOfMeasure = ingredient.unitOfMeasure
        )
    }
}