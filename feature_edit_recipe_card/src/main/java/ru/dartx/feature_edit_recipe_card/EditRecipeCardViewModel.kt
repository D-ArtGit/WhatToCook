package ru.dartx.feature_edit_recipe_card

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.dartx.core.dto.RecipeCore
import ru.dartx.core.dto.RecipeState
import ru.dartx.repo_edit_recipe_card.EditRecipeCardRepository
import javax.inject.Inject

class EditRecipeCardViewModel @Inject constructor(private val editRecipeCardRepository: EditRecipeCardRepository) :
    ViewModel() {
    private val _recipeState = mutableStateOf(RecipeState())
    val recipeState: State<RecipeState> get() = _recipeState

    private val _recipeValidatorState = mutableStateOf(RecipeValidatorState())
    val recipeValidatorState: State<RecipeValidatorState> get() = _recipeValidatorState

    fun getRecipe(id: Int, extId: Int) {
        viewModelScope.launch {
            _recipeState.value = _recipeState.value.copy(isLoading = true)
            val recipeData = editRecipeCardRepository.getRecipe(id, extId)
            _recipeState.value = _recipeState.value.copy(
                recipe = recipeData.recipeCore,
                errorMessage = recipeData.errorMessage,
                throwable = recipeData.throwable,
                isLoading = false
            )
        }
    }

    fun setRecipeName(name: String) {
        _recipeState.value =
            _recipeState.value.copy(recipe = _recipeState.value.recipe?.copy(name = name))
        if (name.isEmpty()) {
            _recipeValidatorState.value =
                _recipeValidatorState.value.copy(nameErrorMessage = R.string.name_is_empty)
        } else {
            _recipeValidatorState.value =
                _recipeValidatorState.value.copy(nameErrorMessage = null)
        }
    }

    fun setRecipeInstruction(instruction: String) {
        _recipeState.value =
            _recipeState.value.copy(recipe = _recipeState.value.recipe?.copy(instruction = instruction))
        if (instruction.isEmpty()) {
            _recipeValidatorState.value =
                _recipeValidatorState.value.copy(instructionErrorMessage = R.string.instruction_is_empty)
        } else {
            _recipeValidatorState.value =
                _recipeValidatorState.value.copy(instructionErrorMessage = null)
        }
    }


    fun saveRecipe(recipe: RecipeCore): Boolean {
        viewModelScope.launch {
            val newRecipeId = editRecipeCardRepository.saveRecipe(recipe)
            _recipeState.value =
                _recipeState.value.copy(recipe = recipe.copy(id = newRecipeId, isSaved = true))
        }
        return true
    }

    fun deleteRecipe(recipe: RecipeCore) {
        viewModelScope.launch {
            editRecipeCardRepository.deleteRecipe(recipe)
            _recipeState.value =
                _recipeState.value.copy(recipe = recipe.copy(id = 0, isSaved = false))
        }
    }

    data class RecipeValidatorState(
        val nameErrorMessage: Int? = null,
        val instructionErrorMessage: Int? = null,
        val thumbnailErrorMessage: Int? = null,
        val ingredients: List<IngredientValidatorState> = listOf(),
        val sourceUrlErrorMessage: Int? = null,
        val youTubeUrlErrorMessage: Int? = null,
    )

    data class IngredientValidatorState(
        val ingredientErrorMessage: Int? = null,
        val quantityErrorMessage: Int? = null,
        val unitOfMeasureErrorMessage: Int? = null,
    )

}