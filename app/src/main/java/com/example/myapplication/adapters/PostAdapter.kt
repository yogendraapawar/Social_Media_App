package com.example.myapplication.adapters

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.Schema.Post
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class PostAdapter(private val posts: ArrayList<Post>) :
    RecyclerView.Adapter<PostAdapter.PostViewHolder>(), CoroutineScope by CoroutineScope(
    Dispatchers.Main
) {
    var onItemClick: ((Post) -> Unit)? = null

    inner class PostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val username: TextView = view.findViewById<TextView>(R.id.username)
        val likes: TextView = view.findViewById<TextView>(R.id.likes_count)
        val caption: TextView = view.findViewById<TextView>(R.id.caption)
        val profilePicture: ImageView = view.findViewById<ImageView>(R.id.profile_picture)
        val viewPager: ViewPager2 = view.findViewById<ViewPager2>(R.id.post_view_pager)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val viewHolder =
            LayoutInflater.from(parent.context).inflate(R.layout.post, parent, false)
        return PostViewHolder(viewHolder)
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]

        holder.username.text = post.username
        holder.caption.text = post.caption
        holder.likes.text = post.likes.toString()

        fetchProfilePictureUrl(post) { url ->
            if (url.isNotEmpty()) {
                Glide.with(holder.itemView.context)
                    .load(url)
                    .into(holder.profilePicture)
            }
        }

        holder.viewPager.apply {
            clipChildren = false
            clipToPadding = false
            offscreenPageLimit = 3
            (getChildAt(0) as RecyclerView).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
            adapter = post.imageUri?.let { PostImageAdapter(it) }
            val compositePageTransformer = CompositePageTransformer()
            compositePageTransformer.addTransformer(
                MarginPageTransformer((0 * Resources.getSystem().displayMetrics.density).toInt())
            )
            setPageTransformer(compositePageTransformer)
        }
    }

    private fun fetchProfilePictureUrl(post: Post, callback: (String) -> Unit) {
        Firebase.firestore.collection("users")
            .whereEqualTo("username", post.username)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val url = querySnapshot.documents.getOrNull(0)?.getString("profileurl") ?: ""
                callback(url)
            }
            .addOnFailureListener { exception ->
                callback("")
                // Handle failure here if needed
            }
    }
}
