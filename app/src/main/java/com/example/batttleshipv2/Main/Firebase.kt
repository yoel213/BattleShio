package com.example.batttleshipv2.Main

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

object Firebase {

    const val SHOT: String = "shot"
    const val BACK: String = "back"
    const val SCORE: String = "score"
    const val USERS: String = "users"
    const val MATCHES: String = "matches"
    const val JOINED: String = "joined"
    const val CREATED: String = "created"

    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    val database: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance()
    }

    fun currentUser() = firebaseAuth.currentUser

}