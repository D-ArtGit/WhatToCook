package ru.dartx.feature_recipe_card

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.dartx.core.view_model_factory.ViewModelKey

@Module
interface RecipeCardViewModelModule {
    @IntoMap
    @ViewModelKey(RecipeCardViewModel::class)
    @Binds
    fun bindRecipeCardViewModel(viewModel: RecipeCardViewModel): ViewModel
}