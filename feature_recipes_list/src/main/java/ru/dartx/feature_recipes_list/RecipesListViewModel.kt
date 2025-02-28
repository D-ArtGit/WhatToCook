package ru.dartx.feature_recipes_list

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.dartx.core.dto.RecipeCore
import ru.dartx.core.dto.RecipeItem
import ru.dartx.repo_recipes_list.RecipesListRepository
import javax.inject.Inject

data class RecipesListState(
    val recipesList: List<RecipeItem>,
    val errorMessage: String? = null,
    val throwable: Throwable? = null,
    val isSearchActive: Boolean = false,
)

class RecipesListViewModel @Inject constructor(private val recipesListRepository: RecipesListRepository) :
    ViewModel() {
    private val _recipesListState = mutableStateOf(RecipesListState(recipesList = listOf()))
    val recipesListState: State<RecipesListState> get() = _recipesListState
    private val recipeList = mutableListOf<RecipeCore>()

    init {
        getSavedRecipes()
        _recipesListState.value = _recipesListState.value.copy(isSearchActive = false)
    }


    private fun getSavedRecipes() {
        viewModelScope.launch {
            recipeList.clear()
            recipeList.addAll(recipesListRepository.getSavedRecipes())
            _recipesListState.value =
                _recipesListState.value.copy(recipesList = recipeList.map {
                    mapRecipeToRecipeItem(
                        it
                    )
                })
        }

    }

    fun searchRecipes(condition: String) {
        viewModelScope.launch {
            if (condition.isNotEmpty()) {
                val recipesData = recipesListRepository.searchRecipes(condition)
                recipeList.clear()
                recipeList.addAll(recipesData.recipesList)
                _recipesListState.value =
                    _recipesListState.value.copy(
                        recipesList = recipeList.map {
                            mapRecipeToRecipeItem(
                                it
                            )
                        },
                        errorMessage = recipesData.errorMessage,
                        throwable = recipesData.throwable
                    )
            } else {
                getSavedRecipes()
            }
        }
    }

    fun onSearchClosed() {
        getSavedRecipes()
    }

    fun resetErrorState() {
        _recipesListState.value = _recipesListState.value.copy(
            errorMessage = null,
            throwable = null
        )
    }

    fun setSearchState(isSearchActive: Boolean) {
        val tmpRecipesListState = _recipesListState.value.copy(isSearchActive = isSearchActive)
        _recipesListState.value = tmpRecipesListState
    }

    fun saveRecipe(recipeItem: RecipeItem) {
        viewModelScope.launch {
            val recipeCore =
                recipeList.find { it.id == recipeItem.id && it.extId == recipeItem.extId }
            if (recipeCore != null) {
                val newRecipeId = recipesListRepository.saveRecipe(recipeCore)
                recipeList.replaceAll {
                    if (it.id == recipeItem.id && it.extId == recipeItem.extId) it.copy(
                        id = newRecipeId,
                        isSaved = true
                    ) else it
                }
                _recipesListState.value =
                    _recipesListState.value.copy(recipesList = _recipesListState.value.recipesList.map {
                        if (it.id == recipeItem.id && it.extId == recipeItem.extId) it.copy(
                            id = newRecipeId,
                            isSaved = true
                        ) else it
                    })
            }
        }
    }

    fun deleteRecipe(recipeItem: RecipeItem) {
        viewModelScope.launch {
            val recipeCore =
                recipeList.find { it.id == recipeItem.id && it.extId == recipeItem.extId }
            if (recipeCore != null) {
                recipesListRepository.deleteRecipe(recipeCore)
                if (recipesListState.value.isSearchActive) {
                    recipeList.replaceAll {
                        if (it.id == recipeItem.id && it.extId == recipeItem.extId) it.copy(
                            id = 0,
                            isSaved = false
                        ) else it
                    }
                    _recipesListState.value =
                        _recipesListState.value.copy(recipesList = _recipesListState.value.recipesList.map {
                            if (it.id == recipeItem.id && it.extId == recipeItem.extId) it.copy(
                                id = 0,
                                isSaved = false
                            ) else it
                        })
                } else {
                    recipeList.removeAll { it.id == recipeItem.id && it.extId == recipeItem.extId }
                    _recipesListState.value =
                        _recipesListState.value.copy(
                            recipesList = _recipesListState.value.recipesList.filterNot { it.id == recipeItem.id && it.extId == recipeItem.extId }
                        )
                }
            }
        }
    }

    private fun mapRecipeToRecipeItem(recipeCore: RecipeCore): RecipeItem {
        val ingredientsSB = StringBuilder()
        recipeCore.ingredients.forEachIndexed { index, ingredient ->
            if (ingredient.ingredient.isNotEmpty()) {
                if (index > 0) ingredientsSB.append(", ")
                ingredientsSB.append(ingredient.ingredient)
            }
        }

        return RecipeItem(
            id = recipeCore.id,
            extId = recipeCore.extId,
            name = recipeCore.name,
            thumbnail = recipeCore.thumbnail,
            ingredients = ingredientsSB.toString(),
            isSaved = recipeCore.isSaved
        )
    }
}