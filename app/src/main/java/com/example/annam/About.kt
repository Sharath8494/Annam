package com.example.annam

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class About : AppCompatActivity() {

    private lateinit var instagram: CardView
    private lateinit var facebook: CardView
    private lateinit var twitter: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        instagram = findViewById(R.id.instagram)
        facebook = findViewById(R.id.facebook)
        twitter = findViewById(R.id.twitter)

        instagram.setOnClickListener {
            val myWebLink = Intent(Intent.ACTION_VIEW)
            myWebLink.data = Uri.parse("http://www.instagram.com")
            startActivity(myWebLink)
        }
        facebook.setOnClickListener {
            val myWebLink = Intent(Intent.ACTION_VIEW)
            myWebLink.data = Uri.parse("http://www.facebook.com")
            startActivity(myWebLink)
        }
        twitter.setOnClickListener {
            val myWebLink = Intent(Intent.ACTION_VIEW)
            myWebLink.data = Uri.parse("http://www.twitter.com")
            startActivity(myWebLink)
        }
    }
}


