package com.example.insta

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.insta.databinding.FragmentFeedBinding

class InstaPostAdapter(
    private val values: List<Post>
) : RecyclerView.Adapter<InstaPostAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentFeedBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.uploaderView.text = item.getUploadedBy()?.username
        holder.captionView.text = item.getCaption()
        holder.imageView.setImageURI(Uri.fromFile(item.getImage()?.file)) //really should be non-nullable
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentFeedBinding) : RecyclerView.ViewHolder(binding.root) {
        val uploaderView: TextView = binding.tvPostUploader
        val imageView: ImageView = binding.ivPostImage
        val captionView : TextView = binding.tvPostCaption

    }

}