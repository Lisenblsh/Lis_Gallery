package com.lis.lisgalery.presentation.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lis.lisgalery.R
import com.lis.lisgalery.databinding.FragmentSelectFoldersBinding
import com.lis.lisgalery.presentation.adapters.base.BasePagingAdapter
import com.lis.lisgalery.presentation.adapters.paging.AlbumPagingSelectorAdapter
import com.lis.lisgalery.presentation.viewModels.AlbumViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

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
        confirmButton.setOnClickListener {
            val pref = requireActivity().getSharedPreferences("appSettings",Context.MODE_PRIVATE)
            with(pref.edit()){
                putBoolean("isFirstStart",false)
                apply()
            }
        }
        val adapter = AlbumPagingSelectorAdapter(R.layout.album_check_item)
        adapter.setOnItemClickListener(object : BasePagingAdapter.OnItemClickListener{
            override fun onItemClick(id: Long?) {}

            override fun onButtonOnItemClick(id: Long?) {
                if (id!=null){
                    val directions =
                        SelectFoldersFragmentDirections.actionSelectFoldersFragmentToAlbumFragment(
                            id
                        )
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
