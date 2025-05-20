package com.collage.hunermandandriodapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.collage.hunermandandriodapp.databinding.ActivitySigninCnicBinding
import com.google.firebase.database.*

class SignIn_cnic : AppCompatActivity() {
    lateinit var binding: ActivitySigninCnicBinding
    private val database = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninCnicBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.nextbtn.setOnClickListener {
            val edCnic = binding.edCnic.text.toString().trim()

            if (edCnic.isNotEmpty() && isValidCnic(edCnic) ){
                // Show loading spinner
                binding.progressBar.visibility = View.VISIBLE
                checkCnicInDatabase(edCnic)
            } else {
                if(edCnic.isEmpty()){
                    // Show an error if CNIC is empty
                    binding.edCnic.error = "Please enter your CNIC"
                }else{
                    // Show an error if CNIC is invalid
                    binding.edCnic.error = "Invalid CNIC format (e.g., 12345-6789012-3)"
                }

            }
        }
    }


    private fun isValidCnic(cnic: String): Boolean {
        val cnicRegex = "^\\d{5}-\\d{7}-\\d{1}$"
        return cnic.matches(cnicRegex.toRegex())
    }


    private fun checkCnicInDatabase(cnic: String) {
        val expertsUserRef = database.getReference("ExpertsUser")
        val usersRef = database.getReference("users")

        // Check in "ExpertsUser" node
        expertsUserRef.orderByChild("cnic").equalTo(cnic)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // CNIC found in "ExpertsUser" - navigate to SignIn_password
                        navigateToSignInPassword(cnic)
                    } else {
                        // Check in "users" node if not found in "ExpertsUser"
                        usersRef.orderByChild("cnic").equalTo(cnic)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.exists()) {
                                        // CNIC found in "users" - navigate to SignIn_password
                                        navigateToSignInPassword(cnic)
                                    } else {
                                        // CNIC not found in either nodes - navigate to Registration_form
                                        navigateToRegistrationForm(cnic)
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    handleDatabaseError(error)
                                }
                            })
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    handleDatabaseError(error)
                }
            })
    }


    private fun navigateToSignInPassword(cnic: String) {
        binding.progressBar.visibility = View.GONE  // Hide loading spinner
        val intent = Intent(this, SignIn_password::class.java)
        intent.putExtra("CNIC", cnic)  // Pass CNIC to the next activity
        startActivity(intent)
        finish()
    }

    private fun navigateToRegistrationForm(cnic: String) {
        binding.progressBar.visibility = View.GONE  // Hide loading spinner
        val intent = Intent(this, Registration_form::class.java)
        intent.putExtra("CNIC", cnic)  // Pass CNIC to the registration form
        startActivity(intent)
        finish()
    }

    private fun handleDatabaseError(error: DatabaseError) {
        binding.progressBar.visibility = View.GONE  // Hide loading spinner
        Toast.makeText(this, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
    }
}
