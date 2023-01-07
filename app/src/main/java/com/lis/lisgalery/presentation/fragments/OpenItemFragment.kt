package com.lis.lisgalery.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.codeboy.pager2_transformers.Pager2_PopTransformer
import com.codeboy.pager2_transformers.Pager2_ZoomInTransformer
import com.codeboy.pager2_transformers.Pager2_ZoomOutSlideTransformer
import com.codeboy.pager2_transformers.Pager2_ZoomOutTransformer
import com.lis.domain.models.FolderItemsModel
import com.lis.lisgalery.R
import com.lis.lisgalery.databinding.FragmentOpenItemBinding
import com.lis.lisgalery.presentation.adapters.paging.ViewItemsInAlbumPagingAdapter
import com.lis.lisgalery.presentation.pageTransformers.DepthPageTransformer
import com.lis.lisgalery.presentation.pageTransformers.PopPageTransformer
import com.lis.lisgalery.presentation.viewModels.ItemsInAlbumViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.text.SimpleDateFormat
import java.util.*

class OpenItemFragment : Fragment() {

    private lateinit var binding: FragmentOpenItemBinding
    private val viewModel by sharedViewModel<ItemsInAlbumViewModel>()
    private val itemsAdapter =
        ViewItemsInAlbumPagingAdapter(R.layout.image_show_item, R.layout.video_show_item)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOpenItemBinding.inflate(inflater, container, false)
        binding.bindElements()
        binding.showItems()
        return binding.root
    }

    private fun FragmentOpenItemBinding.showItems() {
        itemsAdapter.setOnClickListener(onAdapterItemClickListener())
        itemsList.registerOnPageChangeCallback(onPageChangeCallbackViewPager())
        itemsList.adapter = itemsAdapter
        itemsList.setPageTransformer(DepthPageTransformer())
        lifecycleScope.launch {
            viewModel.pagingItemsList.collectLatest(itemsAdapter::submitData)
        }
        viewModel.position.value?.let {
            itemsList.setCurrentItem(it, false)
            lifecycleScope.launch {
                delay(1)// need this 'cause recyclerview compute items
                itemsAdapter.notifyItemChanged(it, 1)
            }
        }

        itemsAdapter.itemInfo.observe(viewLifecycleOwner) { setDateTime(it) }
    }

    private fun onPageChangeCallbackViewPager() = object : OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            itemsAdapter.notifyItemChanged(position, 1)
            viewModel.position.value = position
            val prev = itemsAdapter.exoPlayerItems.indexOfFirst { it.exoPlayer.isPlaying }
            if (prev != -1) {
                itemsAdapter.exoPlayerItems[prev].exoPlayer.also {
                    it.pause()
                    it.seekTo(0)
                }
            }
            val next = itemsAdapter.exoPlayerItems.indexOfFirst { it.position == position }
            if (next != -1) {
                itemsAdapter.exoPlayerItems[next].exoPlayer.also {
                    //TODO("тут можно записать логику для обработки следующего видео")
                }
            }
        }
    }

    private fun FragmentOpenItemBinding.setDateTime(it: FolderItemsModel) {
        val date = Date(it.dateModified * 1000)
        val formatDate = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        val formatTime = SimpleDateFormat("HH:mm", Locale.getDefault())
        dateText.text = formatDate.format(date)
        timeText.text = formatTime.format(date)
    }

    private fun FragmentOpenItemBinding.onAdapterItemClickListener() =
        object : ViewItemsInAlbumPagingAdapter.OnClickListener {
            override fun onClick() {
                topPanel.isVisible = !topPanel.isVisible
                bottomPanel.isVisible = !bottomPanel.isVisible
            }

        }

    private fun FragmentOpenItemBinding.bindElements() {
        backButton.setOnClickListener {
            super.requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onPause() {
        super.onPause()
        val index =
            itemsAdapter.exoPlayerItems.indexOfFirst { it.position == binding.itemsList.currentItem }
        if (index != -1) {
            itemsAdapter.exoPlayerItems[index].exoPlayer.also {
                it.pause()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (itemsAdapter.exoPlayerItems.isNotEmpty()) {
            for (item in itemsAdapter.exoPlayerItems) {
                item.exoPlayer.also {
                    it.stop()
                    it.clearMediaItems()
                }
            }
        }
    }
}
