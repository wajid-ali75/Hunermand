package com.collage.hunermandandriodapp

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.collage.hunermandandriodapp.databinding.ActivityEditProfileBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// In EditProfileActivity
class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private val database = FirebaseDatabase.getInstance()
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val cnic = intent.getStringExtra("CNIC")
        if (cnic != null) {
            binding.progressBarTop.visibility = View.VISIBLE
            loadUserProfile(cnic) // Load user data from Firebase
        }

        binding.tvSkillUpdate.setOnClickListener(){
            // Check current visibility status of one of the fields
            if (binding.skills.visibility == View.VISIBLE) {
                // Make fields invisible
                binding.skills.visibility = View.GONE
                binding.experience.visibility = View.GONE
                binding.certificate.visibility = View.GONE
                binding.description.visibility = View.GONE
                rotateIcon(binding.ivRotateIcon, 180f, 0f)
            } else {
                // Make fields visible
                binding.skills.visibility = View.VISIBLE
                binding.experience.visibility = View.VISIBLE
                binding.certificate.visibility = View.VISIBLE
                binding.description.visibility = View.VISIBLE
                rotateIcon(binding.ivRotateIcon, 0f, 180f)
            }
        }

        binding.btnSaveChanges.setOnClickListener {
            val name = binding.edName.text.toString()
            val email = binding.email.text.toString()
            val contact = binding.contactnumber.text.toString()
            val address = binding.address.text.toString()
            val gender = binding.gender.text.toString()
            val skill = binding.skills.text.toString()
            val experience = binding.experience.text.toString()
            val certificate = binding.certificate.text.toString()
            val description = binding.description.text.toString()

            // Email and contact number validation
            if (!isValidEmail(email)) {
                binding.email.error = "Invalid email format (e.g., example@domain.com)"
                return@setOnClickListener
            }
            if (!isValidContactNumber(contact)) {
                binding.contactnumber.error = "Invalid contact number format (e.g., 03XXXXXXXXX)"
                return@setOnClickListener
            }

            if (name.isNotEmpty() && email.isNotEmpty() && contact.isNotEmpty() && address.isNotEmpty() && gender.isNotEmpty()) {
                updateUserProfile(cnic, name, email, contact, address, gender,skill, experience,certificate,description )
            } else {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            }
        }

        binding.ivLogOut.setOnClickListener(){

            sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            sharedPreferences.edit().clear().apply()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            Toast.makeText(this, "Signed Out", Toast.LENGTH_SHORT).show()
            true
        }

    }

    private fun loadUserProfile(cnic: String) {
        val usersRef = database.getReference("users")
        val expertsUserRef = database.getReference("ExpertsUser")

        usersRef.orderByChild("cnic").equalTo(cnic).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val name = userSnapshot.child("name").getValue(String::class.java) ?: ""
                        val email = userSnapshot.child("email").getValue(String::class.java) ?: ""
                        val contact = userSnapshot.child("ph_no").getValue(String::class.java) ?: ""
                        val address = userSnapshot.child("address").getValue(String::class.java) ?: ""
                        val gender = userSnapshot.child("gender").getValue(String::class.java)?:""

                        // Set current data in EditTexts
                        binding.edName.setText(name)
                        binding.email.setText(email)
                        binding.contactnumber.setText(contact)
                        binding.address.setText(address)
                        binding.gender.setText(gender)

                        // Hide Expert-only fields
                        binding.skills.visibility = View.GONE
                        binding.experience.visibility = View.GONE
                        binding.certificate.visibility = View.GONE
                        binding.description.visibility = View.GONE

                        binding.progressBarTop.visibility = View.GONE
                    }
                } else {
                    expertsUserRef.orderByChild("cnic").equalTo(cnic).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {

                            if (snapshot.exists()) {
                                for (userSnapshot in snapshot.children) {
                                    val name = userSnapshot.child("name").getValue(String::class.java) ?: ""
                                    val email = userSnapshot.child("email").getValue(String::class.java) ?: ""
                                    val contact = userSnapshot.child("ph_no").getValue(String::class.java) ?: ""
                                    val address = userSnapshot.child("address").getValue(String::class.java) ?: ""
                                    val gender = userSnapshot.child("gender").getValue(String::class.java) ?: ""

                                    val skill = userSnapshot.child("skills").getValue(String::class.java) ?: ""
                                    val experience = userSnapshot.child("experience").getValue(String::class.java) ?: ""
                                    val certificate = userSnapshot.child("certificate").getValue(String::class.java) ?: ""
                                    val description = userSnapshot.child("description").getValue(String::class.java) ?: ""


                                    // Set current data in EditTexts
                                    binding.edName.setText(name)
                                    binding.email.setText(email)
                                    binding.contactnumber.setText(contact)
                                    binding.address.setText(address)
                                    binding.gender.setText(gender)
                                    binding.skills.setText(skill) // Add these fields in your layout
                                    binding.experience.setText(experience)
                                    binding.certificate.setText(certificate)
                                    binding.description.setText(description)

                                    binding.tvSkillUpdate.visibility = View.VISIBLE


                                    binding.progressBarTop.visibility = View.GONE
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(this@EditProfileActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@EditProfileActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateUserProfile(cnic: String?, name: String, email: String, contact:
    String, address: String, gender:String, skill:String?, experience: String?, certificate:String?,description:String?) {

        binding.progressBar.visibility = View.VISIBLE

        if (cnic != null) {
            val usersRef = database.getReference("users")
            val expertsUserRef = database.getReference("ExpertsUser")

            usersRef.orderByChild("cnic").equalTo(cnic).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {



                    if (snapshot.exists()) {
                        for (userSnapshot in snapshot.children) {
                            val userKey = userSnapshot.key
                            usersRef.child(userKey!!).child("name").setValue(name)
                            usersRef.child(userKey).child("email").setValue(email)
                            usersRef.child(userKey).child("ph_no").setValue(contact)
                            usersRef.child(userKey).child("address").setValue(address)
                            usersRef.child(userKey).child("gender").setValue(gender)

                            binding.progressBar.visibility = View.GONE

                            Toast.makeText(this@EditProfileActivity, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                            finish() // Close the activity after update
                        }
                    } else {
                        expertsUserRef.orderByChild("cnic").equalTo(cnic).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {

                                binding.progressBar.visibility = View.VISIBLE


                                if (snapshot.exists()) {
                                    for (userSnapshot in snapshot.children) {
                                        val userKey = userSnapshot.key
                                        expertsUserRef.child(userKey!!).child("name").setValue(name)
                                        expertsUserRef.child(userKey).child("email").setValue(email)
                                        expertsUserRef.child(userKey).child("ph_no").setValue(contact)
                                        expertsUserRef.child(userKey).child("address").setValue(address)
                                        expertsUserRef.child(userKey).child("gender").setValue(gender)

                                        expertsUserRef.child(userKey).child("skills").setValue(skill)
                                        expertsUserRef.child(userKey).child("experience").setValue(experience)
                                        expertsUserRef.child(userKey).child("certificate").setValue(certificate)
                                        expertsUserRef.child(userKey).child("description").setValue(description)

                                        binding.progressBar.visibility = View.GONE


                                        Toast.makeText(this@EditProfileActivity, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                                        finish() // Close the activity after update
                                    }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Toast.makeText(this@EditProfileActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                            }
                        })
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@EditProfileActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
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


    // Rotate the expand/collapse icon
    private fun rotateIcon(icon: ImageView, from: Float, to: Float) {
        val rotate = RotateAnimation(
            from, to,
            RotateAnimation.RELATIVE_TO_SELF, 0.5f,
            RotateAnimation.RELATIVE_TO_SELF, 0.5f
        )
        rotate.duration = 300
        rotate.fillAfter = true
        icon.startAnimation(rotate)
    }


}
