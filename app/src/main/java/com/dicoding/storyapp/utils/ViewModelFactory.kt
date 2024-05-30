package com.dicoding.storyapp.utils

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapp.data.AuthRepository
import com.dicoding.storyapp.data.StoryRepository
import com.dicoding.storyapp.di.Injection
import com.dicoding.storyapp.ui.landing.login.LoginViewModel
import com.dicoding.storyapp.ui.landing.register.RegisterViewModel
import com.dicoding.storyapp.ui.main.addStory.AddStoryViewModel
import com.dicoding.storyapp.ui.main.maps.MapsViewModel
import com.dicoding.storyapp.ui.main.stories.StoriesViewModel
import com.dicoding.storyapp.ui.main.storyDetail.StoryDetailViewModel

class ViewModelFactory private constructor (
    private val authRepository: AuthRepository? = null,
    private val storyRepository: StoryRepository? = null
    ) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when (modelClass) {
        LoginViewModel::class.java -> LoginViewModel(authRepository)
        RegisterViewModel::class.java -> RegisterViewModel(authRepository)
        StoriesViewModel::class.java -> StoriesViewModel(storyRepository)
        StoryDetailViewModel::class.java -> StoryDetailViewModel(storyRepository)
        AddStoryViewModel::class.java -> AddStoryViewModel(storyRepository)
        MapsViewModel::class.java -> MapsViewModel(storyRepository)
        else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    } as T

    companion object {
        @Volatile
        private var authInstance: ViewModelFactory? = null

        @Volatile
        private var storyInstance: ViewModelFactory? = null
        fun getAuthInstance(context: Context): ViewModelFactory =
            authInstance ?: synchronized(this) {
                authInstance ?: ViewModelFactory(authRepository = Injection.provideAuthRepository(context))
            }.also { authInstance = it }

        fun getStoryInstance(context: Context): ViewModelFactory =
            storyInstance ?: synchronized(this) {
                storyInstance ?: ViewModelFactory(storyRepository = Injection.provideStoryRepository(context))
            }.also { storyInstance = it }
    }

}