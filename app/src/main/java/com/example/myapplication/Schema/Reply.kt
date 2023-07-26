package com.example.myapplication.Schema

import com.google.firebase.Timestamp


data class Reply(
    var replyId: String? = null,
    var userId: String? = null,
    var commentId: String? = null,
    var message: String? = null,
    var timestamp: Timestamp? = null
)