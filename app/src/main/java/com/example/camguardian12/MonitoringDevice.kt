package com.example.camguardian12

data class MonitoringDevice(
    val id: Int,
    val name: String,
    var status: String = "Unknown"
) {
    // Additional functionalities can be added here

    fun turnOn() {
        this.status = "Online"
    }

    fun turnOff() {
        this.status = "Offline"
    }

    // Other utility methods as per your needs
}
