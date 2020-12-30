package com.markel.passwordreminder.base.ui

import androidx.lifecycle.ViewModel
import com.markel.passwordreminder.database.entity.NoteEntity
import com.markel.passwordreminder.di.CoroutineProvider
import com.markel.passwordreminder.ui.page_fragment.note.NoteDisplay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import org.koin.core.component.KoinComponent

/**
 * Базовый класс для ViewModel'ов
 * Они инжектятся в [viewModelModule]
 *
 * Использует `kotlin coroutines`
 * в базовом классе есть [scope], [coroutineJob], а также [scopeProvider]
 *
 * IO-запросы (интернет, базы данных) нужно проводить через [UiCaller.makeRequest]
 *
 * Пользователи классов (фрагменты) должны
 * подписываться на [errorLiveData] - он использует [EventWrapper]
 * а также на [statusLiveData] - для отслеживания событий загрузки данных
 */
abstract class BaseViewModel(
    protected val scopeProvider: CoroutineProvider = CoroutineProvider(),
    protected var coroutineJob: Job = SupervisorJob(),
    protected val scope: CoroutineScope = CoroutineScope(coroutineJob + scopeProvider.IO),
    protected val uiCaller: UiCaller = UiCallerImpl(scope, scopeProvider)
) : ViewModel(), KoinComponent, UiCaller by uiCaller {

    open fun stop() {
        coroutineJob.cancelChildren()
    }

    override fun onCleared() {
        super.onCleared()
        coroutineJob.cancel()
    }

    fun mapNotes(
        it: NoteEntity
    ) = NoteDisplay(
        it
    )
}