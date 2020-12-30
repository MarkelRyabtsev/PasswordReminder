package com.markel.passwordreminder.di

import com.markel.passwordreminder.data.repository.GroupRepository
import com.markel.passwordreminder.data.repository.NoteRepository
import org.koin.dsl.module
import org.koin.experimental.builder.single

val repositoryModule = module {
    single<NoteRepository>()
    single<GroupRepository>()
}