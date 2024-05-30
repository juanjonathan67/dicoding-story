package com.dicoding.storyapp.ui.main.storyDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.Result
import com.dicoding.storyapp.data.StoryRepository
import com.dicoding.storyapp.data.remote.response.StoryResponse
import kotlinx.coroutines.launch

class StoryDetailViewModel (private val storyRepository: StoryRepository?) : ViewModel() {
    fun getStoryDetail (storyId: String) : LiveData<Result<StoryResponse>> {
        val result: MutableLiveData<Result<StoryResponse>> = MutableLiveData(Result.Loading)
        viewModelScope.launch {
            result.value = storyRepository?.getStoryDetail(storyId)
        }
        return result
    }

}