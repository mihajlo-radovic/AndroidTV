package com.example.androidtv

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class CameraActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.camera_activity)

        val deviceName = intent.getStringExtra("device_name")

        val textView = findViewById<TextView>(R.id.camera_name)
        val toggleCameraButton = findViewById<Button>(R.id.toggle_camera_button)
        val image = findViewById<ImageView>(R.id.active)
        textView.text = deviceName

        val preferences = getSharedPreferences("preferences", MODE_PRIVATE)
        var isOn = preferences.getBoolean(deviceName, false)

        if(isOn){
            image.setImageResource(R.drawable.green)
        }else{
            image.setImageResource(R.drawable.red)
        }

        toggleCameraButton.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Camera Control")
                .setMessage("Do you want to toggle this camera?")
                .setPositiveButton("Yes") { _, _ ->

                    isOn = !isOn
                    val edit = preferences.edit()

                    if(isOn){
                        val allPreferences = preferences.all
                        for ((key, value) in allPreferences){
                            if (key != deviceName && value is Boolean){
                                edit.putBoolean(key, false)
                            }
                        }
                    }

                    edit.putBoolean(deviceName, isOn).apply()

                    if(isOn){
                        image.setImageResource(R.drawable.green)
                    }else{
                        image.setImageResource(R.drawable.red)
                    }
                }
                .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }.show()
        }
    }
}