package com.collage.hunermandandriodapp

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.collage.hunermandandriodapp.databinding.FragmentProfileBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import java.io.IOException
import  android.view.MenuItem
import androidx.core.content.ContextCompat

class ProfileFragment : Fragment() {

    override fun onResume() {
        super.onResume()
        // Change the status bar color when this fragment is visible
        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.blu_logo2)
    }
    override fun onPause() {
        super.onPause()
        // This will make sure that the status bar color returns to default or previous color when switching fragments
        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.blu_logo)
    }


    private lateinit var binding: FragmentProfileBinding
    private lateinit var sharedPreferences: SharedPreferences
    private val database = FirebaseDatabase.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val PICK_IMAGE_REQUEST = 71
    private var imageUri: Uri? = null
    private lateinit var jobsAdapter: JobsAdapter
    private val JobsList = ArrayList<Jobs_Dataclass>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // Retrieve CNIC from SharedPreferences
        val cnic = sharedPreferences.getString("cnic", null)

        // Ensure CNIC is available
        if (cnic != null) {
            showLoading(true)
            loadUserProfile(cnic)
            loadUserJobs(cnic) // Load only the user's jobs
        } else {
            Toast.makeText(requireContext(), "No CNIC found, please log in again", Toast.LENGTH_SHORT).show()
        }

        // Set up Three-Dot Menu
        binding.ivThreeDotButton.setOnClickListener {
            val popupMenu = PopupMenu(requireContext(), it)
            popupMenu.inflate(R.menu.profile_menu)

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.edite_profil -> {
                        val intent = Intent(requireContext(), EditProfileActivity::class.java)
                        val cnic = sharedPreferences.getString("cnic", null)
                        // Pass the user data (you can pass more fields as required)
                        cnic?.let {
                            intent.putExtra("CNIC", it)
                            startActivity(intent)

                        }
                        true
                    }
                    R.id.log_out -> {
                        // Log Out
                        sharedPreferences.edit().clear().apply()
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        Toast.makeText(requireContext(), "Signed Out", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.delete_account -> {
                        // Delete Account Confirmation Dialog
                        AlertDialog.Builder(requireContext())
                            .setTitle("Delete Account")
                            .setMessage("Are you sure you want to delete your account?")
                            .setPositiveButton("Yes") { _, _ ->
                                val cnic = sharedPreferences.getString("cnic", null)
                                if (cnic != null) {
                                    deleteAccount(cnic)
                                }
                            }
                            .setNegativeButton("No", null)
                            .show()
                        true
                    }
                    else -> false
                }
            }

            popupMenu.show()
        }

        // Set up Profile Image Click Listener
        binding.ivProfileImage.setOnClickListener {
            openImagePicker()
        }

        // Set up RecyclerView for displaying user jobs
        binding.userJobRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        jobsAdapter = JobsAdapter(JobsList, isProfileFragment = true)
        binding.userJobRecyclerView.adapter = jobsAdapter


        binding.refreshButton.setOnClickListener {
            cnic?.let {
                showLoading(true)
                loadUserProfile(it)
                loadUserJobs(it)
            }
        }

        return binding.root


    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }



    //this the code for loading user profile to application
    private fun loadUserProfile(cnic: String) {
        val usersRef = database.getReference("users")
        val expertsUserRef = database.getReference("ExpertsUser")

        // Check in "users" node first
        usersRef.orderByChild("cnic").equalTo(cnic)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        displayUserData(snapshot)
                    } else {
                        // If not found in "users", check in "ExpertsUser" node
                        expertsUserRef.orderByChild("cnic").equalTo(cnic)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.exists()) {
                                        displayUserData(snapshot)
                                    } else {
                                        Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()
                                    }
                                    showLoading(false)
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                                    showLoading(false)
                                }
                            })
                    }
                    showLoading(false)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                    showLoading(false)
                }
            })
    }



    //this the code for displaying user data
    private fun displayUserData(snapshot: DataSnapshot) {
        for (userSnapshot in snapshot.children) {
            val name = userSnapshot.child("name").getValue(String::class.java) ?: "N/A"
            val email = userSnapshot.child("email").getValue(String::class.java) ?: "N/A"
            val contactNumber = userSnapshot.child("ph_no").getValue(String::class.java) ?: "N/A"
            val imageUrl = userSnapshot.child("imageUrl").getValue(String::class.java)

            // Set data to views in the profile fragment
            binding.tvName.text = name
            binding.tvEmail.text = email
            binding.tvPhon.text = contactNumber

            // Load profile image using Picasso if imageUrl is available
            imageUrl?.let {
                Picasso.get().load(it).into(binding.ivProfileImage)
            }
        }
    }



// this the code for loading the login user jobs from database use the cnic
    private fun loadUserJobs(cnic: String) {
        val jobsRef = database.getReference("jobs")

        jobsRef.orderByChild("cnic").equalTo(cnic) // Assuming "postedBy" is the key used to store CNIC
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    JobsList.clear() // Clear the list to avoid duplicates
                    for (jobSnapshot in snapshot.children) {
                        val job = jobSnapshot.getValue(Jobs_Dataclass::class.java)?.copy(key = jobSnapshot.key)
                        job?.let { JobsList.add(it) }
                    }
                    jobsAdapter.notifyDataSetChanged() // Update adapter with the new data
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Failed to load jobs", Toast.LENGTH_SHORT).show()
                }
            })
    }




    //this the function for picking the image from device
    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == -1 && data != null) {
            imageUri = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, imageUri)
                binding.ivProfileImage.setImageBitmap(bitmap)
                showLoading(true)
                uploadProfileImage(imageUri)
            } catch (e: IOException) {
                e.printStackTrace()
                showLoading(false)
            }
        }
    }




    //this the code for uploading the image to database
    private fun uploadProfileImage(uri: Uri?) {
        if (uri == null) return

        val storageRef: StorageReference = storage.reference
        val profileImagesRef = storageRef.child("profile_images/${System.currentTimeMillis()}.jpg")

        val uploadTask = profileImagesRef.putFile(uri)
        uploadTask.addOnSuccessListener {
            profileImagesRef.downloadUrl.addOnSuccessListener { downloadUri ->
                val imageUrl = downloadUri.toString()
                updateProfileImage(imageUrl)
                showLoading(false)
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Image upload failed", Toast.LENGTH_SHORT).show()
            showLoading(false)
        }
    }


    //this the code for updating the image
    private fun updateProfileImage(imageUrl: String) {
        val cnic = sharedPreferences.getString("cnic", null)
        if (cnic != null) {
            val usersRef = database.getReference("users")
            val expertsUserRef = database.getReference("ExpertsUser")

            usersRef.orderByChild("cnic").equalTo(cnic)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            snapshot.children.forEach { userSnapshot ->
                                val userKey = userSnapshot.key
                                usersRef.child(userKey!!).child("imageUrl").setValue(imageUrl)
                                Toast.makeText(requireContext(), "Profile image updated", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            expertsUserRef.orderByChild("cnic").equalTo(cnic)
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        if (snapshot.exists()) {
                                            snapshot.children.forEach { userSnapshot ->
                                                val userKey = userSnapshot.key
                                                expertsUserRef.child(userKey!!).child("imageUrl").setValue(imageUrl)
                                                Toast.makeText(requireContext(), "Profile image updated", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                        showLoading(false)
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                                        showLoading(false)
                                    }
                                })
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                        showLoading(false)
                    }
                })
        }
    }



    private fun deleteAccount(cnic: String) {
        val usersRef = database.getReference("users")
        val expertsUserRef = database.getReference("ExpertsUser")
        val jobsRef = database.getReference("jobs")

        // Delete user's jobs
        jobsRef.orderByChild("cnic").equalTo(cnic)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (jobSnapshot in snapshot.children) {
                        jobSnapshot.ref.removeValue() // Remove each job posted by this user
                    }
                    // After jobs are deleted, proceed to delete the user account
                    deleteUserAccountData(cnic)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    // Helper function to delete user account data
    private fun deleteUserAccountData(cnic: String) {
        val usersRef = database.getReference("users")
        val expertsUserRef = database.getReference("ExpertsUser")

        usersRef.orderByChild("cnic").equalTo(cnic)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        snapshot.children.forEach { userSnapshot ->
                            userSnapshot.ref.removeValue() // Remove user data
                        }
                        onAccountDeleted()
                    } else {
                        // If not found in "users", check "ExpertsUser"
                        expertsUserRef.orderByChild("cnic").equalTo(cnic)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.exists()) {
                                        snapshot.children.forEach { expertSnapshot ->
                                            expertSnapshot.ref.removeValue() // Remove expert data
                                        }
                                        onAccountDeleted()
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                                }
                            })
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun onAccountDeleted() {
        // Clear SharedPreferences
        sharedPreferences.edit().clear().apply()

        // Redirect to login screen
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        Toast.makeText(requireContext(), "Account and jobs deleted", Toast.LENGTH_SHORT).show()
    }




}
