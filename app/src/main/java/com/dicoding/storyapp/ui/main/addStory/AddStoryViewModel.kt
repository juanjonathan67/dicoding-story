package com.dicoding.storyapp.ui.main.addStory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.Result
import com.dicoding.storyapp.data.StoryRepository
import com.dicoding.storyapp.data.remote.response.ErrorResponse
import kotlinx.coroutines.launch
import java.io.File

class AddStoryViewModel (private val storyRepository: StoryRepository?) : ViewModel() {
    fun uploadStory (
        imageFile: File,
        description: String,
        lat: Float? = null,
        lon: Float? = null,
    ) : LiveData<Result<ErrorResponse>> {
        val result: MutableLiveData<Result<ErrorResponse>> = MutableLiveData(Result.Loading)
        viewModelScope.launch {
            result.value = storyRepository?.uploadStory(imageFile, description, lat, lon)
        }
        return result
    }
}