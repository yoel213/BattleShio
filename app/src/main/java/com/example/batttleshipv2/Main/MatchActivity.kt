package com.example.batttleshipv2.Main

import android.R
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.batttleshipv2.Main.MainActivity.Companion.OWNER
import com.example.batttleshipv2.Main.MainActivity.Companion.CURRENTROLE
import com.example.batttleshipv2.Main.MainActivity.Companion.CURRENTMATCH
import com.example.batttleshipv2.Main.MainActivity.Companion.RIVAL
import com.example.batttleshipv2.Main.data.Player
import com.google.firebase.database.*


class MatchActivity: AppCompatActivity() {

    lateinit var owner: Player
    var actualMatch: String = ""
    var roleName: String = ""
    private lateinit var matchListener: ValueEventListener
    private lateinit var database: FirebaseDatabase
    private lateinit var roomsRef: DatabaseReference
    private lateinit var roomRef: DatabaseReference
    private lateinit var createRoomButton: Button
    private lateinit var roomsView: ListView

    var availableMatches: ArrayList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.batttleshipv2.R.layout.activity_match)
        title = "Buscar Partida"
        createRoomButton = findViewById(com.example.batttleshipv2.R.id.createRoomButton)
        roomsView = findViewById(com.example.batttleshipv2.R.id.roomsView)

        database = Firebase.database
        owner = intent.getParcelableExtra(OWNER)!!

        createRoomButton.setOnClickListener {

            createRoomButton.text = "Crear Partida"
            createRoomButton.isEnabled = false
            actualMatch = owner.playerName
            addNewRoom(owner.playerName)
        }

        roomsView.setOnItemClickListener { _: AdapterView<*>, _: View, position: Int, _: Long ->
            actualMatch = availableMatches[position]
            addToExistingRoom(actualMatch, owner.playerName)
        }

        showRooms()
    }
    private fun showRooms() {
        roomsRef = database.getReference(Firebase.MATCHES)
        matchListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                availableMatches.clear()
                val rooms: Iterable<DataSnapshot> = dataSnapshot.children
                for (snapShot: DataSnapshot in rooms) {
                    availableMatches.add(snapShot.key!!)
                    val adapter: ArrayAdapter<String> = ArrayAdapter(
                        this@MatchActivity, R.layout.simple_list_item_1,
                        availableMatches
                    )
                    roomsView.adapter = adapter
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
        roomsRef.addValueEventListener(matchListener)

    }

    private fun addToExistingRoom(actualMatch: String, username: String) {

        if (actualMatch == username) {
            roomRef = database.getReference(Firebase.MATCHES)
                .child(actualMatch + "/" + Firebase.CREATED)
            roomRef.setValue(username)
            roleName = Firebase.CREATED
        } else {
            roomRef = database.getReference(Firebase.MATCHES)
                .child(actualMatch + "/" + Firebase.JOINED)
            roomRef.setValue(username)
            roleName = Firebase.JOINED
        }

        setRoomValue(roomRef, "Partida Encontrada")
    }

    private fun addNewRoom(username: String) {

        roomRef = database.getReference(Firebase.MATCHES)
            .child(username + "/" + Firebase.CREATED)

        roomRef.setValue(username)
        roleName = Firebase.CREATED

        setRoomValue(roomRef, "Partida Creada")
    }

    private fun setRoomValue(roomRef: DatabaseReference, message: String) {

        val roomListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                availableMatches.clear()

                Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
                createRoomButton.text = "Crear Partida"
                createRoomButton.isEnabled = true

                val intent = Intent(this@MatchActivity, ConfigActivity::class.java)

                intent.apply {
                    putExtra(RIVAL, Player("Player", 0))
                    putExtra(OWNER, owner)
                    putExtra(CURRENTMATCH, actualMatch)
                    putExtra(CURRENTROLE, roleName)
                }

                startActivity(intent)
                finish()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT).show()
                createRoomButton.text = "Crear Partida"
                createRoomButton.isEnabled = true
            }
        }
        roomRef.addValueEventListener(roomListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        roomsRef.removeEventListener(matchListener)
    }
}