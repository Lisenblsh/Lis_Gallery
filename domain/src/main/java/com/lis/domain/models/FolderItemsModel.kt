package com.lis.domain.models

import android.net.Uri
import java.time.Duration

data class FolderItemsModel(
    val id: Long,
    val name: String,
    val path: String,
    val uri: Uri?,
    var dateModified: Long,
    var isSelected: Boolean = false,
    val isVideo: Boolean = false,
    val duration: Long = 0
)