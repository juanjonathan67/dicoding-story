package com.dicoding.storyapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.storyapp.ui.main.maps.MapsActivity
import com.dicoding.storyapp.R
import com.dicoding.storyapp.databinding.ActivityMainBinding
import com.dicoding.storyapp.ui.landing.LandingActivity
import com.dicoding.storyapp.utils.UserPreferences
import com.dicoding.storyapp.utils.datastore
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var prefs : UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = UserPreferences.getInstance(this.datastore)

        binding.appBar.toolbar.setOnMenuItemClickListener {menuItem ->
            when(menuItem.itemId) {
                R.id.maps -> {
                    startActivity(Intent(this, MapsActivity::class.java))
                    true
                }
                R.id.language_settings -> {
                    startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                    true
                }
                R.id.action_logout -> {
                    runBlocking {
                        prefs.deleteUserToken()
                    }
                    val landingIntent = Intent(this, LandingActivity::class.java)
                    landingIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(landingIntent)
                    true
                }
                else -> {
                    false
                }
            }
        }

        binding.appBar.toolbar.setNavigationOnClickListener {
            supportFragmentManager.popBackStack()
        }

    }

}