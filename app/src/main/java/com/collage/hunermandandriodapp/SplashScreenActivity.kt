package com.collage.hunermandandriodapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.collage.hunermandandriodapp.databinding.ActivitySplashScreenBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding
    private val database = FirebaseDatabase.getInstance()
    private lateinit var logoImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        logoImageView = binding.logoImageView

        // Start decision-making logic
        checkLoginStatus()
    }

    private fun checkLoginStatus() {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val cnic = sharedPreferences.getString("cnic", null)

        if (cnic != null) {
            // User is already logged in, check user type and navigate
            checkUserTypeAndNavigate(cnic)
        } else {
            // First time user, navigate to MainActivity
            navigateToMainActivity()
        }
    }

    private fun checkUserTypeAndNavigate(cnic: String) {
        binding.progressBar.visibility = View.VISIBLE
        val expertsUserRef = database.getReference("ExpertsUser")
        expertsUserRef.orderByChild("cnic").equalTo(cnic)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // CNIC found in "ExpertsUser"
                        navigateToActivity(Jobs::class.java)
                        binding.progressBar.visibility = View.GONE
                    } else {
                        val usersRef = database.getReference("users")
                        usersRef.orderByChild("cnic").equalTo(cnic)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.exists()) {
                                        // CNIC found in "users"
                                        navigateToActivity(Experts::class.java)
                                    } else {
                                        // CNIC not found, go to MainActivity
                                        navigateToMainActivity()

                                        binding.progressBar.visibility = View.GONE
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    showToast("Error checking user type: ${error.message}")
                                    navigateToMainActivity()
                                }
                            })
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    showToast("Error checking user type: ${error.message}")
                    navigateToMainActivity()
                }
            })
    }

    private fun navigateToMainActivity() {
        applyZoomOutAnimation {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun navigateToActivity(activityClass: Class<*>) {
        startActivity(Intent(this, activityClass))
        finish() // Ensures SplashScreenActivity is not in the back stack
    }

    private fun applyZoomOutAnimation(onAnimationEnd: () -> Unit) {
        val zoomOutAnimation = AnimationUtils.loadAnimation(this, R.anim.zoom_out)
        zoomOutAnimation.duration = 5000 // 5 seconds for MainActivity
        zoomOutAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                onAnimationEnd()
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })
        logoImageView.startAnimation(zoomOutAnimation)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}