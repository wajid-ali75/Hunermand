package com.collage.hunermandandriodapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.collage.hunermandandriodapp.databinding.ActivitySigninPasswordBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class SignIn_password : BackToLoginActivity() {
    lateinit var binding: ActivitySigninPasswordBinding
    private val database = FirebaseDatabase.getInstance()
    private lateinit var cnic: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener(){
            val intent = Intent(this,SignIn_cnic::class.java)
            startActivity(intent)
            finish()
        }

        FirebaseApp.initializeApp(this)


        // Get CNIC from previous activity
        cnic = intent.getStringExtra("CNIC").toString()

        binding.btnNext.setOnClickListener {
            val enteredPassword = binding.edPassword.text.toString().trim()

            if (enteredPassword.isNotEmpty()) {

                binding.progressBar.visibility = View.VISIBLE

                checkPasswordInDatabase(enteredPassword)
            } else {
                // Show an error if password is empty
                binding.edPassword.error = "Please enter your password"
            }
        }

        // Handle "Forgot Password" button click
        binding.btnForgotPassword.setOnClickListener {
            retrievePassword()
        }
    }

    private fun checkPasswordInDatabase(password: String) {
        val expertsUserRef = database.getReference("ExpertsUser")
        val usersRef = database.getReference("users")

        // Check password in "ExpertsUser" node
        expertsUserRef.orderByChild("cnic").equalTo(cnic)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (userSnapshot in snapshot.children) {
                            val storedPassword = userSnapshot.child("password").getValue(String::class.java)
                            if (storedPassword == password) {
                                navigateToJobs()
                                return
                            } else {
                                showPasswordError()
                                return
                            }
                        }
                    } else {
                        // Check password in "users" node if not found in "ExpertsUser"
                        usersRef.orderByChild("cnic").equalTo(cnic)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.exists()) {
                                        for (userSnapshot in snapshot.children) {
                                            val storedPassword = userSnapshot.child("password").getValue(String::class.java)
                                            if (storedPassword == password) {
                                                navigateToExperts()
                                                return
                                            } else {
                                                showPasswordError()
                                                return
                                            }
                                        }
                                    } else {
                                        showPasswordError()
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    binding.progressBar.visibility = View.GONE
                                    Toast.makeText(this@SignIn_password, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
                                }
                            })
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this@SignIn_password, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }



    private fun retrievePassword() {
        val expertsUserRef = database.getReference("ExpertsUser")
        val usersRef = database.getReference("users")

        binding.progressBar.visibility = View.VISIBLE

        // Check in ExpertsUser first
        expertsUserRef.orderByChild("cnic").equalTo(cnic)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (userSnapshot in snapshot.children) {
                            val email = userSnapshot.child("email").getValue(String::class.java)

                            if (email != null) {
                                // Send password reset email to the user's email
                                sendPasswordToUser(email)
                                binding.progressBar.visibility = View.GONE
                                return
                            } else {
                                Toast.makeText(this@SignIn_password, "Email not found.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {

                        binding.progressBar.visibility = View.VISIBLE

                        // If not found in ExpertsUser, check in users
                        usersRef.orderByChild("cnic").equalTo(cnic)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.exists()) {
                                        for (userSnapshot in snapshot.children) {
                                            val email = userSnapshot.child("email").getValue(String::class.java)

                                            if (email != null) {
                                                // Send password reset email to the user's email
                                                sendPasswordToUser(email)
                                                binding.progressBar.visibility = View.GONE
                                                return
                                            } else {
                                                Toast.makeText(this@SignIn_password, "Email not found.", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    } else {
                                        Toast.makeText(this@SignIn_password, "No account found with this CNIC.", Toast.LENGTH_SHORT).show()
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Toast.makeText(this@SignIn_password, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
                                }
                            })
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@SignIn_password, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun sendPasswordToUser(email: String?) {
        binding.progressBar.visibility = View.VISIBLE
        if (email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            val auth = FirebaseAuth.getInstance()

            // Send the password reset email to the user
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        binding.progressBar.visibility = View.GONE

                        // Email sent successfully
                        Toast.makeText(this, "Password reset email sent to: $email", Toast.LENGTH_LONG).show()
                    } else {
                        // Catching specific exceptions
                        val errorMessage = task.exception?.message ?: "Unknown error"
                        Toast.makeText(this, "Failed to send reset email: $errorMessage", Toast.LENGTH_LONG).show()
                        Log.e("ResetPassword", "Error sending email: $errorMessage")
                    }
                }
        } else {
            // If the email format is incorrect
            Toast.makeText(this, "Error: Invalid email address. Please check the email or contact support.", Toast.LENGTH_SHORT).show()
        }
    }





    private fun navigateToExperts() {

        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("cnic", cnic)
        editor.apply()

        binding.progressBar.visibility = View.GONE
        val intent = Intent(this, Experts::class.java)
        startActivity(intent)
        finish()
    }


    private fun navigateToJobs() {

        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("cnic", cnic)
        editor.apply()

        binding.progressBar.visibility = View.GONE
        val intent = Intent(this, Jobs::class.java)
        startActivity(intent)
        finish()
    }

    private fun showPasswordError() {
        Toast.makeText(this, "Incorrect password. Please try again.", Toast.LENGTH_SHORT).show()
        binding.edPassword.error = "Incorrect password"
    }

}
