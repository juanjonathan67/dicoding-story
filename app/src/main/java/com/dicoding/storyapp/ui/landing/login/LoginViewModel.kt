package com.dicoding.storyapp.ui.landing.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.AuthRepository
import com.dicoding.storyapp.data.Result
import com.dicoding.storyapp.data.remote.response.LoginResponse
import kotlinx.coroutines.launch

class LoginViewModel (private val authRepository: AuthRepository?) : ViewModel() {

    fun login (
        email: String,
        password: String
    ) : LiveData<Result<LoginResponse>> {
        val result: MutableLiveData<Result<LoginResponse>> = MutableLiveData(Result.Loading)
        viewModelScope.launch {
            result.value = authRepository?.login(email, password)
        }
        return result
    }
}