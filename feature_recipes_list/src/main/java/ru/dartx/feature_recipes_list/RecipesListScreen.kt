package ru.dartx.feature_recipes_list

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.dartx.core.dto.RecipeItem
import ru.dartx.core.view_model_factory.ViewModelFactory
import ru.dartx.ui_kit.theme.medium
import ru.dartx.ui_kit.theme.small
import ru.dartx.ui_kit.theme.smaller
import ru.dartx.ui_kit.theme.smallest


@Composable
fun RecipesListScreen(
    modifier: Modifier = Modifier,
    viewModelFactory: ViewModelFactory,
    viewModel: RecipesListViewModel = viewModel(factory = viewModelFactory),
) {
    val recipesListState by viewModel.recipesListState
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current



    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            RecipesListScreenTopAppBar(
                onSearchDisplayChanged = { viewModel.searchRecipes(it) },
                onSearchDisplayClosed = { viewModel.onSearchClosed() },
                onExpandedChanged = { viewModel.setSearchState(it) },
                isSearchActive = recipesListState.isSearchActive
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(smallest)
        ) {
            if (recipesListState.recipesList.isEmpty()) {
                when (recipesListState.isSearchActive) {
                    true -> {
                        item { EmptyListTextMessage(stringResource(R.string.empty_search_result_message)) }
                    }

                    else -> item { EmptyListTextMessage(stringResource(R.string.no_saved_recipes_message)) }
                }
            } else {
                items(
                    items = recipesListState.recipesList,
                    key = { it.id to it.extId }) { recipeItem ->
                    RecipeItemView(
                        recipeItem = recipeItem,
                        onSaveRecipe = viewModel::saveRecipe,
                        onDeleteRecipe = viewModel::deleteRecipe
                    )
                }
            }
        }

        Snackbar(recipesListState, scope, snackbarHostState, viewModel, context)
    }
}

@Composable
private fun Snackbar(
    recipesListState: RecipesListState,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    viewModel: RecipesListViewModel,
    context: Context,
) {
    if (recipesListState.isSearchActive) {
        var snackbarWasShown = false
        recipesListState.errorMessage?.let {
            snackbarWasShown = true
            scope.launch {
                snackbarHostState.showSnackbar(it)
                viewModel.resetErrorState()
            }
        }
        if (!snackbarWasShown) {
            recipesListState.throwable?.let {
                scope.launch {
                    val message = if (it.message != null) {
                        it.message!!
                    } else {
                        context.getString(R.string.unknown_error)
                    }
                    snackbarHostState.showSnackbar(message)
                    viewModel.resetErrorState()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecipesListScreenTopAppBar(
    onSearchDisplayChanged: (String) -> Unit,
    onSearchDisplayClosed: () -> Unit,
    onExpandedChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    isSearchActive: Boolean = false,
) {
    TopAppBar(
        modifier = modifier,
        title = {
            ExpandableSearchView(
                onSearchDisplayChanged = onSearchDisplayChanged,
                onSearchDisplayClosed = onSearchDisplayClosed,
                modifier = modifier,
                isSearchActive = isSearchActive,
                onExpandedChanged = onExpandedChanged,
                tint = MaterialTheme.colorScheme.onPrimary
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

@Composable
private fun ExpandableSearchView(
    onSearchDisplayChanged: (String) -> Unit,
    onSearchDisplayClosed: () -> Unit,
    modifier: Modifier = Modifier,
    isSearchActive: Boolean = false,
    onExpandedChanged: (Boolean) -> Unit,
    tint: Color,
) {
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    BackHandler {
        if (isSearchActive) {
            onExpandedChanged(false)
            onSearchDisplayClosed()
        } else {
            backDispatcher?.onBackPressed()
        }
    }

    Box(modifier = modifier.height(56.dp)) {
        val screenWidthDp: Dp = with(LocalConfiguration.current) {
            screenWidthDp.dp
        }

        AnimatedVisibility(
            visible = isSearchActive,
            enter = slideInHorizontally(initialOffsetX = { screenWidthDp.value.toInt() }) + fadeIn(),
            exit = slideOutHorizontally(targetOffsetX = { -screenWidthDp.value.toInt() }) + fadeOut()
        ) {
            ExpandedSearchView(
                onSearchDisplayChanged = onSearchDisplayChanged,
                onSearchDisplayClosed = onSearchDisplayClosed,
                onExpandedChanged = onExpandedChanged,
                tint = tint
            )
        }
        AnimatedVisibility(
            visible = !isSearchActive,
            enter = expandHorizontally() + fadeIn(),
            exit = shrinkHorizontally() + fadeOut()
        ) {
            CollapsedSearchView(
                onExpandedChanged = onExpandedChanged,
                tint = tint
            )
        }
    }
}

@Composable
private fun CollapsedSearchView(
    onExpandedChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    tint: Color,
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.header),
            style = MaterialTheme.typography.headlineSmall,
            color = tint,
            modifier = Modifier
                .padding(start = medium)
        )
        IconButton(onClick = { onExpandedChanged(true) }) {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = null,
                tint = tint
            )
        }
    }
}

@Composable
private fun ExpandedSearchView(
    onSearchDisplayChanged: (String) -> Unit,
    onSearchDisplayClosed: () -> Unit,
    onExpandedChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    tint: Color,
) {
    val focusManager = LocalFocusManager.current

    val textFieldFocusRequester = remember { FocusRequester() }

    SideEffect {
        textFieldFocusRequester.requestFocus()
    }

    var textFieldValue by remember {
        mutableStateOf(TextFieldValue())
    }

    LaunchedEffect(textFieldValue) {
        delay(750L)
        onSearchDisplayChanged(textFieldValue.text)
    }

    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {
            onExpandedChanged(false)
            onSearchDisplayClosed()
        }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                tint = tint
            )
        }
        OutlinedTextField(
            value = textFieldValue,
            onValueChange = {
                textFieldValue = it
            },
            trailingIcon = {
                if (textFieldValue.text.isNotEmpty()) {
                    IconButton(onClick = {
                        textFieldValue = TextFieldValue()
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Clear,
                            contentDescription = null,
                            tint = tint
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(textFieldFocusRequester),
            placeholder = {
                Text(
                    text = stringResource(R.string.search),
                    color = tint,
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            textStyle = MaterialTheme.typography.bodyLarge,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                }
            ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.primary,
                unfocusedContainerColor = MaterialTheme.colorScheme.primary,
                focusedTextColor = tint,
                unfocusedTextColor = tint,
                cursorColor = tint
            )
        )
    }
}

@Composable
private fun EmptyListTextMessage(
    message: String,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier
            .padding(medium)
            .fillMaxWidth(),
        text = message,
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun RecipeItemView(
    recipeItem: RecipeItem,
    modifier: Modifier = Modifier,
    onSaveRecipe: (RecipeItem) -> Unit,
    onDeleteRecipe: (RecipeItem) -> Unit,
) {
    Surface(
        shadowElevation = smaller,
        modifier = modifier
            .fillMaxWidth()
            .padding(small)
            .border(
                width = smallest,
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium
            )
            .height(120.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Row {
            AsyncImage(
                modifier = Modifier
                    .size(120.dp)
                    .clip(
                        MaterialTheme.shapes.medium.copy(
                            topEnd = CornerSize(0.dp),
                            bottomEnd = CornerSize(0.dp)
                        )
                    ),
                model = recipeItem.thumbnail.ifEmpty { R.drawable.thumb_example },
                contentDescription = null
            )

            Column(modifier = Modifier.padding(smaller)) {
                Row {
                    Text(
                        modifier = Modifier
                            .weight(1F)
                            .padding(smaller),
                        text = recipeItem.name,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    IconButton(modifier = Modifier
                        .size(32.dp),
                        colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.tertiary),
                        onClick = {
                            if (recipeItem.isSaved) onDeleteRecipe(recipeItem)
                            else onSaveRecipe(recipeItem)
                        }) {
                        Icon(
                            imageVector = if (recipeItem.isSaved) Icons.Filled.Favorite
                            else Icons.Filled.FavoriteBorder,
                            contentDescription = null
                        )
                    }
                }
                Text(
                    modifier = Modifier.padding(smaller),
                    text = recipeItem.ingredients,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}



