package com.dicoding.storyapp.ui.main.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.Result
import com.dicoding.storyapp.data.StoryRepository
import com.dicoding.storyapp.data.remote.response.StoriesResponse
import kotlinx.coroutines.launch

class MapsViewModel (private val storyRepository: StoryRepository?) : ViewModel() {

    fun getStoriesWithLocation() : LiveData<Result<StoriesResponse>> {
        val result: MutableLiveData<Result<StoriesResponse>> = MutableLiveData(Result.Loading)
        viewModelScope.launch {
            result.value = storyRepository?.getStoriesWithLocation()
        }
        return result
    }
}