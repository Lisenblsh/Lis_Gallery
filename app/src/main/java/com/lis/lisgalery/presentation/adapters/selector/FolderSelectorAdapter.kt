package com.lis.lisgalery.presentation.adapters.selector

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.lis.domain.models.FolderModel
import com.lis.lisgalery.databinding.AlbumCheckItemBinding
import com.lis.lisgalery.presentation.adapters.base.BaseSelectorAdapter


class FolderSelectorAdapter(folderList: List<FolderModel>, idLayout: Int) :
    BaseSelectorAdapter<FolderSelectorAdapter.FolderSelectorViewHOlder>(folderList, idLayout) {

    inner class FolderSelectorViewHOlder(private val binding: AlbumCheckItemBinding) :
        BaseSelectorViewHolder(binding.root) {
        override fun bind(item: Any?) {
            if (item != null) {
                showData(item)
            }
        }

        @SuppressLint("Range")
        override fun showData(item: Any) {
            if (item is FolderModel) {
                if (item.uri != null) {
                    try {
                        val id = item.uri!!.lastPathSegment!!.split(":")[0].toLong()
                        Log.e("idL","$id")

                        val thumbnail: Bitmap =
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                binding.root.context.contentResolver.loadThumbnail(
                                    item.uri!!, Size(640, 640), null
                                )
                            } else {
                                MediaStore.Images.Thumbnails.getThumbnail(
                                    binding.root.context.contentResolver, id,
                                    MediaStore.Images.Thumbnails.MINI_KIND,
                                    null
                                )
                            }
                        Glide.with(binding.folderImage)
                            .asBitmap()
                            .load(thumbnail)
                            .into(object : CustomTarget<Bitmap>() {
                                override fun onResourceReady(
                                    resource: Bitmap,
                                    transition: Transition<in Bitmap>?
                                ) {
                                    binding.folderImage.setImageBitmap(resource)
                                }

                                override fun onLoadCleared(placeholder: Drawable?) {
                                }
                            })
                    } catch (e: Exception) {
                    }
                }
                binding.itemsCount.text = item.count.toString()
                binding.folderName.text = item.name

            }
        }

        override fun unselectedItem(item: Any?) {
            //TODO("Not yet implemented")
        }

        override fun selectedItem(item: Any?) {
            //TODO("Not yet implemented")
        }

    }

    override fun createViewHolder(itemView: View): FolderSelectorViewHOlder {
        val binding = AlbumCheckItemBinding.bind(itemView)
        return FolderSelectorViewHOlder(binding)
    }
}