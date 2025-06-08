package com.example.annam

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class Logup : AppCompatActivity() {

    private lateinit var mEmail: EditText
    private lateinit var mPassword: EditText
    private lateinit var mLoginBtn: Button
    private lateinit var mRegisterBtn: TextView
    private lateinit var fAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logup)

        mEmail = findViewById(R.id.email)
        mPassword = findViewById(R.id.password)
        mRegisterBtn = findViewById(R.id.register)
        mLoginBtn = findViewById(R.id.login)

        fAuth = FirebaseAuth.getInstance()

        if (fAuth.currentUser != null) {
            val intent = Intent(this@Logup, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        mLoginBtn.setOnClickListener {
            val email = mEmail.text.toString().trim()
            val password = mPassword.text.toString().trim()

            if (TextUtils.isEmpty(email)) {
                mEmail.error = "Email is Required."
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(password)) {
                mPassword.error = "Password is Required."
                return@setOnClickListener
            }

            if (password.length < 6) {
                mPassword.error = "Password Must be >=6 Characters"
                return@setOnClickListener
            }

            // authenticate the user
            fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task: Task<AuthResult> ->
                if (task.isSuccessful) {
                    Toast.makeText(this@Logup, "Logged in Successfully.", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@Logup, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                } else {
                    Toast.makeText(this@Logup, "Error! " + task.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        mRegisterBtn.setOnClickListener {
            // redirect to RegisterActivity
            val intent = Intent(applicationContext, Signup::class.java)
            startActivity(intent)
        }
    }
}

