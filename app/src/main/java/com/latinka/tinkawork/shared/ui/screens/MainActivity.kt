package com.latinka.tinkawork.shared.ui.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment

import com.google.firebase.auth.FirebaseAuth

import com.latinka.tinkawork.R
import com.latinka.tinkawork.account.ui.screens.AccountOptionsScreenFragment
import com.latinka.tinkawork.auth.ui.screens.LoginActivity
import com.latinka.tinkawork.databinding.ActivityMainBinding
import com.latinka.tinkawork.timeRecord.ui.screens.HistoryScreenFragment

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()

        val user = auth.currentUser
        if (user == null) {
            startActivity(
                Intent(applicationContext, LoginActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            )
            return
        }

        setContentView(binding.root)

        updateFragment(HomeScreenFragment.newInstance())

        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.homeScreenFragment -> {
                    updateFragment(HomeScreenFragment.newInstance())
                    true
                }
                R.id.historyScreenFragment -> {
                    updateFragment(HistoryScreenFragment.newInstance())
                    true
                }
                R.id.accountOptionsScreenFragment -> {
                    updateFragment(AccountOptionsScreenFragment.newInstance())
                    true
                }
                else -> false
            }
        }

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

    private fun updateFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_container, fragment)
        transaction.commit()
    }
}
