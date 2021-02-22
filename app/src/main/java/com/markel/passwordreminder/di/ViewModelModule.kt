package com.markel.passwordreminder.di

import com.markel.passwordreminder.ui.dialog.FolderActionsViewModel
import com.markel.passwordreminder.ui.folders.edit_folder.EditFolderViewModel
import com.markel.passwordreminder.ui.folders.folder_list.FolderListViewModel
import com.markel.passwordreminder.ui.folders.include_notes.IncludeNotesViewModel
import com.markel.passwordreminder.ui.folders.new_folder.NewFolderViewModel
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
    viewModel { FolderListViewModel(get()) }
    viewModel { NewFolderViewModel(get(), get()) }
    viewModel { IncludeNotesViewModel(get()) }
    viewModel { (folderId: Int) -> FolderActionsViewModel(get(), folderId) }
    viewModel { (folderId: Int) -> EditFolderViewModel(get(), get(), folderId) }
}