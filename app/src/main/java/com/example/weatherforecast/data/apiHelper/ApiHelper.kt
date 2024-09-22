package com.example.weatherforecast.data.apiHelper

import com.google.gson.JsonParseException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response

open class ApiHelper {

    protected suspend fun <T> serviceCall(
        apiCall: suspend () -> Response<T>
    ): Flow<Result<T>> = withContext(Dispatchers.IO) {
        try {
            val response = apiCall.invoke()
            return@withContext getResult(response)
        } catch (e: Exception) {
            return@withContext flow {
                emit(Result.Failure(e.message ?: e.localizedMessage, e, null, null))
            }
        } catch (e: JsonParseException) {
            return@withContext flow {
                emit(Result.Failure(e.message ?: e.localizedMessage, e, null, null))
            }
        } catch (e: HttpException) {
            return@withContext flow {
                emit(Result.Failure(e.message ?: e.localizedMessage, e, e.code(), null))
            }
        }
    }

    private fun <T> getResult(response: Response<T>): Flow<Result<T>> {
        return flow {
            if (response.isSuccessful && response.body() != null) {
                emit(Result.Success(response.body(), responseCode = response.code()))
            } else {
                emit(
                    Result.Failure(
                        response.message(), null, response.code(), response.errorBody()
                    )
                )
            }
        }.catch {
            emit(
                Result.Failure(
                    it.localizedMessage ?: it.toString(), it, null, null
                )
            )
        }
    }
}