package com.lis.lisgalery

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lis.data.Gallery
import com.lis.lisgalery.databinding.FragmentFirstStartBinding
import com.lis.lisgalery.presentation.adapters.paging.AlbumPagingAdapter
import com.lis.lisgalery.presentation.adapters.selector.FolderSelectorAdapter
import com.lis.lisgalery.presentation.viewModels.AlbumViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FirstStartFragment : Fragment() {

    private lateinit var binding: FragmentFirstStartBinding

    private val viewModel by viewModel<AlbumViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFirstStartBinding.inflate(inflater, container, false)
        binding.viewFolderList()
        return binding.root
    }


    private fun FragmentFirstStartBinding.viewFolderList() {
        val adapter = AlbumPagingAdapter(R.layout.album_check_item)

        foldersList.adapter = adapter
        foldersList.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL,false)

        lifecycleScope.launch{
            viewModel.pagingAlbumList.collectLatest(adapter::submitData)
        }
    }
}
