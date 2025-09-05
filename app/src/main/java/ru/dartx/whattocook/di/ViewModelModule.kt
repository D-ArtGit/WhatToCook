package ru.dartx.whattocook.di

import dagger.Module
import ru.dartx.feature_edit_recipe_card.EditRecipeCardViewModelModule
import ru.dartx.feature_ingredients_recalculation.IngredientsRecalculationViewModelModule
import ru.dartx.feature_recipe_card.RecipeCardViewModelModule
import ru.dartx.feature_recipes_list.RecipesListViewModelModule

@Module(
    includes = [
        RecipesListViewModelModule::class,
        RecipeCardViewModelModule::class,
        EditRecipeCardViewModelModule::class,
        IngredientsRecalculationViewModelModule::class
    ]
)
interface ViewModelModule