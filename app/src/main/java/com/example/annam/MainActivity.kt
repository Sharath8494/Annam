package com.example.annam

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity() {

    private lateinit var donate: CardView
    private lateinit var receive: CardView
    private lateinit var logout: CardView
    private lateinit var about: CardView
    private lateinit var contact: CardView
    private lateinit var history: CardView
    private lateinit var profileName: TextView
    private lateinit var profileEmail: TextView
    private lateinit var fAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize FirebaseAuth
        fAuth = FirebaseAuth.getInstance()

        // Reference to views
        donate = findViewById(R.id.cardDonate)
        receive = findViewById(R.id.cardReceive)
        logout = findViewById(R.id.cardLogOut)
        history = findViewById(R.id.cardHistory)
        about = findViewById(R.id.cardAboutUs)
        contact = findViewById(R.id.cardContact)
        profileName = findViewById(R.id.profile_name)
        profileEmail = findViewById(R.id.profile_email)

        // Get the current user from FirebaseAuth
        val currentUser: FirebaseUser? = fAuth.currentUser

        if (currentUser != null) {
            // Extract email and optionally set a name (here extracting from email)
            val email = currentUser.email
            profileEmail.text = email
            profileName.text = getUserNameFromEmail(email)
        } else {
            // If the user is not logged in, redirect to the LandingPage
            val intent = Intent(this@MainActivity, LandingPage::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
            finish()
        }

        // Set up click listeners for the various cards
        donate.setOnClickListener {
            startActivity(Intent(applicationContext, Donate::class.java))
        }

        receive.setOnClickListener {
            startActivity(Intent(applicationContext, Receive::class.java))
        }

        about.setOnClickListener {
            startActivity(Intent(applicationContext, About::class.java))
        }

        history.setOnClickListener {
            startActivity(Intent(applicationContext, UserdataActivity::class.java))
        }

        contact.setOnClickListener {
            startActivity(Intent(applicationContext, Contact::class.java))
        }

        logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this@MainActivity, LandingPage::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
            finish()
        }
    }

    // Function to extract username from email
    private fun getUserNameFromEmail(email: String?): String {
        return email?.substringBefore("@") ?: "User"
    }
}