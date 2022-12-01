package com.lis.data

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
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

    fun getAlbums(count: Int, offset: Int): List<FolderModel> {
        if(albumList.size < offset){
            return emptyList()
        }
        val _count = if (albumList.size < offset + count) {
            albumList.size - offset
        } else {
            count
        }
        return albumList.subList(offset, offset + _count)
    }

    @SuppressLint("Recycle")
    private fun findAlbums(): List<FolderModel> {
        TODO("Организовать это все лучше чем есть сейчас" +
                "И добавить path для иконки, что бы не грузить битмап")
        val collection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(
                    MediaStore.VOLUME_EXTERNAL
                )
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }

        val findAlbums = HashMap<Long, FolderModel>()

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATE_MODIFIED,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.DISPLAY_NAME
        )

        val sortOrder = "${MediaStore.Images.Media.DATE_MODIFIED} DESC"

        val query = contentResolver.query(
            collection,
            projection,
            null,
            null,
            sortOrder
        )

        query?.use { cursor ->
            val imageIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val bucketIdColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)
            val bucketNameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            val bucketDataModified =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED)


            while (cursor.moveToNext()) {
                val bucketId = cursor.getLong(bucketIdColumn)
                val dateModified = cursor.getLong(bucketDataModified)

                val album = findAlbums[bucketId] ?: let {
                    val bucketName = cursor.getString(bucketNameColumn)
                    val imageId = cursor.getLong(imageIdColumn)
                    val uri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        imageId
                    )

                    val album = FolderModel(
                        id = bucketId,
                        name = bucketName ?: "",
                        uri = uri,
                        dateModified = dateModified,
                        count = 0
                    )

                    findAlbums[bucketId] = album

                    album
                }
                album.count++
            }
        }

        val collection2 =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Video.Media.getContentUri(
                    MediaStore.VOLUME_EXTERNAL
                )
            } else {
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            }

        val projection2 = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DATE_MODIFIED,
            MediaStore.Video.Media.BUCKET_ID,
            MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Video.Media.DISPLAY_NAME
        )

        val sortOrder2 = "${MediaStore.Video.Media.DATE_MODIFIED} DESC"

        val query2 = contentResolver.query(
            collection2,
            projection2,
            null,
            null,
            sortOrder2
        )

        query2?.use { cursor ->
            val videoIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val bucketIdColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_ID)
            val bucketNameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)
            val bucketDataModified =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED)

            while (cursor.moveToNext()) {
                val bucketId = cursor.getLong(bucketIdColumn)
                val dateModified = cursor.getLong(bucketDataModified)
                val videoId = cursor.getLong(videoIdColumn)
                val uri = ContentUris.withAppendedId(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    videoId
                )
                val album = findAlbums[bucketId] ?: let {
                    val bucketName = cursor.getString(bucketNameColumn)

                    val album = FolderModel(
                        id = bucketId,
                        name = bucketName ?: "",
                        uri = uri,
                        dateModified = dateModified,
                        count = 0
                    )

                    findAlbums[bucketId] = album

                    album
                }
                if (dateModified >= findAlbums[bucketId]?.dateModified!!) {
                    album.dateModified = dateModified
                    album.uri = uri
                }
                album.count++
            }
        }
        return findAlbums.values.toList().sortedByDescending { it.dateModified }
    }

    private var itemsList: List<FolderItemsModel> = emptyList()
    private var folderId: Long? = null

    fun getItemsFromAlbum(folderId: Long? = null, count: Int, offset: Int): List<FolderItemsModel> {
        Log.e("folderId","$folderId")
        if(itemsList.isEmpty() || folderId != this.folderId){
            itemsList = selectItemFromFolder(folderId)
            this.folderId = folderId
            Log.e("itemsList","${itemsList.count()}")
        }
        if(itemsList.size < offset){
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
        val (selection, selectionArgs) = checkIsNull(folderId)

        val items = mutableListOf<FolderItemsModel>()

        val collection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(
                    MediaStore.VOLUME_EXTERNAL
                )
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }
        val pathUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.RELATIVE_PATH
        } else {
            MediaStore.Images.Media.DATA
        }


        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATE_MODIFIED,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            pathUri
        )

        val sortOrder = "${MediaStore.Images.Media.DATE_MODIFIED} DESC"


        val query = contentResolver.query(
            collection,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )

        query?.use { cursor ->
            val imageIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val imageNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val imageDateModified =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED)
            val imagePathColumn = cursor.getColumnIndexOrThrow(pathUri)

            while (cursor.moveToNext()) {
                val imageId = cursor.getLong(imageIdColumn)
                val dateModified = cursor.getLong(imageDateModified)
                val uri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    imageId
                )
                val name = cursor.getString(imageNameColumn)
                val path = cursor.getString(imagePathColumn)

                items.add(
                    FolderItemsModel(
                        id = imageId,
                        name = name,
                        path = "${Environment.getExternalStorageDirectory()}/${path}${name}",
                        uri = uri,
                        dateModified = dateModified
                    )
                )
            }
        }

        val collection2 =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Video.Media.getContentUri(
                    MediaStore.VOLUME_EXTERNAL
                )
            } else {
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            }
        val pathUri2 = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Video.Media.RELATIVE_PATH
        } else {
            MediaStore.Video.Media.DATA
        }


        val projection2 = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DATE_MODIFIED,
            MediaStore.Video.Media.BUCKET_ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DURATION,
            pathUri2
        )

        val sortOrder2 = "${MediaStore.Video.Media.DATE_MODIFIED} DESC"

        val (selection2, selectionArgs2) = checkIsNull2(folderId)

        val query2 = contentResolver.query(
            collection2,
            projection2,
            selection2,
            selectionArgs2,
            sortOrder2
        )

        query2?.use { cursor ->
            val imageIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val imageNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val imageDateModified =
                cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_MODIFIED)
            val videoDurationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
            val imagePathColumn = cursor.getColumnIndexOrThrow(pathUri)

            while (cursor.moveToNext()) {
                val imageId = cursor.getLong(imageIdColumn)
                val dateModified = cursor.getLong(imageDateModified)
                val uri = ContentUris.withAppendedId(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    imageId
                )
                val name = cursor.getString(imageNameColumn)
                val path = cursor.getString(imagePathColumn)
                val duration = cursor.getLong(videoDurationColumn)

                items.add(
                    FolderItemsModel(
                        id = imageId,
                        name = name,
                        path = "${Environment.getExternalStorageDirectory()}/${path}${name}",
                        uri = uri,
                        dateModified = dateModified,
                        isVideo = true,
                        duration = duration
                    )
                )
            }
        }
        return items.sortedByDescending { it.dateModified }.toList()
    }

    private fun checkIsNull(folderId: Long?) = if (folderId == null || folderId == -1L) {
        Pair(null, null)
    } else {
        Pair("${MediaStore.Images.Media.BUCKET_ID} == ?", arrayOf(folderId.toString()))
    }
    private fun checkIsNull2(folderId: Long?) = if (folderId == null || folderId == -1L) {
        Pair(null, null)
    } else {
        Pair("${MediaStore.Video.Media.BUCKET_ID} == ?", arrayOf(folderId.toString()))
    }

}