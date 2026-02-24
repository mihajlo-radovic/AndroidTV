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

        val preferences = getSharedPreferences("preferences", MODE_PRIVATE)
        var isOn = preferences.getBoolean(deviceName, false)

        if(isOn){
            image.setImageResource(R.drawable.green)
        }else{
            image.setImageResource(R.drawable.red)
        }

        when(deviceName) {

            "Microphone","Microphone 2","Microphone 3" -> {
                toggleMicrophoneButton.setOnClickListener {
                    AlertDialog.Builder(this)
                        .setTitle("Microphone Control")
                        .setMessage("Do you want to toggle this microphone?")
                        .setPositiveButton("Confirm") { _, _ ->

                            isOn = !isOn
                            val edit = preferences.edit()

                            if(isOn){
                                if(deviceName == "Microphone"){
                                    edit.putBoolean("Microphone 2", false)
                                    edit.putBoolean("Microphone 3", false)
                                }

                                if(deviceName == "Microphone 2"){
                                    edit.putBoolean("Microphone", false)
                                    edit.putBoolean("Microphone 3", false)
                                }

                                if(deviceName == "Microphone 3"){
                                    edit.putBoolean("Microphone", false)
                                    edit.putBoolean("Microphone 2", false)
                                }
                            }

                            edit.putBoolean(deviceName, isOn).apply()

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