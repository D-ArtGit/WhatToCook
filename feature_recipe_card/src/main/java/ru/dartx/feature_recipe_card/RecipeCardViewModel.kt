package ru.dartx.feature_recipe_card

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.dartx.core.dto.RecipeCore
import ru.dartx.core.dto.RecipeState
import ru.dartx.repo_recipe_card.RecipeCardRepository
import javax.inject.Inject

class RecipeCardViewModel @Inject constructor(private val recipeCardRepository: RecipeCardRepository) :
    ViewModel() {
    private val _recipeState = mutableStateOf(RecipeState())
    val recipeState: State<RecipeState> get() = _recipeState

    fun getRecipe(id: Int, extId: Int) {
        viewModelScope.launch {
            _recipeState.value = _recipeState.value.copy(isLoading = true)
            val recipeData = recipeCardRepository.getRecipe(id, extId)
            _recipeState.value = _recipeState.value.copy(
                recipe = recipeData.recipeCore,
                errorMessage = recipeData.errorMessage,
                throwable = recipeData.throwable,
                isLoading = false
            )
        }
    }

    fun addRecipeToFavorites(recipe: RecipeCore) {
        viewModelScope.launch {
            val newRecipeId = recipeCardRepository.addRecipeToFavorites(recipe)
            _recipeState.value = _recipeState.value.copy(recipe = recipe.copy(id = newRecipeId, isSaved = true))
        }
    }

    fun removeRecipeFromFavorites(recipe: RecipeCore) {
        viewModelScope.launch {
            recipeCardRepository.removeRecipeFromFavorites(recipe)
            _recipeState.value = _recipeState.value.copy(recipe = recipe.copy(id = 0, isSaved = false))
        }
    }
}