    package com.example.myapplication.bottomnavigation

    import android.content.Context
    import android.os.Bundle
    import androidx.fragment.app.Fragment
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.Toast
    import androidx.recyclerview.widget.LinearLayoutManager
    import androidx.recyclerview.widget.RecyclerView
    import com.example.myapplication.R
    import com.example.myapplication.Schema.Post
    import com.example.myapplication.Schema.User
    import com.example.myapplication.adapters.PostAdapter
    import com.example.myapplication.databinding.FragmentHomeBinding
    import com.example.myapplication.databinding.FragmentProfileViewerBinding
    import com.google.android.gms.tasks.Tasks
    import com.google.firebase.Timestamp
    import com.google.firebase.firestore.DocumentSnapshot
    import com.google.firebase.firestore.FirebaseFirestore
    import com.google.firebase.firestore.ktx.toObject
    import kotlinx.coroutines.Dispatchers
    import kotlinx.coroutines.GlobalScope
    import kotlinx.coroutines.Job
    import kotlinx.coroutines.delay
    import kotlinx.coroutines.joinAll
    import kotlinx.coroutines.launch
    import kotlinx.coroutines.runBlocking
    import kotlinx.coroutines.tasks.await
    import java.util.Collections
    import java.util.Comparator
    import kotlin.coroutines.resume
    import kotlin.coroutines.resumeWithException
    import kotlin.coroutines.suspendCoroutine

    //private lateinit var posts:ArrayList<Post>
    var username:String = "";
    class HomeFragment : Fragment() {
        private val db = FirebaseFirestore.getInstance()
        var following=ArrayList<String>()
        lateinit var posts:ArrayList<Post>
        private lateinit var recyclerView: RecyclerView
        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            // Inflate the layout for this fragment

            var binding = FragmentHomeBinding.inflate(inflater, container, false)
            val view=binding.root
            val pref=requireActivity().getSharedPreferences("LOG_IN", Context.MODE_PRIVATE)
            val username=pref.getString("emailOrUsername", "")
            posts = ArrayList()
            recyclerView = binding.postRecycler
            recyclerView.layoutManager=LinearLayoutManager(this.requireContext())

    // Somewhere in your code
            GlobalScope.launch(Dispatchers.Main) {
                try {
                    val following = username?.let { fetchUsers(it) }
                    println(following)
                    if (following != null) {
                        fetchPosts(following)
                    }

                    // Now you have the fetched data in the 'posts' list
                    // You can continue with your code here
                    posts.sortWith(Comparator { post1, post2 ->
                        post2.timestamp?.compareTo(post1.timestamp ?: Timestamp(0, 0)) ?: 0
                    })

                    println(posts)
                    recyclerView.adapter = PostAdapter(posts)
                } catch (e: Exception) {
                    // Handle exceptions here
                    println("An error occurred: ${e.message}")
                }
            }


            return binding.root
        }

        private suspend fun fetchUsers(username: String): List<String> {
            val querySnapshot = db.collection("users")
                .whereEqualTo("username", username)
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                return querySnapshot.documents[0].toObject<User>()?.following ?: emptyList()
            }

            return emptyList()
        }

        private suspend fun fetchPosts(following: List<String>) {
             // Initialize the posts list

            if (following.isNotEmpty()) {
                val fetchJobs = ArrayList<Job>()

                for (username in following) {
                    val fetchJob = GlobalScope.launch(Dispatchers.Default) {
                        val querySnapshot = db.collection("posts")
                            .whereEqualTo("username", username)
                            .get()
                            .await()

                        if (!querySnapshot.isEmpty) {
                            for (document in querySnapshot.documents) {
                                val post = document.toObject<Post>()
                                post?.let { posts.add(it) }
                            }
                        }
                    }
                    fetchJobs.add(fetchJob)
                }

                fetchJobs.joinAll() // Wait for all fetch jobs to complete
            }
        }

    }
