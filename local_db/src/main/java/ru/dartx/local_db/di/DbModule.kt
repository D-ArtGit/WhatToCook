package ru.dartx.local_db.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import ru.dartx.local_db.MainDataBase
import ru.dartx.local_db.dao.RecipeDao
import ru.dartx.local_db.dao.RecipesListDao


@Module
object DbModule {
    @Provides
    fun provideDatabase(context: Application): MainDataBase =
        Room.databaseBuilder(context, MainDataBase::class.java, "recipes.db")
            .build()

    @Provides
    fun providesRecipeDao(mainDataBase: MainDataBase): RecipeDao = mainDataBase.recipeDao()

    @Provides
    fun providesRecipesListDao(mainDataBase: MainDataBase): RecipesListDao = mainDataBase.recipesListDao()
}