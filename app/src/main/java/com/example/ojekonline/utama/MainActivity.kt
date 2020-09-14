package com.example.ojekonline.utama

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.ojekonline.R
import com.example.ojekonline.fragment.HistoryFragment
import com.example.ojekonline.fragment.HomeFragment
import com.example.ojekonline.fragment.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigation.setOnNavigationItemSelectedListener(onBottomNavListener)

        val frag = supportFragmentManager
            .beginTransaction()
        frag.add(R.id.container, HomeFragment())
        frag.commit()
    }


    private val onBottomNavListener = BottomNavigationView
        .OnNavigationItemSelectedListener { i ->

            var selectedFragment: Fragment = HomeFragment()

            when (i.itemId) {
                R.id.navigation_home -> {
                    selectedFragment = HomeFragment()
                }
                R.id.navigation_history -> {
                    selectedFragment = HistoryFragment()
                }
                R.id.navigation_profile -> {
                    selectedFragment = ProfileFragment()
                }
            }
            val frag = supportFragmentManager.beginTransaction()
            frag.replace(R.id.container, selectedFragment)
            frag.commit()

            true

    }
}
