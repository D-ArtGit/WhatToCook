package ru.dartx.core_impl

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