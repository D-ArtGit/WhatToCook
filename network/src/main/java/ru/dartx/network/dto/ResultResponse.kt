package ru.dartx.network.dto

sealed class ResultResponse<out T> {
    data class Success<T>(val data: T?): ResultResponse<T>()
    data class Error(val throwable: Throwable?, val message: String?) : ResultResponse<Nothing>()
}