package com.collage.hunermandandriodapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.collage.hunermandandriodapp.databinding.ActivityRegistrationPasswordBinding
import com.google.firebase.database.FirebaseDatabase

class registration_password : BackToLoginActivity() {
    lateinit var binding : ActivityRegistrationPasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.ivBack.setOnClickListener(){
            val backintent = Intent(this,Registration_form::class.java)
            startActivity(backintent)
            finish()
        }


        // Retrieve data from the second activity
        val cnic = intent.getStringExtra("CNIC").toString()
        val name = intent.getStringExtra("Name").toString()
        val email = intent.getStringExtra("Email").toString()
        val contactNumber = intent.getStringExtra("ContactNumber").toString()
        val gender = intent.getStringExtra("Gender").toString()
        val address = intent.getStringExtra("Address").toString()

        // Insert data into Firebase on button click
        binding.btnregZaroratmand.setOnClickListener {
            val password = binding.edPassword.text.toString()
            val confirmPassword = binding.edConfermPassword.text.toString()

            // Check if the password fields are filled and match
            if (password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                if (password == confirmPassword) {

                    binding.progressBar.visibility = View.VISIBLE

                    // Firebase Database reference
                    val database = FirebaseDatabase.getInstance().getReference("users")

                    // Create a user object with password (adjust Usersdataclass if needed)
                    val user = Usersdataclass(cnic, name, email, contactNumber, gender, address, password,confirmPassword)

                    // Insert into Firebase
                    database.child(cnic).setValue(user)
                        .addOnSuccessListener {

                            val intent = Intent(this,Experts::class.java)
                            startActivity(intent)
                            finish()

                            val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putString("cnic", cnic)
                            editor.apply()


                            binding.progressBar.visibility = View.GONE
                            // Show success message
                            Toast.makeText(this, "Successfully Saved", Toast.LENGTH_LONG).show()
                        }
                        .addOnFailureListener {
                            // Log and show failure message
                            Toast.makeText(this, "Failed to insert data", Toast.LENGTH_LONG).show()
                            binding.progressBar.visibility = View.GONE
                        }


                } else {
                    // Password and Confirm Password do not match
                    binding.edConfermPassword.error = "Passwords do not match"
                }
            } else {
                // Show error if password fields are empty
                if (password.isEmpty()) binding.edPassword.error = "Please enter a password"
                if (confirmPassword.isEmpty()) binding.edConfermPassword.error = "Please confirm your password"
            }
        }


        binding.btnregHunerband.setOnClickListener {
            val cnic = intent.getStringExtra("CNIC").toString()
            val name = intent.getStringExtra("Name").toString()
            val email = intent.getStringExtra("Email").toString()
            val contactNumber = intent.getStringExtra("ContactNumber").toString()
            val gender = intent.getStringExtra("Gender").toString()
            val address = intent.getStringExtra("Address").toString()

            val password = binding.edPassword.text.toString()
            val confirmPassword = binding.edConfermPassword.text.toString()

            if (password == confirmPassword) {

                binding.progressBar.visibility = View.VISIBLE

                val intent = Intent(this, Hunermand_Form::class.java)
                intent.putExtra("CNIC", cnic)
                intent.putExtra("Name", name)
                intent.putExtra("Email", email)
                intent.putExtra("ContactNumber", contactNumber)
                intent.putExtra("Gender", gender)
                intent.putExtra("Address", address)
                intent.putExtra("Password", password)
                intent.putExtra("ConfirmPassword",confirmPassword)
                startActivity(intent)
                finish()

                binding.progressBar.visibility = View.GONE
            } else {
                binding.edConfermPassword.error = "Passwords do not match"
            }
        }


    }
}



      /*  binding.btnregZaroratmand.setOnClickListener(){
            var intent = Intent(this,Experts::class.java)
            startActivity(intent)
            this.finish()
        }

        binding.btnregHunerband.setOnClickListener(){
            var intent = Intent(this,Hunermand_Form::class.java)
            startActivity(intent)
            this.finish()
        }
        //Back arrow
        binding.ivBack.setOnClickListener {
            var intent = Intent(this,Registration_form::class.java )
            startActivity(intent)
            this.finish()
        }
    }
}*/