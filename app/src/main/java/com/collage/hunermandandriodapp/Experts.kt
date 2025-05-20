package com.collage.hunermandandriodapp

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.collage.hunermandandriodapp.databinding.ActivityExpertsBinding

class Experts : BaseActivity() {
    private lateinit var binding: ActivityExpertsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpertsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateBottomNavigationView()
        // Load HomeFragment by default
        loadFragment(HomeFragment())


        binding.buttom.setItemActiveIndicatorColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.Kelly_Green)))


        // Set up Bottom Navigation
        binding.buttom.setOnNavigationItemSelectedListener { item ->

            when (item.itemId) {
                R.id.home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.experts -> {
                    loadFragment(ExpertsPragment())
                    true
                }
                R.id.postJob -> {

                    if (isUserLoggedIn()) {
                        loadFragment(PostJobFragment())
                    } else {
                        showLoginRequiredDialog()
                    }


                    true
                }
                R.id.profile -> {
                    handleProfileOrLogin()
                    true
                }
                R.id.jobs -> {
                    var intent = Intent(this,Jobs::class.java)
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
            profileMenuItem.setIcon(R.drawable.baseline_profile)  // Set to profile icon
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


    // Function to check if the user is logged in
    private fun isUserLoggedIn(): Boolean {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.contains("cnic")
    }

    // Function to show a dialog prompting the user to log in
    private fun showLoginRequiredDialog() {
        AlertDialog.Builder(this)
            .setTitle("Login Required")
            .setMessage("Please log in, to post a job.")
            .setPositiveButton("Log In") { dialog, _ ->
                // Navigate to login screen
                val intent = Intent(this, SignIn_cnic::class.java)
                startActivity(intent)
                dialog.dismiss()
                finish()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                (this as? Experts)?.getBottomNavigationView()?.selectedItemId = R.id.home
            }
            .show()
    }


}
