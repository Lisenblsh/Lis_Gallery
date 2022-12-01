package com.lis.lisgalery.di

import com.lis.data.AlbumRepositoryImpl
import com.lis.data.Gallery
import org.koin.dsl.module

val dataModule = module {

    single<Gallery> {
        Gallery(context = get())
    }

    single<AlbumRepositoryImpl> {
        AlbumRepositoryImpl(gallery = get())
    }
}