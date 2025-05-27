package com.example.hotelbookingapp1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeFragment : Fragment() {

    private lateinit var rvNearLocation: RecyclerView
    private lateinit var rvPopularHotel: RecyclerView

    // Tab TextViews
    private lateinit var tabHotel: TextView
    private lateinit var tabHomestay: TextView
    private lateinit var tabApart: TextView

    private lateinit var nearLocationAdapter: NearLocationAdapter
    private lateinit var popularHotelAdapter: PopularHotelAdapter

    // Current selected tab (0 = Hotel, 1 = Homestay, 2 = Apart)
    private var selectedTab = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupTabs()
        setupRecyclerViews()
    }

    override fun onResume() {
        super.onResume()
        // Refresh adapters when fragment becomes visible to update favorite states
        nearLocationAdapter.notifyDataSetChanged()
        popularHotelAdapter.notifyDataSetChanged()
    }

    private fun initViews(view: View) {
        rvNearLocation = view.findViewById(R.id.rvNearLocation)
        rvPopularHotel = view.findViewById(R.id.rvPopularHotel)

        // Initialize tab views - you'll need to add IDs to your XML
        tabHotel = view.findViewById(R.id.tabHotel)
        tabHomestay = view.findViewById(R.id.tabHomestay)
        tabApart = view.findViewById(R.id.tabApart)
    }

    private fun setupTabs() {
        // Set initial tab selection
        updateTabSelection(0)

        // Set click listeners for tabs
        tabHotel.setOnClickListener {
            selectTab(0)
        }

        tabHomestay.setOnClickListener {
            selectTab(1)
        }

        tabApart.setOnClickListener {
            selectTab(2)
        }
    }

    private fun selectTab(tabIndex: Int) {
        if (selectedTab != tabIndex) {
            selectedTab = tabIndex
            updateTabSelection(tabIndex)
            updateContent(tabIndex)
        }
    }

    private fun updateTabSelection(selectedIndex: Int) {
        val tabs = listOf(tabHotel, tabHomestay, tabApart)

        tabs.forEachIndexed { index, tab ->
            if (index == selectedIndex) {
                // Selected tab style
                tab.setBackgroundResource(R.drawable.tab_selected_bg)
                tab.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
            } else {
                // Unselected tab style
                tab.setBackgroundResource(R.drawable.tab_unselected_bg)
                tab.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_text))
            }
        }
    }

    private fun updateContent(tabIndex: Int) {
        // Update the content based on selected tab
        when (tabIndex) {
            0 -> {
                // Hotel tab - show hotel data
                nearLocationAdapter.updateData(HotelDataManager.getNearLocationHotels().toMutableList())
                popularHotelAdapter.updateData(HotelDataManager.getPopularHotels().toMutableList())
            }
            1 -> {
                // Homestay tab - show homestay data (you'll need to implement this)
                nearLocationAdapter.updateData(HotelDataManager.getNearLocationHomestays().toMutableList())
                popularHotelAdapter.updateData(HotelDataManager.getPopularHomestays().toMutableList())
            }
            2 -> {
                // Apart tab - show apartment data (you'll need to implement this)
                nearLocationAdapter.updateData(HotelDataManager.getNearLocationApartments().toMutableList())
                popularHotelAdapter.updateData(HotelDataManager.getPopularApartments().toMutableList())
            }
        }
    }

    private fun setupRecyclerViews() {
        // Setup Near Location RecyclerView (Horizontal)
        nearLocationAdapter = NearLocationAdapter(HotelDataManager.getNearLocationHotels().toMutableList())
        nearLocationAdapter.setFragment(this) // Set fragment reference for favorites

        val nearLocationLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rvNearLocation.layoutManager = nearLocationLayoutManager
        rvNearLocation.adapter = nearLocationAdapter

        // Setup Popular Hotel RecyclerView (Vertical)
        popularHotelAdapter = PopularHotelAdapter(HotelDataManager.getPopularHotels().toMutableList())
        popularHotelAdapter.setFragment(this) // Set fragment reference for favorites

        val popularHotelLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rvPopularHotel.layoutManager = popularHotelLayoutManager
        rvPopularHotel.adapter = popularHotelAdapter
    }
}