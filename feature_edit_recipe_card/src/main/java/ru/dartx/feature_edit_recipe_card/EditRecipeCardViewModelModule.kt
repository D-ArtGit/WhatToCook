package ru.dartx.feature_edit_recipe_card

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.dartx.core.view_model_factory.ViewModelKey

@Module
interface EditRecipeCardViewModelModule {
    @IntoMap
    @ViewModelKey(EditRecipeCardViewModel::class)
    @Binds
    fun bindRecipeCardViewModel(viewModel: EditRecipeCardViewModel): ViewModel
}