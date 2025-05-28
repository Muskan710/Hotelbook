package com.example.hotelbookingapp1

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SearchFragment : Fragment() {

    private lateinit var etSearch: EditText
    private lateinit var rvSearchResults: RecyclerView
    private lateinit var llNoResults: LinearLayout
    private lateinit var tvResultsCount: TextView

    private lateinit var searchAdapter: SearchResultsAdapter
    private val allHotels = mutableListOf<Hotel>()
    private val filteredHotels = mutableListOf<Hotel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupData()
        setupRecyclerView()
        setupSearchFunctionality()
    }

    private fun initViews(view: View) {
        etSearch = view.findViewById(R.id.etSearch)
        rvSearchResults = view.findViewById(R.id.rvSearchResults)
        llNoResults = view.findViewById(R.id.llNoResults)

        // Fix the ClassCastException - make sure you're finding the correct TextView
        tvResultsCount = view.findViewById(R.id.tvResultsCount)

        // Add null checks to prevent crashes
        if (!::etSearch.isInitialized) {
            throw IllegalStateException("etSearch not found in layout")
        }
        if (!::rvSearchResults.isInitialized) {
            throw IllegalStateException("rvSearchResults not found in layout")
        }
        if (!::llNoResults.isInitialized) {
            throw IllegalStateException("llNoResults not found in layout")
        }
        if (!::tvResultsCount.isInitialized) {
            throw IllegalStateException("tvResultsCount not found in layout")
        }
    }

    private fun setupData() {
        // Get all hotels from the shared data manager
        allHotels.addAll(HotelDataManager.getAllHotels())

        // Initially show all hotels
        filteredHotels.addAll(allHotels)
        updateResultsCount()
    }

    private fun setupRecyclerView() {
        searchAdapter = SearchResultsAdapter(filteredHotels) { hotel ->
            // Handle hotel click - you can navigate to hotel details
            onHotelClick(hotel)
        }

        // Set the fragment reference for favorites functionality
        searchAdapter.setFragment(this)

        rvSearchResults.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = searchAdapter
        }
    }

    private fun setupSearchFunctionality() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                filterHotels(s.toString().trim())
            }
        })
    }

    private fun filterHotels(query: String) {
        filteredHotels.clear()

        if (query.isEmpty()) {
            filteredHotels.addAll(allHotels)
        } else {
            val searchQuery = query.lowercase()
            filteredHotels.addAll(
                allHotels.filter { hotel ->
                    hotel.name.lowercase().contains(searchQuery) ||
                            hotel.location.lowercase().contains(searchQuery)
                }
            )
        }

        searchAdapter.notifyDataSetChanged()
        updateUI()
    }

    private fun updateUI() {
        if (filteredHotels.isEmpty() && etSearch.text.toString().trim().isNotEmpty()) {
            llNoResults.visibility = View.VISIBLE
            rvSearchResults.visibility = View.GONE
            tvResultsCount.visibility = View.GONE
        } else {
            llNoResults.visibility = View.GONE
            rvSearchResults.visibility = View.VISIBLE
            tvResultsCount.visibility = View.VISIBLE
            updateResultsCount()
        }
    }

    private fun updateResultsCount() {
        val count = filteredHotels.size
        val resultText = if (count == 1) {
            "$count hotel found"
        } else {
            "$count hotels found"
        }
        tvResultsCount.text = resultText

        // Update RecyclerView content description for accessibility
        rvSearchResults.contentDescription = "Search results: $resultText"
    }

    private fun onHotelClick(hotel: Hotel) {
        // Handle hotel click - navigate to hotel details
        val context = requireContext()
        val intent = HotelDetailActivity.createIntent(context, hotel.id)
        context.startActivity(intent)
    }
}