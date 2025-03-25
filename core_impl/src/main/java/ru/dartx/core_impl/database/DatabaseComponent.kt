package ru.dartx.core_impl.database

import dagger.Component
import ru.dartx.core.database.DatabaseProvider
import ru.dartx.core.mediator.AppProvider
import javax.inject.Singleton

@Singleton
@Component(
    dependencies = [AppProvider::class],
    modules = [DbModule::class]
)
interface DatabaseComponent : DatabaseProvider