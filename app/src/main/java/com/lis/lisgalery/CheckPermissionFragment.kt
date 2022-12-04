package com.lis.lisgalery

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import com.lis.lisgalery.databinding.FragmentCheckPermissionBinding


class CheckPermissionFragment : Fragment() {
    private lateinit var binding: FragmentCheckPermissionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPermission()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCheckPermissionBinding.inflate(inflater, container, false)

        return binding.root
    }

    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            Log.e("isGranted", "$isGranted")
            if (isGranted) {
                val navigation =
                    CheckPermissionFragmentDirections.actionCheckPermissionFragmentToSelectFoldersFragment()
                NavHostFragment.findNavController(this).navigate(navigation)
            } else {
            }
        }

    private fun checkPermission() {
        Log.e("isGranted2", "${isPermissionGranted()}")
        if (isPermissionGranted()) {
            checkFirstStart()
        } else {
            requestPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private fun checkFirstStart() {
        if (isFirstStart()){
            val navigation =
                CheckPermissionFragmentDirections.actionCheckPermissionFragmentToSelectFoldersFragment()
            NavHostFragment.findNavController(this).navigate(navigation)
        }else {
            val navigation =
                CheckPermissionFragmentDirections.actionCheckPermissionFragmentToMobileNavigation()
            NavHostFragment.findNavController(this).navigate(navigation)
        }
    }

    private fun isFirstStart(): Boolean {
        val pref = requireActivity().getSharedPreferences("appSettings", Context.MODE_PRIVATE)
        return pref.getBoolean("isFirstStart",true)
    }

    private fun isPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requireActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }
}