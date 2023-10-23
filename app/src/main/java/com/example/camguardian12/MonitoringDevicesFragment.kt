package com.example.camguardian12

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.camguardian12.adapters.MonitoringDeviceAdapter

class MonitoringDevicesFragment : Fragment() {

    private lateinit var monitoringDeviceAdapter: MonitoringDeviceAdapter // Make sure this class exists and is imported
    private var monitoringDeviceList: MutableList<MonitoringDevice> = mutableListOf() // Ensure MonitoringDevice class matches this

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_monitoring_devices, container, false)

        // Initialize your RecyclerView Adapter and other UI elements here
        val recyclerView: RecyclerView = view.findViewById(R.id.monitoring_devices_recycler_view)
        monitoringDeviceAdapter = MonitoringDeviceAdapter(monitoringDeviceList)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = monitoringDeviceAdapter

        // Populate your RecyclerView or perform other tasks here
        loadMonitoringDevices()

        return view
    }

    private fun loadMonitoringDevices() {
        // Assuming MonitoringDevice constructor takes two strings for this example
        // Make sure your MonitoringDevice constructor matches these parameters
        monitoringDeviceList.add(MonitoringDevice(123, "Online"))
        monitoringDeviceList.add(MonitoringDevice(2347, "Offline"))

        // Notify the adapter that the data has changed
        monitoringDeviceAdapter.notifyDataSetChanged()
    }
}
