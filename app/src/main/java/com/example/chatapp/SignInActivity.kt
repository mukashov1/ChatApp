package com.example.chatapp

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SignInActivity : AppCompatActivity() {

    private lateinit var signInIconImageView: ImageView
    private lateinit var emailEditText: EditText
    private lateinit var nameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var repeatPasswordEditText: EditText
    private lateinit var signUpButton: Button
    private lateinit var toggleSignInTextView: TextView

    private var isSignInActive = false

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var usersReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        auth = Firebase.auth
        database = Firebase.database
        usersReference = database.getReference("users")

        signInIconImageView = findViewById(R.id.signInIcon)
        emailEditText = findViewById(R.id.emailEditText)
        nameEditText = findViewById(R.id.nameEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        repeatPasswordEditText = findViewById(R.id.repeatPasswordEditText)
        signUpButton = findViewById(R.id.signUpButton)
        toggleSignInTextView = findViewById(R.id.toggleSignInTextView)

        if (auth.currentUser != null) startActivity(Intent(this, MainActivity::class.java))

        signUpButton.setOnClickListener {
            signUpUser(
                emailEditText.text.toString().trim(), passwordEditText.text.toString().trim()
            )
        }

    }

    private fun signUpUser(email: String, password: String) {

        if (isSignInActive) {

            when {
                email == "" -> Toast.makeText(
                    baseContext, "Please input your email", Toast.LENGTH_SHORT
                ).show()
                password.length < 7 -> Toast.makeText(
                    baseContext, "Password length must be at least 7 characters", Toast.LENGTH_SHORT
                ).show()
                else -> {
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                Log.d(TAG, "signInWithEmail:success")
                                val user = auth.currentUser
                                startActivity(Intent(this, MainActivity::class.java))
                            } else {
                                Log.w(TAG, "signInWithEmail:failure", task.exception)
                                Toast.makeText(
                                    baseContext, "Authentication failed.", Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                }
            }

        } else {

            when {
                email == "" -> Toast.makeText(
                    baseContext, "Please input your email", Toast.LENGTH_SHORT
                ).show()
                password.length < 7 -> Toast.makeText(
                    baseContext, "Password length must be at least 7 characters", Toast.LENGTH_SHORT
                ).show()
                password != repeatPasswordEditText.text.toString().trim() -> Toast.makeText(
                    baseContext, "Passwords doesn't match", Toast.LENGTH_SHORT
                ).show()
                else -> {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                Log.d(TAG, "createUserWithEmail:success")
                                val user = auth.currentUser
                                createUser(user)
                                var intent = Intent(this, MainActivity::class.java)
                                intent.putExtra("userName", nameEditText.text.toString().trim())
                                startActivity(intent)
                            } else {
                                Log.w(TAG, "createUserWithEmail:failure", task.exception)
                                Toast.makeText(
                                    baseContext, "Authentication failed.", Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                }
            }
        }
    }

    private fun createUser(firebaseUser: FirebaseUser?) {
        var user = User()
        user.id = firebaseUser?.uid
        user.email = firebaseUser?.email
        user.name = nameEditText.text.toString().trim()

        usersReference.push().setValue(user)
    }

    fun toggleSignIn(view: View) {
        if (isSignInActive) {
            isSignInActive = false
            signUpButton.text = "Sign Up"
            toggleSignInTextView.text = "Tap To Sign In"
            repeatPasswordEditText.visibility = View.VISIBLE
        } else {
            isSignInActive = true
            signUpButton.text = "Sign In"
            toggleSignInTextView.text = "Tap To Sing Up"
            repeatPasswordEditText.visibility = View.GONE
        }
    }
}