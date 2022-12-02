package com.lis.domain.pagingSources

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.lis.domain.AlbumRepository
import com.lis.domain.models.FolderModel

class AlbumPagingSource(private val repository: AlbumRepository) :
    PagingSource<Int, FolderModel>() {
    override fun getRefreshKey(state: PagingState<Int, FolderModel>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchorPosition) ?: return null
        return page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FolderModel> {
        val page = params.key ?: 1
        val pageSize = params.loadSize

        val list = getAlbums(
            count = pageSize,
            offset = pageSize * (page - 1)
        )

        val prevKey = if (page == 1) null else page - 1
        val nextKey = if (list.size < pageSize) null else page + 1

        return LoadResult.Page(list, prevKey,nextKey)
    }

    private suspend fun getAlbums(count: Int, offset: Int): List<FolderModel> {
        return repository.getAlbums(count, offset)
    }
}