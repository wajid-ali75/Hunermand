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
import com.collage.hunermandandriodapp.databinding.FragmentJobsHomeFragementBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class JobsHomeFragement : Fragment() {

    private lateinit var binding : FragmentJobsHomeFragementBinding
    private lateinit var imageSlider: ViewPager2
    private lateinit var sliderIndicator: TabLayout
    private val sliderHandler = Handler(Looper.getMainLooper())
    private var currentPage = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentJobsHomeFragementBinding.inflate(inflater,container,false)
        val rootView = binding.root

        binding.topToolbar.findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }


        binding.expertSearch.setOnClickListener(){
            val jobsFragment = JobsFragment()
            val bundle = Bundle()
            bundle.putBoolean("focusSearch", true) // Add this flag for search focus
            jobsFragment.arguments = bundle
            (activity as? Jobs)?.loadFragment(jobsFragment)
        }


        binding.driverImage.setOnClickListener { navigateToJobsFragment("Driver") }
        binding.electricianImag.setOnClickListener { navigateToJobsFragment("Electrician") }
        binding.painterImage.setOnClickListener { navigateToJobsFragment("Painter") }
        binding.cookImage.setOnClickListener { navigateToJobsFragment("Cook") }
        binding.plumberImage.setOnClickListener { navigateToJobsFragment("Plumber") }
        binding.salesmanImage.setOnClickListener { navigateToJobsFragment("Salesman") }
        binding.officeBoyImage.setOnClickListener { navigateToJobsFragment("Office Boy") }
        binding.carpenterImage.setOnClickListener { navigateToJobsFragment("Carpenter") }
        binding.tailorImage.setOnClickListener { navigateToJobsFragment("Tailor") }


        // Initialize the ViewPager2 and TabLayout
        imageSlider = rootView.findViewById(R.id.imageSlider)
        sliderIndicator = rootView.findViewById(R.id.tabIndicator)

        // List of images (use your drawable resources or replace with URLs if needed)
        val images = listOf(
            R.drawable.electrician_job,
            R.drawable.plumber_job, // Replace with your drawable resources
            R.drawable.tailer_job,
            R.drawable.carpenter_job,
            R.drawable.cook_job,
            R.drawable.shopkeeper_job,
            R.drawable.driver_job,
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


    private fun navigateToJobsFragment(query: String) {
        val jobFragment = JobsFragment()
        val bundle = Bundle()
        bundle.putString("searchQuery", query)
        jobFragment.arguments = bundle
        (activity as? Jobs)?.loadFragment(jobFragment)
    }


}
