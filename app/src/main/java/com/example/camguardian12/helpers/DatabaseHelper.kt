package com.example.camguardian12.helpers

// Fix the import for CameraDevice
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.camguardian12.CameraDevice
import com.example.camguardian12.MonitoringDevice

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        // Return empty list or handle the actual implementation later
        fun getAllMonitoringDevices(): List<MonitoringDevice> {
            return emptyList()
        }

        // Return empty list or handle the actual implementation later
        fun getAllCameraDevices(): List<CameraDevice> {
            return emptyList()
        }

        private const val DATABASE_NAME = "CamGuardianDB"
        private const val DATABASE_VERSION = 1

        private const val TABLE_MONITORING_DEVICES = "monitoring_devices"
        private const val TABLE_CAMERA_DEVICES = "camera_devices"

        private const val KEY_ID = "id"
        private const val KEY_MONITORING_DEVICE_NAME = "device_name"
        private const val KEY_CAMERA_DEVICE_NAME = "camera_name"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.let {
            it.execSQL("CREATE TABLE $TABLE_MONITORING_DEVICES($KEY_ID INTEGER PRIMARY KEY, $KEY_MONITORING_DEVICE_NAME TEXT)")
            it.execSQL("CREATE TABLE $TABLE_CAMERA_DEVICES($KEY_ID INTEGER PRIMARY KEY, $KEY_CAMERA_DEVICE_NAME TEXT)")
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_MONITORING_DEVICES")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_CAMERA_DEVICES")
        onCreate(db)
    }

    fun getAllMonitoringDevices(): List<MonitoringDevice> {
        val deviceList = mutableListOf<MonitoringDevice>()
        val db = this.readableDatabase
        val cursor: Cursor? = db.rawQuery("SELECT * FROM $TABLE_MONITORING_DEVICES", null)
        cursor?.use {  // automatically closes the cursor after use
            if (it.moveToFirst()) {
                do {
                    val idIndex = it.getColumnIndex(KEY_ID)
                    val nameIndex = it.getColumnIndex(KEY_MONITORING_DEVICE_NAME)
                    if (idIndex != -1 && nameIndex != -1) {
                        val id = it.getInt(idIndex)
                        val name = it.getString(nameIndex)
                        deviceList.add(MonitoringDevice(123, name))  // Assuming MonitoringDevice accepts these two arguments
                    }
                } while (it.moveToNext())
            }
        }
        db.close()
        return deviceList
    }

    fun getAllCameraDevices(): List<CameraDevice> {
        val cameraList = mutableListOf<CameraDevice>()
        val db = this.readableDatabase
        val cursor: Cursor? = db.rawQuery("SELECT * FROM $TABLE_CAMERA_DEVICES", null)
        cursor?.use {  // automatically closes the cursor after use
            if (it.moveToFirst()) {
                do {
                    val idIndex = it.getColumnIndex(KEY_ID)
                    val nameIndex = it.getColumnIndex(KEY_CAMERA_DEVICE_NAME)
                    if (idIndex != -1 && nameIndex != -1) {
                        val id = it.getInt(idIndex)
                        val name = it.getString(nameIndex)
                        val defaultStatus = "Offline" // or whatever default you'd like
                        cameraList.add(CameraDevice(id.toString(), name, defaultStatus))
                    }
                } while (it.moveToNext())
            }
        }
        db.close()
        return cameraList
    }


}
