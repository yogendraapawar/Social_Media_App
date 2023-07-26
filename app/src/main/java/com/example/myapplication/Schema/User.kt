package com.example.myapplication.Schema

data class User(
    var firstname:String?=null,
    var lastname:String?=null,
    var email:String?=null,
    var password:String?=null,
    var username:String?=null,
    var posts:ArrayList<Post>?=null,
    var searchByUsername: String?=null,
    var searchByName: String?=null,
    var followers: ArrayList<String>?=null,
    var following: ArrayList<String>? =null
)