package com.example.myapplication.bottomnavigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.Schema.User
import com.example.myapplication.adapters.SearchAdapter
import com.example.myapplication.databinding.FragmentSearchBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class SearchFragment : Fragment() {

    // Initialize the Firestore instance
    private val db = FirebaseFirestore.getInstance()

    // Declare variables
    private lateinit var recyclerView: RecyclerView
    private lateinit var list: ArrayList<User>
    private lateinit var searchAdapter: SearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout using the data binding
        val binding = FragmentSearchBinding.inflate(inflater, container, false)
        val view = binding.root

        // Initialize the RecyclerView
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialize the list and the adapter
        list = ArrayList()
        searchAdapter = SearchAdapter(list)

        // Set up item click listener for the adapter
        searchAdapter.onItemClick = { user ->
            Toast.makeText(context, "Clicked ${user.firstname}", Toast.LENGTH_SHORT).show()
            val fragment = user.username?.let { ProfileViewerFragment.newInstance(it) }
            if (fragment != null) {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        }

        // Attach the adapter to the RecyclerView
        recyclerView.adapter = searchAdapter

        // Set up the query text listener for the SearchView
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Clear the list and perform search on query submission
                list.clear()
                query?.let { searchProcess(it.trim()) }
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                // Clear the list and perform search on query text change
                list.clear()
                searchProcess(newText.trim())
                return true
            }
        })

        return view
    }

    private fun searchProcess(query: String) {
        list.clear() // Clear the list before adding new search results

        // Perform Firestore query
        db.collection("users")
            .whereGreaterThanOrEqualTo("searchByUsername", query)
            .whereLessThanOrEqualTo("searchByUsername", query + "\uf8ff")
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    for (user in querySnapshot) {
                        val filteredUser = user.toObject<User>()
                        if (!list.contains(filteredUser)) { // Avoid duplicates
                            list.add(filteredUser)
                        }
                    }
                } else {
                    println("Not found")
                    list.clear()
                }
                searchAdapter.notifyDataSetChanged() // Update the adapter
            }
    }
}
