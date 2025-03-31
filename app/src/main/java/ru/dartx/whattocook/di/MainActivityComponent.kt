package ru.dartx.whattocook.di

import dagger.Component
import ru.dartx.core.mediator.ProvidersFacade
import ru.dartx.whattocook.MainActivity

@MainActivityScope
@Component(dependencies = [ProvidersFacade::class], modules = [ViewModelModule::class])
interface MainActivityComponent {

    fun inject(activity: MainActivity)


    companion object {
        fun init(providersFacade: ProvidersFacade): MainActivityComponent {
            return DaggerMainActivityComponent.builder()
                .providersFacade(providersFacade)
                .build()
        }
    }
}