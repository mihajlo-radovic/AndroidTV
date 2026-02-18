package com.example.androidtv

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MicrophoneActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.microphone_activity)

        val deviceName = intent.getStringExtra("device_name")

        val textView = findViewById<TextView>(R.id.microphone_name)
        val toggleMicrophoneButton = findViewById<Button>(R.id.toggle_microphone_button)
        val image = findViewById<ImageView>(R.id.active)
        textView.text = deviceName

        var isOn = false

        when(deviceName) {

            "Microphone","Microphone 2","Microphone 3" -> {
                toggleMicrophoneButton.setOnClickListener {
                    AlertDialog.Builder(this)
                        .setTitle("Microphone Control")
                        .setMessage("Do you want to toggle this microphone?")
                        .setPositiveButton("Yes") { _, _ ->
                            isOn = !isOn
                            if (isOn) {
                                image.setImageResource(R.drawable.green)
                            } else {
                                image.setImageResource(R.drawable.red)
                            }
                        }
                        .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }.show()
                }
            }
        }
    }
}