package com.lis.lisgalery.presentation.viewModels

import android.graphics.pdf.PdfDocument.Page
import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.lis.data.AlbumRepositoryImpl
import com.lis.domain.models.AlbumPagingSource
import com.lis.domain.models.AlbumRepository
import com.lis.domain.models.FolderModel
import kotlinx.coroutines.flow.Flow

class AlbumViewModel(private val repository: AlbumRepositoryImpl): ViewModel() {
    val pagingAlbumList: Flow<PagingData<FolderModel>>

    init {
        pagingAlbumList = getAlbumList()
    }

    private fun getAlbumList(): Flow<PagingData<FolderModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = true,
                initialLoadSize = INITIAL_SIZE
            )
        ){
            AlbumPagingSource(repository)
        }.flow

    }

    companion object {
        const val PAGE_SIZE = 10
        const val INITIAL_SIZE = 20
    }
}