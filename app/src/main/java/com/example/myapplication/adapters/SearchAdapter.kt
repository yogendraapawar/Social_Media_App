package com.example.myapplication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.Schema.User

class SearchAdapter(private val searchResults: ArrayList<User>) :
    RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    var onItemClick: ((User) -> Unit)? = null

    class SearchViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val viewHolder =
            LayoutInflater.from(parent.context).inflate(R.layout.searchitem, parent, false)

        return SearchViewHolder(viewHolder)
    }

    override fun getItemCount(): Int {
        return searchResults.size
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val user = searchResults[position]
        val username = holder.itemView.findViewById<TextView>(R.id.username)
        username.text = searchResults[position].username
        val name = holder.itemView.findViewById<TextView>(R.id.name)
        name.text = searchResults[position].firstname + " " + searchResults[position].lastname
        holder.itemView.findViewById<ImageView>(R.id.cancel_button).setOnClickListener {
            searchResults.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, searchResults.size)
        }
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(user)
        }
    }
}
