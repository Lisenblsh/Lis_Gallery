package com.lis.lisgalery.presentation.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.lis.data.AlbumRepositoryImpl
import com.lis.domain.models.FolderItemsModel
import com.lis.domain.pagingSources.ItemsInAlbumPagingSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

class ItemsInAlbumViewModel(private val repository: AlbumRepositoryImpl) : ViewModel() {
    var pagingItemsList: Flow<PagingData<FolderItemsModel>> = emptyFlow()
    val nameFolder = MutableLiveData<String>()
    val position = MutableLiveData<Int>(0)

    fun getItemsList(folderId: Long?): Flow<PagingData<FolderItemsModel>> {
        pagingItemsList = Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false,
                initialLoadSize = INITIAL_SIZE
            )
        ) {
            val pagingSource = ItemsInAlbumPagingSource(repository, folderId)
            viewModelScope.launch { nameFolder.value = pagingSource.getNameFolder() }
            pagingSource
        }.flow.cachedIn(viewModelScope)
        return pagingItemsList
    }

    companion object {
        const val PAGE_SIZE = 50
        const val INITIAL_SIZE = PAGE_SIZE
    }
}