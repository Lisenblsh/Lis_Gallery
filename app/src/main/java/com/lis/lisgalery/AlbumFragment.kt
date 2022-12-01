package com.lis.lisgalery

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lis.lisgalery.databinding.FragmentAlbumBinding
import com.lis.lisgalery.presentation.adapters.paging.ItemsInAlbumPagingAdapter
import com.lis.lisgalery.presentation.viewModels.AlbumViewModel
import com.lis.lisgalery.presentation.viewModels.ItemsInAlbumViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class AlbumFragment : Fragment() {

    private lateinit var binding: FragmentAlbumBinding

    private val viewModel by viewModel<ItemsInAlbumViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAlbumBinding.inflate(inflater, container, false)
        binding.viewList()
        return binding.root
    }

    private fun FragmentAlbumBinding.viewList() {
        val adapter = ItemsInAlbumPagingAdapter(R.layout.album_item)
        itemsList.adapter = adapter
        itemsList.layoutManager =
            GridLayoutManager(requireContext(), 4, RecyclerView.VERTICAL, false)


        lifecycleScope.launch{
            val args = AlbumFragmentArgs.fromBundle(requireArguments())
            viewModel.getItemsList(args.folderId).collectLatest(adapter::submitData)
        }
    }
}
