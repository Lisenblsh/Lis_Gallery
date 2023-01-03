package com.lis.lisgalery.presentation.adapters.paging

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.lis.domain.models.FolderItemsModel
import com.lis.domain.tools.ImageFun
import com.lis.lisgalery.databinding.ImageShowItemBinding
import com.lis.lisgalery.databinding.VideoShowItemBinding
import com.lis.lisgalery.presentation.adapters.base.BaseAdapter

class ViewItemsInAlbumPagingAdapter(private val imageLayout: Int, private val videoLayout: Int) :
    PagingDataAdapter<FolderItemsModel, ViewItemsInAlbumPagingAdapter.ViewItemsInAlbumPagingViewHolder>(
        ITEMS_COMPARATOR
    ) {

    override fun onBindViewHolder(holder: ViewItemsInAlbumPagingViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position)?.isVideo == true) {
            TYPE_VIDEO
        } else {
            TYPE_IMAGE
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewItemsInAlbumPagingViewHolder {
        val layout = when (viewType) {
            TYPE_IMAGE -> imageLayout
            TYPE_VIDEO -> videoLayout
            else -> throw IllegalArgumentException("Invalid view type")
        }

        val view = LayoutInflater.from(parent.context)
            .inflate(layout, parent, false)
        return ViewItemsInAlbumPagingViewHolder(view)
    }

    inner class ViewItemsInAlbumPagingViewHolder(itemView: View) :
        BaseAdapter.BaseViewHolder(itemView) {
        override fun bind(item: Any?) {
            if (item != null) {
                showData(item)
            }
        }

        override fun showData(item: Any) {
            if (item is FolderItemsModel) {
                if (item.isVideo) {
                    val binding = VideoShowItemBinding.bind(itemView)
                    showVideo(binding, item)
                } else {
                    val binding = ImageShowItemBinding.bind(itemView)
                    showImage(binding, item)
                }
            }
        }

        private fun showImage(binding: ImageShowItemBinding, item: FolderItemsModel) {
            ImageFun().setImage(binding.imageView, item.path)
        }

        private fun showVideo(binding: VideoShowItemBinding, item: FolderItemsModel) {
            val player: ExoPlayer = ExoPlayer.Builder(binding.root.context).build()
            binding.videoPlayer.player = player
            val media = MediaItem.fromUri(item.path)
            player.addMediaItem(media)
            player.prepare()
        }
    }

    companion object {
        private val ITEMS_COMPARATOR = object : DiffUtil.ItemCallback<FolderItemsModel>() {
            override fun areItemsTheSame(
                oldItem: FolderItemsModel,
                newItem: FolderItemsModel
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: FolderItemsModel,
                newItem: FolderItemsModel
            ): Boolean {
                return oldItem == newItem
            }
        }

        private const val TYPE_IMAGE = 0
        private const val TYPE_VIDEO = 1
    }
}