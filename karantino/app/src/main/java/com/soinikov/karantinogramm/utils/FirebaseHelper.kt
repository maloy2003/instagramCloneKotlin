package com.soinikov.karantinogramm.utils

import android.app.Activity
import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.soinikov.karantinogramm.showToast

class FirebaseHelper (private val activity: Activity) {
    val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val mDatabase: DatabaseReference = FirebaseDatabase.getInstance().reference
    val mStorage: StorageReference = FirebaseStorage.getInstance().reference

    fun uploadUserPhoto(photo: Uri, onSuccess: (UploadTask.TaskSnapshot) -> Unit) =
        mStorage.child("users/${mAuth.currentUser!!.uid}/photo").putFile(photo).addOnCompleteListener {
            if (it.isSuccessful)
                onSuccess(it.result!!)
            else
                activity.showToast(it.exception!!.message!!)
        }

    fun updateUserPhoto(photoUrl: String, onSuccess: () -> Unit) =
        mDatabase.child("users/${mAuth.currentUser!!.uid}/photo").setValue(photoUrl)
            .addOnComplete { onSuccess() }

    fun updateUser(updates: Map<String, Any?>, onSuccess: () -> Unit) =
        mDatabase.child("users").child(mAuth.currentUser!!.uid).updateChildren(updates)
            .addOnComplete { onSuccess() }

    fun updateEmail(email: String, onSuccess: () -> Unit) =
        mAuth.currentUser!!.updateEmail(email).addOnComplete { onSuccess() }

    fun uploadSharePhoto(localPhotoUrl: Uri, onSuccess: (UploadTask.TaskSnapshot) -> Unit) =
        mStorage.child("users/${mAuth.currentUser!!.uid}").child("images")
            .child(localPhotoUrl.lastPathSegment!!)
            .putFile(localPhotoUrl)
            .addOnCompleteListener {
                if (it.isSuccessful)
                    onSuccess(it.result!!)
                else
                    activity.showToast(it.exception!!.message!!)
            }

    fun addSharePhoto(globalPhotoUrl: String, onSuccess: () -> Unit) =
        mDatabase.child("images").child(mAuth.currentUser!!.uid)
            .push().setValue(globalPhotoUrl)
            .addOnComplete { onSuccess() }

    fun reauthenticate(credential: AuthCredential, onSuccess: () -> Unit) =
    mAuth.currentUser!!.reauthenticate(credential).addOnComplete { onSuccess()}

    fun currentUserReference(): DatabaseReference =
        mDatabase.child("users").child(mAuth.currentUser!!.uid)

    private fun Task<Void>.addOnComplete(onSuccess: () -> Unit) {
        addOnCompleteListener {
            if (it.isSuccessful)
                onSuccess()
            else
                activity.showToast(it.exception!!.message!!)
        }
    }


}