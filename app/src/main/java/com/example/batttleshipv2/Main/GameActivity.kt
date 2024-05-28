package com.example.batttleshipv2.Main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.GridView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.batttleshipv2.Main.ConfigActivity.Companion.BOARD
import com.example.batttleshipv2.Main.ConfigActivity.Companion.BOATS
import com.example.batttleshipv2.Main.MainActivity.Companion.OWNER
import com.example.batttleshipv2.Main.MainActivity.Companion.CURRENTROLE
import com.example.batttleshipv2.Main.MainActivity.Companion.CURRENTMATCH
import com.example.batttleshipv2.Main.MainActivity.Companion.RIVAL
import com.example.batttleshipv2.Main.data.*
import com.example.batttleshipv2.R
import com.example.batttleshipv2.getViewModel
import com.google.firebase.database.*
import kotlin.math.floor

class GameActivity : AppCompatActivity() {

    companion object{
        const val result = "result"
        const val score = "score"
        const val role = "role"
        const val win = "win"
        const val x = "x"
        const val y = "y"
    }
    private lateinit var scoreText: TextView
    private lateinit var myScoreText: TextView
    private lateinit var myBoardView: GridView
    private lateinit var opponentBoardView: GridView
    private lateinit var myBoardAdapter: BoardGridAdapter
    private lateinit var opponentBoardAdapter: BoardGridAdapter
    lateinit var owner: Player
    var actualMatch: String = ""



private val viewModel by lazy {
    getViewModel {
        val actualMatch = intent.getStringExtra(CURRENTMATCH)
        val roleName = intent.getStringExtra(CURRENTROLE)
        val owner: Player = intent.getParcelableExtra(OWNER)!!
        val rival: Player = intent.getParcelableExtra(RIVAL)!!
        GameViewModel(actualMatch!!, roleName!!, owner, rival) }
}

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        scoreText = findViewById(R.id.scoreText)
        myScoreText = findViewById(R.id.myScoreText)
        myBoardView = findViewById(R.id.myBoardView)
        opponentBoardView = findViewById(R.id.opponentBoardView)
        initObservers()

        initScores()
        initOwnBoardAdapter()
        initOpponentBoardAdapter()

        viewModel.initReferences()

        addShotListener()
        addScoreListener()
        addShotBackListener()
        //addScoresToDbListener()

    }

    private fun initObservers() {
        viewModel.apply {
            refreshMyBoardLiveData.observe(this@GameActivity,
                Observer { board -> refreshMyBoard(board.fieldStatus) })
            refreshOpponentBoardLiveData.observe(this@GameActivity,
                Observer { board -> refreshOpponentBoard(board.fieldStatus) })
        }
    }

    private fun initScores() {
        scoreText.text = viewModel.rival.score.toString()
        myScoreText.text = viewModel.owner.score.toString()
    }

    private fun initOwnBoardAdapter() {
        viewModel.myBoard = Board()

        viewModel.myBoard.fieldStatus = intent?.extras?.getSerializable(BOARD) as Array<Array<Int>>
        viewModel.myBoard.ships = intent?.extras?.getSerializable(BOATS) as ArrayList<Ship>

        myBoardAdapter = BoardGridAdapter(this, viewModel.myBoard.fieldStatus)
        { view: View, position: Int -> handleBoardClick(view, position) }

        myBoardView.adapter = myBoardAdapter
    }

    private fun initOpponentBoardAdapter() {

        opponentBoardAdapter = BoardGridAdapter(this, viewModel.opponentBoard.fieldStatus)
        { view: View, position: Int -> handleBoardClick(view, position) }

        opponentBoardView.adapter = opponentBoardAdapter
    }

    private fun handleBoardClick(view: View, position: Int) {

        if ((view.parent as FrameLayout).parent == myBoardView) {

            return

        } else if (!viewModel.isOpponentBoardEnabled) {

            Toast.makeText(this, "Por favor espera tu turno", Toast.LENGTH_SHORT).show()
            return

        } else {

            val x: Int = floor((position / viewModel.opponentBoard.boardX).toDouble()).toInt()
            val y: Int = position % viewModel.opponentBoard.boardX

            viewModel.isOpponentBoardEnabled = false
            viewModel.sendCoordinate(x, y, viewModel.roleName)
        }
    }

    private fun addShotListener() {

        var shotListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.value != null) {

                    if (viewModel.isCREATED()) {

                        if (dataSnapshot.child(role).value.toString() == Role.JOINED.id.toString()) {

                            viewModel.isOpponentBoardEnabled = true

                            shotCoordinatesReceived(dataSnapshot)

                        }
                    } else {

                        if (dataSnapshot.child(role).value.toString() == Role.CREATED.id.toString()) {

                            viewModel.isOpponentBoardEnabled = true

                            shotCoordinatesReceived(dataSnapshot)

                        }
                    }
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                this@GameActivity.viewModel.shotRef.setValue(viewModel.shot)
            }
        }
        this.viewModel.shotRef.addValueEventListener(shotListener)
    }

    private fun shotCoordinatesReceived(dataSnapshot: DataSnapshot) {

        val shipKilled = viewModel.shotCoordinatesReceived(dataSnapshot)

        if (!shipKilled.isBlank()) {

            Toast.makeText(this, "Ouch! $shipKilled ha sido destruido!", Toast.LENGTH_LONG).show()

            val points: Int = viewModel.scoreSum(dataSnapshot)

            viewModel.rival.addScore(points)
            viewModel.score = viewModel.rival.score
            scoreText.text =  viewModel.score.toString()

            if (viewModel.myBoard.gameOver()) {
                val user = Firebase.currentUser()
                val userId: String = user!!.uid
                var database: FirebaseDatabase = FirebaseDatabase.getInstance()
                owner = intent.getParcelableExtra(OWNER)!!
                var scores: DatabaseReference = database.getReference("ScoresTables").child(userId)
                var name = scores.child("name")
                var scoreV = scores.child("score")
                name.setValue(owner.playerName)
                scoreV.setValue(viewModel.score)
                viewModel.sendScore(viewModel.score, viewModel.roleName, 1)
                showMessage("Final", "Juego finalizadoo")
            } else {
                viewModel.sendScore(viewModel.score, viewModel.roleName, 0)
            }
        }

    }

    private fun addShotBackListener() {
        var shotBackListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.value != null) {
                    if (viewModel.isCREATED()) {
                        if (dataSnapshot.child(role).value.toString() == Role.JOINED.id.toString()) {
                            viewModel.shotBackCoordinatesReceived(dataSnapshot)
                        }
                    } else {
                        if (dataSnapshot.child(role).value.toString() == Role.CREATED.id.toString()) {
                            viewModel.shotBackCoordinatesReceived(dataSnapshot)
                        }
                    }

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                this@GameActivity.viewModel.shotRef.setValue(viewModel.shot)
            }
        }
        this.viewModel.shotBackRef.addValueEventListener(shotBackListener)
    }

    private fun addScoreListener() {
        var scoreListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.value != null) {
                    if (viewModel.isCREATED()) {
                        if (dataSnapshot.child(role).value.toString() == Role.CREATED.id.toString())
                            setScoreReceived(dataSnapshot)
                    } else {
                        if (dataSnapshot.child(role).value.toString() == Role.JOINED.id.toString())
                            setScoreReceived(dataSnapshot)
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                this@GameActivity.viewModel.scoreRef.setValue(score)
            }
        }
        this.viewModel.scoreRef.addValueEventListener(scoreListener)
    }

    private fun setScoreReceived(dataSnapshot: DataSnapshot) {

        val score: String = dataSnapshot.child(score).value.toString()
        viewModel.owner.score = score.toInt()
        myScoreText.text = score

        val win: Int = dataSnapshot.child(win).value.toString().toInt()
        if (win == 1) showMessage("Felicidades", "Ganastte")


    }
/*    private fun addScoresToDbListener(){
        val user = Firebase.currentUser()
        val userId: String = user!!.uid
        val points: Int = viewModel.owner.score
        var database: FirebaseDatabase = FirebaseDatabase.getInstance()
        owner = intent.getParcelableExtra(OWNER)!!
        var scores: DatabaseReference = database.getReference("ScoresTables").child(userId)
        //val reference = FirebaseDatabase.getInstance().getReference(Firebase.USERS).child(userId)
        var name = scores.child("name")
        var scoreV = scores.child("score")
        name.setValue(owner.playerName)
        scoreV.setValue(viewModel.sendScore())


    }*/

    private fun showMessage(title: String, subTitle: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(subTitle)

        builder.setPositiveButton(android.R.string.ok) { _, _ ->

            viewModel.owner.score = 0
            viewModel.rival.score = 0

            val intent = Intent(this@GameActivity, MainActivity::class.java)
            startActivity(intent)
        }

        builder.show()
    }

    private fun refreshMyBoard(fieldStatus: Array<Array<Int>>) {
        myBoardAdapter.refresh(fieldStatus)
        Toast.makeText(this, "¡¡Tu turno de atacar!!", Toast.LENGTH_LONG).show()

    }

    private fun refreshOpponentBoard(fieldStatus: Array<Array<Int>>) {
        opponentBoardAdapter.refresh(fieldStatus)
    }

}