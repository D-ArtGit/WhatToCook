package ru.dartx.whattocook.di

import android.app.Application
import dagger.Component
import ru.dartx.core.database.DatabaseProvider
import ru.dartx.core.mediator.AppProvider
import ru.dartx.core.mediator.ProvidersFacade
import ru.dartx.core_factory.CoreProvidersFactory

@Component(dependencies = [AppProvider::class, DatabaseProvider::class])
interface FacadeComponent : ProvidersFacade {

    companion object {
        fun init(application: Application): FacadeComponent {
            return DaggerFacadeComponent.builder()
                .appProvider(ApplicationComponent.create(application))
                .databaseProvider(
                    CoreProvidersFactory.createDatabaseBuilder(
                        ApplicationComponent.create(
                            application
                        )
                    )
                )
                .build()
        }
    }
}