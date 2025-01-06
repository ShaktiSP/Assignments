package com.example.authlistapp.viewModel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.authlistapp.network.PostRepository

class AuthViewModelFactory(
    private val application: Application,
    private val repository: PostRepository
) : ViewModelProvider.AndroidViewModelFactory(application) {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        when {
            modelClass.isAssignableFrom(PostViewModel::class.java) -> {
                return PostViewModel(application, repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
