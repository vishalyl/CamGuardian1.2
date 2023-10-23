package com.example.camguardian12.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.camguardian12.MonitoringDevice
import com.example.camguardian12.R

class MonitoringDeviceAdapter(private val items: List<MonitoringDevice>) :
    RecyclerView.Adapter<MonitoringDeviceAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_monitoring_device, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView

        init {
            nameTextView = itemView.findViewById(R.id.nameTextView)
        }

        fun bind(item: MonitoringDevice) {
            nameTextView.text = item.name
            // Add more bindings here if needed
        }
    }
}