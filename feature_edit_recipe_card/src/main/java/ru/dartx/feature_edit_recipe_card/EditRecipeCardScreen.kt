package ru.dartx.feature_edit_recipe_card

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import ru.dartx.core.dto.RecipeCore
import ru.dartx.core.navigation.IngredientsRecalc
import ru.dartx.core.view_model_factory.ViewModelFactory
import ru.dartx.ui_kit.components.ErrorTextMessage
import ru.dartx.ui_kit.components.IngredientsList
import ru.dartx.ui_kit.components.LoadingScreen
import ru.dartx.ui_kit.components.TopAppBarWithArrowBack
import ru.dartx.ui_kit.theme.medium
import ru.dartx.ui_kit.theme.smaller

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun EditRecipeCardScreen(
    navHostController: NavHostController,
    viewModelFactory: ViewModelFactory,
    id: Int,
    extId: Int,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    modifier: Modifier = Modifier,
    viewModel: EditRecipeCardViewModel = viewModel(factory = viewModelFactory),
) {
    val recipeState by viewModel.recipeState
    val recipeValidatorState by viewModel.recipeValidatorState

    if (recipeState.recipe == null && (id != 0 || extId != 0)) {
        LaunchedEffect(Unit) {
            viewModel.getRecipe(id, extId)
        }
    }
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBarWithArrowBack(
                title = stringResource(
                    id = if (id != 0) R.string.recipe_card_title
                    else R.string.new_recipe_card_title
                ),
                actions = {
                    IconButton(
                        onClick = {
                            if (recipeState.recipe?.let { viewModel.saveRecipe(it) } == true)
                                navHostController.navigateUp()
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Save,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                onBackArrowPressed = { navHostController.navigateUp() },
            )
        }
    ) { paddingValues ->

        if (recipeState.isLoading) {
            LoadingScreen(modifier = Modifier.padding(paddingValues))
        } else {
            recipeState.errorMessage?.let {
                ErrorTextMessage(
                    message = it,
                    modifier = Modifier.padding(paddingValues),
                )
            } ?: recipeState.throwable?.let {
                ErrorTextMessage(
                    message = it.message
                        ?: stringResource(R.string.unknown_error),
                    modifier = Modifier.padding(paddingValues),
                )
            } ?: recipeState.recipe?.let { recipe ->
                RecipeCard(
                    recipe = recipe,
                    recipeValidatorState = recipeValidatorState,
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
                    onRecipeNameChanged = { viewModel.setRecipeName(it) },
                    onRecipeInstructionChanged = { viewModel.setRecipeInstruction(it) },
                    sharedTransitionScope = sharedTransitionScope,
                    animatedContentScope = animatedContentScope,
                    modifier = Modifier.padding(paddingValues),
                )
            }
        }
    }
}


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun RecipeCard(
    recipe: RecipeCore,
    recipeValidatorState: EditRecipeCardViewModel.RecipeValidatorState,
    onSaveRecipe: (RecipeCore) -> Unit,
    onDeleteRecipe: (RecipeCore) -> Unit,
    onClickRecalc: () -> Unit,
    onRecipeNameChanged: (String) -> Unit,
    onRecipeInstructionChanged: (String) -> Unit,
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
        Header(
            onRecipeNameChanged = onRecipeNameChanged,
            onDeleteRecipe = onDeleteRecipe,
            onSaveRecipe = onSaveRecipe,
            recipe = recipe,
            sharedTransitionScope = sharedTransitionScope,
            animatedContentScope = animatedContentScope,
            nameErrorMessage = recipeValidatorState.nameErrorMessage
        )

        IngredientsList(
            recipe = recipe,
            sharedTransitionScope = sharedTransitionScope,
            animatedContentScope = animatedContentScope,
            onClickRecalc = onClickRecalc,
        )

        Instruction(
            onRecipeInstructionChanged = { onRecipeInstructionChanged(it) },
            recipe = recipe,
            sharedTransitionScope = sharedTransitionScope,
            animatedContentScope = animatedContentScope,
            instructionErrorMessage = recipeValidatorState.instructionErrorMessage,
        )

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
    }
}

@Composable
fun Header(
    onRecipeNameChanged: (String) -> Unit,
    onDeleteRecipe: (RecipeCore) -> Unit,
    onSaveRecipe: (RecipeCore) -> Unit,
    recipe: RecipeCore,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    modifier: Modifier = Modifier,
    nameErrorMessage: Int? = null,
) {
    with(sharedTransitionScope) {
        Row(
            modifier = modifier
                .sharedElement(
                    sharedTransitionScope.rememberSharedContentState(key = "row-${recipe.id}-${recipe.extId}"),
                    animatedVisibilityScope = animatedContentScope
                )
                .padding(start = medium, end = medium, top = medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextInputFieldWithErrorState(
                modifier = Modifier
                    .weight(1F)
                    .padding(smaller),
                value = recipe.name,
                onValueChange = { onRecipeNameChanged(it) },
                label = stringResource(id = R.string.name_label),
                errorMessage = nameErrorMessage?.let { stringResource(it) },
                textStyle = MaterialTheme.typography.headlineMedium,
                imeAction = ImeAction.Next
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
}

@Composable
fun Instruction(
    onRecipeInstructionChanged: (String) -> Unit,
    recipe: RecipeCore,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    modifier: Modifier = Modifier,
    instructionErrorMessage: Int? = null,
) {
    with(sharedTransitionScope) {

        Column(
            modifier = modifier
                .sharedElement(
                    sharedTransitionScope.rememberSharedContentState(key = "instruction-${recipe.id}-${recipe.extId}"),
                    animatedVisibilityScope = animatedContentScope
                )
                .padding(start = medium, end = medium)
        ) {
            Text(
                modifier = Modifier
                    .padding(smaller),
                text = stringResource(id = R.string.instruction),
                style = MaterialTheme.typography.headlineSmall
            )

            TextInputFieldWithErrorState(
                modifier = Modifier
                    .padding(smaller),
                value = recipe.instruction,
                onValueChange = { onRecipeInstructionChanged(it) },
                label = stringResource(id = R.string.instruction_label),
                errorMessage = instructionErrorMessage?.let { stringResource(it) },
                textStyle = MaterialTheme.typography.bodyMedium,
                imeAction = ImeAction.Done
            )
        }
    }
}


@Composable
fun TextInputFieldWithErrorState(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    errorMessage: String? = null,
    textStyle: TextStyle = MaterialTheme.typography.headlineMedium,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Done,
) {
    OutlinedTextField(
        modifier = modifier.semantics { },
        value = value,
        onValueChange = {
            onValueChange(it)
        },
        label = {
            Text(
                text = label,
            )
        },
        placeholder = {
            Text(
                text = if (!errorMessage.isNullOrEmpty()) errorMessage else "",
                style = textStyle.copy(color = MaterialTheme.colorScheme.error)
            )
        },
        isError = !errorMessage.isNullOrEmpty(),
        textStyle = textStyle,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = keyboardType,
            imeAction = imeAction
        )
    )
}

@Preview(showBackground = true)
@Composable
fun Preview() {

}