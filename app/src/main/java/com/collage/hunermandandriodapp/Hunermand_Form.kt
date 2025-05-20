package com.collage.hunermandandriodapp
import android.R
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.collage.hunermandandriodapp.databinding.ActivityHunermandFormBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class Hunermand_Form : BackToLoginActivity() {
    lateinit var binding: ActivityHunermandFormBinding
    private var imageUri: Uri? = null
    private val IMAGE_PICK_CODE = 1000
    private val storageReference = FirebaseStorage.getInstance().reference.child("ProfileImages")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHunermandFormBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Populate job title suggestions
        val Skills = arrayOf(
            "Driver",
            "Electrician",
            "Painter",
            "Cook",
            "Plumber",
            "Salesman",
            "Office Boy",
            "Carpenter",
            "Software Engineer",
            "Graphic Designer",
            "Data Analyst",
            "Marketing Specialist",
            "Product Manager",
            "Tailor"
        )

// Set up the adapter
        val adapter = ArrayAdapter(
            this,R.layout.simple_dropdown_item_1line,Skills
        )
        binding.edSkills.setAdapter(adapter)




        binding.ivBack.setOnClickListener(){
            val intent = Intent(this,registration_password::class.java)
            startActivity(intent)
            finish()
        }

        // Set onClickListener for ImageView to pick an image from the device
        binding.ivExpertprofile.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, IMAGE_PICK_CODE)
        }

        binding.btnRegister.setOnClickListener {
            if (validateInputs()) {
                val cnic = intent.getStringExtra("CNIC").toString()
                val name = intent.getStringExtra("Name").toString()
                val email = intent.getStringExtra("Email").toString()
                val contactNumber = intent.getStringExtra("ContactNumber").toString()
                val gender = intent.getStringExtra("Gender").toString()
                val address = intent.getStringExtra("Address").toString()
                val password = intent.getStringExtra("Password").toString()
                val confirmpassword = intent.getStringExtra("ConfirmPassword").toString()

                val skills = binding.edSkills.text.toString()
                val experience = binding.edExperience.text.toString()
                val certificate = binding.edCertificate.text.toString()
                val description = binding.edDescription.text.toString()

                if (imageUri != null) {
                    uploadImageToFirebase(cnic, name, email, contactNumber, gender, address, password, confirmpassword, skills, experience, certificate,description)


                    val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("cnic", cnic)
                    editor.apply()


                }
                else {
                    Toast.makeText(this, "Please select a profile image", Toast.LENGTH_LONG).show()
                }
            }



        }
    }

    // Image selection result handling
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data
            binding.ivExpertprofile.setImageURI(imageUri)
        }
    }

    // Method to upload the selected image to Firebase Storage
    private fun uploadImageToFirebase(
        cnic: String, name: String, email: String, contactNumber: String, gender: String, address: String, password: String,
        confirmpassword: String, skills: String, experience: String, certificate: String, description: String
    ) {

        binding.progressBar.visibility = View.VISIBLE

        val fileName = UUID.randomUUID().toString() // Generate a unique filename
        val imageRef = storageReference.child("$name/$fileName") // Create a reference for the image

        imageUri?.let { uri ->
            imageRef.putFile(uri) // Upload the image
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { imageUrl ->
                        // After successful upload, save user data to the database
                        saveUserToDatabase(cnic, name, email, contactNumber, gender, address, password, confirmpassword, skills, experience, certificate, description, imageUrl.toString())

                        binding.progressBar.visibility = View.GONE

                        val intent = Intent(this,Jobs::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to upload image", Toast.LENGTH_LONG).show()
                    binding.progressBar.visibility = View.GONE
                }
        }
    }

    // Method to save user data to Firebase Realtime Database
    private fun saveUserToDatabase(
        cnic: String, name: String, email: String, contactNumber: String, gender: String, address: String, password: String,
        confirmpassword: String, skills: String, experience: String, certificate: String, description: String, imageUrl: String
    ) {
        val database = FirebaseDatabase.getInstance().getReference("ExpertsUser")
        val user = ExpertUserDataclass(cnic, name, email, contactNumber, gender, address, password,confirmpassword, skills, experience, certificate, description, imageUrl)

        database.child(cnic).setValue(user)
            .addOnSuccessListener {
                Toast.makeText(this, "Data successfully inserted", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to insert data", Toast.LENGTH_LONG).show()
            }
    }

    // Validation method to check if all required fields are filled
    private fun validateInputs(): Boolean {
        val skills = binding.edSkills.text.toString().trim()
        val experience = binding.edExperience.text.toString().trim()
        val certificate = binding.edCertificate.text.toString().trim()
        val description = binding.edDescription.text.toString().trim()

        return when {
            skills.isEmpty() -> {
                Toast.makeText(this, "Skills field cannot be empty", Toast.LENGTH_SHORT).show()
                false
            }
            experience.isEmpty() -> {
                Toast.makeText(this, "Experience field cannot be empty", Toast.LENGTH_SHORT).show()
                false
            }
            certificate.isEmpty() -> {
                Toast.makeText(this, "Certificate field cannot be empty", Toast.LENGTH_SHORT).show()
                false
            }

            description.isEmpty() -> {
                Toast.makeText(this, "Description field cannot be empty", Toast.LENGTH_SHORT).show()
                false
            }
            else -> true // All validations passed
        }
    }
}
