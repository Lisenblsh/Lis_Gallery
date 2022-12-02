package com.lis.lisgalery.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.lis.data.AlbumRepositoryImpl
import com.lis.domain.pagingSources.AlbumPagingSource
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
        const val PAGE_SIZE = 20
        const val INITIAL_SIZE = PAGE_SIZE
    }
}