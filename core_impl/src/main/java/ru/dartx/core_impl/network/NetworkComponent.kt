package ru.dartx.core_impl.network

import dagger.Component
import ru.dartx.core.network.NetworkClientProvider
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class])
interface NetworkComponent : NetworkClientProvider