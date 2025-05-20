package com.collage.hunermandandriodapp

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.collage.hunermandandriodapp.databinding.ActivityJobsBinding


class Jobs : BaseActivity() {

    private lateinit var binding: ActivityJobsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJobsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateBottomNavigationView()
        loadFragment(JobsHomeFragement())

        binding.buttom.setItemActiveIndicatorColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.Kelly_Green)))

        binding.buttom.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    loadFragment(JobsHomeFragement())
                    true
                }
                R.id.jobs -> {
                    loadFragment(JobsFragment())
                    true
                }
                R.id.profile -> {
                    handleProfileOrLogin()
                    true
                }
                R.id.experts -> {
                    val intent = Intent(this,Experts::class.java)
                    startActivity(intent)
                    finish()
                    true
                }

                else -> false
            }
        }

    }

    fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }


    // Public function to get the BottomNavigationView
    fun getBottomNavigationView() = binding.buttom

    // Function to update BottomNavigationView based on login status
    private fun updateBottomNavigationView() {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val cnic = sharedPreferences.getString("cnic", null)

        val menu = binding.buttom.menu
        val profileMenuItem = menu.findItem(R.id.profile)

        // Update the title based on login status
        if (cnic == null) {
            profileMenuItem.title = "Login"
            profileMenuItem.setIcon(R.drawable.baseline_login)
        } else {
            profileMenuItem.title = "Profile"
            profileMenuItem.setIcon(R.drawable.baseline_profile)
        }
    }


    // Function to handle Profile/Login menu item click
    private fun handleProfileOrLogin() {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val cnic = sharedPreferences.getString("cnic", null)

        if (cnic == null) {
            // User is not logged in, navigate to SignIn_cnic activity
            val intent = Intent(this, SignIn_cnic::class.java)
            startActivity(intent)
            finish()
        } else {
            // User is logged in, load ProfileFragment
            loadFragment(ProfileFragment())
        }
    }


}