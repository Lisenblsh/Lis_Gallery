package com.lis.lisgalery.presentation.adapters.paging

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import com.lis.domain.models.FolderItemsModel
import com.lis.domain.tools.ImageFun
import com.lis.domain.tools.getStringDurationsFromLong
import com.lis.lisgalery.databinding.AlbumItemBinding
import com.lis.lisgalery.presentation.adapters.base.BasePagingAdapter

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
                itemView.setOnClickListener {
                    clickListener.onItemClick(bindingAdapterPosition)
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