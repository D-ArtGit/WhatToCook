package ru.dartx.feature_recipes_list

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import ru.dartx.ui_kit.theme.medium

@Composable
fun ExpandableSearchView(
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
        IconButton(
            modifier = Modifier
                .testTag("search_button"),
            onClick = { onExpandedChanged(true) }
        ) {
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
        IconButton(
            modifier = Modifier
                .testTag("close_search_button"),
            onClick = {
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
                .focusRequester(textFieldFocusRequester)
                .testTag("search_field"),
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