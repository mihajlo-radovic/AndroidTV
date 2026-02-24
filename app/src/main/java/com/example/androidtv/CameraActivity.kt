package com.example.androidtv

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class CameraActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.camera_activity)

        val deviceName = intent.getStringExtra("device_name")

        val textView = findViewById<TextView>(R.id.camera_name)
        val toggleCameraButton = findViewById<Button>(R.id.toggle_camera_button)
        val image = findViewById<ImageView>(R.id.active)
        textView.text = deviceName

        var isOn = false

        val preferences = getSharedPreferences("preferences", MODE_PRIVATE)
        isOn = preferences.getBoolean(deviceName, false)

        if(isOn){
            image.setImageResource(R.drawable.green)
        }else{
            image.setImageResource(R.drawable.red)
        }

        when(deviceName) {
            "Camera", "Camera 2", "Camera 3" -> {
                toggleCameraButton.setOnClickListener {
                    AlertDialog.Builder(this)
                        .setTitle("Camera Control")
                        .setMessage("Do you want to toggle this camera?")
                        .setPositiveButton("Yes") { _, _ ->

                            isOn = !isOn
                            val edit = preferences.edit()

                            if(isOn){
                                if(deviceName == "Camera"){
                                    edit.putBoolean("Camera 2", false)
                                    edit.putBoolean("Camera 3", false)
                                }

                                if(deviceName == "Camera 2"){
                                    edit.putBoolean("Camera", false)
                                    edit.putBoolean("Camera 3", false)
                                }

                                if(deviceName == "Camera 3"){
                                    edit.putBoolean("Camera", false)
                                    edit.putBoolean("Camera 2", false)
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
    }
}

//funkcija da zameni if else statement