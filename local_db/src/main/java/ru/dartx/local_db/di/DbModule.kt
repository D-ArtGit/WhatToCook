package ru.dartx.local_db.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.dartx.local_db.MainDataBase
import ru.dartx.local_db.dao.RecipeDao
import ru.dartx.local_db.dao.RecipesDao


@Module
@InstallIn(SingletonComponent::class)
object DbModule {
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): MainDataBase =
        Room.databaseBuilder(context, MainDataBase::class.java, "recipes.db")
            .build()

    @Provides
    fun providesRecipeDao(mainDataBase: MainDataBase): RecipeDao = mainDataBase.recipeDao()

    @Provides
    fun providesRecipesListDao(mainDataBase: MainDataBase): RecipesDao = mainDataBase.recipesListDao()
}