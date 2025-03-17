package ru.dartx.feature_ingredients_recalculation

import android.widget.Toast
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import ru.dartx.core.dto.RecipeCore
import ru.dartx.core.view_model_factory.ViewModelFactory
import ru.dartx.ui_kit.components.ErrorTextMessage
import ru.dartx.ui_kit.components.IngredientsList
import ru.dartx.ui_kit.components.LoadingScreen
import ru.dartx.ui_kit.components.TopAppBarWithArrowBack
import ru.dartx.ui_kit.theme.larger
import ru.dartx.ui_kit.theme.small
import ru.dartx.ui_kit.theme.smaller

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun IngredientsRecalculationScreen(
    navHostController: NavHostController,
    viewModelFactory: ViewModelFactory,
    id: Int,
    extId: Int,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    modifier: Modifier = Modifier,
    viewModel: IngredientsRecalculationViewModel = viewModel(factory = viewModelFactory),
) {
    val recipeState by viewModel.recipeState
    LaunchedEffect(Unit) {
        viewModel.getRecipe(id, extId)
    }
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBarWithArrowBack(
                title = stringResource(id = R.string.ingredients_recalculation_title),
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
            } else if (recipeState.recipe != null) {
                IngredientsRecalculationContent(
                    recipe = recipeState.recipe!!,
                    onClickRecalc = { viewModel.recalculateIngredients(it) },
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
fun IngredientsRecalculationContent(
    recipe: RecipeCore,
    onClickRecalc: (Double) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    val textFieldFocusRequester = remember { FocusRequester() }
    var textFieldValue by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue())
    }
    val textFieldAsDouble = remember {
        derivedStateOf {
            textFieldValue.text
                .replace(',', '.')
                .toDoubleOrNull()
        }
    }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        delay(500)
        textFieldFocusRequester.requestFocus()
    }

    Column(modifier = modifier.padding(top = small)) {
        IngredientsList(
            recipe = recipe,
            sharedTransitionScope = sharedTransitionScope,
            animatedContentScope = animatedContentScope,
            modifier = Modifier.weight(1F)
        )
        Row(
            modifier = Modifier.padding(small),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(smaller)
        ) {
            OutlinedTextField(
                value = textFieldValue,
                onValueChange = { textFieldValue = it },
                modifier = Modifier
                    .weight(1F)
                    .focusRequester(textFieldFocusRequester)
                    .testTag("coefficient"),
                textStyle = MaterialTheme.typography.bodyLarge,
                placeholder = {
                    Text(
                        text = stringResource(R.string.coefficient),
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                ),
                isError = textFieldAsDouble.value == null && textFieldValue.text.isNotEmpty(),
                colors = OutlinedTextFieldDefaults.colors(errorTextColor = MaterialTheme.colorScheme.error)
            )
            TextButton(
                onClick = {
                    textFieldAsDouble.value?.let { onClickRecalc(it) } ?: Toast.makeText(
                        context,
                        context.getString(R.string.enter_correct_coefficient), Toast.LENGTH_SHORT
                    ).show()
                },
                modifier = Modifier
                    .weight(1F)
                    .height(larger)
                    .testTag("ingredients_recalc_button"),
                shape = MaterialTheme.shapes.extraSmall,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = stringResource(R.string.recalculate),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
