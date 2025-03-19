package ru.dartx.repo_recipes_list

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import ru.dartx.core.dto.RecipeCore
import ru.dartx.core.database.RecipesDao
import ru.dartx.local_db.mapper.LocalDbEntityMapper
import ru.dartx.network.RecipesApi
import ru.dartx.network.dto.ResultResponse
import ru.dartx.network.mapper.NetworkEntityMapper
import javax.inject.Inject

data class RecipesData(
    val recipesList: List<RecipeCore> = listOf(),
    val errorMessage: String? = null,
    val throwable: Throwable? = null,
)

class RecipesListRepository @Inject constructor(
    private val recipesDao: RecipesDao,
    private val recipesApi: RecipesApi,
    private val networkEntityMapper: NetworkEntityMapper,
    private val localDbEntityMapper: LocalDbEntityMapper,
) {

    suspend fun searchRecipes(condition: String): RecipesData {
        return coroutineScope {
            val cond = condition.replace(" ", "%")
            val deferredRecipesFromNetData = async { searchRecipesFromNet(cond) }
            val deferredSavedRecipesList = async { searchSavedRecipes(cond) }
            val recipesFromNetData = deferredRecipesFromNetData.await()
            val savedRecipesList = deferredSavedRecipesList.await()
            val recipesListFromNet = recipesFromNetData.recipesList
            val finalRecipesList = savedRecipesList as MutableList
            finalRecipesList.addAll(recipesListFromNet.filter { recipeFromNet ->
                savedRecipesList.find { it.extId == recipeFromNet.extId } == null
            })
            recipesFromNetData.copy(recipesList = finalRecipesList.sortedBy { it.name })
        }
    }

    suspend fun getSavedRecipes(): List<RecipeCore> {
        val savedRecipes = recipesDao.getRecipes()
        val savedRecipesIngredients = recipesDao.getIngredients()
        return savedRecipes.map { recipe ->
            localDbEntityMapper.recipeFromDbToCore(
                recipe,
                savedRecipesIngredients.filter { ingredient -> ingredient.recipeId == recipe.id })
        }
    }

    private suspend fun searchRecipesFromNet(condition: String): RecipesData {
        return when (val response = recipesApi.searchRecipes(condition)) {
            is ResultResponse.Success -> {
                RecipesData(
                    recipesList = response.data?.meals?.map {
                        networkEntityMapper.mealToRecipe(it)
                    } ?: listOf(),
                    errorMessage = null,
                    throwable = null
                )
            }

            is ResultResponse.Error -> {
                RecipesData(
                    recipesList = listOf(),
                    errorMessage = response.message,
                    throwable = response.throwable
                )
            }
        }
    }

    private suspend fun searchSavedRecipes(condition: String) =
        recipesDao.searchRecipes(
            if (condition.length == 1) "$condition%" else "%$condition%"
        ).map { recipe ->
            localDbEntityMapper.recipeFromDbToCore(
                recipe,
                recipesDao.getIngredientsById(recipe.id)
            )
        }

    suspend fun saveRecipe(recipeCore: RecipeCore): Int {
        val recipe = localDbEntityMapper.recipeCoreToDb(recipeCore)
        val ingredients = recipeCore.ingredients.map { localDbEntityMapper.ingredientCoreToDb(it) }
        val recipeId = recipesDao.saveRecipe(recipe, ingredients)
        return recipeId
    }

    suspend fun deleteRecipe(recipeCore: RecipeCore) {
        recipesDao.deleteRecipeWithIngredients(recipeCore.id)
    }
}