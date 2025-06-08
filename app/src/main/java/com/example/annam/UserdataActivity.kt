package com.example.annam

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class UserdataActivity : AppCompatActivity() {
    var recyclerView: RecyclerView? = null
    var datalist: ArrayList<Model?>? = null
    var db: FirebaseFirestore? = null
    var adapter: MyAdapter? = null
    var fAuth: FirebaseAuth = FirebaseAuth.getInstance()
    var userID: String = fAuth.currentUser!!.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.userdata)

        recyclerView = findViewById<View>(R.id.rec_view) as RecyclerView
        recyclerView!!.layoutManager = LinearLayoutManager(this)
        datalist = ArrayList<Model?>()
        adapter = MyAdapter(datalist)
        recyclerView!!.adapter = adapter

        db = FirebaseFirestore.getInstance()
        db!!.collection("user data").orderBy("timestamp", Query.Direction.DESCENDING).get()
            .addOnSuccessListener { queryDocumentSnapshots ->
                val list = queryDocumentSnapshots.documents
                for (d in list) {
                    val obj: Model? = d.toObject(Model::class.java)
                    //datalist.add(obj);
                    val Userid = d["userid"] as String?
                    if (Userid == userID) {
                        datalist!!.add(obj)
                    }
                }
                adapter!!.notifyDataSetChanged()
            }
    }
}