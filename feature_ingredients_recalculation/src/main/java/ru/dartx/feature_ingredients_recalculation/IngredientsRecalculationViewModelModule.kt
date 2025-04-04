package ru.dartx.feature_ingredients_recalculation

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.dartx.core.view_model_factory.ViewModelKey

@Module
interface IngredientsRecalculationViewModelModule {
    @IntoMap
    @ViewModelKey(IngredientsRecalculationViewModel::class)
    @Binds
    fun bindIngredientsRecalculationViewModel(viewModel: IngredientsRecalculationViewModel): ViewModel
}