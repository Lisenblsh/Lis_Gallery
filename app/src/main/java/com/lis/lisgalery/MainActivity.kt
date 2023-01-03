package com.lis.lisgalery

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lis.lisgalery.databinding.ActivityMainBinding
import com.lis.lisgalery.presentation.viewModels.ItemsInAlbumViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel by viewModel<ItemsInAlbumViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
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

                //TODO("Надо помнять текст тут
                // да и вообще разобраться с этим")
                val alertDialogBuilder = AlertDialog.Builder(this)
                    .setTitle("Change Permissions in Settings")
                    .setMessage(
                        """
        Click SETTINGS to Manually Set
        Permissions to use Database Storage
        """.trimIndent()
                    )
                    .setCancelable(false)
                    .setPositiveButton("SETTINGS"
                    ) { _, _ ->
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri: Uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        startActivityForResult(intent, 1000) // Comment 3.
                    }

                val alertDialog: AlertDialog = alertDialogBuilder.create()
                alertDialog.show()
                Toast.makeText(this, "В доступе отказано", Toast.LENGTH_SHORT).show()
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