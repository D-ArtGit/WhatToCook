package ru.dartx.core_factory

import ru.dartx.core.database.DatabaseProvider
import ru.dartx.core.mediator.AppProvider
import ru.dartx.core_impl.database.DaggerDatabaseComponent

object CoreProvidersFactory {
    fun createDatabaseBuilder(appProvider: AppProvider): DatabaseProvider = DaggerDatabaseComponent.builder().appProvider(appProvider).build()
}