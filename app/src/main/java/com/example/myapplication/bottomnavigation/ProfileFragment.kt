package com.example.myapplication.bottomnavigation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.activities.Authentication
import com.example.myapplication.adapters.PostAdapter
import com.example.myapplication.R
import com.example.myapplication.Schema.Post
import com.example.myapplication.Schema.User
import com.example.myapplication.databinding.FragmentProfileBinding
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase


class ProfileFragment : Fragment() {
private lateinit var recyclerView:RecyclerView
    private lateinit var posts:List<Post>
    private var db= Firebase.firestore
    lateinit var arrayList:ArrayList<Post>
    lateinit var postAdapter:Any
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view= binding.root
        arrayList = ArrayList<Post>()
     binding.button2.setOnClickListener{
            val pref=requireActivity().getSharedPreferences("LOG_IN", Context.MODE_PRIVATE)
            val editor = pref.edit()
            editor.putBoolean("isLoggedIn", false)
            editor.apply()
            val i=Intent(requireContext(), Authentication()::class.java)
            startActivity(i)
        }

        recyclerView=binding.recyclerView
        recyclerView.layoutManager=LinearLayoutManager(this.requireContext())

        val pref=requireActivity().getSharedPreferences("LOG_IN", Context.MODE_PRIVATE)
        val username=pref.getString("emailOrUsername", "")
        fetchInfo(username, binding)
        db=FirebaseFirestore.getInstance()
        val query=db.collection("posts")
            .whereEqualTo("username",username )
            .addSnapshotListener{querySnapshot, e ->
                if (e != null) {
                    Toast.makeText(context, "Failed to listen for updates", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if ((querySnapshot != null) && !querySnapshot.isEmpty) {

                    val document = querySnapshot.documents
                    for(document:DocumentSnapshot in querySnapshot.documents){
                        document.toObject<Post>()?.let { arrayList.add(it) }
                    }

                    val adapter=PostAdapter(arrayList)
                    recyclerView.adapter= adapter
            }
            }



        return view

    }

    private fun fetchInfo(username: String?, binding: FragmentProfileBinding) {
            db.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnSuccessListener { querySnapshot->
                    val doc=querySnapshot.documents[0]
                    val user=doc.toObject<User>()
                    if (user != null) {
                        val followerCount = user.followers?.size?:0
                        binding.followersCount.text = followerCount.toString()

                        val followingCount = user.following?.size ?: 0
                        binding.followingCount.text = followingCount.toString()

                        val postsCount=arrayList.size
                        binding.postsCount.text=postsCount.toString()

                        binding.name.text=user.firstname + " "+user.lastname
                    }
                }

    }


    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.introContainer, fragment)
            .addToBackStack(null)
            .commit()
    }




}