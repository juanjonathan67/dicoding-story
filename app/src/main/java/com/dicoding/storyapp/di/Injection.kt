package com.dicoding.storyapp.di

import android.content.Context
import com.dicoding.storyapp.data.AuthRepository
import com.dicoding.storyapp.data.StoryRepository
import com.dicoding.storyapp.data.local.database.StoryDatabase
import com.dicoding.storyapp.data.remote.retrofit.ApiConfig
import com.dicoding.storyapp.utils.UserPreferences
import com.dicoding.storyapp.utils.datastore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideAuthRepository(context: Context): AuthRepository {
        val prefs = UserPreferences.getInstance(context.datastore)
        val apiService = ApiConfig.getAuthApiService()
        return AuthRepository.getInstance(apiService, prefs)
    }

    fun provideStoryRepository(context: Context): StoryRepository {
        val prefs = UserPreferences.getInstance(context.datastore)
        val token = runBlocking { prefs.getUserToken().first() }
        val apiService = ApiConfig.getStoryApiService(token)
        val database = StoryDatabase.getDatabase(context)
        return StoryRepository.getInstance(database, apiService)
    }
}