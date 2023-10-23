package com.example.camguardian12

import android.Manifest
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.bluetooth.BluetoothClass
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager2.widget.ViewPager2
import com.example.camguardian12.adapters.DashboardPagerAdapter
import com.example.camguardian12.adapters.MonitoringDeviceAdapter
import com.example.camguardian12.helpers.DatabaseHelper
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


private const val CAMERA_PERMISSION_REQUEST_CODE = 1

class DashboardActivity : AppCompatActivity() {



    private val monitoringDeviceList = mutableListOf<MonitoringDevice>() // Assuming MonitoringDevice is a data class
    private val monitoringDeviceAdapter = MonitoringDeviceAdapter(monitoringDeviceList) // Assuming MonitoringDeviceAdapter is your RecyclerView.Adapter
    private val cameraDeviceList = mutableListOf<CameraDevice>() // Assuming CameraDevice is a data class
    private val cameraDeviceAdapter = CameraDeviceAdapter(cameraDeviceList)
    private lateinit var myRecyclerViewAdapter: MyRecyclerViewAdapter
    private lateinit var connectionStatusTextView: TextView
    private lateinit var noDevicesConnectedMessage: TextView// Assuming CameraDeviceAdapter is your RecyclerView.Adapter

    // Declare your class variables here, such as RecyclerView adapter, TabLayout, etc.
        // Note: monitoringDeviceList, cameraDeviceList, monitoringDeviceAdapter, and cameraDeviceAdapter will be declared here

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_dashboard)  // Set the layout resource file

            setupToolbar()
            setupTabLayoutAndViewPager()

            myRecyclerViewAdapter = MyRecyclerViewAdapter()
            connectionStatusTextView = findViewById(R.id.connectionStatusTextView)
            noDevicesConnectedMessage = findViewById(R.id.noDevicesConnectedMessage)

            // Initialize and load devices from the database
            getConnectedDevicesFromDatabase()

            val fab: FloatingActionButton = findViewById(R.id.fab)
            fab.setOnClickListener {
                handleFloatingActionButtonClick()
            }

            requestCameraPermission()
        }




    // Menu inflater for Toolbar
        override fun onCreateOptionsMenu(menu: Menu?): Boolean {
            menuInflater.inflate(R.menu.main_menu, menu)
            return true
        }

        // Modified setupToolbar function
        private fun setupToolbar() {
            val toolbar: Toolbar = findViewById(R.id.toolbar)
            setSupportActionBar(toolbar)
            supportActionBar?.title = "CamGuardian"
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu) // The menu icon drawable

            toolbar.setNavigationOnClickListener {
                val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
                drawerLayout.openDrawer(GravityCompat.START) // Opens drawer from the start (left side)
            }

            toolbar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_settings -> {
                        val intent = Intent(this, SettingsActivity::class.java)
                        startActivity(intent)
                        true
                    }
                    // Additional cases for other menu items can be added here
                    else -> false
                }
            }
        }

    // Add a function to get connected devices from the database
    private fun getConnectedDevicesFromDatabase(): List<BluetoothClass.Device> {
        // Logic to get devices from database will be added here
        // Implementing the DatabaseHelper's methods getAllMonitoringDevices and getAllCameraDevices

        // Placeholder list
        return listOf()
    }



    private fun setupTabLayoutAndViewPager() {
        val tabLayout: TabLayout = findViewById(R.id.tabLayout)
        val viewPager: ViewPager2 = findViewById(R.id.viewPager)

        // Initialize the ViewPager Adapter
        val pagerAdapter = DashboardPagerAdapter(this)
        viewPager.adapter = pagerAdapter

        // Link TabLayout and ViewPager
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Monitoring Devices"
                }
                1 -> {
                    tab.text = "Camera Devices"
                }
            }
        }.attach()
    }

    private fun loadMonitoringDevices() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val monitoringDevices = DatabaseHelper.getAllMonitoringDevices()
                monitoringDeviceList.clear()
                monitoringDeviceList.addAll(monitoringDevices)
                monitoringDeviceAdapter.notifyDataSetChanged()
            } catch (e: Exception) {
                // Handle the exception
            }
        }
    }

    




    private fun loadCameraDevices() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val cameraDevices = DatabaseHelper.getAllCameraDevices()
                cameraDeviceList.clear()
                cameraDeviceList.addAll(cameraDevices)
                cameraDeviceAdapter.notifyDataSetChanged()
            } catch (e: Exception) {
                // Handle the exception
            }
        }
    }



    private fun handleFloatingActionButtonClick() {
        // Create a dialog
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Connect to a Camera Device")

        // Inflate the custom layout for the dialog
        val dialogView = layoutInflater.inflate(R.layout.dialog_device_connect, null)

        // Get the EditText to input the 10-digit code
        val editTextCode: EditText = dialogView.findViewById(R.id.et_device_code)

        dialog.setView(dialogView)

        // Set positive and negative buttons
        dialog.setPositiveButton("Connect") { _, _ ->
            val code = editTextCode.text.toString()
            if (code.length == 10) {
                // Validate and connect the device
                validateAndConnectDevice(code)
            } else {
                // Show an error message
                Toast.makeText(this, "Please enter a valid 10-digit code", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.setNegativeButton("Cancel") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }

        // Show the dialog
        dialog.show()
    }





    private fun validateAndConnectDevice(code: String) {
        // Step 1: Validate if the code is 10-digit or not
        if (code.length != 10) {
            Toast.makeText(this, "Please enter a valid 10-digit code", Toast.LENGTH_SHORT).show()
            return
        }

        // Step 2: Check if the device code is valid (from the database or server)
        // Here you can put your logic to communicate with your backend server to validate the device code.
        // I'll simulate this with a local function for this example.
        if (!isValidCode(code)) {
            Toast.makeText(this, "Invalid device code. Retry.", Toast.LENGTH_SHORT).show()
            return
        }

        // Step 3: If the code is valid, connect to the device
        // Logic to initiate the device connection, like sending a request to the device for pairing or
        // starting a background service to manage the device connection.
        connectToDevice(code)

        // Step 4: Update the UI after a successful connection
        updateUIAfterConnection(code)
    }

    private fun isValidCode(code: String): Boolean {
        // This is a placeholder. In a real application, you would check the code against a database or API.
        return code == "1234567890"  // Just a hardcoded example
    }

    private fun connectToDevice(code: String) {
        // Logic to connect to the device using the validated code.
        // In a real application, this could involve network operations or Bluetooth connection logic.
        Toast.makeText(this, "Successfully connected to device with code: $code", Toast.LENGTH_SHORT).show()
    }

    private fun updateUIAfterConnection(code: String) {
        // Update your UI elements here, maybe refresh a list of connected devices, or navigate to a new screen.
    }


    private fun requestCameraPermission() {
        // Check if the permission has already been granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // The permission is already granted, you can go ahead and use the camera
            // For example: initializeCamera() // hypothetical function to initialize camera
        } else {
            // If the permission is not yet granted, request for it
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        }
    }

    // Handle the result of the permission request
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission granted, initialize the camera
                    // For example: initializeCamera() // hypothetical function to initialize camera
                } else {
                    // Permission denied, inform the user they can't use the camera feature
                    Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    private fun handleDeviceConnectionRequest(deviceCode: String) {
        // Here you would usually call your backend service to handle the connection
        // For simplicity, let's assume the device code "1234" is valid
        if (deviceCode == "1234") {
            // Connect to the device
            // Update database or shared preferences
            showConnectionStatus(true)
        } else {
            showConnectionStatus(false)
        }
    }


    private fun showConnectionStatus(isSuccessful: Boolean) {
        if (isSuccessful) {
            Toast.makeText(this, "Connection successful!", Toast.LENGTH_LONG).show()
            // You might also want to navigate to another activity or update the UI here
        } else {
            Toast.makeText(this, "Connection failed!", Toast.LENGTH_LONG).show()
        }
    }


    private fun updateUIAfterConnection() {
        // Retrieve the updated list of connected devices from your data source
        val connectedDevices = getConnectedDevicesFromDatabase()

        // Update the RecyclerView Adapter
        myRecyclerViewAdapter.submitList(connectedDevices)

        // If you have some UI elements that indicate connection status, update those
        val connectionStatusTextView: TextView = findViewById(R.id.connectionStatusTextView)
        connectionStatusTextView.text = "Connected to ${connectedDevices.size} devices"

        // Hide or show certain UI elements depending on connection status
        val noDevicesConnectedMessage: TextView = findViewById(R.id.noDevicesConnectedMessage)
        if (connectedDevices.isEmpty()) {
            noDevicesConnectedMessage.visibility = View.VISIBLE
        } else {
            noDevicesConnectedMessage.visibility = View.GONE
        }
    }


    private fun handlePushNotification() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
        })

        // This would usually be inside a FirebaseMessagingService subclass
        // For the purpose of this example, let's assume it's in this function
        val intent = intent
        val message = intent.getStringExtra("message")
        val eventType = intent.getStringExtra("eventType")

        when(eventType) {
            "DeviceConnected" -> {
                // Update your UI to reflect that a device has connected
                // For example, you can call updateUIAfterConnection() here
                updateUIAfterConnection()
            }
            "DeviceDisconnected" -> {
                // Update your UI to reflect that a device has disconnected
                // Here, you might want to change the status or remove the device from a list
            }
            "MotionDetected" -> {
                // Show an alert or notification that motion has been detected by one of the camera devices
            }
            // Add other event types as needed
        }

        // Optionally, show a notification if your app is in the background
        if (message != null && eventType != null) {
            showNotification(message, eventType)
        }
    }

    private fun showNotification(message: String, eventType: String) {
        val notificationId = 1 // Replace with a unique notification ID

        // Create a notification channel if on Android Oreo or above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Your Channel Name"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("your_channel_id", name, importance)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(this, "your_channel_id")
            .setSmallIcon(R.drawable.ic_notification) // Set your own icon here
            .setContentTitle(eventType)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)



    }



    private var retryCount = 0 // Declare this variable at the class level to keep track of the number of retries

    private fun showRetryDialog() {
        if (retryCount >= 5) {
            Toast.makeText(this, "Maximum retry attempts reached", Toast.LENGTH_LONG).show()
            return
        }

        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Connection Failed")
        alertDialogBuilder.setMessage("Do you want to retry the connection?")
        alertDialogBuilder.setPositiveButton("Retry") { dialog, _ ->
            retryCount++
            validateAndConnectDevice() // Try to connect again
            dialog.dismiss()
        }
        alertDialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun validateAndConnectDevice() {
        TODO("Not yet implemented")
    }


    private fun logout() {
        // Clear user session data (if you're using SharedPreferences or some other form of session management)
        val sharedPreferences = getSharedPreferences("userSession", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()

        // Redirect to the login screen
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

}
