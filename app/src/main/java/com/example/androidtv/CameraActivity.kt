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

        when(deviceName) {
            "Camera", "Camera 2", "Camera 3" -> {
                toggleCameraButton.setOnClickListener {
                    AlertDialog.Builder(this)
                        .setTitle("Camera Control")
                        .setMessage("Do you want to toggle this camera?")
                        .setPositiveButton("Yes") { _, _ ->
                            isOn = !isOn
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

