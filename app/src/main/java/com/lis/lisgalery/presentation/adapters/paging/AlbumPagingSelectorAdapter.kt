package com.lis.lisgalery.presentation.adapters.paging

import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Size
import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.DiffUtil
import com.lis.domain.models.FolderModel
import com.lis.domain.tools.ImageFun
import com.google.android.material.R.attr
import com.lis.lisgalery.R
import com.lis.lisgalery.databinding.AlbumCheckItemBinding
import com.lis.lisgalery.presentation.adapters.base.BasePagingAdapter
import java.io.FileNotFoundException

class AlbumPagingSelectorAdapter(idLayout: Int) :
    BasePagingAdapter<FolderModel, AlbumPagingSelectorAdapter.AlbumPagingViewHolder>(
        idLayout, ALBUM_COMPARISON
    ) {

    val selectedFolders= mutableListOf<Long>()
    inner class AlbumPagingViewHolder(private val binding: AlbumCheckItemBinding) :
        BasePagingViewHolder(binding.root) {

        override fun unselectedItem(item: Any?) {
            val value = TypedValue()
            binding.root.context.theme.resolveAttribute(attr.colorBackgroundFloating, value, true)
            binding.layout.setBackgroundColor(value.data)
        }

        override fun selectedItem(item: Any?) {
            val value = TypedValue()
            binding.root.context.theme.resolveAttribute(attr.colorSecondary, value, true)
            binding.layout.setBackgroundColor(value.data)
        }

        override fun bind(item: Any?) {
            if (item != null) {
                showData(item)
            }
        }

        override fun showData(item: Any) {
            if (item is FolderModel) {
                setImage(item.uri)
                binding.itemsCount.text = item.count.toString()
                binding.folderName.text = item.name
                if(item.isSelected){
                    selectedItem(item)
                } else {
                    unselectedItem(item)
                }

                binding.viewFolder.setOnClickListener{
                    clickListener.onButtonOnItemClick(item.id)
                }
                itemView.setOnClickListener {
                    item.isSelected = !item.isSelected
                    if(item.isSelected){
                        selectedFolders.add(item.id)
                    }else {
                        selectedFolders.remove(item.id)
                    }

                    clickListener.onItemClick(item.id)
                    notifyItemChanged(bindingAdapterPosition, itemView)
                }
            }
        }

        private fun setImage(uri: Uri?) {
            if (uri != null) {
                try {
                    val thumbnail: Bitmap = getThumbnail(uri)
                    ImageFun().setBitmapImage(binding.folderImage, thumbnail)
                } catch (_: FileNotFoundException) {
                    ImageFun().setImage(binding.folderImage, R.drawable.test_image)
                }
            }
        }

        private fun getThumbnail(uri: Uri): Bitmap {
            val id = uri.lastPathSegment!!.split(":")[0].toLong()

            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                binding.root.context.contentResolver.loadThumbnail(
                    uri, Size(640, 640), null
                )
            } else {
                MediaStore.Images.Thumbnails.getThumbnail(
                    binding.root.context.contentResolver,
                    id,
                    MediaStore.Images.Thumbnails.MINI_KIND,
                    null
                )
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