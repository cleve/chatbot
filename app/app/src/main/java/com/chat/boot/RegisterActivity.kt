package com.chat.boot

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        setSupportActionBar(findViewById(R.id.toolbar))

        // Init values.
        val email = findViewById<EditText>(R.id.editTextTextEmailAddress)
        val username = findViewById<EditText>(R.id.editTextTextUsername)
        val password = findViewById<EditText>(R.id.editTextPassword)
        val buttonRegister = findViewById<Button>(R.id.registerUser)

    }
}