package com.example.authlistapp.model

data class PostListResponseItem(
    val body: String,
    val id: Int,
    val title: String,
    val userId: Int
)