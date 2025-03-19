package ru.dartx.whattocook.di

import android.app.Application
import ru.dartx.core.mediator.AppWithFacade
import ru.dartx.core.mediator.ProvidersFacade

class App : Application(), AppWithFacade {
    override fun getFacade(): ProvidersFacade {
        return facadeComponent ?: FacadeComponent.init(this).also { facadeComponent = it }
    }

    companion object {
        private var facadeComponent: FacadeComponent? = null
    }

    override fun onCreate() {
        super.onCreate()
        getFacade()
    }
}