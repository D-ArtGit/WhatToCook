package ru.dartx.local_db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import ru.dartx.local_db.dto.Ingredient
import ru.dartx.local_db.dto.Recipe

@Dao
interface RecipesDao {
    @Query("SELECT * FROM recipes ORDER BY name")
    suspend fun getRecipes(): List<Recipe>

    @Query("SELECT * FROM ingredients")
    suspend fun getIngredients(): List<Ingredient>

    @Query("SELECT * FROM recipes WHERE name LIKE :cond")
    suspend fun searchRecipes(cond: String): List<Recipe>

    @Query("SELECT * FROM recipes WHERE id IS :id LIMIT 1")
    suspend fun getRecipeById(id: Int): Recipe

    @Query("SELECT * FROM ingredients WHERE recipeId IS :recipeId")
    suspend fun getIngredientsById(recipeId: Int): List<Ingredient>

    @Transaction
    suspend fun saveRecipe(recipe: Recipe, ingredients: List<Ingredient>): Int {
        val recipeId = insertRecipe(recipe).toInt()
        ingredients.forEach {
            insertIngredient(it.copy(recipeId = recipeId))
        }
        return recipeId
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: Recipe): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIngredient(ingredient: Ingredient)

    @Transaction
    suspend fun deleteRecipeWithIngredients(recipeId: Int) {
        deleteRecipe(recipeId)
        deleteIngredients(recipeId)
    }

    @Query("DELETE FROM recipes WHERE id IS :recipeId")
    suspend fun deleteRecipe(recipeId: Int)

    @Query("DELETE FROM ingredients WHERE recipeId IS :recipeId")
    suspend fun deleteIngredients(recipeId: Int)

}