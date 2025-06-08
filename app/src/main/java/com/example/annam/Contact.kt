package com.example.annam

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class Contact : AppCompatActivity() {
    private lateinit var name: EditText
    private lateinit var email: EditText
    private lateinit var message: EditText
    private lateinit var submit: Button
    private var isNameValid = false
    private var isEmailValid = false
    private var isMessageValid = false
    private lateinit var fAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore
    private lateinit var userID: String
    private lateinit var nameError: TextInputLayout
    private lateinit var emailError: TextInputLayout
    private lateinit var messageError: TextInputLayout

    companion object {
        const val TAG = "TAG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)
        name = findViewById(R.id.name)
        email = findViewById(R.id.email)
        message = findViewById(R.id.message)
        submit = findViewById(R.id.submit)
        nameError = findViewById(R.id.nameError)
        emailError = findViewById(R.id.emailError)
        messageError = findViewById(R.id.messageError)

        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()

        submit.setOnClickListener {
            setValidation()
        }
    }

    private fun setValidation() {
        // Check for a valid name.
        if (name.text.toString().isEmpty()) {
            nameError.error = resources.getString(R.string.name_error)
            isNameValid = false
        } else {
            isNameValid = true
            nameError.isErrorEnabled = false
        }

        // Check for a valid email address.
        if (email.text.toString().isEmpty()) {
            emailError.error = resources.getString(R.string.email_error)
            isEmailValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()) {
            emailError.error = resources.getString(R.string.error_invalid_email)
            isEmailValid = false
        } else {
            isEmailValid = true
            emailError.isErrorEnabled = false
        }

        // Check for a valid message.
        if (message.text.toString().isEmpty()) {
            messageError.error = resources.getString(R.string.phone_error)
            isMessageValid = false
        } else {
            isMessageValid = true
            messageError.isErrorEnabled = false
        }

        if (isNameValid && isEmailValid && isMessageValid) {
            val Name = name.text.toString().trim()
            val Email = email.text.toString().trim()
            val Message = message.text.toString().trim()
            userID = fAuth.currentUser?.uid ?: ""
            val collectionReference = fStore.collection("contact data")

            val user = hashMapOf(
                "timestamp" to FieldValue.serverTimestamp(),
                "name" to Name,
                "email" to Email,
                "message" to Message,
                "userid" to userID
            )

            collectionReference.add(user)
                .addOnSuccessListener { documentReference ->
                    Toast.makeText(applicationContext, "Success!", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "Successfully! We will shortly revert you back.")
                    val intent = Intent(this@Contact, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(applicationContext, "Error!", Toast.LENGTH_SHORT).show()
                    Log.w(TAG, "Error!", e)
                }
        }
    }
}


