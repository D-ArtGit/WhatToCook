package ru.dartx.core_impl

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.Reusable
import ru.dartx.core.database.DatabaseContract
import ru.dartx.core.database.RecipesDao
import javax.inject.Singleton


@Module
class DbModule {
    @Provides
    @Singleton
    fun provideDatabase(context: Context): DatabaseContract =
        Room.databaseBuilder(context, MainDataBase::class.java, "recipes.db")
            .build()

    @Provides
    @Reusable
    fun providesRecipesListDao(databaseContract: DatabaseContract): RecipesDao =
        databaseContract.recipesDao()
}