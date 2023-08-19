package com.example.myapplication.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import com.example.myapplication.R


class SelectedImagesAdapter(private val carouselDataList: ArrayList<String>) :
    RecyclerView.Adapter<SelectedImagesAdapter.CarouselItemViewHolder>() {

    private lateinit var mlistener: onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener){
        mlistener=listener
    }

    class CarouselItemViewHolder(view: View, listener: onItemClickListener) : RecyclerView.ViewHolder(view){
        val image=view.findViewById<ImageView>(R.id.imageView)

        init{
            view.setOnClickListener{
                listener.onItemClick(adapterPosition)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselItemViewHolder {
        val viewHolder = LayoutInflater.from(parent.context).inflate(R.layout.selected_image_preview, parent, false)
        return CarouselItemViewHolder(viewHolder, mlistener)
    }

    override fun onBindViewHolder(holder: CarouselItemViewHolder, position: Int) {
        val imageView = holder.itemView.findViewById<ImageView>(R.id.imageView)
        val imageUri = Uri.parse(carouselDataList[position])
        imageView.setImageURI(imageUri)

    }

    override fun getItemCount(): Int {
        return carouselDataList.size
    }


}

