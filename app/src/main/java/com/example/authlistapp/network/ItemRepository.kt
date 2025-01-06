package com.example.authlistapp.network

import com.example.authlistapp.model.PostListResponse

class PostRepository {

    suspend fun getPosts(page: Int, limit: Int): retrofit2.Response<PostListResponse> {
        return RetrofitInstance.getApi().getPosts(page, limit)
    }

}
