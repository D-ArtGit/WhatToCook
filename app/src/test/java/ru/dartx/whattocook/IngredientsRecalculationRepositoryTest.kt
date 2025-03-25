package ru.dartx.whattocook

import android.content.Context
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import ru.dartx.core.database.RecipesDao
import ru.dartx.core.mediator.ProvidersFacade
import ru.dartx.local_db.mapper.LocalDbEntityMapper
import ru.dartx.network.RecipesApi
import ru.dartx.network.dto.Meal
import ru.dartx.network.dto.ResultResponse
import ru.dartx.network.mapper.NetworkEntityMapper
import ru.dartx.repo_ingredients.IngredientsRecalculationRepository
import ru.dartx.repo_ingredients.R
import ru.dartx.whattocook.RecipesFactory.getIngredients
import ru.dartx.whattocook.RecipesFactory.getRecipe
import ru.dartx.whattocook.RecipesFactory.getRecipeCoreFromDb
import ru.dartx.whattocook.RecipesFactory.getRecipeCoreFromNetwork

class IngredientsRecalculationRepositoryTest {

    private lateinit var recipesDao: RecipesDao
    private lateinit var recipesApi: RecipesApi
    private lateinit var networkEntityMapper: NetworkEntityMapper
    private lateinit var localDbEntityMapper: LocalDbEntityMapper
    private lateinit var providersFacade: ProvidersFacade
    private lateinit var context: Context
    private lateinit var ingredientsRecalculationRepository: IngredientsRecalculationRepository

    @Before
    fun setup() {
        recipesDao = mock()
        recipesApi = mock()
        networkEntityMapper = mock()
        localDbEntityMapper = mock()
        providersFacade = mock()
        context = mock()
        ingredientsRecalculationRepository = IngredientsRecalculationRepository(
            recipesDao = recipesDao,
            recipesApi = recipesApi,
            networkEntityMapper = networkEntityMapper,
            localDbEntityMapper = localDbEntityMapper,
            providersFacade = providersFacade
        )
    }

    @Test
    fun `getRecipe should return local recipe when id is provided`() = runTest {
        // Arrange
        val recipeDb = getRecipe()
        val ingredientsDb = getIngredients()
        val expectedRecipe = getRecipeCoreFromDb()
        val id = expectedRecipe.id

        whenever(recipesDao.getRecipeById(id)).thenReturn(recipeDb)
        whenever(recipesDao.getIngredientsById(id)).thenReturn(ingredientsDb)
        whenever(localDbEntityMapper.recipeFromDbToCore(recipeDb, ingredientsDb)).thenReturn(
            expectedRecipe
        )

        // Act
        val result = ingredientsRecalculationRepository.getRecipe(id, 0)

        // Assert
        verify(recipesDao).getRecipeById(id)
        verify(recipesDao).getIngredientsById(id)
        verify(localDbEntityMapper).recipeFromDbToCore(recipeDb, ingredientsDb)
        assertEquals(expectedRecipe, result.recipeCore)
        assertNull(result.errorMessage)
        assertNull(result.throwable)
    }

    @Test
    fun `getRecipe should fetch from API when id is 0 with success`() = runTest {
        // Arrange
        val meal = mock<Meal>()
        val expectedRecipe = getRecipeCoreFromNetwork()
        val extId = expectedRecipe.extId

        whenever(recipesApi.getRecipeById(extId)).thenReturn(ResultResponse.Success(meal))
        whenever(networkEntityMapper.mealToRecipe(meal)).thenReturn(expectedRecipe)

        // Act
        val result = ingredientsRecalculationRepository.getRecipe(0, extId)

        // Assert
        verify(recipesApi).getRecipeById(extId)
        verify(networkEntityMapper).mealToRecipe(meal)
        assertEquals(expectedRecipe, result.recipeCore)
        assertNull(result.errorMessage)
        assertNull(result.throwable)
    }

    @Test
    fun `getRecipe should handle API error when fetching by extId`() = runTest {
        // Arrange
        val errorMessage = "API Error"
        val throwable = Throwable(errorMessage)
        val extId = 123

        whenever(recipesApi.getRecipeById(extId)).thenReturn(
            ResultResponse.Error(
                throwable,
                errorMessage
            )
        )

        // Act
        val result = ingredientsRecalculationRepository.getRecipe(0, extId)

        // Assert
        verify(recipesApi).getRecipeById(extId)
        assertNull(result.recipeCore)
        assertEquals(errorMessage, result.errorMessage)
        assertEquals(throwable, result.throwable)
    }

    @Test
    fun `getRecipe handles null API response`() = runTest {
        // Arrange
        val extId = 123
        whenever(recipesApi.getRecipeById(extId)).thenReturn(ResultResponse.Success(null))
        whenever(providersFacade.provideContext()).thenReturn(context)
        whenever(
            context.getString(R.string.recipe_not_found)
        ).thenReturn("Recipe not found")

        // Act
        val result = ingredientsRecalculationRepository.getRecipe(0, extId)

        // Assert
        assertEquals("Recipe not found", result.errorMessage)
        assertNull(result.recipeCore)
    }
}