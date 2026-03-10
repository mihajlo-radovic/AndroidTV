package com.example.androidtv

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.slider.Slider
import kotlinx.coroutines.launch

class MicrophoneActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.microphone_activity)

        val deviceName = intent.getStringExtra("device_name")
        val deviceId = intent.getIntExtra("device_id", -1)

        val backButton = findViewById<Button>(R.id.back_button)

        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val textView = findViewById<TextView>(R.id.microphone_name)
        val toggleMicrophoneButton = findViewById<Button>(R.id.toggle_microphone_button)
        val image = findViewById<ImageView>(R.id.active)
        textView.text = deviceName

        val volumeSlider = findViewById<Slider>(R.id.slider)

        volumeSlider.addOnChangeListener {_, value, _ ->
            lifecycleScope.launch {
                try {
                    val request = UpdateDeviceReq(volume = value.toInt())
                    DevicesClient.instance.updateDevices(deviceId, request)
                }catch (e: Exception){
                    e.printStackTrace()
                }
            }
        }

        var isOn = false

        lifecycleScope.launch {
            try {
                val response = DevicesClient.instance.getDevices("")
                val device = response.body()?.find { it.id == deviceId }
                isOn = device?.active ?: false

                if(isOn){
                    image.setImageResource(R.drawable.green)
                }else {
                    image.setImageResource(R.drawable.red)
                }
            }catch (e: Exception){
                e.printStackTrace()
            }
        }

        toggleMicrophoneButton.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Microphone Control")
                .setMessage("Do you want to toggle this microphone?")
                .setPositiveButton("Yes") { _, _ ->
                    lifecycleScope.launch {
                        try {
                            val request = ActiveRequest(!isOn)
                            val response = DevicesClient.instance.setActive(deviceId, request)

                            if (response.isSuccessful){
                                val updated = response.body()
                                isOn = updated?.active ?: false
                                if(isOn){
                                    image.setImageResource(R.drawable.green)
                                }else {
                                    image.setImageResource(R.drawable.red)
                                }
                            }
                        }catch (e: Exception){
                            e.printStackTrace()
                        }
                    }
                }
                .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }.show()
        }
    }
}