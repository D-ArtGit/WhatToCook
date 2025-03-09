package ru.dartx.feature_recipes_list

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.dartx.core.dto.RecipeItem
import ru.dartx.core.navigation.Recipe
import ru.dartx.core.view_model_factory.ViewModelFactory
import ru.dartx.ui_kit.components.ErrorTextMessage
import ru.dartx.ui_kit.components.LoadingScreen
import ru.dartx.ui_kit.theme.medium
import ru.dartx.ui_kit.theme.small
import ru.dartx.ui_kit.theme.smaller
import ru.dartx.ui_kit.theme.smallest


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun RecipesListScreen(
    navHostController: NavHostController,
    viewModelFactory: ViewModelFactory,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    modifier: Modifier = Modifier,
    viewModel: RecipesListViewModel = viewModel(factory = viewModelFactory),
) {
    val recipesListState by viewModel.recipesListState
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val listState = rememberLazyListState()
    var previousSearchRequest by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(Unit) {
        if (previousSearchRequest == "") viewModel.getSavedRecipes()
        else viewModel.searchRecipes(previousSearchRequest)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            RecipesListScreenTopAppBar(
                onSearchDisplayChanged = {
                    if (previousSearchRequest != it) {
                        viewModel.searchRecipes(it)
                        scope.launch {
                            listState.requestScrollToItem(0)
                        }
                        previousSearchRequest = it
                    }
                },
                onExpandedChanged = {
                    viewModel.setSearchState(it)
                    if (!it && previousSearchRequest.isNotEmpty()) {
                        scope.launch {
                            listState.requestScrollToItem(0)
                        }
                        previousSearchRequest = ""
                    }
                },
                isSearchActive = recipesListState.isSearchActive
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        when (recipesListState.isLoading) {
            true -> {
                LoadingScreen(modifier = Modifier.padding(paddingValues))
            }

            false -> {
                RecipeListContent(
                    recipesListState = recipesListState,
                    viewModel = viewModel,
                    navHostController = navHostController,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedContentScope = animatedContentScope,
                    listState = listState,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }

        Snackbar(recipesListState, scope, snackbarHostState, viewModel, context)
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun RecipeListContent(
    recipesListState: RecipesListState,
    viewModel: RecipesListViewModel,
    navHostController: NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    listState: LazyListState,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        state = listState,
        verticalArrangement = Arrangement.spacedBy(smallest)
    ) {

        if (recipesListState.recipesList.isEmpty()) {
            when (recipesListState.isSearchActive) {
                true -> {
                    item { ErrorTextMessage(stringResource(R.string.empty_search_result_message)) }
                }

                else -> item { ErrorTextMessage(stringResource(R.string.no_saved_recipes_message)) }
            }
        } else {
            items(
                items = recipesListState.recipesList,
                key = { it.id to it.extId }) { recipeItem ->
                RecipeItemView(
                    recipeItem = recipeItem,
                    onSaveRecipe = viewModel::saveRecipe,
                    onDeleteRecipe = viewModel::deleteRecipe,
                    onClick = {
                        navHostController.navigate(
                            Recipe(
                                recipeItem.id,
                                recipeItem.extId
                            )
                        )
                    },
                    sharedTransitionScope = sharedTransitionScope,
                    animatedContentScope = animatedContentScope,
                )
            }
        }
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
    onExpandedChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    isSearchActive: Boolean = false,
) {
    TopAppBar(
        modifier = modifier,
        title = {
            ExpandableSearchView(
                onSearchDisplayChanged = onSearchDisplayChanged,
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
    modifier: Modifier = Modifier,
    isSearchActive: Boolean = false,
    onExpandedChanged: (Boolean) -> Unit,
    tint: Color,
) {
    BackHandler(enabled = isSearchActive) {
        onExpandedChanged(false)
    }

    AnimatedContent(
        targetState = isSearchActive,
        modifier = modifier.height(56.dp),
        transitionSpec = {
            if (targetState > initialState) {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300)
                ) togetherWith fadeOut(
                    animationSpec = tween(
                        durationMillis = 200,
                        delayMillis = 100
                    )
                )
            } else {
                fadeIn(animationSpec = tween(200)) togetherWith slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300)
                )
            }.apply {
                targetContentZIndex = if (isSearchActive) 1f else 0f
            }
        }
    ) { targetState ->
        when (targetState) {
            true -> {
                ExpandedSearchView(
                    onSearchDisplayChanged = onSearchDisplayChanged,
                    onExpandedChanged = onExpandedChanged,
                    tint = tint
                )
            }

            false -> {
                CollapsedSearchView(
                    onExpandedChanged = onExpandedChanged,
                    tint = tint
                )
            }
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
    onExpandedChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    tint: Color,
) {
    val focusManager = LocalFocusManager.current

    val textFieldFocusRequester = remember { FocusRequester() }
    var isNeedFocusRequest by rememberSaveable { mutableStateOf(true) }
    var textFieldValue by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue())
    }

    LaunchedEffect(isNeedFocusRequest) {
        if (isNeedFocusRequest)
            textFieldFocusRequester.requestFocus()
        isNeedFocusRequest = false
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
                        isNeedFocusRequest = true
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
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.primary,
                focusedTextColor = tint,
                unfocusedTextColor = tint,
                cursorColor = tint
            )
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun RecipeItemView(
    recipeItem: RecipeItem,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    modifier: Modifier = Modifier,
    onSaveRecipe: (RecipeItem) -> Unit,
    onDeleteRecipe: (RecipeItem) -> Unit,
    onClick: () -> Unit,
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
            .height(120.dp)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium
    ) {
        Row {
            with(sharedTransitionScope) {
                AsyncImage(
                    modifier = Modifier
                        .sharedElement(
                            sharedTransitionScope.rememberSharedContentState(key = "image-${recipeItem.id}-${recipeItem.extId}"),
                            animatedVisibilityScope = animatedContentScope
                        )
                        .size(120.dp)
                        .clip(
                            MaterialTheme.shapes.medium.copy(
                                topEnd = CornerSize(0.dp),
                                bottomEnd = CornerSize(0.dp)
                            )
                        ),
                    model = recipeItem.thumbnail,
                    contentDescription = null
                )
            }

            Column(modifier = Modifier.padding(smaller)) {
                with(sharedTransitionScope) {
                    Row(
                        modifier = Modifier
                            .sharedElement(
                                sharedTransitionScope.rememberSharedContentState(key = "row-${recipeItem.id}-${recipeItem.extId}"),
                                animatedVisibilityScope = animatedContentScope
                            )
                    ) {
                        Text(
                            modifier = Modifier
                                .weight(1F)
                                .padding(smaller),
                            text = recipeItem.name,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        IconButton(
                            modifier = Modifier
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
                }

                with(sharedTransitionScope) {
                    Text(
                        modifier = Modifier
                            .sharedElement(
                                sharedTransitionScope.rememberSharedContentState(key = "ingredients-${recipeItem.id}-${recipeItem.extId}"),
                                animatedVisibilityScope = animatedContentScope
                            )
                            .padding(smaller),
                        text = recipeItem.ingredients,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

            }
        }
    }
}



