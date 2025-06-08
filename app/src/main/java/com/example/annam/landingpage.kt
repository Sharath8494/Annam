package com.example.annam

import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import android.content.Intent
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth

class LandingPage : AppCompatActivity() {

    private lateinit var login: CardView
    private lateinit var register: CardView
    private lateinit var about: CardView
    private lateinit var fAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landingpage)

        login = findViewById(R.id.cardLogin)
        register = findViewById(R.id.cardRegister)
        about = findViewById(R.id.cardAboutus)

        fAuth = FirebaseAuth.getInstance()
        if (fAuth.currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        login.setOnClickListener {
            startActivity(Intent(applicationContext, Logup::class.java))
        }
        register.setOnClickListener {
            startActivity(Intent(applicationContext, Signup::class.java))
        }
        about.setOnClickListener {
            startActivity(Intent(applicationContext, About::class.java))
        }
    }
}

