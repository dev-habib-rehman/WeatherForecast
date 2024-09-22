package com.example.weatherforecast.data.apiHelper

import okhttp3.ResponseBody

sealed class Result<out T> {
    data class Empty<T>(var message: String?) : Result<T>()
    data class Loading<T>(var message: String? = "Loading") : Result<T>()
    data class Success<T>(var data: T?, var responseCode: Int) : Result<T>()
    data class Failure<T>(var message: String, var exception: Throwable? = null, var errorCode: Int?, var errorBody: ResponseBody? = null) : Result<T>()
}