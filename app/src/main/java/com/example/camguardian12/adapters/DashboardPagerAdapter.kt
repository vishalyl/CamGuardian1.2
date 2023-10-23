package com.example.camguardian12.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.camguardian12.CameraDevicesFragment
import com.example.camguardian12.MonitoringDevicesFragment

class DashboardPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> MonitoringDevicesFragment()  // Replace with your actual MonitoringDevicesFragment class
            1 -> CameraDevicesFragment()  // Replace with your actual CameraDevicesFragment class
            else -> throw IllegalStateException("Invalid position")
        }
    }
}
