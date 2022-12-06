package com.lis.data

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import com.lis.domain.models.FolderItemsModel
import com.lis.domain.models.FolderModel

class Gallery(private val context: Context) {
    private val contentResolver by lazy {
        context.contentResolver
    }

    private val albumList: List<FolderModel>

    init {
        albumList = findAlbums()
    }

    fun getAlbums(count: Int, offset: Int): List<FolderModel> {
        if (albumList.size < offset) {
            return emptyList()
        }
        val _count = if (albumList.size < offset + count) {
            albumList.size - offset
        } else {
            count
        }
        return albumList.subList(offset, offset + _count)
    }

    private fun findAlbums(): List<FolderModel> {
        val findAlbums = HashMap<Long, FolderModel>()
        getImageAlbum(findAlbums)
        getVideoAlbum(findAlbums)
        return findAlbums.values.toList().sortedByDescending { it.dateModified }
    }

    private fun getVideoAlbum(findAlbums: HashMap<Long, FolderModel>) {

        val videoQuery = getQuery(
            videoCollection(),
            videoProjection,
            bucketId = null,
            folderId = null,
            sortOrder = "${MediaStore.Video.Media.DATE_MODIFIED} DESC"
        )

        videoQuery?.use { cursor ->
            val bucketIdColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_ID)
            val bucketNameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)
            val bucketDataModified =
                cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_MODIFIED)
            val pathColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)

            getAlbumsFromCursor(
                cursor,
                bucketIdColumn,
                bucketDataModified,
                bucketNameColumn,
                pathColumn,
                findAlbums
            )
        }
    }

    private fun getImageAlbum(findAlbums: HashMap<Long, FolderModel>) {
        val imageQuery = getQuery(
            imageCollection(),
            imageProjection,
            bucketId = null,
            folderId = null,
            sortOrder = "${MediaStore.Images.Media.DATE_MODIFIED} DESC"
        )


        imageQuery?.use { cursor ->
            val bucketIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)
            val bucketNameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            val bucketDataModified =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED)
            val pathColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)


            getAlbumsFromCursor(
                cursor,
                bucketIdColumn,
                bucketDataModified,
                bucketNameColumn,
                pathColumn,
                findAlbums
            )
        }
    }

    private fun getAlbumsFromCursor(
        cursor: Cursor,
        bucketIdColumn: Int,
        bucketDataModified: Int,
        bucketNameColumn: Int,
        pathColumn: Int,
        findAlbums: HashMap<Long, FolderModel>
    ) {
        while (cursor.moveToNext()) {
            val bucketId = cursor.getLong(bucketIdColumn)
            val dateModified = cursor.getLong(bucketDataModified)
            val album = findAlbums[bucketId] ?: let {
                val bucketName = cursor.getString(bucketNameColumn)

                val album = FolderModel(
                    id = bucketId,
                    name = bucketName ?: context.resources.getString(R.string.root_folder),
                    path = cursor.getString(pathColumn),
                    dateModified = dateModified,
                    count = 0
                )

                findAlbums[bucketId] = album

                album
            }
            if (dateModified >= findAlbums[bucketId]?.dateModified!!) {
                album.dateModified = dateModified
                album.path = cursor.getString(pathColumn)
            }
            album.count++
        }
    }


    private var itemsList: List<FolderItemsModel> = emptyList()

    private var folderId: Long? = null

    var nameFolder: String = ""
        private set

    fun getItemsFromAlbum(folderId: Long? = null, count: Int, offset: Int): List<FolderItemsModel> {
        itemsList = selectItemFromFolder(folderId)
        this.folderId = folderId
        if (itemsList.size < offset) {
            return emptyList()
        }
        val _count = if (itemsList.size < offset + count) {
            itemsList.size - offset
        } else {
            count
        }
        return itemsList.subList(offset, offset + _count)
    }

    private fun selectItemFromFolder(folderId: Long?): List<FolderItemsModel> {
        val items = mutableListOf<FolderItemsModel>()
        val time = System.currentTimeMillis()
        items.addAll(getImages(folderId))
        items.addAll(getVideos(folderId))
        Log.e("timer", "${System.currentTimeMillis() - time}")
        return items.sortedByDescending { it.dateModified }.toList()
    }

    private fun getImages(folderId: Long?): List<FolderItemsModel> {
        val imageQuery = getQuery(
            collection = imageCollection(),
            projection = imageProjection,
            bucketId = MediaStore.Images.Media.BUCKET_ID,
            folderId = folderId,
            sortOrder = "${MediaStore.Images.Media.DATE_MODIFIED} DESC"
        )

        imageQuery?.use { cursor ->
            val imageIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val imageDateModified =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED)
            val imagePathColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            val bucketNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            cursor.moveToFirst()
            nameFolder = cursor.getString(bucketNameColumn)?: context.resources.getString(R.string.root_folder)
            cursor.moveToPrevious()

            return getListFromCursor(
                cursor,
                imageIdColumn,
                imageDateModified,
                imagePathColumn
            )
        }
        return emptyList()
    }

    private fun getVideos(folderId: Long?): List<FolderItemsModel> {
        val videoQuery = getQuery(
            collection = videoCollection(),
            projection = videoProjection,
            bucketId = MediaStore.Video.Media.BUCKET_ID,
            folderId = folderId,
            sortOrder = "${MediaStore.Video.Media.DATE_MODIFIED} DESC"
        )

        videoQuery?.use { cursor ->
            val videoIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val videoDateModifiedColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_MODIFIED)
            val videoDurationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
            val videoPathColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)

            return getListFromCursor(
                cursor,
                videoIdColumn,
                videoDateModifiedColumn,
                videoPathColumn,
                videoDurationColumn,
                isVideo = true
            )
        }
        return emptyList()
    }

    private fun getListFromCursor(
        cursor: Cursor,
        idColumn: Int,
        dateModifiedColumn: Int,
        pathColumn: Int,
        durationColumn: Int? = null,
        isVideo: Boolean = false
    ): List<FolderItemsModel> {
        val items = mutableListOf<FolderItemsModel>()
        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)
            val dateModified = cursor.getLong(dateModifiedColumn)
            val path = cursor.getString(pathColumn)

            val duration = durationColumn?.let { cursor.getLong(it) } ?: 0

            items.add(
                FolderItemsModel(
                    id = id,
                    path = path,
                    dateModified = dateModified,
                    isVideo = isVideo,
                    duration = if (isVideo) duration else 0
                )
            )
        }
        return items
    }

    private fun getQuery(
        collection: Uri,
        projection: Array<String>,
        bucketId: String? = null,
        folderId: Long? = null,
        sortOrder: String
    ): Cursor? {
        val (selection, selectionArgs) = if (bucketId != null) {
            getSelection(
                folderId,
                bucketId
            )
        } else {
            Pair(null, null)
        }

        return contentResolver.query(
            collection, projection, selection, selectionArgs, sortOrder
        )
    }

    companion object {
        private fun getSelection(folderId: Long?, bucketId: String) =
            if (folderId == null || folderId == -1L) {
                Pair(null, null)
            } else {
                Pair("$bucketId == ?", arrayOf(folderId.toString()))
            }


        private val videoProjection: Array<String> = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DATE_MODIFIED,
            MediaStore.Video.Media.BUCKET_ID,
            MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.DATA
        )

        private fun videoCollection(): Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Video.Media.getContentUri(
                MediaStore.VOLUME_EXTERNAL
            )
        } else {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        }

        private val imageProjection: Array<String> = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATE_MODIFIED,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATA
        )

        private fun imageCollection(): Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(
                MediaStore.VOLUME_EXTERNAL
            )
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
    }

}