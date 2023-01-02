package com.lis.lisgalery.presentation.adapters.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil

abstract class BasePagingAdapter<T : Any,VH : BaseAdapter.BaseViewHolder>(
    private val idLayout: Int,
    ITEMS_COMPARATOR: DiffUtil.ItemCallback<T>
) : PagingDataAdapter<T, VH>(ITEMS_COMPARATOR) {

    var selectedItemPos = -1
    var lastItemSelectedPos = -1

    abstract fun createViewHolder(itemView: View): VH

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int) = idLayout

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(idLayout, parent,false)
        return createViewHolder(view)
    }

    abstract class BasePagingViewHolder(itemView: View): BaseAdapter.BaseViewHolder(itemView) {

        abstract fun unselectedItem(item: Any?)

        abstract fun selectedItem(item: Any?)
    }

    interface OnItemClickListener {
        fun onFolderClick(id: Long?)
        fun onImageClick(path: String?)
        fun onVideoClick(path: String?)
        fun onButtonOnItemClick(id: Long?)
    }

    lateinit var clickListener: OnItemClickListener

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.clickListener = listener
    }

}