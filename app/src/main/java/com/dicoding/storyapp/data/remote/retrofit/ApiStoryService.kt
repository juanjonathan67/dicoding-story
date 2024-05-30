package com.dicoding.storyapp.data.remote.retrofit

import com.dicoding.storyapp.data.remote.response.ErrorResponse
import com.dicoding.storyapp.data.remote.response.StoriesResponse
import com.dicoding.storyapp.data.remote.response.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiStoryService {
    @Multipart
    @POST("stories")
    suspend fun uploadStory(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody? = null,
        @Part("lon") lon: RequestBody? = null,
    ) : ErrorResponse

    @GET("stories")
    suspend fun getStories(
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 10
    ) : StoriesResponse

    @GET("stories")
    suspend fun getStoriesWithLocation(
        @Query("location") location : Int = 1,
    ) : StoriesResponse

    @GET("stories/{id}")
    suspend fun getStoryDetail(@Path("id") id: String) : StoryResponse
}