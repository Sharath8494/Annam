package com.example.annam

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

class MyAdapter(private val dataList: ArrayList<Model?>?) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    private val fAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val userID: String = fAuth.currentUser?.uid ?: ""
    private var uid: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.singlerow, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = dataList?.get(position)
        holder.tName.text = model?.name
        holder.tType.text = model?.type
        holder.tDescription.text = model?.description
    }

    fun deleteItem(position: Int) {
//         getSnapshots().getSnapshot(position).reference.delete()
//         notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return dataList!!.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tName: TextView = itemView.findViewById(R.id.name)
        val tType: TextView = itemView.findViewById(R.id.type)
        val tDescription: TextView = itemView.findViewById(R.id.description)
    }
}
