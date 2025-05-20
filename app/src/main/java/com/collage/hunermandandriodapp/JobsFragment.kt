package com.collage.hunermandandriodapp

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.collage.hunermandandriodapp.databinding.FragmentExpertsBinding
import com.collage.hunermandandriodapp.databinding.FragmentJobsBinding
import com.collage.hunermandandriodapp.databinding.FragmentPostJobBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class JobsFragment : Fragment() {
    private lateinit var binding: FragmentJobsBinding
    private lateinit var database: DatabaseReference
    private lateinit var JobsList: ArrayList<Jobs_Dataclass>
    private lateinit var filteredList: ArrayList<Jobs_Dataclass>
    private lateinit var jobsAdapter: JobsAdapter
    private var searchQuery: String? = null
    private var focusSearch: Boolean = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentJobsBinding.inflate(inflater, container, false)

        binding.topToolbar.findViewById<LinearLayout>(R.id.btnBack).setOnClickListener {
            (activity as? Jobs)?.getBottomNavigationView()?.selectedItemId = R.id.home
        }

        // Initialize Firebase database reference
        database = FirebaseDatabase.getInstance().getReference("jobs")
        JobsList = ArrayList()
        filteredList = ArrayList()
        jobsAdapter = JobsAdapter(filteredList, isProfileFragment = false)

        // Set up RecyclerView
        binding.jobRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.jobRecyclerView.adapter = jobsAdapter


        arguments?.let {
            searchQuery = it.getString("searchQuery")
            focusSearch = it.getBoolean("focusSearch", false)
        }

        // Handle the focus flag for the search bar
        if (focusSearch) {
            binding.jobSearchbar.requestFocus()
            binding.jobSearchbar.post {
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
                imm.showSoftInput(binding.jobSearchbar, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT)
            }
        }

        // Set the retrieved searchQuery in the SearchView
        if (!searchQuery.isNullOrEmpty()) {
            binding.jobSearchbar.setQuery(searchQuery, false) // Set the text without submitting
        }
        binding.jobSearchbar.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false // We handle changes in `onQueryTextChange`.
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterExperts(newText)
                return true
            }
        })

        // Fetch data from Firebase
        fetchDataFromFirebase()
        return binding.root
      
    }


    // this code method for searching expert
    private fun filterExperts(query: String?) {
        filteredList.clear()
        if (!query.isNullOrEmpty()) {
            val searchQuery = query.lowercase()
            filteredList.addAll(JobsList.filter {
                it.jobtitle?.lowercase()?.contains(searchQuery) == true
            })
        } else {
            // If query is empty, reset to full list
            filteredList.addAll(JobsList)
        }
        jobsAdapter.notifyDataSetChanged()
    }

    private fun fetchDataFromFirebase() {

        // Show ProgressBar while loading data
        binding.progressBar.visibility = View.VISIBLE
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Clear previous list if needed
                JobsList.clear()
                // Loop through all children in the snapshot
                for (userSnapshot in snapshot.children) {
                    // Convert each child snapshot to User object
                    val user = userSnapshot.getValue(Jobs_Dataclass::class.java)
                    if (user != null) {
                        JobsList.add(user)
                    }
                }

                filteredList.clear()
                if (!searchQuery.isNullOrEmpty()) {
                    filteredList.addAll(JobsList.filter {
                        it.jobtitle?.lowercase()?.contains(searchQuery!!.lowercase()) == true
                    })
                } else {
                    filteredList.addAll(JobsList)
                }

                jobsAdapter.notifyDataSetChanged()
                binding.progressBar.visibility = View.GONE

                // Check if filtered list is empty and show custom dialog
                if (filteredList.isEmpty()) {
                    showNoResultsDialog(searchQuery)
                } else {
                    // Close the dialog if no results are found and results appear
                    dismissNoResultsDialog()
                }

            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors here
                Toast.makeText(requireContext(), "Failed to load data.", Toast.LENGTH_SHORT).show()
                // Hide ProgressBar on error
                binding.progressBar.visibility = View.GONE
            }
        })
    }


    // this method is for show a dilog message
    private var noResultsDialog: Dialog? = null

    private fun showNoResultsDialog(query: String?) {
        // Show the dialog if it's not already shown
        if (noResultsDialog == null) {
            val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_no_results, null)
            val dialogMessage = dialogView.findViewById<TextView>(R.id.message_text)
            dialogMessage.text = "No experts found for '$query'"  // Set the message dynamically

            // Find and set the click listener for the close icon
            val closeIcon = dialogView.findViewById<TextView>(R.id.close_icon)
            closeIcon.setOnClickListener {
                dismissNoResultsDialog()  // Dismiss the dialog when the close icon is clicked
            }

            noResultsDialog = AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setCancelable(true)
                .create()

            noResultsDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            noResultsDialog?.show()
        }

        // Clear the query and bundle when no results are found
        arguments?.remove("searchQuery") // Remove query from the bundle
        searchQuery = null               // Clear the local searchQuery
        binding.jobSearchbar.setQuery("", false) // Clear the search bar text
    }

    private fun dismissNoResultsDialog() {
        // Dismiss the dialog if it's showing
        noResultsDialog?.dismiss()
        noResultsDialog = null
    }


}