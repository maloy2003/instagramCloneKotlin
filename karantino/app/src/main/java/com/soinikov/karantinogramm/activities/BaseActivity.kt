package com.soinikov.karantinogramm.activities

import android.content.Intent

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.soinikov.karantinogramm.*
import kotlinx.android.synthetic.main.bottom_nav_view.*


abstract class BaseActivity(val navNumber: Int) : AppCompatActivity() {
    private val TAG = "BaseActivity"

    fun setupBottomNavigation() {
        bottom_nav_view.setIconSize(29f, 29f)
        bottom_nav_view.setTextVisibility(false)
        bottom_nav_view.enableItemShiftingMode(false)
        bottom_nav_view.enableShiftingMode(false)
        bottom_nav_view.enableAnimation(false)
        for (i in 0 until bottom_nav_view.menu.size()) {
            bottom_nav_view.setIconTintList(i, null)
        }
        bottom_nav_view.setOnNavigationItemSelectedListener {
            val nextActivity =
                when (it.itemId) {
                    R.id.nav_item_home -> HomeActivity::class.java
                    R.id.nav_item_search -> SearchActivity::class.java
                    R.id.nav_item_share -> ShareActivity::class.java
                    R.id.nav_item_likes -> LikesActivity::class.java
                    R.id.nav_item_profile -> ProfileActivity::class.java
                    else -> {
                        Log.e(TAG, "unknown nav item clicked $it")
                        null
                    }
                }
            if (nextActivity != null) {
                val intent = Intent(this, nextActivity)
                intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                startActivity(intent)
                overridePendingTransition(0, 0)
                true
            } else {
                false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (bottom_nav_view != null) {
            bottom_nav_view.menu.getItem(navNumber).isChecked = true
        }
    }
}