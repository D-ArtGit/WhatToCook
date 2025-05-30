package ru.dartx.ui_kit.components

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import ru.dartx.core.dto.IngredientCore
import ru.dartx.core.dto.RecipeCore
import ru.dartx.ui_kit.theme.medium
import ru.dartx.ui_kit.theme.small
import ru.dartx.ui_kit.theme.smaller
import ru.dartx.ui_kit.R

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun IngredientsList(
    recipe: RecipeCore,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    modifier: Modifier = Modifier,
    onClickRecalc: (() -> Unit)? = null,
) {
    with(sharedTransitionScope) {
        Column(
            modifier = modifier
                .sharedElement(
                    sharedTransitionScope.rememberSharedContentState(key = "ingredients-${recipe.id}-${recipe.extId}"),
                    animatedVisibilityScope = animatedContentScope
                )
                .padding(start = medium, end = medium)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    modifier = Modifier
                        .weight(1F)
                        .padding(smaller),
                    text = stringResource(id = R.string.ingredients),
                    style = MaterialTheme.typography.headlineSmall
                )
                if (onClickRecalc != null) {
                    IconButton(
                        modifier = Modifier.testTag("recalc_button"),
                        onClick = onClickRecalc,
                        colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.tertiaryContainer)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_calculate),
                            contentDescription = null
                        )
                    }
                }
            }
            recipe.ingredients.forEach { ingredient ->
                IngredientItem(ingredient)
            }
        }
    }

}

@Composable
fun IngredientItem(
    ingredient: IngredientCore,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        Row {
            Text(
                modifier = Modifier
                    .weight(1F)
                    .padding(smaller),
                text = ingredient.ingredient,
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = FontFamily.Cursive
            )
            Text(
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(smaller)
                    .testTag("quantity"),
                text = ingredient.quantity,
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = FontFamily.Cursive
            )
            Text(
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(smaller),
                text = ingredient.unitOfMeasure,
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = FontFamily.Cursive
            )
        }
        HorizontalDivider(
            modifier = Modifier.padding(end = small),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}