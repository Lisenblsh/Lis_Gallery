package com.lis.lisgalery.presentation.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lis.lisgalery.R
import com.lis.lisgalery.databinding.FragmentAlbumBinding
import com.lis.lisgalery.presentation.adapters.paging.ItemsInAlbumPagingAdapter
import com.lis.lisgalery.presentation.viewModels.ItemsInAlbumViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class AlbumFragment : Fragment() {

    private lateinit var binding: FragmentAlbumBinding

    private val viewModel by viewModel<ItemsInAlbumViewModel>()

    private val photoAdapter = ItemsInAlbumPagingAdapter(R.layout.album_item)

    private var gridLayoutManager: GridLayoutManager? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAlbumBinding.inflate(inflater, container, false)
        binding.viewList()
        binding.bindMenu()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            photoAdapter.loadStateFlow.collectLatest { loadState ->
                if (loadState.refresh is LoadState.NotLoading) {
                    val position =
                        (binding.itemsList.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                    if (position == 0) {
                        binding.itemsList.scrollToPosition(0)
                    }
                }
            }
        }
        photoAdapter.refresh()
    }

    private fun FragmentAlbumBinding.bindMenu() {
        viewModel.nameFolder.observe(viewLifecycleOwner) {
            folderName.text = it
        }
        backButton.setOnClickListener { super.requireActivity().onBackPressedDispatcher.onBackPressed() }
        requireActivity().menuInflater.inflate(R.menu.album_menu, albumMenu.menu)
        albumMenu.menu.getItem(0).setOnMenuItemClickListener {
            return@setOnMenuItemClickListener topToBottomClick(it)
        }

    }

    private fun FragmentAlbumBinding.topToBottomClick(menuItem: MenuItem): Boolean {
        gridLayoutManager?.reverseLayout = !menuItem.isChecked
        menuItem.isChecked = !menuItem.isChecked
        itemsList.layoutDirection = if (menuItem.isChecked) {
            View.LAYOUT_DIRECTION_RTL
        } else {
            View.LAYOUT_DIRECTION_LTR
        }
        return false
    }

    private fun FragmentAlbumBinding.viewList() {
        gridLayoutManager = GridLayoutManager(requireContext(), 4, RecyclerView.VERTICAL, false)
        itemsList.adapter = photoAdapter
        itemsList.layoutManager = gridLayoutManager

        lifecycleScope.launch {
            val args = AlbumFragmentArgs.fromBundle(requireArguments())
            viewModel.getItemsList(args.folderId).collectLatest(photoAdapter::submitData)
        }
    }
}
