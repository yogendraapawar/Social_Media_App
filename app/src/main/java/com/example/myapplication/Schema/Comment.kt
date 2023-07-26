package com.example.myapplication.Schema

import com.google.firebase.Timestamp

data class Comment(
    var commentId: String? = null,
    var userId: String? = null,
    var postId: String? = null,
    var message: String? = null,
    var timestamp: Timestamp? = null,
    var replies: List<Reply>? = null
)