package com.lis.lisgalery.presentation.fragments

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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import com.lis.lisgalery.databinding.FragmentCheckPermissionBinding


class CheckPermissionFragment : Fragment() {
    private lateinit var binding: FragmentCheckPermissionBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCheckPermissionBinding.inflate(inflater, container, false)
        binding.getStoragePermission.setOnClickListener{

        }
        return binding.root
    }


}