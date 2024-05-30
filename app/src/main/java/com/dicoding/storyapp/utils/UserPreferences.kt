package com.dicoding.storyapp.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = "token")
class UserPreferences private constructor(private val dataStore: DataStore<Preferences>){
    private val TOKEN_KEY = stringPreferencesKey("user_token")

    fun getUserToken(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[TOKEN_KEY] ?: ""
        }
    }

    suspend fun saveUserToken(userToken: String) {
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = userToken
        }
    }

    suspend fun deleteUserToken() {
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = ""
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): UserPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}