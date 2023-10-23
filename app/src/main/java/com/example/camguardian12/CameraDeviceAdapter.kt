package com.example.camguardian12

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CameraDeviceAdapter(private val cameraDevices: List<CameraDevice>) :
    RecyclerView.Adapter<CameraDeviceAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val deviceName: TextView = itemView.findViewById(R.id.device_name)
        val deviceStatus: TextView = itemView.findViewById(R.id.device_status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.camera_device_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cameraDevice = cameraDevices[position]
        holder.deviceName.text = cameraDevice.name
        holder.deviceStatus.text = cameraDevice.status
    }

    override fun getItemCount() = cameraDevices.size
}
