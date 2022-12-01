package com.lis.domain.models

import android.net.Uri

data class FolderModel(
    val id: Long,
    val name: String,
    var count: Int = 0,
    var uri: Uri?,
    var dateModified: Long,
    var isSelected: Boolean = false
)