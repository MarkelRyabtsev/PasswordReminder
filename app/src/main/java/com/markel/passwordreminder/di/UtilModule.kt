package com.markel.passwordreminder.di

import com.markel.passwordreminder.routers.MainRouter
import org.koin.dsl.module
import org.koin.experimental.builder.single

val utilModule = module {
    single<MainRouter>()
}