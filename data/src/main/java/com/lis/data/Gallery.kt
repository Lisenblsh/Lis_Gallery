package com.lis.data

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
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

    suspend fun getAlbums(count: Int, offset: Int): List<FolderModel> {
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
        val imageCollection = getImageCollection()


        val imageProjection = getImageProjection()

        val imageQuery = getQuery(
            imageCollection,
            imageProjection,
            bucketId = null,
            folderId = null,
            sortOrder = "${MediaStore.Images.Media.DATE_MODIFIED} DESC"
        )

        val findAlbums = HashMap<Long, FolderModel>()

        imageQuery?.use { cursor ->
            val imageNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val bucketIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)
            val bucketNameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            val bucketDataModified =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED)
            val pathColumn =
                cursor.getColumnIndexOrThrow(imageProjection[0]) //projection[0] - element with path


            while (cursor.moveToNext()) {
                val bucketId = cursor.getLong(bucketIdColumn)
                val dateModified = cursor.getLong(bucketDataModified)
                val name = cursor.getString(imageNameColumn)

                val path = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    "${Environment.getExternalStorageDirectory()}" +
                            "/" +
                            cursor.getString(pathColumn) +
                            name
                } else {
                    cursor.getString(pathColumn)
                }

                val album = findAlbums[bucketId] ?: let {
                    val bucketName = cursor.getString(bucketNameColumn)


                    val album = FolderModel(
                        id = bucketId,
                        name = bucketName ?: "",
                        path = path,
                        dateModified = dateModified,
                        count = 0
                    )

                    findAlbums[bucketId] = album

                    album
                }

                album.count++
            }
        }

        val videoCollection = getVideoCollection()

        val videoProjection = getVideoProjection()

        val videoQuery = getQuery(
            videoCollection,
            videoProjection,
            bucketId = null,
            folderId = null,
            sortOrder = "${MediaStore.Video.Media.DATE_MODIFIED} DESC"
        )

        videoQuery?.use { cursor ->
            val bucketIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_ID)
            val imageNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val bucketNameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)
            val bucketDataModified =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED)
            val pathColumn =
                cursor.getColumnIndexOrThrow(imageProjection[0]) //projection[0] - element with path

            while (cursor.moveToNext()) {
                val bucketId = cursor.getLong(bucketIdColumn)
                val dateModified = cursor.getLong(bucketDataModified)

                val name = cursor.getString(imageNameColumn)

                val path = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    "${Environment.getExternalStorageDirectory()}" +
                            "/" +
                            cursor.getString(pathColumn) +
                            name
                } else {
                    cursor.getString(pathColumn)
                }
                val album = findAlbums[bucketId] ?: let {
                    val bucketName = cursor.getString(bucketNameColumn)

                    val album = FolderModel(
                        id = bucketId,
                        name = bucketName ?: "",
                        path = path,
                        dateModified = dateModified,
                        count = 0
                    )

                    findAlbums[bucketId] = album

                    album
                }
                if (dateModified >= findAlbums[bucketId]?.dateModified!!) {
                    album.dateModified = dateModified
                    album.path = path
                }
                album.count++
            }
        }
        return findAlbums.values.toList().sortedByDescending { it.dateModified }
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

    private fun getVideoProjection(): Array<String> {
        val pathUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Video.Media.RELATIVE_PATH
        } else {
            MediaStore.Video.Media.DATA
        }
        return arrayOf(
            pathUri,
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DATE_MODIFIED,
            MediaStore.Video.Media.BUCKET_ID,
            MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DURATION
        )
    }

    private fun getVideoCollection(): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Video.Media.getContentUri(
                MediaStore.VOLUME_EXTERNAL
            )
        } else {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        }
    }

    private fun getImageProjection(): Array<String> {
        val pathUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.RELATIVE_PATH
        } else {
            MediaStore.Images.Media.DATA
        }
        return arrayOf(
            pathUri,
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATE_MODIFIED,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.DISPLAY_NAME
        )
    }

    private fun getImageCollection(): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(
                MediaStore.VOLUME_EXTERNAL
            )
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
    }


    private var itemsList: List<FolderItemsModel> = emptyList()

    private var folderId: Long? = null

    fun getItemsFromAlbum(folderId: Long? = null, count: Int, offset: Int): List<FolderItemsModel> {
        Log.e("folderId", "$folderId")
        if (itemsList.isEmpty() || folderId != this.folderId) {
            itemsList = selectItemFromFolder(folderId)
            this.folderId = folderId
            Log.e("itemsList", "${itemsList.count()}")
        }
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
        items.addAll(getImages(folderId))
        items.addAll(getVideos(folderId))
        return items.sortedByDescending { it.dateModified }.toList()
    }

    private fun getVideos(folderId: Long?): List<FolderItemsModel> {
        val videoCollection = getVideoCollection()

        val videoProjection = getVideoProjection()

        val videoQuery = getQuery(
            videoCollection,
            videoProjection,
            bucketId = MediaStore.Video.Media.BUCKET_ID,
            folderId = folderId,
            sortOrder = "${MediaStore.Video.Media.DATE_MODIFIED} DESC"
        )

        videoQuery?.use { cursor ->
            val videoIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val videoNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val videoDateModifiedColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_MODIFIED)
            val videoDurationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
            val videoPathColumn = cursor.getColumnIndexOrThrow(videoProjection[0])

            return getListFromCursor(
                cursor,
                videoIdColumn,
                videoDateModifiedColumn,
                videoNameColumn,
                videoPathColumn,
                videoDurationColumn,
                isVideo = true
            )
        }
        return emptyList()
    }

    private fun getImages(folderId: Long?): List<FolderItemsModel> {
        val imageCollection = getImageCollection()

        val imageProjection = getImageProjection()

        val imageQuery = getQuery(
            imageCollection,
            imageProjection,
            bucketId = MediaStore.Images.Media.BUCKET_ID,
            folderId = folderId,
            sortOrder = "${MediaStore.Images.Media.DATE_MODIFIED} DESC"
        )

        imageQuery?.use { cursor ->
            val imageIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val imageNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val imageDateModified =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED)
            val imagePathColumn =
                cursor.getColumnIndexOrThrow(imageProjection[0]) //projection[0] - element with path

            return getListFromCursor(
                cursor,
                imageIdColumn,
                imageDateModified,
                imageNameColumn,
                imagePathColumn
            )
        }
        return emptyList()
    }

    private fun getListFromCursor(
        cursor: Cursor,
        idColumn: Int,
        dateModifiedColumn: Int,
        nameColumn: Int,
        pathColumn: Int,
        durationColumn: Int? = null,
        isVideo: Boolean = false
    ): List<FolderItemsModel> {
        val items = mutableListOf<FolderItemsModel>()
        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)
            val dateModified = cursor.getLong(dateModifiedColumn)
            val uri = ContentUris.withAppendedId(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id
            )
            val name = cursor.getString(nameColumn)
            val path = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                "${Environment.getExternalStorageDirectory()}" +
                        "/" +
                        cursor.getString(pathColumn) +
                        name
            } else {
                cursor.getString(pathColumn)
            }

            val duration = durationColumn?.let { cursor.getLong(it) } ?: 0

            items.add(
                FolderItemsModel(
                    id = id,
                    name = name,
                    path = path,
                    uri = uri,
                    dateModified = dateModified,
                    isVideo = isVideo,
                    duration = if (isVideo) duration else 0
                )
            )
        }
        return items
    }

    private fun getSelection(folderId: Long?, bucketId: String) =
        if (folderId == null || folderId == -1L) {
            Pair(null, null)
        } else {
            Pair("$bucketId == ?", arrayOf(folderId.toString()))
        }

}