package com.collage.hunermandandriodapp

import Expert_Dataclass
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.collage.hunermandandriodapp.databinding.FragmentExpertsBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ExpertsPragment : Fragment(R.layout.fragment_experts) {

    private lateinit var binding: FragmentExpertsBinding
    private lateinit var database: DatabaseReference
    private lateinit var expertList: ArrayList<Expert_Dataclass>
    private lateinit var filteredList: ArrayList<Expert_Dataclass>
    private lateinit var adapter: ExpertAdapter
    private var searchQuery: String? = null
    private var focusSearch: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate layout and initialize view binding
        binding = FragmentExpertsBinding.inflate(inflater, container, false)

        // Set up toolbar buttons
        binding.topToolbar.findViewById<Button>(R.id.btn_postjob).setOnClickListener {
            // Navigate to PostJobFragment and update BottomNavigationView selection
            (activity as? Experts)?.loadFragment(PostJobFragment())
            (activity as? Experts)?.getBottomNavigationView()?.selectedItemId = R.id.postJob
        }
        binding.topToolbar.findViewById<LinearLayout>(R.id.btnBack).setOnClickListener {
            (activity as? Experts)?.getBottomNavigationView()?.selectedItemId = R.id.home
        }



        // Initialize Firebase database reference
        database = FirebaseDatabase.getInstance().getReference("ExpertsUser")
        expertList = ArrayList()
        filteredList = ArrayList()
        adapter = ExpertAdapter(filteredList)

        // Set up the RecyclerView
        binding.expertRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.expertRecyclerView.adapter = adapter


        arguments?.let {
            searchQuery = it.getString("searchQuery")
            focusSearch = it.getBoolean("focusSearch", false)
        }

        // Handle the focus flag for the search bar
        if (focusSearch) {
            binding.expertSearchbar.requestFocus()
            binding.expertSearchbar.post {
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
                imm.showSoftInput(binding.expertSearchbar, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT)
            }
        }
        // Set the retrieved searchQuery in the SearchView
        if (!searchQuery.isNullOrEmpty()) {
            binding.expertSearchbar.setQuery(searchQuery, false) // Set the text without submitting
        }

        // Set up search bar for dynamic filtering
        binding.expertSearchbar.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterExperts(newText)
                return true
            }
        })

        fetchDataFromFirebase()

        return binding.root


    }


    private fun fetchDataFromFirebase() {
        binding.progressBar.visibility = View.VISIBLE

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                expertList.clear()
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(Expert_Dataclass::class.java)
                    if (user != null) {
                        expertList.add(user)
                    }
                }

                filteredList.clear()
                if (!searchQuery.isNullOrEmpty()) {
                    filteredList.addAll(expertList.filter {
                        it.skills?.lowercase()?.contains(searchQuery!!.lowercase()) == true
                    })
                } else {
                    filteredList.addAll(expertList)
                }

                adapter.notifyDataSetChanged()
                binding.progressBar.visibility = View.GONE

                // Show a message if no experts match the search query
                if (filteredList.isEmpty()) {
                    showNoResultsDialog(searchQuery)
                } else {
                    dismissNoResultsDialog()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to load data.", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
            }
        })
    }


    private fun filterExperts(query: String?) {
        filteredList.clear()
        if (!query.isNullOrEmpty()) {
            val searchQuery = query.lowercase()
            println("DEBUG: Filtering experts with query: $searchQuery")
            filteredList.addAll(expertList.filter {
                it.skills?.lowercase()?.contains(searchQuery) == true
            })
        } else {
            filteredList.addAll(expertList)
        }

        // Check if filtered list is empty and show message
        if (filteredList.isEmpty()) {
            Toast.makeText(requireContext(), "No experts found for '$query'", Toast.LENGTH_SHORT).show()
        }

        println("DEBUG: Filtered list size: ${filteredList.size}")
        adapter.notifyDataSetChanged()
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
        binding.expertSearchbar.setQuery("", false) // Clear the search bar text
    }

    private fun dismissNoResultsDialog() {
        // Dismiss the dialog if it's showing
        noResultsDialog?.dismiss()
        noResultsDialog = null
    }


    // Function to load the Post Job Fragment
    private fun loadPostJobFragment() {
        if (activity is Experts) {
            (activity as Experts).loadFragment(PostJobFragment())
        }
    }
}
