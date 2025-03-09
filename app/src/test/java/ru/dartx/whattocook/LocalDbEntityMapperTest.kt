package ru.dartx.whattocook

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import ru.dartx.local_db.mapper.LocalDbEntityMapper
import ru.dartx.whattocook.RecipesFactory.getIngredients
import ru.dartx.whattocook.RecipesFactory.getRecipe
import ru.dartx.whattocook.RecipesFactory.getRecipeCoreFromDb

class LocalDbEntityMapperTest {
    private lateinit var localDbEntityMapper: LocalDbEntityMapper

    @Before
    fun setup() {
        localDbEntityMapper = LocalDbEntityMapper()
    }

    @Test
    fun `recipeFromDbToCore maps Recipe correctly`() {
        val recipe = getRecipe()
        val ingredients = getIngredients()
        val result = localDbEntityMapper.recipeFromDbToCore(recipe, ingredients)
        val expected = getRecipeCoreFromDb()
        assertEquals(expected, result)
    }

    @Test
    fun `recipeCoreToDb maps RecipeCore correctly`() {
        val recipeCore = getRecipeCoreFromDb()
        val result = localDbEntityMapper.recipeCoreToDb(recipeCore)
        val expected = getRecipe()
        assertEquals(expected, result)
    }

    @Test
    fun `ingredientCoreToDb maps IngredientCore correctly`() {
        val ingredientsCore = getRecipeCoreFromDb().ingredients
        val result = ingredientsCore.map { localDbEntityMapper.ingredientCoreToDb(it) }
        val expected = getIngredients()
        assertEquals(expected, result)
    }
}