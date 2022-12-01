package com.lis.data

import com.lis.domain.models.AlbumRepository
import com.lis.domain.models.FolderModel

class AlbumRepositoryImpl(private val gallery: Gallery) : AlbumRepository {
    override fun getAlbums(count: Int, offset: Int): List<FolderModel> =
        gallery.getAlbums(count, offset)
}