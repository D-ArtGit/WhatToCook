package ru.dartx.whattocook.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import ru.dartx.feature_recipes_list.RecipesListViewModelModule
import ru.dartx.whattocook.MainActivity

@ApplicationScope
@Component(modules = [RecipesListViewModelModule::class, ru.dartx.local_db.di.DbModule::class, ru.dartx.network.di.NetworkModule::class])
interface ApplicationComponent {
    fun inject(mainActivity: MainActivity)

    fun provideApplication() : Application

    @Component.Factory
    interface ApplicationComponentFactory {
        fun create(@BindsInstance context: Application): ApplicationComponent
    }
}