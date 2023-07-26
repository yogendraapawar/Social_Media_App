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
import com.example.myapplication.R
import com.example.myapplication.Schema.Post
import com.example.myapplication.Schema.User

class PostAdapter(private val posts:ArrayList<Post>):
    RecyclerView.Adapter<PostAdapter.PostViewHolder>()  {
    var onItemClick:((Post)->Unit)?=null
    class PostViewHolder(view: View): RecyclerView.ViewHolder(view){
        val username: TextView =view.findViewById<TextView>(R.id.username)
        val likes: TextView =view.findViewById<TextView>(R.id.likes_count)
        val caption: TextView =view.findViewById<TextView>(R.id.caption)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val viewHolder = LayoutInflater.from(parent.context).inflate(R.layout.post, parent, false)

        return PostViewHolder(viewHolder)
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.username.text=posts[position].username
        holder.caption.text=posts[position].caption
        holder.likes.text= posts[position].likes.toString()
        val viewPager=holder.itemView.findViewById<ViewPager2>(R.id.post_view_pager)
        viewPager.apply {
            clipChildren = false  // No clipping the left and right items
            clipToPadding = false  // Show the viewpager in full width without clipping the padding
            offscreenPageLimit = 3  // Render the left and right items
            (getChildAt(0) as RecyclerView).overScrollMode =
                RecyclerView.OVER_SCROLL_NEVER // Remove the scroll effect
        }
        val adapter = posts[position].imageUri?.let { PostImageAdapter(it) }
        viewPager.adapter = adapter
        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer((40 * Resources.getSystem().displayMetrics.density).toInt()))
        viewPager.setPageTransformer(compositePageTransformer)



    }

}