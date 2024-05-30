package com.dicoding.storyapp.ui.landing.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.AuthRepository
import com.dicoding.storyapp.data.Result
import com.dicoding.storyapp.data.remote.response.ErrorResponse
import kotlinx.coroutines.launch

class RegisterViewModel (private val authRepository: AuthRepository?) : ViewModel() {

    fun register (
        name: String,
        email: String,
        password: String
    ) : LiveData<Result<ErrorResponse>> {
        val result: MutableLiveData<Result<ErrorResponse>> = MutableLiveData(Result.Loading)
        viewModelScope.launch {
            result.value = authRepository?.register(name, email, password)
        }
        return result
    }
}