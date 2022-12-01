package com.lis.data

import com.lis.domain.AlbumRepository
import com.lis.domain.models.FolderItemsModel
import com.lis.domain.models.FolderModel

class AlbumRepositoryImpl(private val gallery: Gallery) : AlbumRepository {
    override fun getAlbums(count: Int, offset: Int): List<FolderModel> =
        gallery.getAlbums(count, offset)

    override fun getItemsInAlbum(folderId: Long?, count: Int, offset: Int): List<FolderItemsModel> {
        return gallery.getItemsFromAlbum(folderId, count, offset)
    }
}