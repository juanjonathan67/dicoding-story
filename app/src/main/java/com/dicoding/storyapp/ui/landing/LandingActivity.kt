package com.dicoding.storyapp.ui.landing

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.storyapp.databinding.ActivityLandingBinding
import com.dicoding.storyapp.ui.main.MainActivity
import com.dicoding.storyapp.utils.UserPreferences
import com.dicoding.storyapp.utils.datastore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class LandingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLandingBinding
    private lateinit var prefs : UserPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        prefs = UserPreferences.getInstance(this.datastore)
        runBlocking {
            if (prefs.getUserToken().first() != "") {
                val mainIntent = Intent(this@LandingActivity, MainActivity::class.java)
                mainIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(mainIntent)
            }
        }

        super.onCreate(savedInstanceState)
        binding = ActivityLandingBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}