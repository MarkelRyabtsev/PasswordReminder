package com.markel.passwordreminder.di

import com.markel.passwordreminder.ui.main.MainViewModel
import com.markel.passwordreminder.ui.page_fragment.PageViewModel
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.experimental.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import kotlin.coroutines.CoroutineContext

val viewModelModule = module {
    factory<CoroutineContext>(named("io")) { Dispatchers.IO }
    factory<CoroutineContext>(named("main")) { Dispatchers.Main }
    viewModel<MainViewModel>()
    viewModel<PageViewModel>()
}