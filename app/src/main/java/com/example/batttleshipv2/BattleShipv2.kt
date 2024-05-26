package com.example.batttleshipv2

import android.app.Application
import android.util.Log
import com.google.firebase.database.FirebaseDatabase
open class BattleShipv2: Application() {
    override fun onCreate() {
        super.onCreate()

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        Log.d("Battle Ship", "Juego iniciado");
    }
}