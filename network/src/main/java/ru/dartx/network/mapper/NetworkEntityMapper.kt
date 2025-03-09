package ru.dartx.network.mapper

import androidx.core.text.isDigitsOnly
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
            val ingredientProperty =
                clazz.memberProperties.find { it.name == strIngredientFieldName }
            val ingredient = ingredientProperty?.getter?.call(meal) as? String
            if (!ingredient.isNullOrEmpty()) {
                val strMeasureFieldName = "strMeasure$i"
                val measureProperty =
                    clazz.memberProperties.find { it.name == strMeasureFieldName }
                val measure = measureProperty?.getter?.call(meal) as? String ?: ""
                val splitMeasure = splitMeasure(measure)
                val quantity = splitMeasure.first
                val unitOfMeasure = splitMeasure.second
                ingredients.add(
                    IngredientCore(
                        id = 0,
                        recipeId = 0,
                        extId = meal.idMeal,
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

    private fun splitMeasure(measure: String): Pair<String, String> {
        val measureWithCorrectedFraction = if (measure.contains(Regex("[½¼¾⅓⅔⅕]+"))) {
            val fractions = setOf("½", "¼", "¾", "⅓", "⅔", "⅕")
            val index = measure.indexOfFirst { it.toString() in fractions }
            if (index > 0) {
                val firstPart = measure.substring(0, index).replace('-', ' ').trim()
                val correctedMeasure =
                    if (firstPart.isDigitsOnly()) {
                        parseFraction(
                            measure.substring(0, index + 1)
                        ) + measure.substring(index + 1)
                    } else measure
                correctedMeasure
            } else measure
        } else measure
        val finalMeasure =
            if (measureWithCorrectedFraction.contains('/')
                && measureWithCorrectedFraction.substringBefore('/').contains(Regex("[0-9 ]+"))
                && !measureWithCorrectedFraction.substringBefore('/').contains(Regex("[a-zA-Z]+"))
            ) {
                val firstPart =
                    measureWithCorrectedFraction.substringBefore('/').filter { it.isDigit() }
                val secondPart =
                    measureWithCorrectedFraction.substringAfter('/')
                "$firstPart/$secondPart"
            } else if (measureWithCorrectedFraction.contains('/')
                && measureWithCorrectedFraction.substringBefore('/').contains(Regex("[a-zA-Z]+"))
                && measureWithCorrectedFraction.substringAfter('/').contains(Regex("[0-9]+"))
            ) {
                measureWithCorrectedFraction.substringBefore('/')
            } else measureWithCorrectedFraction
        var quantity = ""
        var unitOfMeasure = ""
        val regex = Regex("""([\d/½¼¾⅓⅔⅕,.-]+)?\s*(.*)?""")
        val splitResult = regex.find(finalMeasure)
        if (splitResult != null) {
            val quantityPart = splitResult.groupValues.getOrNull(1) ?: ""
            quantity = parseFraction(quantityPart)
            unitOfMeasure = splitResult.groupValues.getOrNull(2) ?: ""
        }
        if (quantity.contains('-')) {
            quantity = if (quantity.substringAfter('-').isDigitsOnly()) {
                quantity.substringBefore('-')
            } else {
                quantity.replace('-', ' ')
            }
        }
        if (quantity.contains('/') && quantity.substringBefore('/').length > 1) {
            val wholeFractions = quantity.subSequence(0, quantity.substringBefore('/').length - 1)
            val fraction =
                quantity.subSequence(quantity.substringBefore('/').length - 1, quantity.length)
            quantity = "$wholeFractions $fraction"
        }
        return quantity.trim() to unitOfMeasure
    }

    private fun parseFraction(quantity: String) = quantity
        .replace("½", "1/2")
        .replace("¼", "1/4")
        .replace("¾", "3/4")
        .replace("⅓", "1/3")
        .replace("⅔", "2/3")
        .replace("⅕", "1/5")
        .replace(',', '.')
}