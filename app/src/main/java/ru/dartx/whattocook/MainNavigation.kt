package ru.dartx.whattocook

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import ru.dartx.core.navigation.IngredientsRecalc
import ru.dartx.core.navigation.Recipe
import ru.dartx.core.navigation.RecipesList
import ru.dartx.core.view_model_factory.ViewModelFactory
import ru.dartx.feature_ingredients_recalculation.IngredientsRecalculationScreen
import ru.dartx.feature_recipe_card.RecipeCardScreen
import ru.dartx.feature_recipes_list.RecipesListScreen
import ru.dartx.ui_kit.theme.WhatToCookTheme

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MainNavigation(
    viewModelFactory: ViewModelFactory,
    modifier: Modifier = Modifier,
    darkTheme: Boolean = false,
) {
    WhatToCookTheme(darkTheme = darkTheme) {
        SharedTransitionLayout(modifier = modifier) {
            val navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = RecipesList
            ) {
                composable<RecipesList> {
                    RecipesListScreen(
                        navHostController = navController,
                        viewModelFactory = viewModelFactory,
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedContentScope = this@composable
                    )
                }

                composable<Recipe> { backStackEntry ->
                    val recipe: Recipe = backStackEntry.toRoute()
                    RecipeCardScreen(
                        navHostController = navController,
                        viewModelFactory = viewModelFactory,
                        recipe.id,
                        recipe.extId,
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedContentScope = this@composable
                    )
                }

                composable<IngredientsRecalc> { backStackEntry ->
                    val recipe: Recipe = backStackEntry.toRoute()
                    IngredientsRecalculationScreen(
                        navHostController = navController,
                        viewModelFactory = viewModelFactory,
                        recipe.id,
                        recipe.extId,
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedContentScope = this@composable
                    )
                }
            }
        }

    }
}