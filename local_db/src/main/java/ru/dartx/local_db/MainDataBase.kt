package ru.dartx.local_db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.dartx.local_db.dao.RecipeDao
import ru.dartx.local_db.dao.RecipesDao
import ru.dartx.local_db.dto.Ingredient
import ru.dartx.local_db.dto.Recipe

@Database(
    entities = [Recipe::class, Ingredient::class],
    version = 1,
    exportSchema = false
)
abstract class MainDataBase : RoomDatabase() {
    abstract fun recipesListDao(): RecipesDao
    abstract fun recipeDao(): RecipeDao
}