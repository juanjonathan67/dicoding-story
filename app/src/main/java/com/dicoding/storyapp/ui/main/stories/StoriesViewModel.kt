package com.dicoding.storyapp.ui.main.stories

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.storyapp.data.StoryRepository
import com.dicoding.storyapp.data.remote.response.ListStoryItem

class StoriesViewModel (storyRepository: StoryRepository?) : ViewModel() {

    val stories: LiveData<PagingData<ListStoryItem>>? = storyRepository?.getStories()?.cachedIn(viewModelScope)
}