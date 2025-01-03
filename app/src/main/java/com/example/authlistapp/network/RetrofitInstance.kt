package com.example.authlistapp.network

import com.google.android.gms.common.api.Api.Client
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

object RetrofitInstance {

    private lateinit var retrofit: Retrofit
    private lateinit var REST_CLIENT: ApiService

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    fun getApi(): ApiService {
        retrofit = Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        REST_CLIENT = retrofit.create(ApiService::class.java)
        return REST_CLIENT
    }

    fun fetchDataFromMockApi(): String? {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://jsonplaceholder.typicode.com/posts/1")
            .build()

        return try {
            val response = client.newCall(request).execute()
            response.body?.string()
        } catch (e: Exception) {
            null
        }
    }

}
