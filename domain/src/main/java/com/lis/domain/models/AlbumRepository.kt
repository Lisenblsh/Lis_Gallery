package com.lis.domain.models

interface AlbumRepository {
    fun getAlbums(count: Int, offset: Int): List<FolderModel>
}