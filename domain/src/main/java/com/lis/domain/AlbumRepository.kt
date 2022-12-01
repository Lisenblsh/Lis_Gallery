package com.lis.domain

import com.lis.domain.models.FolderItemsModel
import com.lis.domain.models.FolderModel

interface AlbumRepository {
    fun getAlbums(count: Int, offset: Int): List<FolderModel>

    fun getItemsInAlbum(folderId: Long? = null, count: Int, offset: Int): List<FolderItemsModel>
}