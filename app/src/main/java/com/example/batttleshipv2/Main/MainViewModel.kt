package com.example.batttleshipv2.Main

import androidx.lifecycle.ViewModel
import com.example.batttleshipv2.Main.data.Player

class MainViewModel : ViewModel() {

    fun getowner(): Player {
        val currentUser = Firebase.currentUser()
        return Player(currentUser?.displayName.toString(), 0)
    }
}