 package com.soinikov.karantinogramm.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.soinikov.karantinogramm.R
import com.soinikov.karantinogramm.utils.*
import com.soinikov.karantinogramm.coordinateBtnAndInputs
import com.soinikov.karantinogramm.showToast
import kotlinx.android.synthetic.main.activity_login.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener

class LoginActivity : AppCompatActivity(), KeyboardVisibilityEventListener, View.OnClickListener {
    private val TAG = "LoginActivity"
    private lateinit var mFirebaseHelper: FirebaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Log.d(TAG, "onCreate")

        KeyboardVisibilityEvent.setEventListener(this, this)
        coordinateBtnAndInputs(login_page_button, loginpage_email_input, loginpage_password_input)
        login_page_button.setOnClickListener(this)
        login_page_register_toolbar.setOnClickListener(this)

        mFirebaseHelper = FirebaseHelper(this)
    }

    override fun onClick(view: View) {
        when(view.id) {
            R.id.login_page_button -> {
                val email = loginpage_email_input.text.toString()
                val password = loginpage_password_input.text.toString()
                if (validate(email, password)) {
                    mFirebaseHelper.mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener{
                        if (it.isSuccessful) {
                            startActivity(Intent(this, HomeActivity::class.java))
                            finish()
                        }
                    }
                } else {
                    showToast("Please enter email and password")
                }
            }
            R.id.login_page_register_toolbar -> {
                startActivity(Intent(this, RegisterActivity::class.java))
            }
        }
    }

    override fun onVisibilityChanged(isKeyboardOpen: Boolean) {
        if (isKeyboardOpen) {
            login_page_register_toolbar.visibility = View.GONE
        } else {
            login_page_register_toolbar.visibility = View.VISIBLE
        }
    }

    private fun validate(email: String, password: String) =
        email.isNotEmpty() && password.isNotEmpty()

}