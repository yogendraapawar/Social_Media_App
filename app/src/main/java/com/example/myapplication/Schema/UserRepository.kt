package com.example.myapplication.Schema

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class UserRepository {
    private val databaseReference: DatabaseReference =FirebaseDatabase.getInstance().getReference("users")

}