package com.dicoding.storyapp.data

import com.dicoding.storyapp.data.remote.response.ErrorResponse
import com.dicoding.storyapp.data.remote.response.LoginResponse
import com.dicoding.storyapp.data.remote.retrofit.ApiAuthService
import com.dicoding.storyapp.utils.UserPreferences
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class AuthRepository private constructor(
    private val apiAuthService: ApiAuthService,
    private val prefs: UserPreferences
){

    suspend fun register (
        name: String,
        email: String,
        password: String
    ) : Result<ErrorResponse> {
        return withContext(Dispatchers.IO) {
            try {
                return@withContext Result.Success(apiAuthService.register(name, email, password))
            } catch (e : HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                return@withContext Result.Error(errorResponse.message)
            }
        }
    }

    suspend fun login (
        email: String,
        password: String
    ) : Result<LoginResponse> {

        return withContext(Dispatchers.IO) {
            try {
                val loginResponse = apiAuthService.login(email, password)
                prefs.saveUserToken(loginResponse.loginResult.token)
                return@withContext Result.Success(loginResponse)
            } catch (e : HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                return@withContext Result.Error(errorResponse.message)
            }
        }
    }

    companion object {
        @Volatile
        private var instance: AuthRepository? = null

        fun getInstance(
            apiAuthService: ApiAuthService,
                prefs: UserPreferences
        ): AuthRepository =
            instance ?: synchronized(this) {
                instance ?: AuthRepository(apiAuthService, prefs)
            }.also { instance = it }
    }
}