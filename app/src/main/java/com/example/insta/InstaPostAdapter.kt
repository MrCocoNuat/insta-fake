package com.example.insta

import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.insta.databinding.FragmentFeedBinding
import org.w3c.dom.Text

class InstaPostAdapter(
    private val posts: List<Post>
) : RecyclerView.Adapter<InstaPostAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(FragmentFeedBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = posts[position]
        holder.uploaderView.text = post.getUploadedBy()?.username
        holder.captionView.text = post.getCaption()
        holder.dateView.text = ""
        holder.imageView.setImageURI(Uri.fromFile(post.getImage()?.file)) //really should be non-nullable
    }

    override fun getItemCount(): Int = posts.size

    inner class ViewHolder(binding: FragmentFeedBinding) : RecyclerView.ViewHolder(binding.root) {
        val uploaderView: TextView = binding.tvPostUploader
        val imageView: ImageView = binding.ivPostImage
        val captionView : TextView = binding.tvPostCaption
        val dateView : TextView = binding.tvPostDate
    }
}