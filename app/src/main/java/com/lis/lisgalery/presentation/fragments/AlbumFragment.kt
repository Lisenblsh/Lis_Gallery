package com.lis.lisgalery.presentation.fragments

import android.content.res.Resources
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowMetrics
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.lis.lisgalery.R
import com.lis.lisgalery.databinding.FragmentAlbumBinding
import com.lis.lisgalery.presentation.adapters.base.BasePagingAdapter
import com.lis.lisgalery.presentation.adapters.paging.ItemsInAlbumPagingAdapter
import com.lis.lisgalery.presentation.viewModels.ItemsInAlbumViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class AlbumFragment : Fragment() {

    private lateinit var binding: FragmentAlbumBinding

    private val viewModel by sharedViewModel<ItemsInAlbumViewModel>()

    private val photoAdapter = ItemsInAlbumPagingAdapter(R.layout.album_item)

    private var gridLayoutManager: GridLayoutManager? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (!this::binding.isInitialized){
            binding = FragmentAlbumBinding.inflate(inflater, container, false)
            binding.viewList()
            binding.bindMenu()
        }

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
        viewModel.position.observe(viewLifecycleOwner){
            Log.e("position", it.toString())
            (binding.itemsList.layoutManager as GridLayoutManager).scrollToPositionWithOffset(it,offsetForLayout)
        }
    }

    companion object{
        private val offsetForLayout = Resources.getSystem().displayMetrics.heightPixels/3
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


        (itemsList.itemAnimator as SimpleItemAnimator).supportsChangeAnimations  = false

        photoAdapter.setOnItemClickListener(object : BasePagingAdapter.OnItemClickListener {
            override fun onFolderClick(id: Long?) {

            }

            override fun onItemClick(position: Int) {
                layoutPosition = position
                    val directions =
                        AlbumFragmentDirections.actionAlbumFragmentToOpenItemFragment()
                    NavHostFragment.findNavController(this@AlbumFragment).navigate(directions)
            }

            override fun onButtonOnItemClick(id: Long?) {
            }
        })
        itemsList.adapter = photoAdapter
        itemsList.layoutManager = gridLayoutManager

        lifecycleScope.launch {
            val args = AlbumFragmentArgs.fromBundle(requireArguments())
            viewModel.getItemsList(args.folderId).collectLatest(photoAdapter::submitData)
        }
    }

    private var layoutPosition = 0

    override fun onPause() {
        viewModel.position.value = layoutPosition
        viewModel.position.removeObservers(viewLifecycleOwner)
        super.onPause()
    }

    override fun onDestroy() {
        viewModel.position.value = 0
        super.onDestroy()

    }
}
