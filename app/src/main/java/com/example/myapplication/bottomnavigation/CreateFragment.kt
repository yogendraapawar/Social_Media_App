package com.example.myapplication.bottomnavigation

import android.content.ContentValues.TAG
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.example.myapplication.adapters.SelectedImagesAdapter
import com.example.myapplication.R
import android.content.res.Resources
import android.util.Log
import android.widget.Toast
import com.example.myapplication.Schema.Post
import com.example.myapplication.databinding.FragmentCreateBinding
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class CreateFragment : Fragment() {

    private lateinit var images: ArrayList<String>
    private val db = FirebaseFirestore.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentCreateBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment

        val view = binding.root

        val count = this.requireArguments().getInt("count")
        images = ArrayList()
        for (i in 0 until count) {
            val myValue = this.requireArguments().getString("image$i")!!
            images.add(myValue)
        }

        val viewPager = view.findViewById<ViewPager2>(R.id.view_pager)

        viewPager.apply {
            clipChildren = false  // No clipping the left and right items
            clipToPadding = false  // Show the viewpager in full width without clipping the padding
            offscreenPageLimit = 3  // Render the left and right items
            (getChildAt(0) as RecyclerView).overScrollMode =
                RecyclerView.OVER_SCROLL_NEVER // Remove the scroll effect
        }
        val adapter = SelectedImagesAdapter(images)
        viewPager.adapter = adapter
        adapter.setOnItemClickListener(object : SelectedImagesAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {
                println("you clicked $position")

            }

        })
        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer((40 * Resources.getSystem().displayMetrics.density).toInt()))
        viewPager.setPageTransformer(compositePageTransformer)

        binding.submitPost.setOnClickListener{
            binding.progressBar.visibility=View.VISIBLE
            binding.progressBar.bringToFront()
            val caption=binding.caption.text.toString()
            val tags=binding.tags.text.toString()
            val pref=requireActivity().getSharedPreferences("LOG_IN", Context.MODE_PRIVATE)
            val username=pref.getString("emailOrUsername", "")

            uploadImagesToStorage(images){uploadedUrls->
                val post = Post(
                    null,
                    username,
                    caption,
                    uploadedUrls,  // Replace the images ArrayList with uploaded URLs
                    tags,
                    Timestamp.now(),
                    null,
                    null
                )



                post.generatePostId() // Generate unique ID for the post

                db.collection("posts")
                    .document(post.postId!!)
                    .set(post)
                    .addOnSuccessListener {
                        Toast.makeText(context, "posted", Toast.LENGTH_SHORT).show()
                        binding.progressBar.visibility=View.GONE
                        val fragmentManager = requireActivity().supportFragmentManager
                        fragmentManager.popBackStack()
                        replaceFragment(HomeFragment())
                    }
                    .addOnFailureListener{
                            e ->
                        Log.w(TAG, "Error adding post", e)
                    }

            }


        }

        return view


    }

    private fun uploadImagesToStorage(images: ArrayList<String>, onSuccess: (ArrayList<String>) -> Unit) {
        val storageRef = FirebaseStorage.getInstance().reference
        val uploadedImages = ArrayList<String>()
        val uploadTasks = ArrayList<Task<Uri>>()

        for (i in 0 until images.size) {
            val imageUri = Uri.parse(images[i])
            val uniqueFilename = UUID.randomUUID().toString()
            val imageRef = storageRef.child("images/$uniqueFilename.jpg")

            val uploadTask = imageRef.putFile(imageUri)
                .addOnProgressListener {

                }
                .continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    imageRef.downloadUrl
                }
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUrl = task.result.toString()
                        uploadedImages.add(downloadUrl)
                    } else {
                        // Handle unsuccessful upload
                        Log.e(TAG, "Failed to upload image: ${task.exception}")
                    }

                    // Check if all images have been uploaded
                    if (uploadedImages.size == images.size) {
                        onSuccess(uploadedImages)
                    }
                }

            uploadTasks.add(uploadTask)
        }

        Tasks.whenAllComplete(uploadTasks)
            .addOnFailureListener { exception ->
                // Handle failure
                Log.e(TAG, "Failed to upload images: $exception")
            }
    }

    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.introContainer, fragment)
            .addToBackStack(null)
            .commitNow()
    }



}



