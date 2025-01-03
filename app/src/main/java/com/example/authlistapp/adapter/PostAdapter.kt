package com.example.authlistapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.authlistapp.databinding.ItemPostBinding
import com.example.authlistapp.model.PostListResponseItem

class PostAdapter(val context: Context, val list: ArrayList<PostListResponseItem>) :
    RecyclerView.Adapter<PostAdapter.MyViewHolder>() {

    inner class MyViewHolder(val binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = ItemPostBinding.inflate(inflater, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int{
        return list.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.title.text = list[position].title
        holder.binding.body.text = list[position].body
    }
}

