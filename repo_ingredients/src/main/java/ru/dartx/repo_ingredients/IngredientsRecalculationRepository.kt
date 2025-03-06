package ru.dartx.repo_ingredients

import ru.dartx.core.dto.RecipeData
import ru.dartx.local_db.dao.RecipesDao
import ru.dartx.local_db.mapper.LocalDbEntityMapper
import ru.dartx.network.RecipesApi
import ru.dartx.network.dto.ResultResponse
import ru.dartx.network.mapper.NetworkEntityMapper
import javax.inject.Inject

class IngredientsRecalculationRepository @Inject constructor(
    private val recipesDao: RecipesDao,
    private val recipesApi: RecipesApi,
    private val networkEntityMapper: NetworkEntityMapper,
    private val localDbEntityMapper: LocalDbEntityMapper,
) {
    suspend fun getRecipe(id: Int, extId: Int): RecipeData {
        return if (id != 0) {
            val recipeCore = localDbEntityMapper.recipeFromDbToCore(
                recipesDao.getRecipeById(id),
                recipesDao.getIngredientsById(id)
            )
            RecipeData(
                recipeCore = recipeCore
            )
        } else {
            when (val response = recipesApi.getRecipeById(extId)) {
                is ResultResponse.Success -> {
                    response.data?.let { RecipeData(recipeCore = networkEntityMapper.mealToRecipe(it)) }
                        ?: RecipeData(
                            errorMessage = "Recipe not found",
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
    }
}