package com.lis.lisgalery.presentation.adapters.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<VH : BaseAdapter.BaseViewHolder>(
    private val listItem: List<*>,
    private val idLayout: Int
) : RecyclerView.Adapter<VH>() {

    abstract fun createViewHolder(itemView: View): VH

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VH {
        val view = LayoutInflater.from(parent.context).inflate(idLayout, parent, false)
        return createViewHolder(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(listItem[position])
    }

    override fun getItemCount() = listItem.size


    abstract class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(item: Any?)

        abstract fun showData(item: Any)
    }

    interface OnItemClickListener {
        fun onItemClick(id: Int?)
        fun onButtonOnItemClick(id: Int?)
    }

    lateinit var clickListener: OnItemClickListener

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.clickListener = listener
    }

}