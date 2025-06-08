package com.example.annam

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class History : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private val notebookRef = db.collection("user data")  // Firestore collection reference for donations
    private val usersRef = db.collection("users")  // Firestore collection reference for user profiles
    private val TAG = "TAG"
    private lateinit var textViewData: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        textViewData = findViewById(R.id.data)

        // Load all donation data, including the received user details
        loadDonationsWithUserDetails()
    }

    // Function to load donations with received user details
    private fun loadDonationsWithUserDetails() {
        notebookRef.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    var data = ""
                    if (task.result!!.isEmpty) {
                        data = "No donations available at the moment."
                    } else {
                        for (document in task.result!!) {
                            Log.d(TAG, "${document.id} => ${document.data}")

                            // Check for required fields before displaying
                            if (document.contains("name") && document.contains("description") && document.contains("status")) {
                                val name = document.getString("name")
                                val description = document.getString("description")
                                val ts = document.getTimestamp("timestamp")
                                val dateAndTime = ts?.toDate().toString()
                                val status = document.getBoolean("status") ?: false
                                val userid = document.getString("userid")

                                // Append donation details
                                data += "Name: $name\nDescription: $description\nDate & Time: $dateAndTime\nStatus: ${if (status) "Claimed" else "Available"}\n"

                                // If the donation has been claimed, fetch the user details
                                if (status && userid != null) {
                                    fetchUserDetails(userid) { userInfo ->
                                        // Append user details once they are fetched
                                        data += "Received by: $userInfo\n\n"
                                        textViewData.text = data  // Update the TextView with the new data
                                    }
                                } else {
                                    data += "\n"
                                }
                            }
                        }
                    }
                    textViewData.text = data  // Set initial data without user details (updated later)
                } else {
                    Log.d(TAG, "Error fetching data: ", task.exception)
                    textViewData.text = "Failed to load data. Please try again later."
                }
            }
    }

    // Function to fetch user details based on the userid
    private fun fetchUserDetails(userid: String, callback: (String) -> Unit) {
        usersRef.document(userid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val userName = document.getString("name") ?: "Unknown"
                    val userEmail = document.getString("email") ?: "Unknown"
                    val userInfo = "Name: $userName, Email: $userEmail"
                    callback(userInfo)  // Return the user info once fetched
                } else {
                    callback("User details not available")
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error fetching user details: ", exception)
                callback("Failed to fetch user details")
            }
    }
}
