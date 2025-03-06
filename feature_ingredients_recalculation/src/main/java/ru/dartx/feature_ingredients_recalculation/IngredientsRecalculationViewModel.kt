package ru.dartx.feature_ingredients_recalculation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.dartx.core.dto.IngredientCore
import ru.dartx.core.dto.RecipeState
import ru.dartx.repo_ingredients.IngredientsRecalculationRepository
import java.math.RoundingMode
import javax.inject.Inject

class IngredientsRecalculationViewModel @Inject constructor(
    private val ingredientsRecalculationRepository: IngredientsRecalculationRepository,
) : ViewModel() {
    private var initialIngredients: List<IngredientCore>? = null
    private val _recipeState = mutableStateOf(RecipeState())
    val recipeState: State<RecipeState> get() = _recipeState

    fun getRecipe(id: Int, extId: Int) {
        viewModelScope.launch {
            _recipeState.value = _recipeState.value.copy(isLoading = true)
            val recipeData = ingredientsRecalculationRepository.getRecipe(id, extId)
            initialIngredients =
                recipeData.recipeCore?.let { stringToDigitIngredientsMapper(it.ingredients) }
            _recipeState.value = _recipeState.value.copy(
                recipe = initialIngredients?.let { recipeData.recipeCore?.copy(ingredients = it) },
                errorMessage = recipeData.errorMessage,
                throwable = recipeData.throwable,
                isLoading = false
            )
        }
    }

    fun recalculateIngredients(coefficient: Double) {
        initialIngredients?.let { ingredients ->
            val recipeWithRecalculatedIngredients =
                recipeState.value.recipe?.copy(ingredients = ingredients.map {
                    recalculationMapper(
                        it,
                        coefficient
                    )
                })
            _recipeState.value = recipeState.value.copy(
                recipe = recipeWithRecalculatedIngredients
            )
        }

    }

    private fun stringToDigitIngredientsMapper(ingredients: List<IngredientCore>): List<IngredientCore> =
        ingredients.map { ingredient ->
            if (ingredient.quantity.contains('/')) {
                val divisible = ingredient.quantity.substringBefore('/').toDouble()
                val divider = ingredient.quantity.substringAfter('/').toDouble()
                val quantity = divisible / divider
                val roundedQuantity =
                    quantity.toBigDecimal().setScale(2, RoundingMode.HALF_UP).toString()
                        .replace("0*$".toRegex(), "")
                        .replace("\\.$".toRegex(), "")
                ingredient.copy(quantity = roundedQuantity)
            } else {
                ingredient
            }
        }

    private fun recalculationMapper(
        ingredient: IngredientCore,
        coefficient: Double,
    ): IngredientCore {
        if (ingredient.quantity.isNotEmpty()) {
            val newQuantity = ingredient.quantity.toDouble() * coefficient
            val roundedNewQuantity =
                newQuantity.toBigDecimal().setScale(2, RoundingMode.HALF_UP).toString()
                    .replace("0*$".toRegex(), "")
                    .replace("\\.$".toRegex(), "")
            return ingredient.copy(quantity = roundedNewQuantity)
        } else {
            return ingredient
        }
    }
}