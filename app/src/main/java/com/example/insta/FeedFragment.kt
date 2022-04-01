package com.example.insta

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.parse.ParseQuery
import com.parse.ParseUser


class FeedFragment(val user : ParseUser?) : Fragment() {

    val posts : MutableList<Post> = mutableListOf()
    lateinit var swipeContainer : SwipeRefreshLayout
    lateinit var adapter : InstaPostAdapter

    @SuppressLint("NotifyDataSetChanged")
    fun refreshList(){
        val query: ParseQuery<Post> = ParseQuery.getQuery(Post::class.java)
        // no condition = find all
        // username passed = find only those
        if (user != null)
            query.whereEqualTo(Post.KEY_UPLOADEDBY, user)
        query.addDescendingOrder("createdAt")
        query.include(Post.KEY_UPLOADEDBY)
        query.include(Post.KEY_UPLOADEDAT)
        query.limit = 20
        query.findInBackground{ posts, e ->
            if (e == null) {
                for (post in posts){
                    Log.i(MainActivity.TAG,"Post: ${post.getCaption()} by ${post.getUploadedBy()?.username}")
                }
                this.posts.clear()
                this.posts.addAll(posts)
                adapter.notifyDataSetChanged()
            } else {
                Log.e(MainActivity.TAG, "Failed to load posts from server")
            }
        }
        swipeContainer.isRefreshing = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_feed_list, container, false)

        val rvPosts = view.findViewById<RecyclerView>(R.id.list)
        // Set the adapter
        rvPosts.layoutManager = LinearLayoutManager(context)
        rvPosts.adapter = InstaPostAdapter(posts)
        this.adapter = rvPosts.adapter as InstaPostAdapter


        // Lookup the swipe container view
        swipeContainer = view.findViewById(R.id.swipeContainer)
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener {
            refreshList()
        }

        refreshList()

        return view
    }

    companion object {

    }
}