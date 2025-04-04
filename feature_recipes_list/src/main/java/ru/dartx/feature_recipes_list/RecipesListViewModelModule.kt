package ru.dartx.feature_recipes_list

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.dartx.core.view_model_factory.ViewModelKey

@Module
interface RecipesListViewModelModule {
    @IntoMap
    @ViewModelKey(RecipesListViewModel::class)
    @Binds
    fun bindRecipesListViewModel(viewModel: RecipesListViewModel): ViewModel
}