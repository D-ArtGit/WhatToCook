package ru.dartx.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.appendEncodedPathSegments
import ru.dartx.network.dto.Meal
import ru.dartx.network.dto.Meals
import ru.dartx.network.dto.ResultResponse
import javax.inject.Inject

class RecipesApi @Inject constructor(
    private val httpClient: HttpClient,
) {
    suspend fun searchRecipes(conditions: String): ResultResponse<Meals> {
        return try {
            ResultResponse.Success(searchRecipesFromNet(conditions))
        } catch (e: Exception) {
            ResultResponse.Error(e, e.message)
        }

    }

    suspend fun getRecipeById(extId: Int): ResultResponse<Meal> {
        return try {
            ResultResponse.Success(getRecipeFromNet(extId).meals?.firstOrNull())
        } catch (e: Exception) {
            ResultResponse.Error(e, e.message)
        }
    }

    private suspend fun searchRecipesFromNet(conditions: String): Meals = httpClient.get {
        url {
            appendEncodedPathSegments(
                "api",
                "json",
                "v1",
                "1",
                "search.php"
            )
            parameter(
                if (conditions.length == 1) "f" else "s",
                conditions
            )
        }
    }.body()

    private suspend fun getRecipeFromNet(extId: Int): Meals = httpClient.get {
        url {
            appendEncodedPathSegments(
                "api",
                "json",
                "v1",
                "1",
                "lookup.php"
            )
            parameter(
                "i",
                extId
            )
        }
    }.body()
}