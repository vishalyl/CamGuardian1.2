package com.example.camguardian12

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize your UI components and other variables here
    }

    // Implement your event listeners or other functions

    override fun onResume() {
        super.onResume()
        // Do something when the activity becomes visible
    }

    override fun onPause() {
        super.onPause()
        // Do something when the activity goes into the background
    }

    override fun onDestroy() {
        super.onDestroy()
        // Perform any cleanup tasks
    }
}
