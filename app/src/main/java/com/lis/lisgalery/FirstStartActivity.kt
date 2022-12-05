package com.lis.lisgalery

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import com.lis.lisgalery.databinding.ActivityFirstStartBinding

class FirstStartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFirstStartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFirstStartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkPermission()
    }


    private fun setNavGraph(fragment: Int) {
        val navHostFragment = (supportFragmentManager.findFragmentById(R.id.home_nav_fragment) as NavHostFragment)
        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.nav_graph)
        graph.setStartDestination(fragment)
        navHostFragment.navController.graph = graph
    }

    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                checkFirstStart()
            } else {
            }
        }

    private fun checkPermission() {
        if (isPermissionGranted()) {
            checkFirstStart()
        } else {

            requestPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private fun checkFirstStart() {
        if (isFirstStart()){
            setNavGraph(R.id.selectFoldersFragment)
        }else {
            setNavGraph(R.id.mobile_navigation)

        }
    }

    private fun isFirstStart(): Boolean {
        val pref = getSharedPreferences("appSettings", Context.MODE_PRIVATE)
        return pref.getBoolean("isFirstStart",true)
    }

    private fun isPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

}