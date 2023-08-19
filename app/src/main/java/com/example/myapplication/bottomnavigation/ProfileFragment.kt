package com.example.myapplication.bottomnavigation

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.Schema.Post
import com.example.myapplication.Schema.User
import com.example.myapplication.activities.Authentication
import com.example.myapplication.adapters.PostAdapter
import com.example.myapplication.databinding.FragmentProfileBinding
import com.example.myapplication.databinding.ModalBottomSheetContentBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.SAVE_FIT_TO_CONTENTS
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID


lateinit var editName: String
lateinit var editSurname: String
lateinit var editBio: String
lateinit var standardBottomSheetBehavior: BottomSheetBehavior<FrameLayout>
lateinit var editprofileurl: String
private var pickedImage=false

class ModalBottomSheet : BottomSheetDialogFragment() {
    private var db = Firebase.firestore
    private lateinit var imageUri: Uri
    private val storageRef = FirebaseStorage.getInstance().reference
    private val uniqueFilename = UUID.randomUUID().toString()
    private val imageRef = storageRef.child("images/$uniqueFilename.jpg")
    private lateinit var editProfilePicture: ImageView
    private lateinit var progressBar:ProgressBar
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = ModalBottomSheetContentBinding.inflate(inflater, container, false)
        val pref = requireActivity().getSharedPreferences("LOG_IN", Context.MODE_PRIVATE)
        username = pref.getString("emailOrUsername", "").toString()
        progressBar=binding.progressBar
        progressBar.visibility=View.GONE
        db = FirebaseFirestore.getInstance()

        standardBottomSheetBehavior = BottomSheetBehavior.from(binding.standardBottomSheet)
        standardBottomSheetBehavior.saveFlags = SAVE_FIT_TO_CONTENTS
        standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        binding.editName.setText(editName)
        binding.editSurname.setText(editSurname)
        binding.editBio.setText(editBio)
        editProfilePicture = binding.editProfilePicture


        if (editprofileurl.isNotEmpty()) {
            Glide.with(this)
                .load(editprofileurl)
                .into(binding.editProfilePicture)
        }


        binding.saveButton.setOnClickListener {
            editName = binding.editName.text.toString()
            editSurname = binding.editSurname.text.toString()
            editBio = binding.editBio.text.toString()
            val userUpdates = mapOf(
                "firstname" to editName,
                "lastname" to editSurname,
                "bio" to editBio
            )
            updateUserData(userUpdates, 3)

            if (pickedImage) {
                setProfilePicture(imageUri)

            }

            progressBar.visibility = View.GONE
        }
        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                // Callback is invoked after the user selects a  closesmedia item or the
                // photo picker.

                if (uri != null) {
                    imageUri = uri
                    editProfilePicture.setImageURI(imageUri)
                    pickedImage=true
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }

        binding.changeProfilePicture.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }


        return binding.root
    }

    private fun setProfilePicture(imageUri: Uri) {
        imageRef.putFile(imageUri)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Get the download URL from the task result
                    task.result?.storage?.downloadUrl?.addOnSuccessListener { downloadUrl ->
                        val imageUrl = downloadUrl.toString()
                        println(imageUrl)
                        val user = mapOf("profileurl" to imageUrl)
                        updateUserData(user, 3)
                    }?.addOnFailureListener {
                        // Handle failure to get the download URL
                        Toast.makeText(context, "Failed to get download URL", Toast.LENGTH_SHORT)
                            .show()
                    }

                } else {
                    // Handle unsuccessful upload
                    Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnProgressListener { snapshot ->
                // Handle upload progress
                progressBar.visibility=View.VISIBLE
                val progress = (100.0 * snapshot.bytesTransferred / snapshot.totalByteCount).toInt()
                println(progress)
                // Update progress if needed
                progressBar.progress = progress

            }
            .addOnSuccessListener {
                // Handle successful upload

                Toast.makeText(context, "Image uploaded successfully", Toast.LENGTH_SHORT).show()

            }
    }


    companion object {
        const val TAG = "ModalBottomSheet"
    }

    private fun updateUserData(userUpdates: Map<String, Any>, retryCount: Int) {
        db.collection("users")
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    // Get the document ID
                    val documentId = document.id

                    // Update the document with the new data
                    db.collection("users").document(documentId)
                        .update(userUpdates)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()

                        }
                        .addOnFailureListener { e ->
                            if (retryCount > 0) {
                                updateUserData(userUpdates, retryCount - 1)
                            } else {
                                Toast.makeText(
                                    context,
                                    "Error while updating details",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }
            }.addOnFailureListener { e ->
                if (retryCount > 0) {
                    updateUserData(userUpdates, retryCount - 1)
                } else {
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
                }
            }

    }


}

class ProfileFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private var db = Firebase.firestore
    private lateinit var arrayList: ArrayList<Post>
    var username: String = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentProfileBinding.inflate(inflater, container, false)

        val view = binding.root


        val modalBottomSheet = ModalBottomSheet()


        arrayList = ArrayList()



        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this.requireContext())

        val pref = requireActivity().getSharedPreferences("LOG_IN", Context.MODE_PRIVATE)
        username = pref.getString("emailOrUsername", "").toString()

        fetchInfo(username, binding)

        db = FirebaseFirestore.getInstance()

        //fetch posts
        fetchPosts()

        //handle reload
        binding.swiperefresh.setOnRefreshListener {
            arrayList.clear()
            fetchInfo(username, binding)
            fetchPosts()
            binding.swiperefresh.isRefreshing=false
        }
        //handle logout button
        binding.logOutButton.setOnClickListener {
            val pref = requireActivity().getSharedPreferences("LOG_IN", Context.MODE_PRIVATE)
            val editor = pref.edit()
            editor.putBoolean("isLoggedIn", false)
            editor.apply()
            val i = Intent(requireContext(), Authentication()::class.java)
            startActivity(i)
        }

        //handle editProfile
        binding.profilePicture.setOnClickListener {
            modalBottomSheet.show(requireActivity().supportFragmentManager, ModalBottomSheet.TAG)
        }

        return view

    }

    @SuppressLint("SetTextI18n")
    private fun fetchInfo(username: String?, binding: FragmentProfileBinding) {
        db.collection("users")
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val doc = querySnapshot.documents[0]
                val user = doc.toObject<User>()
                if (user != null) {
                    val followerCount = user.followers?.size ?: 0
                    binding.followersCount.text = followerCount.toString()

                    val followingCount = user.following?.size ?: 0
                    binding.followingCount.text = followingCount.toString()

                    val postsCount = arrayList.size
                    binding.postsCount.text = postsCount.toString()

                    binding.name.text = user.firstname + " " + user.lastname

                    binding.bio.text = user.bio

                    Glide.with(this)
                        .load(user.profileurl)
                        .into(binding.profilePicture)

                    editprofileurl = user.profileurl.toString()



                    editName = user.firstname.toString()
                    editSurname = user.lastname.toString()
                    editBio = user.bio.toString()
                }
            }

    }

    private fun fetchPosts() {
        db.collection("posts")
            .whereEqualTo("username", username)
            .addSnapshotListener { querySnapshot, e ->
                if (e != null) {
                    Toast.makeText(context, "Failed to listen for updates", Toast.LENGTH_SHORT)
                        .show()
                    return@addSnapshotListener
                }

                if ((querySnapshot != null) && !querySnapshot.isEmpty) {

                    for (document: DocumentSnapshot in querySnapshot.documents) {
                        document.toObject<Post>()?.let { arrayList.add(it) }
                    }

                    val adapter = PostAdapter(arrayList)
                    recyclerView.adapter = adapter
                }
            }

    }


}