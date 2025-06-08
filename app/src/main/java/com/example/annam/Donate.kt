package com.example.annam

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class Donate : AppCompatActivity() {

    private lateinit var mFullName: EditText
    private lateinit var mFoodItem: EditText
    private lateinit var mDescription: EditText
    private lateinit var mPhone: EditText
    private lateinit var mRecivAdd: EditText
    private lateinit var mSubmitBtn: Button
    private lateinit var fAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore
    private lateinit var userID: String

    companion object {
        const val TAG = "TAG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donate)

        // Initialize views
        mFullName = findViewById(R.id.donorname)
        mFoodItem = findViewById(R.id.fooditem)
        mDescription = findViewById(R.id.description)
        mPhone = findViewById(R.id.phone)
        mRecivAdd = findViewById(R.id.recivAdd)
        mSubmitBtn = findViewById(R.id.submit)

        // Initialize Firebase
        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()
        userID = fAuth.currentUser!!.uid

        // Fetch and set the donor's name from the Firestore database
        fStore.collection("user data").document(userID).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val fullName = document.getString("name")
                    mFullName.setText(fullName)
                    mFullName.isEnabled = false  // Make the name non-editable
                }
            }.addOnFailureListener { e ->
                Log.w(TAG, "Error fetching donor name", e)
            }

        // Set click listener for the submit button
        mSubmitBtn.setOnClickListener {
            val fooditem = mFoodItem.text.toString().trim()
            val description = mDescription.text.toString().trim()
            val phone = mPhone.text.toString().trim()
            val recivAddress = mRecivAdd.text.toString().trim()

            if (TextUtils.isEmpty(fooditem)) {
                mFoodItem.error = "Food item is Required."
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(phone)) {
                mPhone.error = "Phone number is Required."
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(recivAddress)) {
                mRecivAdd.error = "Receive address is Required."
                return@setOnClickListener
            }

            // Check for duplicate donations
            checkForDuplicateDonation(fooditem, description, phone, recivAddress)
        }
    }

    private fun checkForDuplicateDonation(fooditem: String, description: String, phone: String, recivAddress: String) {
        val collectionReference: CollectionReference = fStore.collection("user data")
        collectionReference
            .whereEqualTo("name", mFullName.text.toString())
            .whereEqualTo("food item", fooditem)
            .whereEqualTo("description", description)
            .whereEqualTo("userid", userID)
            .get()
            .addOnSuccessListener { querySnapshot: QuerySnapshot ->
                if (querySnapshot.isEmpty) {
                    submitDonation(fooditem, description, phone, recivAddress)
                } else {
                    // Duplicate donation found
                    Toast.makeText(applicationContext, "Donation already exists!", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error checking for duplicates", e)
            }
    }

    private fun submitDonation(fooditem: String, description: String, phone: String, recivAddress: String) {
        val collectionReference = fStore.collection("donations")
        val newDonationRef = collectionReference.document()
        val donationId = newDonationRef.id

        val donationData = hashMapOf(
            "timestamp" to FieldValue.serverTimestamp(),
            "name" to mFullName.text.toString(),
            "food item" to fooditem,
            "description" to description,
            "phone" to phone,  // Add phone field
            "receiveAddress" to recivAddress,  // Add receive address field
            "userid" to userID,
            "type" to "Donor",
            "donationId" to donationId,
            "status" to false
        )

        newDonationRef.set(donationData)
            .addOnSuccessListener {
                // Clear all input fields after successful submission
                mFoodItem.text.clear()
                mDescription.text.clear()
                mPhone.text.clear()
                mRecivAdd.text.clear()

                // Show success message
                Toast.makeText(applicationContext, "Donation Submitted Successfully!", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Donation Submitted")

                // Redirect to MainActivity
                redirectToMainActivity()
            }
            .addOnFailureListener { e ->
                Toast.makeText(applicationContext, "Error submitting donation!", Toast.LENGTH_SHORT).show()
                Log.w(TAG, "Error!", e)
            }
    }

    private fun redirectToMainActivity() {
        val intent = Intent(this@Donate, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()  // Close current activity
    }
}
