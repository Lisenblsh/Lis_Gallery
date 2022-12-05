package com.lis.lisgalery.presentation.adapters.paging

import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Size
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import com.lis.domain.models.FolderItemsModel
import com.lis.domain.models.FolderModel
import com.lis.domain.tools.ImageFun
import com.lis.domain.tools.getStringDurationsFromLong
import com.lis.lisgalery.R
import com.lis.lisgalery.databinding.AlbumCheckItemBinding
import com.lis.lisgalery.databinding.AlbumItemBinding
import com.lis.lisgalery.presentation.adapters.base.BasePagingAdapter
import java.io.FileNotFoundException
import java.text.MessageFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class ItemsInAlbumPagingAdapter(idLayout: Int):
BasePagingAdapter<FolderItemsModel,ItemsInAlbumPagingAdapter.ItemsInAlbumViewHolder>(idLayout, ITEMS_COMPARATOR){

    inner class ItemsInAlbumViewHolder(private val binding: AlbumItemBinding):
            BasePagingViewHolder(binding.root) {
        override fun bind(item: Any?) {
            if(item != null){
                showData(item)
            }
        }

        override fun showData(item: Any) {
            if(item is FolderItemsModel){
                ImageFun().setThumbnail(binding.image, item.path)
                if(item.isVideo){
                    binding.isVideoCard.isVisible = true
                    binding.videoDuration.text = item.duration.getStringDurationsFromLong()
                } else {
                    binding.isVideoCard.isVisible = false
                }
            }
        }

        override fun unselectedItem(item: Any?) {
            TODO("Not yet implemented")
        }

        override fun selectedItem(item: Any?) {
            TODO("Not yet implemented")
        }

    }

    companion object {
        private val ITEMS_COMPARATOR = object : DiffUtil.ItemCallback<FolderItemsModel>() {
            override fun areItemsTheSame(oldItem: FolderItemsModel, newItem: FolderItemsModel): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: FolderItemsModel, newItem: FolderItemsModel): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun createViewHolder(itemView: View): ItemsInAlbumViewHolder {
        val binding = AlbumItemBinding.bind(itemView)
        return ItemsInAlbumViewHolder(binding)
    }
}