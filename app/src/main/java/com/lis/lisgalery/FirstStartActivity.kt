package com.lis.lisgalery

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import com.lis.lisgalery.databinding.ActivityFirstStartBinding

class FirstStartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFirstStartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFirstStartBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}