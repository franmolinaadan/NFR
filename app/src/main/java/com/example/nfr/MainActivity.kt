package com.example.nfr

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.nfr.databinding.ActivityMainBinding
import com.example.nfr.ui.search.SearchFragment
import com.example.nfr.ui.home.HomeFragment
import com.example.nfr.ui.login.LoginActivity
import com.example.nfr.ui.message.MessageFragment
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var loginActivity: LoginActivity
    private lateinit var bottomNavigationView: BottomNavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)




        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home_bar -> replaceFragment(HomeFragment())
                R.id.nav_search_bar -> replaceFragment(SearchFragment())
                R.id.nav_message_bar -> replaceFragment(MessageFragment())
                R.id.nav_profile_bar -> replaceFragment(SearchFragment())
                else ->{

                }
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}