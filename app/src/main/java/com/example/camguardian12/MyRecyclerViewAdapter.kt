package com.example.camguardian12

import android.bluetooth.BluetoothClass
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class MyRecyclerViewAdapter(private var dataList: List<BluetoothClass.Device>) :
    RecyclerView.Adapter<MyRecyclerViewAdapter.MyViewHolder>() {

    // Secondary constructor allows initializing without a dataList
    constructor() : this(emptyList())

    // Inner class for ViewHolder pattern
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Initialize your view elements here, for example:
        // val textView: TextView = itemView.findViewById(R.id.textView)
    }

    // Inflate the item layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return MyViewHolder(view)
    }

    // Bind data to the item
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = dataList[position]
        // Bind your view elements here, for example:
        // holder.textView.text = data.toString()  // Adjust based on how you want to display BluetoothClass.Device
    }

    // Return the total item count
    override fun getItemCount(): Int {
        return dataList.size
    }

    // Function to update the dataList and refresh the RecyclerView
    fun submitList(newDataList: List<BluetoothClass.Device>) {
        dataList = newDataList
        notifyDataSetChanged()
    }
}
