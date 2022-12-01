package com.lis.lisgalery.presentation.adapters.paging

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.view.View
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.lis.domain.models.FolderModel
import com.lis.lisgalery.databinding.AlbumCheckItemBinding
import com.lis.lisgalery.presentation.adapters.base.BasePagingAdapter

class AlbumPagingAdapter(private val idLayout: Int) :
    BasePagingAdapter<FolderModel, AlbumPagingAdapter.AlbumPagingViewHolder>(
        idLayout,
        ALBUM_COMPARISON
    ) {
    inner class AlbumPagingViewHolder(private val binding: AlbumCheckItemBinding) :
        BasePagingAdapter.BasePagingViewHolder(binding.root) {
        override fun unselectedItem(item: Any?) {
            //TODO("Not yet implemented")
        }

        override fun selectedItem(item: Any?) {
            //TODO("Not yet implemented")
        }

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
                        Log.e("idL", "$id")

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
    }

    override fun createViewHolder(itemView: View): AlbumPagingViewHolder {
        val binding = AlbumCheckItemBinding.bind(itemView)
        return AlbumPagingViewHolder(binding)
    }

    companion object {
        private val ALBUM_COMPARISON = object : DiffUtil.ItemCallback<FolderModel>() {
            override fun areItemsTheSame(oldItem: FolderModel, newItem: FolderModel): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: FolderModel, newItem: FolderModel): Boolean {
                return oldItem == newItem
            }

        }
    }

}