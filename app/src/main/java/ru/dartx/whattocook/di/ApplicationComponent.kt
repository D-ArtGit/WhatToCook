package ru.dartx.whattocook.di

import android.app.Application
import android.content.Context
import dagger.BindsInstance
import dagger.Component
import ru.dartx.core.mediator.AppProvider

@ApplicationScope
@Component
interface ApplicationComponent : AppProvider {

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