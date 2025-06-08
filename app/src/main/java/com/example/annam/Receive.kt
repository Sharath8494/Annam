package com.example.annam

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Receive : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val notebookRef = db.collection("donations") // Ensure you are referencing the correct collection
    private val TAG = "TAG"
    private lateinit var layoutContainer: LinearLayout
    private lateinit var fAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receive)

        fAuth = FirebaseAuth.getInstance()
        layoutContainer = findViewById(R.id.showdata)

        loadDonations() // Load the donations when the activity starts
    }

    // Function to dynamically create donation entries with a "Receive" button
    private fun loadDonations() {
        layoutContainer.removeAllViews() // Clear previous views
        notebookRef.whereEqualTo("status", false) // Fetch donations that are not claimed
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        Log.d(TAG, "${document.id} => ${document.data}")

                        // Ensure required fields are present
                        if (document.contains("name") && document.contains("description")) {
                            val name = document.getString("name") ?: "N/A"
                            val description = document.getString("description") ?: "N/A"
                            val phone = document.getString("phone") ?: "N/A" // Fetch phone number
                            val receiveAddress = document.getString("receiveAddress") ?: "N/A" // Fetch receive address
                            val ts = document.getTimestamp("timestamp")
                            val dateAndTime = ts?.toDate().toString()

                            // Dynamically create a CardView for each donation
                            val cardView = CardView(this)
                            val layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            )
                            layoutParams.setMargins(0, 16, 0, 16)
                            cardView.layoutParams = layoutParams
                            cardView.setCardBackgroundColor(resources.getColor(R.color.gray))
                            cardView.radius = 12f
                            cardView.cardElevation = 5f
                            cardView.setPadding(20, 20, 20, 20)

                            // Create a LinearLayout to hold TextView and Button
                            val linearLayout = LinearLayout(this)
                            linearLayout.orientation = LinearLayout.VERTICAL
                            linearLayout.layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            )

                            // Create TextView for donation details
                            val textView = TextView(this)
                            textView.text = "Name: $name\nDescription: $description\nPhone: $phone\nReceive Address: $receiveAddress\nDate & Time: $dateAndTime"
                            textView.setTextColor(resources.getColor(R.color.white))
                            textView.textSize = 15f
                            textView.layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            )
                            textView.maxLines = 5
                            textView.setPadding(0, 0, 0, 16)

                            // Create "Receive" button
                            val receiveButton = Button(this)
                            receiveButton.text = "Receive"
                            receiveButton.setBackgroundColor(resources.getColor(R.color.black))
                            receiveButton.setTextColor(resources.getColor(R.color.white))
                            receiveButton.textSize = 18f

                            // Set click listener for the receive button
                            receiveButton.setOnClickListener {
                                claimDonation(document.id, receiveButton) // Pass the document ID and button reference
                            }

                            // Add TextView and button to the LinearLayout
                            linearLayout.addView(textView)
                            linearLayout.addView(receiveButton)

                            // Add the LinearLayout to the CardView
                            cardView.addView(linearLayout)

                            // Add the CardView to the layout container
                            layoutContainer.addView(cardView)
                        }
                    }
                } else {
                    Log.d(TAG, "Error fetching data: ", task.exception)
                }
            }
    }

    private fun claimDonation(documentId: String, button: Button) {
        // Check if the donation is still available
        notebookRef.document(documentId).get()
            .addOnSuccessListener { document ->
                if (document.exists() && document.getBoolean("status") == false) {
                    // Update the 'status' to true and assign the donation to the current user
                    notebookRef.document(documentId)
                        .update(mapOf(
                            "status" to true,
                            "userid" to fAuth.currentUser?.uid // Set the current user's ID as the new owner
                        ))
                        .addOnSuccessListener {
                            Toast.makeText(this, "Donation successfully claimed!", Toast.LENGTH_SHORT).show()

                            // Change button color to indicate it has been claimed
                            button.setBackgroundColor(resources.getColor(R.color.white))
                            button.setTextColor(resources.getColor(R.color.black))
                            button.text = "Claimed" // Optionally change the text
                            button.isEnabled = false // Disable the button to prevent further clicks

                            loadDonations() // Reload the data to reflect changes
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "Error updating document", e)
                            Toast.makeText(this, "Failed to claim the donation", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "This donation has already been claimed", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error fetching document", e)
            }
    }
}
