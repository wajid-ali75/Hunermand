package com.collage.hunermandandriodapp

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.collage.hunermandandriodapp.databinding.FragmentPostJobBinding
import com.google.firebase.database.FirebaseDatabase

class PostJobFragment : Fragment(R.layout.fragment_post_job) {
    private lateinit var binding: FragmentPostJobBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostJobBinding.inflate(inflater, container, false)


        // Populate job title suggestions
        val jobTitles = arrayOf("Driver", "Electrician", "Painter", "Cook", "Plumber", "Salesman", "Office Boy",
            "Carpenter", "Software Engineer", "Graphic Designer", "Data Analyst",
            "Marketing Specialist", "Product Manager" , "Tailor")
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            jobTitles
        )
        binding.edJobtitle.setAdapter(adapter)

        // Suggestion list for Gender

        val Gendre = arrayOf("Male", "Female", "Other",)

        val genderAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            Gendre
        )
        binding.edGender.setAdapter(genderAdapter)
        binding.edGender.setOnClickListener {
            binding.edGender.showDropDown()
        }
        binding.edGender.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.edGender.showDropDown()
            }
        }


        // Suggestion list for jobtype
        val jobTypes = arrayOf(
            "Full-Time",
            "Part-Time",
            "Contract",
            "Freelance",
            "Temporary",
            "Internship"
        )

        val jobTypeAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            jobTypes
        )
        binding.edJobtype.setAdapter(jobTypeAdapter)
        binding.edJobtype.setOnClickListener {
            binding.edJobtype.showDropDown()
        }
        binding.edJobtype.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.edJobtype.showDropDown()
            }
        }


        // Retrieve CNIC from SharedPreferences
        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val cnic = sharedPreferences.getString("cnic", null)

        binding.btnPostjob.setOnClickListener {
            val jobtitle = binding.edJobtitle.text.toString()
            val jobtype = binding.edJobtype.text.toString()
            val experience = binding.edExperience.text.toString()
            val gender = binding.edGender.text.toString()
            val sallry = binding.edSallry.text.toString()
            val education = binding.edEducation.text.toString()
            val contactPersonName = binding.edContactPersonName.text.toString()
            val phonNo = binding.edPhonNo.text.toString()
            val description = binding.edDescription.text.toString()
            val location = binding.edLocation.text.toString()

            // Contact number validation
            if (!isValidContactNumber(phonNo)) {
                binding.edPhonNo.error = "Invalid contact number format (e.g., 03XXXXXXXXX)"
                return@setOnClickListener
            }

            if (jobtitle.isNotEmpty() && jobtype.isNotEmpty() &&
                experience.isNotEmpty() && gender.isNotEmpty() && sallry.isNotEmpty() &&
                education.isNotEmpty() && contactPersonName.isNotEmpty() && phonNo.isNotEmpty() &&
                description.isNotEmpty() && cnic != null
            ) {
                binding.progressBar.visibility = View.VISIBLE

                // Include CNIC in the job data
                val database = FirebaseDatabase.getInstance().getReference("jobs")
                val jobData = PostJobDataclass(
                    jobtitle, jobtype, experience, gender, sallry,
                    education, contactPersonName, phonNo, description, location, cnic
                )

                // Use push() to generate a unique key
                val uniqueKey = database.push().key
                if (uniqueKey != null) {
                database.child(uniqueKey).setValue(jobData)
                    .addOnSuccessListener {
                        (activity as? Experts)?.loadFragment(HomeFragment())
                        (activity as? Experts)?.getBottomNavigationView()?.selectedItemId = R.id.home
                        Toast.makeText(requireContext(), "Successfully saved", Toast.LENGTH_LONG).show()
                        binding.progressBar.visibility = View.GONE
                    }

                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Failed to insert data", Toast.LENGTH_LONG).show()
                        binding.progressBar.visibility = View.GONE
                    }

                } else {
                    Toast.makeText(requireContext(), "Error generating unique key", Toast.LENGTH_LONG).show()
                    binding.progressBar.visibility = View.GONE
                }

            } else {
                if (cnic == null) Toast.makeText(requireContext(), "CNIC not found, please log in again", Toast.LENGTH_SHORT).show()
                if (jobtitle.isEmpty()) binding.edJobtitle.error = "Please enter job title"
                if (jobtype.isEmpty()) binding.edJobtype.error = "Please enter job type"
                if (experience.isEmpty()) binding.edExperience.error = "Please enter experience"
                if (gender.isEmpty()) binding.edGender.error = "Please enter gender"
                if (sallry.isEmpty()) binding.edSallry.error = "Please enter salary"
                if (education.isEmpty()) binding.edEducation.error = "Please enter education"
                if (contactPersonName.isEmpty()) binding.edContactPersonName.error = "Please enter contact person name"
                if (phonNo.isEmpty()) binding.edPhonNo.error = "Please enter phone number"
                if (description.isEmpty()) binding.edDescription.error = "Please enter description"
                if (description.isEmpty()) binding.edLocation.error = "Please enter lucation"
            }
        }

        return binding.root
    }

    private fun isValidContactNumber(contactNumber: String): Boolean {
        val contactNumberRegex = "^03\\d{9}$"
        return contactNumber.matches(contactNumberRegex.toRegex())
    }
}
