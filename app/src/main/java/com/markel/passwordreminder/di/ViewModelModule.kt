package com.markel.passwordreminder.di

import com.markel.passwordreminder.ui.main.GroupViewModel
import com.markel.passwordreminder.ui.page_fragment.view_model.AdditionalPageViewModel
import com.markel.passwordreminder.ui.page_fragment.view_model.EditNoteViewModel
import com.markel.passwordreminder.ui.page_fragment.view_model.MainPageViewModel
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import kotlin.coroutines.CoroutineContext

val viewModelModule = module {
    factory<CoroutineContext>(named("io")) { Dispatchers.IO }
    factory<CoroutineContext>(named("main")) { Dispatchers.Main }
    viewModel { GroupViewModel(get()) }
    viewModel { MainPageViewModel(get()) }
    viewModel { (groupId: Int) -> AdditionalPageViewModel(get(), groupId) }
    viewModel { EditNoteViewModel(get()) }
}