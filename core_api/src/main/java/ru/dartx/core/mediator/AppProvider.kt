package ru.dartx.core.mediator

import android.content.Context

interface AppProvider {
    fun provideContext(): Context
}