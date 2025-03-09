package ru.dartx.whattocook

import org.junit.Before
import org.junit.Test
import ru.dartx.network.mapper.NetworkEntityMapper
import ru.dartx.whattocook.RecipesFactory.getMeal
import ru.dartx.whattocook.RecipesFactory.getRecipeCoreFromNetwork

class NetworkEntityMapperTest {
    private lateinit var networkEntityMapper: NetworkEntityMapper

    @Before
    fun setup() {
        networkEntityMapper = NetworkEntityMapper()
    }

    @Test
    fun `mealToRecipe maps Meal correctly`() {
        val meal = getMeal()
        val expected = getRecipeCoreFromNetwork()
        val actual = networkEntityMapper.mealToRecipe(meal)
        assert(actual == expected)
    }
}