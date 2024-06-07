package com.latinka.tinkawork

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController

import com.latinka.tinkawork.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()

        setContentView(binding.root)

        val navHostFragment: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigation.setupWithNavController(navController)

        // Esto es para quitar el espacio vacio inferior que tiene el BottonNavigationView
        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomNavigation) { v, insets ->
            val bottomInsets = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                insets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars()).bottom
            } else { 0 }
            v.setPadding(0, 0, 0, bottomInsets)
            insets
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}