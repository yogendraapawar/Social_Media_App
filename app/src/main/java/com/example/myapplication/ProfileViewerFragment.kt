import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Schema.Post
import com.example.myapplication.Schema.User
import com.example.myapplication.adapters.PostAdapter
import com.example.myapplication.databinding.FragmentProfileViewerBinding
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class ProfileViewerFragment : Fragment() {
    private lateinit var myusername: String
    private lateinit var recyclerView: RecyclerView
    private lateinit var targetUsername: String
    private val db: FirebaseFirestore = Firebase.firestore
    private lateinit var binding: FragmentProfileViewerBinding
    private lateinit var postList: ArrayList<Post>

    companion object {
        private const val ARG_USERNAME = "username"
        fun newInstance(username: String): ProfileViewerFragment {
            val fragment = ProfileViewerFragment()
            val args = Bundle()
            args.putString(ARG_USERNAME, username)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileViewerBinding.inflate(inflater, container, false)
        val view = binding.root
        val pref = requireActivity().getSharedPreferences("LOG_IN", Context.MODE_PRIVATE)
        myusername = pref.getString("emailOrUsername", null).toString()
        postList = ArrayList()

        recyclerView = binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
        }

        targetUsername = arguments?.getString(ARG_USERNAME).toString()

        fetchData()
        val followButton = binding.followButton
        followButton.setOnClickListener {
            db.collection("users")
                .whereEqualTo("username", myusername)
                .get().addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val document = querySnapshot.documents[0]
                        val user = document.toObject<User>()
                        user?.let {
                            val following = it.following ?: ArrayList()
                            if (!following.contains(targetUsername)) {
                                following.add(targetUsername)
                                followButton.hint = "Following"

                            } else {
                                following.remove(targetUsername)
                                followButton.hint = "Follow"
                            }

                            val updates = hashMapOf<String, Any?>("following" to following)

                            document.reference.update(updates)
                                .addOnSuccessListener {
                                    fetchData()
                                }
                                .addOnFailureListener {
                                    showToast("Failed to follow $targetUsername")
                                    followButton.hint = "Follow"
                                }
                        }
                    }
                }


            db.collection("users")
                .whereEqualTo("username", targetUsername)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val document = querySnapshot.documents[0]
                        val user = document.toObject<User>()
                        user?.let {
                            val followers = it.followers ?: ArrayList()
                            if (!followers.contains(myusername)) {
                                followers.add(myusername)

                            } else {
                                followers.remove(myusername)

                            }
                            val updates = hashMapOf<String, Any?>("followers" to followers)

                            document.reference.update(updates)
                                .addOnSuccessListener {
                                    fetchData()
                                }
                                .addOnFailureListener {
                                    showToast("Failed to follow $targetUsername")
                                }
                        }
                    }
                }
        }

        return view
    }

    private fun fetchData() {
        db.collection("posts")
            .whereEqualTo("username", targetUsername)
            .addSnapshotListener { querySnapshot, e ->
                if (e != null) {
                    showToast("Failed to listen for updates")
                    return@addSnapshotListener
                }

                if (querySnapshot != null && !querySnapshot.isEmpty) {
                    postList.clear()
                    for (document: DocumentSnapshot in querySnapshot.documents) {
                        val post = document.toObject<Post>()
                        post?.let { postList.add(it) }
                    }
                    recyclerView.adapter = PostAdapter(postList)
                }
            }

        db.collection("users")
            .whereEqualTo("username", targetUsername)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val user = querySnapshot.documents.firstOrNull()?.toObject<User>()

                user?.let {
                    val followingCount = it.following?.size ?: 0
                    binding.followingCount.text = followingCount.toString()
                    binding.postsCount.text = postList.size.toString()
                    binding.name.text = "${it.firstname} ${it.lastname}"
                }
            }
    }

    fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
