package ru.dartx.whattocook

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import ru.dartx.local_db.dao.RecipesDao
import ru.dartx.local_db.mapper.LocalDbEntityMapper
import ru.dartx.network.RecipesApi
import ru.dartx.network.dto.Meals
import ru.dartx.network.dto.ResultResponse
import ru.dartx.network.mapper.NetworkEntityMapper
import ru.dartx.repo_recipes_list.RecipesListRepository
import ru.dartx.whattocook.RecipesFactory.getIngredients
import ru.dartx.whattocook.RecipesFactory.getMeal
import ru.dartx.whattocook.RecipesFactory.getRecipe
import ru.dartx.whattocook.RecipesFactory.getRecipeCoreFromDb
import ru.dartx.whattocook.RecipesFactory.getRecipeCoreFromNetwork

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class RecipesListRepositoryTest {
    private lateinit var recipesDao: RecipesDao
    private lateinit var recipesApi: RecipesApi
    private lateinit var networkEntityMapper: NetworkEntityMapper
    private lateinit var localDbEntityMapper: LocalDbEntityMapper
    private lateinit var recipesListRepository: RecipesListRepository

    @Before
    fun setup() {
        recipesDao = mock()
        recipesApi = mock()
        networkEntityMapper = mock()
        localDbEntityMapper = mock()
        recipesListRepository = RecipesListRepository(
            recipesDao,
            recipesApi,
            networkEntityMapper,
            localDbEntityMapper
        )
    }

    @Test
    fun `getSavedRecipes should map database entities correctly`() = runTest {
        // Arrange
        val recipeDb = getRecipe()
        val ingredients = getIngredients()
        val expectedRecipe = getRecipeCoreFromDb()

        whenever(recipesDao.getRecipes()).thenReturn(listOf(getRecipe()))
        whenever(recipesDao.getIngredients()).thenReturn(getIngredients())
        whenever(localDbEntityMapper.recipeFromDbToCore(recipeDb, ingredients)).thenReturn(
            expectedRecipe
        )

        // Act
        val result = recipesListRepository.getSavedRecipes()

        // Assert
        verify(recipesDao).getRecipes()
        verify(recipesDao).getIngredients()
        assertEquals(expectedRecipe, result.first())
    }

    @Test
    fun `searchRecipes should merge network and local results without duplicates`() = runTest {
        // Arrange
        val condition = "Test"
        val mockMeal = getMeal()
        val networkRecipe = getRecipeCoreFromNetwork()
        val savedRecipe = getRecipeCoreFromDb()

        whenever(recipesApi.searchRecipes(condition)).thenReturn(
            ResultResponse.Success(Meals(listOf(mockMeal)))
        )
        whenever(networkEntityMapper.mealToRecipe(mockMeal)).thenReturn(networkRecipe)
        whenever(recipesDao.searchRecipes("%$condition%")).thenReturn(listOf(getRecipe()))
        whenever(localDbEntityMapper.recipeFromDbToCore(any(), any())).thenReturn(savedRecipe)
        whenever(recipesDao.getIngredientsById(any())).thenReturn(getIngredients())

        // Act
        val result = recipesListRepository.searchRecipes(condition)

        // Assert
        verify(recipesApi).searchRecipes(condition)
        verify(recipesDao).searchRecipes("%$condition%")
        assertEquals(1, result.recipesList.size)
        assertTrue(result.recipesList.containsAll(listOf(savedRecipe)))
    }

    @Test
    fun `searchRecipes should handle network error`() = runTest {
        // Arrange
        val condition = "Test"
        val errorMessage = "Network error"
        val throwable = Throwable(errorMessage)
        val savedRecipe = getRecipeCoreFromDb()
        whenever(recipesApi.searchRecipes(any())).thenReturn(
            ResultResponse.Error(
                throwable,
                errorMessage
            )
        )
        whenever(recipesDao.searchRecipes("%$condition%")).thenReturn(listOf(getRecipe()))
        whenever(localDbEntityMapper.recipeFromDbToCore(any(), any())).thenReturn(savedRecipe)
        whenever(recipesDao.getIngredientsById(any())).thenReturn(getIngredients())

        // Act
        val result = recipesListRepository.searchRecipes(condition)

        // Assert
        assertEquals(errorMessage, result.errorMessage)
        assertEquals(throwable, result.throwable)
        assertTrue(result.recipesList.containsAll(listOf(savedRecipe)))
    }

    @Test
    fun `saveRecipe should delegate to DAO with mapped entities`() = runTest {
        // Arrange
        val recipeDb = getRecipe()
        val ingredientsDb = getIngredients()
        val recipeCore = getRecipeCoreFromNetwork()
        val expectedId = 1

        whenever(localDbEntityMapper.recipeCoreToDb(recipeCore)).thenReturn(recipeDb)
        whenever(localDbEntityMapper.ingredientCoreToDb(recipeCore.ingredients[0]))
            .thenReturn(ingredientsDb[0])
        whenever(localDbEntityMapper.ingredientCoreToDb(recipeCore.ingredients[1]))
            .thenReturn(ingredientsDb[1])
        whenever(recipesDao.saveRecipe(any(), any())).thenReturn(expectedId)

        // Act
        val id = recipesListRepository.saveRecipe(recipeCore)

        // Assert
        verify(recipesDao).saveRecipe(recipeDb, ingredientsDb)
        assertEquals(id, expectedId)
    }

    @Test
    fun `deleteRecipe should call DAO with correct id`() = runTest {
        // Arrange
        val recipeCore = getRecipeCoreFromDb()

        // Act
        recipesListRepository.deleteRecipe(recipeCore)

        // Assert
        verify(recipesDao).deleteRecipeWithIngredients(recipeCore.id)
    }
}