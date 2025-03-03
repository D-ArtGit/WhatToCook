package ru.dartx.network.mapper

import ru.dartx.core.dto.IngredientCore
import ru.dartx.core.dto.RecipeCore
import ru.dartx.network.dto.Meal
import javax.inject.Inject
import kotlin.reflect.full.memberProperties

class NetworkEntityMapper @Inject constructor() {

    fun mealToRecipe(meal: Meal): RecipeCore {
        val ingredients = mutableListOf<IngredientCore>()

        val clazz = meal::class
        for (i in 1..20) {
            val strIngredientFieldName = "strIngredient$i"
            val ingredientProperty = clazz.memberProperties.find { it.name == strIngredientFieldName }
            val ingredient = ingredientProperty?.getter?.call(meal) as? String
            if (!ingredient.isNullOrEmpty()) {
                val strMeasureFieldName = "strMeasure$i"
                val measureProperty = clazz.memberProperties.find { it.name == strMeasureFieldName }
                val measure = measureProperty?.getter?.call(meal) as? String ?: ""
                val quantity = measure.filter { it.isDigit() || it == '/' }
                val unitOfMeasure =
                    measure.filter { !it.isDigit() && it != '/' }.replace("-", "").trim()
                ingredients.add(
                    IngredientCore(
                        id = 0,
                        recipeId = meal.idMeal,
                        ingredient = ingredient,
                        quantity = quantity,
                        unitOfMeasure = unitOfMeasure
                    )
                )
            }
        }

        return RecipeCore(
            id = 0,
            extId = meal.idMeal,
            name = meal.strMeal,
            category = meal.strCategory,
            area = meal.strArea,
            instruction = meal.strInstructions,
            thumbnail = meal.strMealThumb,
            ingredients = ingredients,
            tags = meal.strTags ?: "",
            sourceUrl = meal.strSource ?: "",
            youTubeUrl = meal.strYoutube ?: "",
            isSaved = false
        )
    }
}