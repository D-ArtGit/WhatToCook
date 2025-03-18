package ru.dartx.feature_recipe_card

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import ru.dartx.core.dto.RecipeCore
import ru.dartx.core.navigation.IngredientsRecalc
import ru.dartx.ui_kit.components.ErrorTextMessage
import ru.dartx.ui_kit.components.IngredientsList
import ru.dartx.ui_kit.components.LoadingScreen
import ru.dartx.ui_kit.components.TopAppBarWithArrowBack
import ru.dartx.ui_kit.theme.medium
import ru.dartx.ui_kit.theme.smaller

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun RecipeCardScreen(
    navHostController: NavHostController,
    id: Int,
    extId: Int,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    modifier: Modifier = Modifier,
    viewModel: RecipeCardViewModel = hiltViewModel(),
) {
    val recipeState by viewModel.recipeState
    if (recipeState.recipe == null) {
        LaunchedEffect(Unit) {
            viewModel.getRecipe(id, extId)
        }
    }
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBarWithArrowBack(
                title = stringResource(id = R.string.recipe_card_title),
                onBackArrowPressed = { navHostController.navigateUp() },
            )
        }
    ) { paddingValues ->

        if (recipeState.isLoading) {
            LoadingScreen(modifier = Modifier.padding(paddingValues))
        } else {
            if (recipeState.errorMessage != null) {
                ErrorTextMessage(
                    message = recipeState.errorMessage!!,
                    modifier = Modifier.padding(paddingValues),
                )
            } else if (recipeState.throwable != null) {
                ErrorTextMessage(
                    message = recipeState.throwable!!.message
                        ?: stringResource(R.string.unknown_error),
                    modifier = Modifier.padding(paddingValues),
                )
            } else {
                recipeState.recipe?.let { recipe ->
                    RecipeCard(
                        recipe = recipe,
                        onSaveRecipe = { viewModel.saveRecipe(it) },
                        onDeleteRecipe = { viewModel.deleteRecipe(it) },
                        onClickRecalc = {
                            navHostController.navigate(
                                IngredientsRecalc(
                                    id = recipe.id,
                                    extId = recipe.extId
                                )
                            )
                        },
                        sharedTransitionScope = sharedTransitionScope,
                        animatedContentScope = animatedContentScope,
                        modifier = Modifier.padding(paddingValues),
                    )
                }

            }
        }
    }
}


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun RecipeCard(
    recipe: RecipeCore,
    onSaveRecipe: (RecipeCore) -> Unit,
    onDeleteRecipe: (RecipeCore) -> Unit,
    onClickRecalc: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    modifier: Modifier = Modifier,
) {

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(medium)
    ) {
        with(sharedTransitionScope) {
            Row(
                modifier = Modifier
                    .sharedElement(
                        sharedTransitionScope.rememberSharedContentState(key = "row-${recipe.id}-${recipe.extId}"),
                        animatedVisibilityScope = animatedContentScope
                    )
                    .padding(start = medium, end = medium, top = medium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .weight(1F)
                        .padding(smaller),
                    text = recipe.name,
                    style = MaterialTheme.typography.headlineMedium
                )
                IconButton(
                    modifier = Modifier.testTag("save_button"),
                    colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.tertiary),
                    onClick = {
                        if (recipe.isSaved) onDeleteRecipe(recipe)
                        else onSaveRecipe(recipe)
                    }) {
                    Icon(
                        imageVector = if (recipe.isSaved) Icons.Filled.Favorite
                        else Icons.Filled.FavoriteBorder,
                        contentDescription = null
                    )
                }
            }
        }

        with(sharedTransitionScope) {
            AsyncImage(
                model = recipe.thumbnail,
                contentDescription = null,
                modifier = Modifier
                    .sharedElement(
                        sharedTransitionScope.rememberSharedContentState(key = "image-${recipe.id}-${recipe.extId}"),
                        animatedVisibilityScope = animatedContentScope
                    )
                    .fillMaxWidth()
                    .aspectRatio(1F / 1F)
            )
        }

        IngredientsList(
            recipe = recipe,
            sharedTransitionScope = sharedTransitionScope,
            animatedContentScope = animatedContentScope,
            onClickRecalc = onClickRecalc,
        )

        Instruction(instruction = recipe.instruction)
    }
}

@Composable
fun Instruction(instruction: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(start = medium, end = medium)) {
        Text(
            modifier = Modifier
                .padding(smaller),
            text = stringResource(id = R.string.instruction),
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            modifier = Modifier
                .padding(smaller),
            text = instruction,
            style = MaterialTheme.typography.bodyMedium,
            fontFamily = FontFamily.Cursive
        )
    }
}
