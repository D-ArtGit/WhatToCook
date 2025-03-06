package ru.dartx.local_db.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("recipes")
data class Recipe(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val extId: Int,
    val name: String,
    val category: String,
    val area: String,
    val instruction: String,
    val thumbnail: String,
    val tags: String,
    val sourceUrl: String,
    val youTubeUrl: String
)

@Entity("ingredients")
data class Ingredient(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val recipeId: Int,
    val extId: Int,
    val ingredient: String,
    val quantity: String,
    val unitOfMeasure: String
)