package com.lis.data

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.os.Build
import android.provider.MediaStore
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
}