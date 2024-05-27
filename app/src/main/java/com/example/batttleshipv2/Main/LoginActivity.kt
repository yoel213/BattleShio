package com.example.batttleshipv2.Main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import com.firebase.ui.auth.AuthUI
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import com.example.batttleshipv2.R
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase



class LoginActivity : AppCompatActivity() {

    companion object {
        const val id = "id"
        const val username = "username"
        const val RC_SIGN_IN: Int = 1234
    }
    private lateinit var buttonSignIn: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        buttonSignIn = findViewById(R.id.buttonSignIn)
        buttonSignIn.setOnClickListener {
            if (Firebase.currentUser() != null) {
                Toast.makeText(applicationContext, "Inicia Sesion PAra continuar", Toast.LENGTH_SHORT).show()
            } else {
                val providers = listOf(AuthUI.IdpConfig.EmailBuilder().build())
                startActivityForResult(
                    AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build(), RC_SIGN_IN
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {
                val user = Firebase.currentUser()

                addPlayer(user)
            }
        } else {
            Toast.makeText(applicationContext, "Error de conexion", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addPlayer(user: FirebaseUser?) {
        val userId: String = user!!.uid
        val reference = FirebaseDatabase.getInstance().getReference(Firebase.USERS).child(userId)

        val hashMap: HashMap<String, String> = hashMapOf()
        hashMap[id] = userId
        hashMap[username] = user.displayName.toString()

        reference.setValue(hashMap).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Toast.makeText(applicationContext, "Bienvenido", Toast.LENGTH_SHORT).show()
                launchMainActivity(user)
            } else {
                Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT).show()
            }
        }

    }
    private fun launchMainActivity(user: FirebaseUser?) {
        if (user != null) {
            val intent = Intent(this, MainActivity::class.java)
            this.startActivity(intent)
            finish();
        }
    }
}
