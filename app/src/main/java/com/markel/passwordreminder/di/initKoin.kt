package com.markel.passwordreminder.di

import android.content.Context
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.logger.Logger
import org.koin.core.logger.MESSAGE
import org.koin.core.qualifier.named
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

fun initKoin(context: Context) {
    startKoin {
        logger(KoinLogger())
        androidLogger(Level.NONE)
        androidContext(context)
        modules(
            listOf(
                repositoryModule,
                viewModelModule,
                roomModule,
                utilModule
            )
        )
    }
}

class KoinLogger : Logger() {
    override fun log(level: Level, msg: MESSAGE) = Timber.v(msg)
}

/**
 * Класс предоставляет потоки для корутин - [Dispatchers.Main] и [Dispatchers.IO]
 * в тестах мокается на [Dispatchers.Unconfined], чтобы все проходило там же, в тестовом потоке
 *
 * Used in [BaseViewModel] to make coroutine scope
 * should be mocked in tests
 */
class CoroutineProvider : KoinComponent {
    val Main: CoroutineContext by inject(named("main"))
    val IO: CoroutineContext by inject(named("io"))
}