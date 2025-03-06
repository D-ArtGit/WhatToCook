package ru.dartx.whattocook.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import ru.dartx.feature_ingredients_recalculation.IngredientsRecalculationViewModelModule
import ru.dartx.feature_recipe_card.RecipeCardViewModelModule
import ru.dartx.feature_recipes_list.RecipesListViewModelModule
import ru.dartx.local_db.di.DbModule
import ru.dartx.network.di.NetworkModule
import ru.dartx.whattocook.MainActivity

@ApplicationScope
@Component(
    modules = [
        RecipesListViewModelModule::class,
        RecipeCardViewModelModule::class,
        IngredientsRecalculationViewModelModule::class,
        DbModule::class, NetworkModule::class
    ]
)
interface ApplicationComponent {
    fun inject(mainActivity: MainActivity)

    fun provideApplication(): Application

    @Component.Factory
    interface ApplicationComponentFactory {
        fun create(@BindsInstance context: Application): ApplicationComponent
    }
}