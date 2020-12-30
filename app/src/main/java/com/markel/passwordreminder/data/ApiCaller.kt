/**
 * (C) Copyright 2019 Ildar Ishalin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.markel.passwordreminder.data

import android.content.Context
import android.provider.Settings.Global.getString
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.markel.passwordreminder.R
import kotlinx.coroutines.coroutineScope
import retrofit2.HttpException
import timber.log.Timber
import java.net.ConnectException
import java.net.HttpURLConnection.HTTP_INTERNAL_ERROR
import java.net.HttpURLConnection.HTTP_NOT_FOUND
import java.net.SocketTimeoutException

interface CoroutineCaller {
    suspend fun <T> apiCall(result: suspend () -> T): RequestResult<T>
}

interface MultiCoroutineCaller {
    suspend fun <T> multiCall(vararg requests: T): List<RequestResult<T>>

    suspend fun <T1, T2, R> zip(
        request1: suspend () -> T1,
        request2: suspend () -> T2,
        zipper: (RequestResult<T1>, RequestResult<T2>) -> R
    ): R

    suspend fun <T1, T2, T3, R> zip(
        request1: suspend () -> T1,
        request2: suspend () -> T2,
        request3: suspend () -> T3,
        zipper: (RequestResult<T1>, RequestResult<T2>, RequestResult<T3>) -> R
    ): R

    suspend fun <T, R> zipArray(
        vararg requests: suspend () -> T,
        zipper: (List<RequestResult<T>>) -> R
    ): R
}

interface ApiCallerInterface : CoroutineCaller, MultiCoroutineCaller

object ApiCaller : ApiCallerInterface {

    private const val HTTP_CODE_ACCOUNT_BLOCKED = 419

    /**
     * Обработчик для однородных запросов на `kotlin coroutines`
     * [requests] должны возвращать один тип данных
     * запускает все [requests] и записывает их в массив [RequestResult]
     * обрабатывает ошибки сервера при помощи [apiCall]
     * обрабатывает ошибки соединения при помощи [apiCall]
     */
    override suspend fun <T> multiCall(vararg requests: T) = requests.map { apiCall { it } }

    /**
     * Обработчик для однородных запросов на `kotlin coroutines`
     * [requests] должны возвращать один тип данных
     * запускает все [requests], записывает их в массив [RequestResult]
     * и передает в обработчик [zipper]
     * обрабатывает ошибки сервера при помощи [apiCall]
     * обрабатывает ошибки соединения при помощи [apiCall]
     */
    override suspend fun <T, R> zipArray(
        vararg requests: suspend () -> T,
        zipper: (List<RequestResult<T>>) -> R
    ) = zipper(requests.map { apiCall(it) })

    /**
     * Обработчик для двух разнородных запросов на `kotlin coroutines`
     * запускает [request1], [request2] и передает в обработчик [zipper]
     * обрабатывает ошибки сервера при помощи [apiCall]
     * обрабатывает ошибки соединения при помощи [apiCall]
     */
    override suspend fun <T1, T2, R> zip(
        request1: suspend () -> T1,
        request2: suspend () -> T2,
        zipper: (RequestResult<T1>, RequestResult<T2>) -> R
    ): R = zipper(apiCall(request1), apiCall(request2))

    /**
     * Обработчик для трех разнородных запросов на `kotlin coroutines`
     * запускает [request1], [request2], [request3] и передает в обработчик [zipper]
     * обрабатывает ошибки сервера при помощи [apiCall]
     * обрабатывает ошибки соединения при помощи [apiCall]
     */
    override suspend fun <T1, T2, T3, R> zip(
        request1: suspend () -> T1,
        request2: suspend () -> T2,
        request3: suspend () -> T3,
        zipper: (RequestResult<T1>, RequestResult<T2>, RequestResult<T3>) -> R
    ): R = zipper(apiCall(request1), apiCall(request2), apiCall(request3))

    /**
     * Обработчик запросов на `kotlin coroutines`
     * ждет выполнения запроса [result]
     * обрабатывает ошибки сервера и соединения
     * возвращает [RequestResult.Success] или [RequestResult.Error]
     * Применяется для suspend-функций Retrofit-api
     */
    override suspend fun <T> apiCall(result: suspend () -> T): RequestResult<T> = try {
        coroutineScope { RequestResult.Success(result.invoke()) }
    } catch (e: Exception) {
        //handleException(e)
        RequestResult.Error(e.toString())
    }

    /*private fun <T> handleException(e: Exception): RequestResult<T> = when (e) {
        is JsonSyntaxException -> {
            RequestResult.Error(getString(R.string.request_json_error))
        }
        is ConnectException -> {
            RequestResult.Error(getString(R.string.request_connection_error))
        }
        is SocketTimeoutException -> {
            RequestResult.Error(getString(R.string.request_timeout))
        }
        is HttpException -> when (e.code()) {
            HTTP_NOT_FOUND -> {
                RequestResult.Error(getString(R.string.request_http_error_404), e.code())
            }
            HTTP_INTERNAL_ERROR -> {
                val errorBody = e.response()?.errorBody()?.string() ?: ""
                if (ServerError.checkCondition(errorBody) { error == "user blocked" }) {
                    RequestResult.Error(
                        getString(R.string.request_http_error_user_blocked),
                        HTTP_CODE_ACCOUNT_BLOCKED
                    )
                } else
                    RequestResult.Error(getString(R.string.request_http_error_500), e.code())
            }
            else -> {
                RequestResult.Error(ServerError.print(
                    e.response()?.errorBody()?.string(),
                    FormatResourceString(R.string.request_http_error_format, e.code())
                ), e.code())
            }
        }
        else -> {
            RequestResult.Error(getString(R.string.request_error, e::class.java.simpleName, e.localizedMessage))
        }
    }*/
}

/**
 * Презентация ответов сервера для `Presentation layer`
 * должно возвращаться репозиториями, использующими [ApiCaller]
 */
sealed class RequestResult<out T : Any?> {
    data class Success<out T : Any?>(val result: T) : RequestResult<T>()
    data class Error(val error: String, val code: Int = 0) : RequestResult<Nothing>()
}