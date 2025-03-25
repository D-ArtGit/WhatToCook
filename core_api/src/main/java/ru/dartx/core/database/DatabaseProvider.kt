package ru.dartx.core.database

interface DatabaseProvider {
    fun provideDatabase(): DatabaseContract
    fun recipesDao(): RecipesDao
}