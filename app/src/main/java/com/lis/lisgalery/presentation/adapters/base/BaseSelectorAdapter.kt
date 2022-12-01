package com.lis.lisgalery.presentation.adapters.base

import android.view.View

abstract class BaseSelectorAdapter<VH : BaseSelectorAdapter<VH>.BaseSelectorViewHolder>(
    private val listItem: List<*>,
    idLayout: Int
) : BaseAdapter<VH>(listItem,idLayout) {
    var selectedItemPos = -1
    var lastItemSelectedPos = -1

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = listItem[position]
        if(position == selectedItemPos)
            holder.selectedItem(item)
        else
            holder.unselectedItem(item)
        holder.bind(item)
    }

    abstract inner class BaseSelectorViewHolder(itemView: View) : BaseViewHolder(itemView) {

        init {
            itemView.setOnClickListener {
                selectedItemPos = bindingAdapterPosition
                lastItemSelectedPos = if(lastItemSelectedPos == -1)
                    selectedItemPos
                else {
                    notifyItemChanged(lastItemSelectedPos)
                    selectedItemPos
                }
                clickListener.onItemClick(selectedItemPos)
                notifyItemChanged(selectedItemPos)
            }
        }

        abstract fun unselectedItem(item: Any?)

        abstract fun selectedItem(item: Any?)
    }
}