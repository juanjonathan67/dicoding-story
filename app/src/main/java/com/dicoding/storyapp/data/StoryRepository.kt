package com.dicoding.storyapp.data

import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.dicoding.storyapp.data.local.database.StoryDatabase
import com.dicoding.storyapp.data.remote.response.ErrorResponse
import com.dicoding.storyapp.data.remote.response.ListStoryItem
import com.dicoding.storyapp.data.remote.response.StoriesResponse
import com.dicoding.storyapp.data.remote.response.StoryResponse
import com.dicoding.storyapp.data.remote.retrofit.ApiStoryService
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class StoryRepository private constructor(
    private val storyDatabase: StoryDatabase,
    private val apiStoryService: ApiStoryService,
){

    suspend fun uploadStory (
        imageFile: File,
        description: String,
        lat: Float? = null,
        lon: Float? = null,
    ) : Result<ErrorResponse> {
        return withContext(Dispatchers.IO) {
            val descRequestBody = description.toRequestBody("text/plain".toMediaType())
            val latRequestBody = lat?.toString()?.toRequestBody("text/plain".toMediaType())
            val lonRequestBody = lon?.toString()?.toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImageFile,
            )

            try {
                val errorResponse = apiStoryService.uploadStory(
                    file = multipartBody,
                    description = descRequestBody,
                    lat = latRequestBody,
                    lon = lonRequestBody,
                )
                return@withContext Result.Success(errorResponse)
            } catch (e : HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                return@withContext Result.Error(errorResponse.message)
            }
        }
    }

    fun getStories() : LiveData<PagingData<ListStoryItem>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiStoryService),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }

    suspend fun getStoriesWithLocation () : Result<StoriesResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val storiesResponse = apiStoryService.getStoriesWithLocation()
                return@withContext Result.Success(storiesResponse)
            } catch (e : HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                return@withContext Result.Error(errorResponse.message)
            }
        }
    }

    suspend fun getStoryDetail (storyId: String) : Result<StoryResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val storyDetailResponse = apiStoryService.getStoryDetail(storyId)
                return@withContext Result.Success(storyDetailResponse)
            } catch (e : HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                return@withContext Result.Error(errorResponse.message)
            }
        }
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(
            storyDatabase: StoryDatabase,
            apiStoryService: ApiStoryService,
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(storyDatabase, apiStoryService)
            }.also { instance = it }
    }
}