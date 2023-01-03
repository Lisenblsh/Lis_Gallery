package com.lis.lisgalery.presentation.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.paging.cachedIn
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.lis.lisgalery.R
import com.lis.lisgalery.databinding.FragmentOpenItemBinding
import com.lis.lisgalery.presentation.adapters.paging.ViewItemsInAlbumPagingAdapter
import com.lis.lisgalery.presentation.viewModels.ItemsInAlbumViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class OpenItemFragment : Fragment() {

    private lateinit var binding: FragmentOpenItemBinding
    private val viewModel by sharedViewModel<ItemsInAlbumViewModel>()
    private val itemsAdapter = ViewItemsInAlbumPagingAdapter(R.layout.image_show_item, R.layout.video_show_item)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOpenItemBinding.inflate(inflater, container, false)
        val args = OpenItemFragmentArgs.fromBundle(requireArguments())

        binding.showItems(args.position)
        return binding.root
    }

    private fun FragmentOpenItemBinding.showItems(position: Int) {
        topPanel.isVisible = !topPanel.isVisible
        bottomPanel.isVisible = !bottomPanel.isVisible
        itemsAdapter.setOnClickListener(object : ViewItemsInAlbumPagingAdapter.OnClickListener{
            override fun onClick() {
                topPanel.isVisible = !topPanel.isVisible
                bottomPanel.isVisible = !bottomPanel.isVisible
            }

        })
        itemsList.adapter = itemsAdapter
        lifecycleScope.launch {
            viewModel.pagingItemsList.collect(itemsAdapter::submitData)
        }
        itemsList.setCurrentItem(position, false)

        itemsList.registerOnPageChangeCallback(object : OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                val prev = itemsAdapter.exoPlayerItems.indexOfFirst { it.exoPlayer.isPlaying }
                if(prev != -1){
                    itemsAdapter.exoPlayerItems[prev].exoPlayer.also {
                        it.pause()
                        it.seekTo(0)
                    }
                }
                val next = itemsAdapter.exoPlayerItems.indexOfFirst { it.position == position }
                if(next != -1){
                    itemsAdapter.exoPlayerItems[next].exoPlayer.also {
                        //TODO("тут можно записать логику для обработки следующего видео")
                    }
                }
            }
        })
    }

    override fun onPause() {
        super.onPause()
        val index = itemsAdapter.exoPlayerItems.indexOfFirst { it.position == binding.itemsList.currentItem }
        if(index != -1){
            itemsAdapter.exoPlayerItems[index].exoPlayer.also {
                it.pause()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(itemsAdapter.exoPlayerItems.isNotEmpty()){
            for (item in itemsAdapter.exoPlayerItems){
                item.exoPlayer.also {
                    it.stop()
                    it.clearMediaItems()
                }
            }
        }
    }
}
