package ru.dartx.core.network

import io.ktor.client.HttpClient

interface NetworkClientProvider {
    fun provideNetworkClient(): HttpClient
}