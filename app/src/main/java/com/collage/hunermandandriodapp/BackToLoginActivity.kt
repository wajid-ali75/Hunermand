package com.collage.hunermandandriodapp

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

open class BackToLoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onBackPressed() {
        // Show confirmation dialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Go to Login")
        builder.setMessage("Are you sure you want to go back to the login screen?")

        // Positive button to navigate to the login activity
        builder.setPositiveButton("Yes") { dialog, _ ->
            dialog.dismiss() // Close the dialog
            navigateToLoginActivity() // Navigate to the login activity
        }

        // Negative button to cancel
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss() // Dismiss the dialog
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun navigateToLoginActivity() {
        val intent = Intent(this, MainActivity::class.java) // Replace with your LoginActivity
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish() // Optionally, finish the current activity
    }
}
