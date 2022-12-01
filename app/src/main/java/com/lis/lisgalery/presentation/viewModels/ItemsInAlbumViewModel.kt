package com.lis.lisgalery.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.lis.data.AlbumRepositoryImpl
import com.lis.domain.models.FolderItemsModel
import com.lis.domain.pagingSources.ItemsInAlbumPagingSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class ItemsInAlbumViewModel(private val repository: AlbumRepositoryImpl):ViewModel() {
    var pagingItemsList: Flow<PagingData<FolderItemsModel>> = emptyFlow()

    fun getItemsList(folderId: Long?): Flow<PagingData<FolderItemsModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = true,
                initialLoadSize = INITIAL_SIZE
            )
        ){
            ItemsInAlbumPagingSource(repository,folderId)
        }.flow
    }

    companion object {
        const val PAGE_SIZE = 50
        const val INITIAL_SIZE = PAGE_SIZE
    }
}