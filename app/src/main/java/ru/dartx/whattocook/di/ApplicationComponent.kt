package ru.dartx.whattocook.di

import android.app.Application
import android.content.Context
import dagger.BindsInstance
import dagger.Component
import ru.dartx.core.mediator.AppProvider
import ru.dartx.core.mediator.ProvidersFacade
import ru.dartx.feature_ingredients_recalculation.IngredientsRecalculationViewModelModule
import ru.dartx.feature_recipe_card.RecipeCardViewModelModule
import ru.dartx.feature_recipes_list.RecipesListViewModelModule
import ru.dartx.network.di.NetworkModule
import ru.dartx.whattocook.MainActivity

@ApplicationScope
@Component(
    dependencies = [ProvidersFacade::class],
    modules = [
        RecipesListViewModelModule::class,
        RecipeCardViewModelModule::class,
        IngredientsRecalculationViewModelModule::class,
        NetworkModule::class
    ]
)
interface ApplicationComponent : AppProvider {

    fun inject(mainActivity: MainActivity)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(context: Context): Builder

        fun build(): ApplicationComponent
    }

    companion object {
        private var appComponent: AppProvider? = null

        fun create(application: Application): AppProvider {
            return appComponent ?: DaggerApplicationComponent.builder()
                .application(application.applicationContext)
                .build().also { appComponent = it }

        }
    }
}