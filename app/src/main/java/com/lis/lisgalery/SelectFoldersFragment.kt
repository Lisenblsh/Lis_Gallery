package com.lis.lisgalery

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lis.lisgalery.databinding.FragmentSelectFoldersBinding
import com.lis.lisgalery.presentation.adapters.base.BasePagingAdapter
import com.lis.lisgalery.presentation.adapters.paging.AlbumPagingSelectorAdapter
import com.lis.lisgalery.presentation.viewModels.AlbumViewModel
import jcifs.CIFSContext
import jcifs.Configuration
import jcifs.config.PropertyConfiguration
import jcifs.context.BaseContext
import jcifs.smb.SmbFile
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class SelectFoldersFragment : Fragment() {

    private lateinit var binding: FragmentSelectFoldersBinding

    private val viewModel by viewModel<AlbumViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if(!this::binding.isInitialized){
            binding = FragmentSelectFoldersBinding.inflate(inflater, container, false)
            binding.viewFolderList()
        }
        return binding.root
    }


    private fun FragmentSelectFoldersBinding.viewFolderList() {
        val adapter = AlbumPagingSelectorAdapter(R.layout.album_check_item)
        adapter.setOnItemClickListener(object : BasePagingAdapter.OnItemClickListener{
            override fun onItemClick(id: Long?) {}

            override fun onButtonOnItemClick(id: Long?) {
                if (id!=null){
                    val directions = SelectFoldersFragmentDirections.actionSelectFoldersFragmentToAlbumFragment(id)
                    NavHostFragment.findNavController(this@SelectFoldersFragment).navigate(directions)
                }
            }

        })
        foldersList.adapter = adapter
        foldersList.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL,false)

        lifecycleScope.launch{
            viewModel.pagingAlbumList.collectLatest(adapter::submitData)
        }
    }
}
