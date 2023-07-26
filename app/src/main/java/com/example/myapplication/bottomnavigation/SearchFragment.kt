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

    private val db = FirebaseFirestore.getInstance()
    private lateinit var recyclerView: RecyclerView
    private lateinit var list: ArrayList<User>
    private lateinit var searchAdapter: SearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSearchBinding.inflate(inflater, container, false)
        val view = binding.root

        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        list = ArrayList()
        searchAdapter = SearchAdapter(list)
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
        recyclerView.adapter = searchAdapter

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { searchProcess(it.trim()) }
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                searchProcess(newText.trim())
                return true
            }
        })

        return view
    }

    private fun searchProcess(query: String) {
        list.clear()
        db.collection("users")
            .whereGreaterThanOrEqualTo("searchByUsername", query)
            .whereLessThanOrEqualTo("searchByUsername", query + "\uf8ff")
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    for (user in querySnapshot) {
                        val filteredUser = user.toObject<User>()
                        list.add(filteredUser)
                    }
                } else {
                    println("Not found")
                }
                searchAdapter.notifyDataSetChanged()
            }
    }
}
