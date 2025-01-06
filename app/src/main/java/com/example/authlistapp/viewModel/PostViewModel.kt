package com.example.authlistapp.viewModel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.authlistapp.model.PostListResponse
import com.example.authlistapp.network.PostRepository
import com.example.authlistapp.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PostViewModel (val application: Application, var authRepository: PostRepository) :
    ViewModel() {

    var postResponse = MutableLiveData<Resource<PostListResponse>>()

    fun bookingApi(page: Int, limit: Int) {
        postResponse.value = Resource.loading(data = null)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = authRepository.getPosts(page, limit)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()

                        if (response.code() == 200) {
                            postResponse.value = Resource.success(
                                data = responseBody,
                                message = responseBody.toString()
                            )
                        } else {
                            postResponse.value = Resource.error(
                                data = responseBody,
                                message = responseBody.toString()
                            )
                        }
                    } else {
                        postResponse.value = Resource.error(
                            data = null,
                            message = response.message()
                        )
                    }
                }
            } catch (t: Throwable) {
                withContext(Dispatchers.Main) {
                    postResponse.value = Resource.error(
                        data = null,
                        message = t.message.toString()
                    )
                }
            }
        }
    }
}

