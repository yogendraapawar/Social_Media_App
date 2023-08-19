package com.example.myapplication.Schema

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore


class Post (
    var postId: String? = null,
    var username:String?=null,
    var caption:String?=null,
    var imageUrl:ArrayList<String>?=null,
    var tags: String?=null,
    var timestamp: Timestamp?=null,
    var likes:Int?=null,
    var comments:ArrayList<Comment>?=null

        ){
    fun generatePostId() {
        val db = FirebaseFirestore.getInstance()
        val postRef = db.collection("posts").document()
        postId = postRef.id
    }
}

