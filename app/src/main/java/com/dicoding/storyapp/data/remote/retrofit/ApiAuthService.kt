package com.dicoding.storyapp.data.remote.retrofit

import com.dicoding.storyapp.data.remote.response.ErrorResponse
import com.dicoding.storyapp.data.remote.response.LoginResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiAuthService {
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ) : ErrorResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ) : LoginResponse
}