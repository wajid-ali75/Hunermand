package com.collage.hunermandandriodapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.collage.hunermandandriodapp.databinding.FragmentHomeBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment : Fragment() {

    private lateinit var binding : FragmentHomeBinding
    private lateinit var imageSlider: ViewPager2
    private lateinit var sliderIndicator: TabLayout
    private val sliderHandler = Handler(Looper.getMainLooper())
    private var currentPage = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentHomeBinding.inflate(inflater,container,false)
        val rootView = binding.root


        // Set up toolbar buttons
        binding.topToolbar.findViewById<Button>(R.id.btn_postjob).setOnClickListener {
            // Navigate to HomeFragment and update BottomNavigationView selection
           // (activity as? Experts)?.loadFragment(HomeFragment())
            (activity as? Experts)?.getBottomNavigationView()?.selectedItemId = R.id.postJob


        }
        binding.topToolbar.findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }


        binding.expertSearch.setOnClickListener {
            val expertsFragment = ExpertsPragment()
            val bundle = Bundle()
            bundle.putBoolean("focusSearch", true) // Add this flag for search focus
            expertsFragment.arguments = bundle
            (activity as? Experts)?.loadFragment(expertsFragment)
        }

        // Image click listeners for search navigation
        binding.driverImage.setOnClickListener { navigateToExpertsFragment("Driver") }
        binding.electricianImage.setOnClickListener { navigateToExpertsFragment("Electrician") }
        binding.painterImage.setOnClickListener { navigateToExpertsFragment("Painter") }
        binding.cookImage.setOnClickListener { navigateToExpertsFragment("Cook") }
        binding.plumberImage.setOnClickListener { navigateToExpertsFragment("Plumber") }
        binding.salesmanImage.setOnClickListener { navigateToExpertsFragment("Salesman") }
        binding.officeBoyImage.setOnClickListener { navigateToExpertsFragment("Office Boy") }
        binding.carpenterImage.setOnClickListener { navigateToExpertsFragment("Carpenter") }
        binding.tailorImage.setOnClickListener { navigateToExpertsFragment("Tailor") }



        // Initialize the ViewPager2 and TabLayout
        imageSlider = rootView.findViewById(R.id.imageSlider)
        sliderIndicator = rootView.findViewById(R.id.tabIndicator)

        // List of images (use your drawable resources or replace with URLs if needed)
        val images = listOf(

            R.drawable.electrician,
            R.drawable.cook,
            R.drawable.salesman,
            R.drawable.carpenter,
            R.drawable.driver, // Replace with your drawable resources
            R.drawable.plumber,

        )

        // Set up the adapter
        val adapter = ImageSliderAdapter(images)
        imageSlider.adapter = adapter

        // Attach TabLayout to ViewPager2
        TabLayoutMediator(sliderIndicator, imageSlider) { _, _ -> }.attach()

        // Customize the dot appearance
        for (i in 0 until sliderIndicator.tabCount) {
            val tab = sliderIndicator.getTabAt(i)
            if (tab != null) {
                val dot = View(requireContext()).apply {
                    layoutParams = LinearLayout.LayoutParams(32, 32).apply {
                        marginEnd = 2 // Add space between dots
                    }
                    setBackgroundResource(R.drawable.tab_indicator_circle)
                }
                tab.customView = dot
            }
        }

        // Start the auto-slide feature
        startAutoSlide(images.size)


        return rootView
    }

    private fun startAutoSlide(totalPages: Int) {
        sliderHandler.postDelayed(object : Runnable {
            override fun run() {
                currentPage = (currentPage + 1) % totalPages
                imageSlider.setCurrentItem(currentPage, true)
                sliderHandler.postDelayed(this, 3000) // Change image every 3 seconds
            }
        }, 3000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Remove callbacks to prevent memory leaks
        sliderHandler.removeCallbacksAndMessages(null)
    }

    // Function to load the Post Job Fragment
    private fun loadPostJobFragment() {
        // Ensure that the activity is an instance of Experts
        if (activity is Experts) {
            (activity as Experts).loadFragment(PostJobFragment())
        }
    }

    private fun navigateToExpertsFragment(query: String) {
        val expertsFragment = ExpertsPragment()
        val bundle = Bundle()
        bundle.putString("searchQuery", query)
        expertsFragment.arguments = bundle
        (activity as? Experts)?.loadFragment(expertsFragment)
    }



}
