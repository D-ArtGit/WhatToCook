package ru.dartx.whattocook

import ru.dartx.core.dto.IngredientCore
import ru.dartx.core.dto.RecipeCore
import ru.dartx.local_db.dto.Ingredient
import ru.dartx.local_db.dto.Recipe
import ru.dartx.network.dto.Meal

object RecipesFactory {
    fun getMeal() = Meal(
        idMeal = 123,
        strMeal = "Test meal",
        strDrinkAlternate = null,
        strCategory = "FastFood",
        strArea = "Europe",
        strInstructions = "Test instructions",
        strMealThumb = "Test thumbnail",
        strTags = null,
        strYoutube = null,
        strIngredient1 = "Test ingredient 1",
        strIngredient2 = "Test ingredient 2",
        strIngredient3 = "",
        strIngredient4 = null,
        strIngredient5 = null,
        strIngredient6 = null,
        strIngredient7 = null,
        strIngredient8 = null,
        strIngredient9 = null,
        strIngredient10 = null,
        strIngredient11 = null,
        strIngredient12 = null,
        strIngredient13 = null,
        strIngredient14 = null,
        strIngredient15 = null,
        strIngredient16 = null,
        strIngredient17 = null,
        strIngredient18 = null,
        strIngredient19 = null,
        strIngredient20 = null,
        strMeasure1 = "100g",
        strMeasure2 = "1-1/2 tsp",
        strMeasure3 = "",
        strMeasure4 = null,
        strMeasure5 = null,
        strMeasure6 = null,
        strMeasure7 = null,
        strMeasure8 = null,
        strMeasure9 = null,
        strMeasure10 = null,
        strMeasure11 = null,
        strMeasure12 = null,
        strMeasure13 = null,
        strMeasure14 = null,
        strMeasure15 = null,
        strMeasure16 = null,
        strMeasure17 = null,
        strMeasure18 = null,
        strMeasure19 = null,
        strMeasure20 = null,
        strSource = null,
        strImageSource = null,
        strCreativeCommonsConfirmed = null,
        dateModified = null
    )

    fun getRecipeCoreFromNetwork() = RecipeCore(
        id = 0,
        extId = 123,
        name = "Test meal",
        category = "FastFood",
        area = "Europe",
        instruction = "Test instructions",
        thumbnail = "Test thumbnail",
        ingredients = listOf(
            IngredientCore(
                id = 0,
                recipeId = 0,
                extId = 123,
                ingredient = "Test ingredient 1",
                quantity = "100",
                unitOfMeasure = "g"
            ),
            IngredientCore(
                id = 0,
                recipeId = 0,
                extId = 123,
                ingredient = "Test ingredient 2",
                quantity = "1 1/2",
                unitOfMeasure = "tsp"
            )
        ),
        tags = "",
        sourceUrl = "",
        youTubeUrl = "",
        isSaved = false
    )

    fun getRecipeCoreFromDb() = RecipeCore(
        id = 1,
        extId = 123,
        name = "Test meal",
        category = "FastFood",
        area = "Europe",
        instruction = "Test instructions",
        thumbnail = "Test thumbnail",
        ingredients = listOf(
            IngredientCore(
                id = 1,
                recipeId = 1,
                extId = 123,
                ingredient = "Test ingredient 1",
                quantity = "100",
                unitOfMeasure = "g"
            ),
            IngredientCore(
                id = 2,
                recipeId = 1,
                extId = 123,
                ingredient = "Test ingredient 2",
                quantity = "1 1/2",
                unitOfMeasure = "tsp"
            )
        ),
        tags = "",
        sourceUrl = "",
        youTubeUrl = "",
        isSaved = true
    )

    fun getRecipe() = Recipe(
        id = 1,
        extId = 123,
        name = "Test meal",
        category = "FastFood",
        area = "Europe",
        instruction = "Test instructions",
        thumbnail = "Test thumbnail",
        tags = "",
        sourceUrl = "",
        youTubeUrl = ""
    )

    fun getIngredients() = listOf(
        Ingredient(
            id = 1,
            recipeId = 1,
            extId = 123,
            ingredient = "Test ingredient 1",
            quantity = "100",
            unitOfMeasure = "g",
        ),
        Ingredient(
            id = 2,
            recipeId = 1,
            extId = 123,
            ingredient = "Test ingredient 2",
            quantity = "1 1/2",
            unitOfMeasure = "tsp",
        )
    )
}