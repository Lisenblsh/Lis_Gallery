package com.lis.domain.models

import android.net.Uri
import java.time.Duration

data class FolderItemsModel(
    val id: Long,
    val path: String,
    var dateModified: Long,
    var isSelected: Boolean = false,
    var isFavourite: Boolean = false,
    val isVideo: Boolean = false,
    val duration: Long = 0
)