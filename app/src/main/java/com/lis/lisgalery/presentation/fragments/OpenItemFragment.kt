package com.lis.lisgalery.presentation.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.paging.cachedIn
import com.lis.lisgalery.R
import com.lis.lisgalery.databinding.FragmentOpenItemBinding
import com.lis.lisgalery.presentation.adapters.paging.ViewItemsInAlbumPagingAdapter
import com.lis.lisgalery.presentation.viewModels.ItemsInAlbumViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class OpenItemFragment : Fragment() {

    private lateinit var binding: FragmentOpenItemBinding
    private val viewModel by sharedViewModel<ItemsInAlbumViewModel>()

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
        val itemsAdapter = ViewItemsInAlbumPagingAdapter(R.layout.image_show_item, R.layout.video_show_item)
        itemsList.adapter = itemsAdapter
        viewModel.nameFolder.observe(viewLifecycleOwner) {
            Log.e("asd",it)
        }
        lifecycleScope.launch {
            viewModel.pagingItemsList.collect(itemsAdapter::submitData)
        }
        itemsList.setCurrentItem(position, false)
    }
}
