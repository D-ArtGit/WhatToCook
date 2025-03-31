package ru.dartx.core_impl.network

import dagger.Module
import dagger.Provides
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.observer.ResponseObserver
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
class NetworkModule {

    @Provides
    @Singleton
    fun provideKtorClient(): HttpClient {
        return HttpClient(CIO) {
            engine { requestTimeout = 10000 }

            install(HttpTimeout) {
                connectTimeoutMillis = 10000
                socketTimeoutMillis = 10000
            }

            install(ContentNegotiation) {
                json(provideJson())
            }

            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        println("DBG: Logger Ktor -> $message")
                    }
                }
                level = LogLevel.ALL
            }

            install(ResponseObserver) {
                onResponse { response ->
                    println("DBG: Http status: ${response.status.value}")
                }
            }

            install(DefaultRequest) {
                host = BASE_URL
                url {
                    protocol = URLProtocol.HTTPS
                }
                header(HttpHeaders.ContentType, ContentType.Application.Json)
            }
        }
    }

    @Provides
    @Singleton
    fun provideJson(): Json {
        return Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
            explicitNulls = false
        }
    }

    companion object {
        private const val BASE_URL = "www.themealdb.com"
    }
}