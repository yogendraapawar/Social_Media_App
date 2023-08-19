package com.example.myapplication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.myapplication.R
import com.example.myapplication.Schema.Post
import com.example.myapplication.Schema.User
import kotlinx.coroutines.NonDisposableHandle.parent

class PostImageAdapter(private val imageUri:ArrayList<String>):RecyclerView.Adapter<PostImageAdapter.PostImageViewHolder>() {
    var onItemClick: (() -> Unit)? = null

    class PostImageViewHolder(view: View):RecyclerView.ViewHolder(view){

        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostImageViewHolder {
        val viewHolder=LayoutInflater.from(parent.context).inflate(R.layout.post_image, parent,false)
        return PostImageViewHolder(viewHolder)
    }

    override fun getItemCount(): Int {
        return imageUri.size
    }

    override fun onBindViewHolder(holder: PostImageViewHolder, position: Int) {

        val imageView=holder.itemView.findViewById<ImageView>(R.id.postView)
        Glide.with(holder.itemView.context)
            .load(imageUri[position])
            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL)) // Optional: Cache the image
            .into(imageView)
    }
}