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
import ru.dartx.core.mediator.AppWithFacade
import ru.dartx.core.view_model_factory.ViewModelFactory
import ru.dartx.whattocook.di.MainActivityComponent
import javax.inject.Inject

class MainActivity : ComponentActivity() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {

        MainActivityComponent.init((application as AppWithFacade).getFacade()).inject(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val insets = WindowInsets.ime
            MainNavigation(
                modifier = Modifier.windowInsetsPadding(insets),
                viewModelFactory = viewModelFactory,
                darkTheme = isSystemInDarkTheme()
            )
        }
    }
}