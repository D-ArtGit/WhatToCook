package ru.dartx.whattocook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.ui.Modifier
import ru.dartx.core.view_model_factory.ViewModelFactory
import ru.dartx.feature_recipes_list.RecipesListScreen
import ru.dartx.ui_kit.theme.WhatToCookTheme
import ru.dartx.whattocook.di.App
import javax.inject.Inject

class MainActivity : ComponentActivity() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val component by lazy { (application as App).component }

    override fun onCreate(savedInstanceState: Bundle?) {
        component.inject(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val insets = WindowInsets.ime
            WhatToCookTheme(darkTheme = isSystemInDarkTheme()) {
                RecipesListScreen(
                    viewModelFactory = viewModelFactory,
                    modifier = Modifier.windowInsetsPadding(insets)
                )
            }
        }
    }
}