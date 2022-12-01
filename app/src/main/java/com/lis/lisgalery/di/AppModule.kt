package com.lis.lisgalery.di

import com.lis.lisgalery.presentation.viewModels.AlbumViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel<AlbumViewModel>{
        AlbumViewModel(repository = get())
    }
}