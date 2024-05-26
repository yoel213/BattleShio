package com.example.batttleshipv2.Main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.batttleshipv2.Main.GameActivity.Companion.role
import com.example.batttleshipv2.Main.data.Board
import com.example.batttleshipv2.Main.data.Coordinate
import com.example.batttleshipv2.Main.data.Player
import com.example.batttleshipv2.Main.data.Role
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class GameViewModel(var actualMatch: String, var roleName: String, var owner: Player, var rival: Player) : ViewModel() {

    lateinit var myBoard: Board
    var opponentBoard: Board = Board()

    var isOpponentBoardEnabled = true

    var database: FirebaseDatabase = FirebaseDatabase.getInstance()

    lateinit var shot: Coordinate
    lateinit var shotRef: DatabaseReference

    lateinit var shotBack: Coordinate
    lateinit var shotBackRef: DatabaseReference

    var score: Int = 0
    lateinit var scoreRef: DatabaseReference

    val refreshMyBoardLiveData = MutableLiveData<Board>()
    val refreshOpponentBoardLiveData = MutableLiveData<Board>()

    fun initReferences() {

        shotRef = database.getReference(Firebase.MATCHES)
            .child(actualMatch + "/" + Firebase.SHOT)

        shotBackRef = database.getReference(Firebase.MATCHES)
            .child(actualMatch + "/" + Firebase.BACK)

        scoreRef = database.getReference(Firebase.MATCHES)
            .child(actualMatch + "/" + Firebase.SCORE)

        scoreRef = database.getReference(Firebase.MATCHES)
            .child(actualMatch + "/" + Firebase.SCORE)


        shotRef.setValue(null)
        scoreRef.setValue(null)
        shotBackRef.setValue(null)
    }

    fun sendCoordinate(x: Int, y: Int, roleName: String) {

        val hashMap: HashMap<String, Int> = hashMapOf()

        if (roleName == Role.JOINED.roleName)
            hashMap[role] = Role.JOINED.id
        else
            hashMap[role] = Role.CREATED.id

        hashMap[GameActivity.x] = x
        hashMap[GameActivity.y] = y

        shotRef.setValue(hashMap);

    }

    private fun sendCoordinatesBack(coordinate: Coordinate, result: Int) {

        shotBack = coordinate
        shotBackRef = database.getReference(Firebase.MATCHES)
            .child(actualMatch + "/" + Firebase.BACK)

        val hashMap: HashMap<String, Int> = hashMapOf()

        if (roleName == Role.JOINED.roleName)
            hashMap[GameActivity.role] = Role.JOINED.id
        else
            hashMap[GameActivity.role] = Role.CREATED.id

        hashMap[GameActivity.result] = result
        hashMap[GameActivity.x] = shotBack.x
        hashMap[GameActivity.y] = shotBack.y

        shotBackRef.setValue(hashMap);
    }

    fun sendScore(score: Int, roleName: String, win: Int) {

        val hashMap: HashMap<String, Int> = hashMapOf()

        if (roleName == Role.JOINED.roleName)
            hashMap[GameActivity.role] = Role.CREATED.id
        else
            hashMap[GameActivity.role] = Role.JOINED.id

        hashMap[GameActivity.score] = score
        hashMap[GameActivity.win] = win

        scoreRef.setValue(hashMap);
    }

    fun isCREATED(): Boolean{
        return roleName == Firebase.CREATED
    }

    fun shotCoordinatesReceived(dataSnapshot: DataSnapshot): String {

        val coordinate: Coordinate = getCoordinatesFromSnapshot(dataSnapshot)
        var result: Int = myBoard.fieldStatus[coordinate.x][coordinate.y]

        if (myBoard.availableShot(coordinate)) {
            result = myBoard.boxOccuped(coordinate)
        }

        refreshMyBoardLiveData.value = myBoard

        sendCoordinatesBack(coordinate, result)

        return myBoard.boatKilledAdvise(coordinate)
    }

    fun scoreSum(dataSnapshot: DataSnapshot) : Int {
        return myBoard.scoreSum(getCoordinatesFromSnapshot(dataSnapshot))
    }

    private fun getCoordinatesFromSnapshot(dataSnapshot: DataSnapshot) : Coordinate{
        val x: Int = dataSnapshot.child(GameActivity.x).value.toString().toInt()
        val y: Int = dataSnapshot.child(GameActivity.y).value.toString().toInt()
        return Coordinate(x, y)
    }

    fun shotBackCoordinatesReceived(dataSnapshot: DataSnapshot) {

        val coordinate: Coordinate = getCoordinatesFromSnapshot(dataSnapshot)
        val result: Int = dataSnapshot.child("result").value.toString().toInt()

        opponentBoard.fieldStatus[coordinate.x][coordinate.y] = result

        refreshOpponentBoardLiveData.value = opponentBoard
    }
}