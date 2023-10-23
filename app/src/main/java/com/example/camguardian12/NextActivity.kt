package com.example.camguardian12



import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class NextActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_next)

        // Set background color to green
        window.decorView.setBackgroundColor(Color.GREEN)
    }
}
