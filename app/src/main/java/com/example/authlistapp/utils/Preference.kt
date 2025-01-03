package com.example.myfirebaseapplication.utils

import android.content.Context
import android.content.SharedPreferences

class Preference {

    enum class Keys() {
      isUserLogin
    }

    private constructor()

    companion object {

        private lateinit var sharedPreferences: SharedPreferences

        fun getInstance(context: Context): Preference {
            sharedPreferences = context.getSharedPreferences("MugMation", Context.MODE_PRIVATE)
            return Preference()
        }
    }

    fun <T> putData(keys: Keys, value: T) {
        when (value) {
            is String -> {
                sharedPreferences.edit().putString(keys.name, value).apply()
            }

            is Boolean -> {
                sharedPreferences.edit().putBoolean(keys.name, value).apply()
            }

            is Int -> {
                sharedPreferences.edit().putInt(keys.name, value).apply()
            }
        }
    }

    fun <T> getData(keys: Keys, defaultValue: T): T? {
        return when (defaultValue) {
            is String -> sharedPreferences.getString(keys.name, defaultValue) as T
            is Boolean -> sharedPreferences.getBoolean(keys.name, defaultValue) as T
            is Int -> sharedPreferences.getInt(keys.name, defaultValue) as T
            else -> null
        }
    }
}