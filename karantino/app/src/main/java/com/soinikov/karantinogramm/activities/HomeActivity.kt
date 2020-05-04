package com.soinikov.karantinogramm.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.soinikov.karantinogramm.R
import com.soinikov.karantinogramm.utils.FirebaseHelper
import com.soinikov.karantinogramm.utils.GlideApp
import com.soinikov.karantinogramm.utils.ValueEventListenerAdapter
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.feed_item.view.*

class HomeActivity : BaseActivity(0) {
    private val TAG = "HomeActivity"
    private lateinit var mFirebase: FirebaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        Log.d(TAG, "onCreate")
        setupBottomNavigation()

        mFirebase = FirebaseHelper(this)
        mFirebase.mAuth.addAuthStateListener{
            if(it.currentUser == null){
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = mFirebase.mAuth.currentUser
        if (currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
        mFirebase.mDatabase.child("feed-posts").child(currentUser.uid)
            .addValueEventListener(ValueEventListenerAdapter{
                val posts = it.children.map{it.getValue(FeedPost::class.java)!!}
                Log.d(TAG, "mFeed: ${posts.first().timestampDate()} ")
                feed_recycler.adapter = FeedAdapter(posts)
                feed_recycler.layoutManager = LinearLayoutManager(this)
            }     )
        }
    }
}

class FeedAdapter(private val posts: List<FeedPost>)
    : RecyclerView.Adapter<FeedAdapter.ViewHolder>() {

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.feed_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        posts[position].image?.let { holder.view.post_image.loadImage(it) }
    }

    override fun getItemCount() = posts.size

    private fun ImageView.loadImage(image: String) {
        GlideApp.with(this).load(image).centerCrop().into(this)
    }
}
