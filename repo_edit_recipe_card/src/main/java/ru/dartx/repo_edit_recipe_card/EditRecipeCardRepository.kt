package ru.dartx.repo_edit_recipe_card

import ru.dartx.core.database.RecipesDao
import ru.dartx.core.dto.RecipeCore
import ru.dartx.core.dto.RecipeData
import ru.dartx.core.mediator.ProvidersFacade
import ru.dartx.local_db.mapper.LocalDbEntityMapper
import ru.dartx.network.RecipesApi
import ru.dartx.network.dto.ResultResponse
import ru.dartx.network.mapper.NetworkEntityMapper
import javax.inject.Inject

class EditRecipeCardRepository @Inject constructor(
    private val recipesDao: RecipesDao,
    private val recipesApi: RecipesApi,
    private val networkEntityMapper: NetworkEntityMapper,
    private val localDbEntityMapper: LocalDbEntityMapper,
    private val providersFacade: ProvidersFacade,
) {

    suspend fun getRecipe(id: Int, extId: Int): RecipeData {
        return when (true) {
            (id != 0) -> {
                val recipeCore = localDbEntityMapper.recipeFromDbToCore(
                    recipesDao.getRecipeById(id),
                    recipesDao.getIngredientsById(id)
                )
                RecipeData(
                    recipeCore = recipeCore
                )
            }

            (extId != 0) -> {
                when (val response = recipesApi.getRecipeById(extId)) {
                    is ResultResponse.Success -> {
                        response.data?.let {
                            RecipeData(
                                recipeCore = networkEntityMapper.mealToRecipe(
                                    it
                                )
                            )
                        }
                            ?: RecipeData(
                                errorMessage = providersFacade.provideContext()
                                    .getString(R.string.recipe_not_found),
                                throwable = null
                            )
                    }

                    is ResultResponse.Error -> {
                        RecipeData(
                            errorMessage = response.message,
                            throwable = response.throwable
                        )
                    }
                }
            }
            else -> {
                RecipeData(
                    recipeCore = null
                )
            }
        }
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