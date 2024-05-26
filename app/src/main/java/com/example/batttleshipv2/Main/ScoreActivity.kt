package com.example.batttleshipv2.Main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.batttleshipv2.Main.Firebase.database
import com.example.batttleshipv2.Main.data.PersonScore
import com.example.batttleshipv2.Main.data.Player
import com.example.batttleshipv2.Main.data.ScoreAdapter
import com.example.batttleshipv2.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ScoreActivity  : AppCompatActivity() {
    lateinit var pointsList: ArrayList<PersonScore>
    lateinit var rv: RecyclerView
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score)
        database = Firebase.database


        rv = findViewById(R.id.rv_score)
        rv.layoutManager = LinearLayoutManager(this@ScoreActivity)
        pointsList = arrayListOf<PersonScore>()
        val myRef = Firebase.database.getReference("ScoresTables")
        myRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var j=0
                for(data in snapshot.children){
                    j++
                    val name = data.child("name").getValue(String::class.java).toString()
                    val score = data.child("score").getValue(Int::class.java).toString()
                    val toAdd = PersonScore(name,score)
                    pointsList.add(toAdd)
                }

                rv.adapter = ScoreAdapter(pointsList)
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}