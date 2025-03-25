package ru.dartx.core_impl.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.dartx.core.database.DatabaseContract
import ru.dartx.core.dto.Ingredient
import ru.dartx.core.dto.Recipe

@Database(
    entities = [Recipe::class, Ingredient::class],
    version = 1,
    exportSchema = false
)
abstract class MainDataBase : RoomDatabase(), DatabaseContract