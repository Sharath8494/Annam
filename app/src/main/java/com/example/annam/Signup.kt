package com.example.annam

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.annam.MainActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class Signup : AppCompatActivity() {
    companion object {
        const val TAG = "TAG"
    }

    private lateinit var mFullName: EditText
    private lateinit var mEmail: EditText
    private lateinit var mPassword: EditText
    private lateinit var mPhone: EditText
    private lateinit var mRegisterBtn: Button
    private lateinit var mLoginBtn: TextView
    private lateinit var fAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore
    private lateinit var userID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        mFullName = findViewById(R.id.name)
        mEmail = findViewById(R.id.email)
        mPassword = findViewById(R.id.password)
        mPhone = findViewById(R.id.phone)
        mRegisterBtn = findViewById(R.id.register)
        mLoginBtn = findViewById(R.id.login)

        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()

        if (fAuth.currentUser != null) {
            val intent = Intent(this@Signup, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        mRegisterBtn.setOnClickListener {
            val email = mEmail.text.toString().trim()
            val password = mPassword.text.toString().trim()
            val name = mFullName.text.toString().trim()
            val phone = mPhone.text.toString().trim()

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

            fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this@Signup, "User Created.", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(applicationContext, Logup::class.java))
                    userID = fAuth.currentUser!!.uid
                    val documentReference: DocumentReference = fStore.collection("users").document(userID)
                    val user: MutableMap<String, Any> = HashMap()
                    user["name"] = name
                    user["email"] = email
                    user["phone"] = phone
                    documentReference.set(user).addOnSuccessListener {
                        Log.d(TAG, "onSuccess: user Profile is created for $userID")
                    }
                } else {
                    Toast.makeText(this@Signup, "Error ! " + task.exception!!.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        mLoginBtn.setOnClickListener {
            startActivity(Intent(applicationContext, Logup::class.java))
        }
    }
}


