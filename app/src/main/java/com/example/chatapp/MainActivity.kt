package com.example.chatapp

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.ProgressBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.database.ktx.snapshots
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var messageListView: ListView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var sendImageBtn: ImageView
    private lateinit var messageEditText: EditText
    private lateinit var sendMessageBtn: Button

    private var userName = "Default User"

    private lateinit var database: FirebaseDatabase
    private lateinit var messagesRef: DatabaseReference
    private lateinit var usersRef: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intent = intent
        if (intent != null) {
            userName = intent.getStringExtra("userName").toString()
        }

        database = Firebase.database
        messagesRef = database.getReference("messages")
        usersRef = database.getReference("users")

        messageListView = findViewById(R.id.listView)
        val messages = mutableListOf<Message>()
        messageAdapter = MessageAdapter(this, R.layout.message_item, mutableListOf<Message>())
        messageListView.adapter = messageAdapter

        progressBar = findViewById(R.id.progressBar)
        sendImageBtn = findViewById(R.id.sendImageBtn)
        messageEditText = findViewById(R.id.messageEditText)
        sendMessageBtn = findViewById(R.id.sendBtn)

        progressBar.visibility = ProgressBar.INVISIBLE

        messageEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                sendMessageBtn.isEnabled = sendMessageBtn.text.trim().isNotEmpty()
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        messageEditText.filters = arrayOf(InputFilter.LengthFilter(500))

        sendMessageBtn.setOnClickListener {

            val message =
                Message(text = messageEditText.text.toString(), name = userName, imageUrl = null)

            messagesRef.push().setValue(message)

            messageEditText.text.clear()
            sendMessageBtn.isEnabled = false
        }

        messagesRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                messageAdapter.add(snapshot.getValue<Message>())
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        usersRef.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val user = snapshot.getValue<User>()
                if (user?.id == FirebaseAuth.getInstance().currentUser?.uid) {
                    userName = user?.name.toString()
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu_item, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.sign_out -> {
                Firebase.auth.signOut()
                startActivity(Intent(this, SignInActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}