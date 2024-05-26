package com.example.batttleshipv2.Main
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.example.batttleshipv2.R
import com.example.batttleshipv2.getViewModel
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseUser



class MainActivity : AppCompatActivity() {

    companion object {
        const val OWNER = "OWNER"
        const val CURRENTMATCH = "CURRENTMATCH"
        const val CURRENTROLE = "CURRENTROLE"
        const val RIVAL = "RIVAL"
    }

    private val viewModel by lazy {
        getViewModel { MainViewModel() }
    }
    private lateinit var logOutBtn: Button
    private lateinit var starBtn: Button
    private lateinit var findMatch: Button
    private lateinit var userLogIngV: TextView
    private lateinit var scoreBtn: Button
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        logOutBtn = findViewById(R.id.logOutBtn)
        scoreBtn = findViewById(R.id.scoreBtn)
        starBtn = findViewById(R.id.starBtn)
        findMatch = findViewById(R.id.findMatch)
        userLogIngV = findViewById(R.id.userLogIngV)
        logOutBtn.setOnClickListener {

            AuthUI.getInstance().signOut(this@MainActivity).addOnCompleteListener {
                Toast.makeText(applicationContext, "Desconectado", Toast.LENGTH_SHORT).show()
                launchLoginActivity()
            }
        }
        scoreBtn.setOnClickListener {
            var intent = Intent(this, ScoreActivity::class.java)
            startActivity(intent)
        }

        findMatch.setOnClickListener {
            val intent = Intent(this, MatchActivity::class.java)
            intent.putExtra(OWNER, viewModel.getowner())
            startActivity(intent)
        }

        starBtn.setOnClickListener {
            launchLoginActivity()
        }
    }

    private fun launchLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        this.startActivity(intent)
        finish()
    }

    public override fun onStart() {
        super.onStart()
        updateUI(Firebase.currentUser())
    }

    private fun updateUI(currentUser: FirebaseUser?) {

        if (currentUser != null) {

            userLogIngV.text = String.format("Iniciado como: - %s", currentUser.displayName)
            starBtn.visibility = View.GONE
            logOutBtn.visibility = View.VISIBLE
            findMatch.visibility = View.VISIBLE

        } else {

            starBtn.visibility = View.VISIBLE
            logOutBtn.visibility = View.GONE
            findMatch.visibility = View.GONE
        }
    }
}