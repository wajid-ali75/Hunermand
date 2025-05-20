package com.collage.hunermandandriodapp

import android.app.AlertDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // Corrected parenthesis
    }

    override fun onBackPressed() {
        // Show exit confirmation dialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Exit Application")
        builder.setMessage("Are you sure you want to exit the application?")

        // Positive button to exit the app
        builder.setPositiveButton("Yes") { dialog, _ ->
            dialog.dismiss() // Close the dialog
            finishAffinity() // Close all activities
        }

        // Negative button to cancel
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss() // Dismiss the dialog
        }

        val dialog = builder.create()
        dialog.show()
    }
}
