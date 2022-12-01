package com.lis.domain.pagingSources

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.lis.domain.AlbumRepository
import com.lis.domain.models.FolderItemsModel

class ItemsInAlbumPagingSource(private val repository: AlbumRepository, private val folderId: Long?) :
    PagingSource<Int, FolderItemsModel>() {
    override fun getRefreshKey(state: PagingState<Int, FolderItemsModel>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchorPosition) ?: return null
        return page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FolderItemsModel> {
        val page = params.key ?: 1
        val pageSize = params.loadSize

        Log.e("pageSize","$pageSize")
        val list = getItemsInAlbum(
            count = pageSize,
            offset = pageSize * (page - 1)
        )
        val prevKey = if (page == 1) null else page - 1
        val nextKey = if (list.size < pageSize) null else page + 1

        return LoadResult.Page(list, prevKey,nextKey)
    }

    private fun getItemsInAlbum(count: Int, offset: Int): List<FolderItemsModel> {
        return repository.getItemsInAlbum(folderId,count, offset)
    }
}