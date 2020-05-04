package com.soinikov.karantinogramm.activities


import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.firebase.database.ServerValue
import com.soinikov.karantinogramm.R
import com.soinikov.karantinogramm.models.User
import com.soinikov.karantinogramm.utils.CameraPictureTaker
import com.soinikov.karantinogramm.utils.FirebaseHelper
import com.soinikov.karantinogramm.utils.GlideApp
import com.soinikov.karantinogramm.utils.ValueEventListenerAdapter
import kotlinx.android.synthetic.main.activity_share.*
import org.w3c.dom.Comment
import java.util.*

class ShareActivity : BaseActivity(2) {
    private val TAG = "ShareActivity"
    private lateinit var mCamera: CameraPictureTaker
    private lateinit var mFirebase: FirebaseHelper
    private lateinit var mUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)
        Log.d(TAG, "onCreate")

        mCamera = CameraPictureTaker(this)
        mCamera.takeCameraPicture()
        mFirebase = FirebaseHelper(this)



        back_image.setOnClickListener{ finish() }
        share_text.setOnClickListener{ share() }

        mFirebase.currentUserReference().addValueEventListener(ValueEventListenerAdapter{
           mUser = it.getValue(User::class.java)!!
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == mCamera.REQUEST_CODE && resultCode == RESULT_OK) {
            GlideApp.with(this).load(mCamera.mImageUri).centerCrop().into(post_image)
        }  else {
            finish()
        }
    }

    private fun share() {
        val imageUri = mCamera.mImageUri
        if (imageUri != null) {
            val uid =  mFirebase.mAuth.currentUser!!.uid
            mFirebase.uploadSharePhoto(imageUri) {
                val imageDownloadUrl = it.metadata!!.reference!!.downloadUrl.toString()
                it.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    mFirebase.addSharePhoto(it.toString()) {
                        startActivity(Intent(this, ProfileActivity::class.java))
                        finish()
                    }
                }
                mFirebase.mDatabase.child("feed-posts").child(uid)
                    .push()
                    .setValue(mkFeedPost(uid, imageDownloadUrl))
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            startActivity(
                                Intent(
                                    this,
                                    ProfileActivity::class.java
                                )
                            )
                            finish()
                        }
                    }
            }
        }
    }

    private fun mkFeedPost(uid: String, imageDownloadUrl: String): FeedPost {
        return FeedPost(
            uid = uid,
            username = mUser.username,
            image = imageDownloadUrl,
            caption = caption_input.text.toString(),
            photo = mUser.photo
        )
    }
}

data class FeedPost(
    val uid: String ="", val username: String="", val photo: String?=null, val image: String? ="",
    val likesCount: Int = 0, val commentsCount: Int = 0, val caption: String = "",
    val comments: List<Comment> = emptyList(), val timestamp: Any = ServerValue.TIMESTAMP){
    fun timestampDate(): Date = Date(timestamp as Long)
}

data class Comment (val uid: String, val username: String, val text: String)
