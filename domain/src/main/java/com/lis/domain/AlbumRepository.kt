package com.lis.domain

import com.lis.domain.models.FolderItemsModel
import com.lis.domain.models.FolderModel

interface AlbumRepository {
    suspend fun getAlbums(count: Int, offset: Int): List<FolderModel>

    suspend fun getItemsInAlbum(folderId: Long? = null, count: Int, offset: Int): List<FolderItemsModel>

    suspend fun getNameFolderById(folderId: Long? = null):String
}