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

        val deviceIdView = findViewById<TextView>(R.id.device_id)
        val deviceTypeView = findViewById<TextView>(R.id.device_type)
        val lastUpdatedView = findViewById<TextView>(R.id.last_updated)
        val deviceStatusView = findViewById<TextView>(R.id.device_status)
        val statusBadge = findViewById<TextView>(R.id.status_badge)
        val microphoneNameCard = findViewById<TextView>(R.id.microphone_name_card)
        val microphoneAddedDate = findViewById<TextView>(R.id.microphone_added_date)

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

                microphoneNameCard.text = device?.name ?: deviceName ?: "Microphone"
                microphoneAddedDate.text = "MICROPHONE · Added ${device?.createdAt?.substring(0, 10) ?: "—"}"
                deviceIdView.text = device?.id?.toString() ?: "—"
                deviceTypeView.text = device?.type ?: "—"
                lastUpdatedView.text = device?.updatedAt?.substring(0, 10) ?: "—"

                device?.volume?.let { volumeSlider.value = it.toFloat() }

                if (isOn) {
                    image.setImageResource(R.drawable.green)
                    deviceStatusView.text = "Enabled"
                    statusBadge.text = "● Active"
                } else {
                    image.setImageResource(R.drawable.red)
                    deviceStatusView.text = "Disabled"
                    statusBadge.text = "● Inactive"
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
                                if (isOn) {
                                    image.setImageResource(R.drawable.green)
                                    deviceStatusView.text = "Enabled"
                                    statusBadge.text = "● Active"
                                } else {
                                    image.setImageResource(R.drawable.red)
                                    deviceStatusView.text = "Disabled"
                                    statusBadge.text = "● Inactive"
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