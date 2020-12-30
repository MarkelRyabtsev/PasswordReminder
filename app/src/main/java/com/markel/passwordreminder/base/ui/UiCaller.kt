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
package com.markel.passwordreminder.base.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.markel.passwordreminder.data.RequestResult
import com.markel.passwordreminder.di.CoroutineProvider
import com.markel.passwordreminder.util.EventWrapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

interface UiProvider {
    val statusLiveData: LiveData<Status>
    val errorLiveData: LiveData<EventWrapper<String>>
}

interface UiCaller : UiProvider {
    override val statusLiveData: MutableLiveData<Status>

    fun <T> makeRequest(
        call: suspend CoroutineScope.() -> T,
        statusLD: MutableLiveData<Status>? = statusLiveData,
        resultBlock: (suspend (T) -> Unit)? = null
    )

    fun <T> unwrap(
        result: RequestResult<T>,
        errorBlock: ((String) -> Unit)? = { setError(it) },
        successBlock: (T) -> Unit
    ): Unit?

    fun set(status: Status, statusLD: MutableLiveData<Status>? = statusLiveData)

    fun setError(error: String)
}

class UiCallerImpl(
    private val scope: CoroutineScope,
    private val scopeProvider: CoroutineProvider,
    _statusLiveData: MutableLiveData<Status> = MutableLiveData(),
    _errorLiveData: MutableLiveData<EventWrapper<String>> = MutableLiveData()
) : UiCaller {
    override val statusLiveData: MutableLiveData<Status> = _statusLiveData
    override val errorLiveData: MutableLiveData<EventWrapper<String>> = _errorLiveData

    /**
     * Presentation-layer-обработчик для запросов через `kotlin coroutines`:
     * запускает [Job] в [scope],
     * вызывает прогресс на [statusLiveData]
     *
     * [call] - `suspend`-функция запроса из репозитория
     * [statusLD] - можно подставлять разные liveData для разных прогрессов (или null)
     * [resultBlock] - функция, которую нужно выполнить по завершении запроса в UI-потоке
     */
    override fun <T> makeRequest(
        call: suspend CoroutineScope.() -> T,
        statusLD: MutableLiveData<Status>?,
        resultBlock: (suspend (T) -> Unit)?
    ) {
        scope.launch(scopeProvider.Main) {
            set(Status.SHOW_LOADING, statusLD)
            try {
                val result = withContext(scopeProvider.IO, call)
                resultBlock?.invoke(result)
            } catch (e: Exception) {
                setError(e.message.orEmpty())
            }
            set(Status.HIDE_LOADING, statusLD)
        }
    }

    /**
     * Чтобы не терять прогрессбар на нескольких запросах
     */
    private var requestCounter = 0

    /**
     * Выставляем статус
     * по дефолту выставлен [statusLiveData]
     * можно подставить свою лайвдату или [null]
     */
    override fun set(status: Status, statusLD: MutableLiveData<Status>?) {
        statusLD ?: return
        if (statusLD === statusLiveData) {
            when (status) {
                Status.SHOW_LOADING -> {
                    requestCounter++
                }
                Status.HIDE_LOADING -> {
                    requestCounter--
                    if (requestCounter > 0) return
                    requestCounter = 0
                }
            }
        }
        scope.launch(scopeProvider.Main) {
            statusLD.value = status
        }
    }

    override fun setError(error: String) {
        scope.launch(scopeProvider.Main) {
            errorLiveData.value = EventWrapper(error)
        }
    }

    /**
     * Обработчик для ответов [RequestResult] репозитория.
     * [errorBlock] - функция обработки ошибок, можно передать `null`, чтобы никак не обрабатывать.
     * [successBlock] - обработка непустого результата
     */
    override fun <T> unwrap(
        result: RequestResult<T>,
        errorBlock: ((String) -> Unit)?,
        successBlock: (T) -> Unit
    ) = when (result) {
        is RequestResult.Success -> successBlock(result.result)
        is RequestResult.Error -> errorBlock?.invoke(result.error)
    }
}

enum class Status {
    SHOW_LOADING,
    HIDE_LOADING,
    ERROR,
    SUCCESS
}