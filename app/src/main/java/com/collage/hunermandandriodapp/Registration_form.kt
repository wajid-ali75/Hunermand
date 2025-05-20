package com.collage.hunermandandriodapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.collage.hunermandandriodapp.databinding.ActivityRegistrationFormBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Registration_form : BackToLoginActivity() {
    lateinit var binding : ActivityRegistrationFormBinding
    lateinit var database : DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ensure the progress bar is hidden initially
        binding.progressBar.visibility = View.GONE

        binding.ivBack.setOnClickListener(){
            val intent = Intent(this,SignIn_cnic::class.java)
            startActivity(intent)
            finish()
        }


        binding.nextbtn.setOnClickListener {
            val cnic = intent.getStringExtra("CNIC").toString()  // Retrieve CNIC from first activity

            if (cnic.isNotEmpty()) {
                val tvname = binding.name.text.toString()
                val tvemail = binding.email.text.toString()
                val tvcontactnumber = binding.contactnumber.text.toString()
                val tvaddress = binding.address.text.toString()

                val selectedGender = when (binding.radioGroupGender.checkedRadioButtonId) {
                    R.id.radioMale -> "Male"
                    R.id.radioFemale -> "Female"
                    R.id.radioOther -> "Other"
                    else -> ""
                }

                // Email validation using regular expression

                if (!isValidEmail(tvemail)) {
                    binding.email.error = "Invalid email format (e.g., example@domain.com)"
                    return@setOnClickListener // Exit the OnClickListener
                }

                // Contact number validation
                if (!isValidContactNumber(tvcontactnumber)) {
                    binding.contactnumber.error = "Invalid contact number format (e.g., 03XXXXXXXXX)"
                    return@setOnClickListener
                }



                if (tvname.isNotEmpty() && tvemail.isNotEmpty() && tvcontactnumber.isNotEmpty() && selectedGender.isNotEmpty() && tvaddress.isNotEmpty()) {

                    binding.progressBar.visibility = View.VISIBLE

                    // Pass all the data to the third activity
                    val intent = Intent(this, registration_password::class.java)
                    intent.putExtra("CNIC", cnic)
                    intent.putExtra("Name", tvname)
                    intent.putExtra("Email", tvemail)
                    intent.putExtra("ContactNumber", tvcontactnumber)
                    intent.putExtra("Gender", selectedGender)
                    intent.putExtra("Address", tvaddress)

                    // Hide the progress bar before starting the new activity
                    binding.progressBar.visibility = View.GONE

                    startActivity(intent)
                    finish()

                } else {
                    // Show errors for empty fields
                    if (tvname.isEmpty()) binding.name.error = "Please enter your name"
                    if (tvemail.isEmpty()) binding.email.error = "Please enter your email"
                    else if (!isValidEmail(tvemail)) { // Show error again if email is invalid
                        binding.email.error = "Invalid email format (e.g., example@domain.com)"
                    }
                    if (tvcontactnumber.isEmpty()) binding.contactnumber.error = "Please enter your contact number"
                    else if (!isValidContactNumber(tvcontactnumber)){
                        binding.email.error = "Invalid phon number (e.g., 03XXXXXXXXX)"
                    }
                    if (selectedGender.isEmpty())   binding.tvGender.error = "Please select your gender"
                    if (tvaddress.isEmpty()) binding.address.error = "Please enter your address"
                }
            } else {
                Toast.makeText(this, "CNIC not found", Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[\\w!#$%&'*+/=?^`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^`{|}~-]+)*@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?$"
        return email.matches(emailRegex.toRegex())
    }

    private fun isValidContactNumber(contactNumber: String): Boolean {
        val contactNumberRegex = "^03\\d{9}$"
        return contactNumber.matches(contactNumberRegex.toRegex())
    }
}