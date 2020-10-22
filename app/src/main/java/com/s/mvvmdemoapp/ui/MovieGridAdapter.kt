package com.s.mvvmdemoapp.ui

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.s.mvvmdemoapp.R
import com.s.mvvmdemoapp.model.Content
import kotlinx.android.synthetic.main.layout_grid_item.view.*

class MovieGridAdapter (private var contentList:MutableList<Content>): RecyclerView.Adapter<MovieGridAdapter.MoviesViewHolder>(){
     private lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoviesViewHolder {
        context = parent.context
        return MoviesViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.layout_grid_item,parent,false)
        )
    }

    override fun getItemCount(): Int {
       return contentList.size
    }

    override fun onBindViewHolder(holder: MoviesViewHolder, position: Int) {
       val content = contentList[position]
        holder.tvMovieName.text = content.name
        Glide.with(context)
            .load(Uri.parse("file:///android_asset/${content.posterImage}"))
            .placeholder(R.drawable.placeholder_for_missing_posters)
            .into(holder.ivMoviePoster)
    }

    fun updateList(list: MutableList<Content>) {
        contentList = list
        notifyDataSetChanged()
    }

    class MoviesViewHolder (itemView: View): RecyclerView.ViewHolder(itemView){
        val tvMovieName = itemView.tv_movie_name as AppCompatTextView
        val ivMoviePoster = itemView.iv_movie_thumb as AppCompatImageView
    }
}