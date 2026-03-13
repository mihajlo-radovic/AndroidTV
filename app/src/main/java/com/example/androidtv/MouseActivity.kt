package com.example.androidtv

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MouseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mouse_activity)

        val deviceName = intent.getStringExtra("device_name")
        val deviceId = intent.getIntExtra("device_id", -1)

        val backButton = findViewById<Button>(R.id.back_button)
        val textView = findViewById<TextView>(R.id.mouse_name)
        val toggleMouseButton = findViewById<Button>(R.id.toggle_mouse_button)
        val image = findViewById<ImageView>(R.id.active)
        val orientationGroup = findViewById<RadioGroup>(R.id.orientation_group)

        val deviceIdView = findViewById<TextView>(R.id.device_id)
        val deviceTypeView = findViewById<TextView>(R.id.device_type)
        val lastUpdatedView = findViewById<TextView>(R.id.last_updated)
        val deviceStatusView = findViewById<TextView>(R.id.device_status)
        val statusBadge = findViewById<TextView>(R.id.status_badge)
        val mouseNameCard = findViewById<TextView>(R.id.mouse_name_card)
        val mouseAddedDate = findViewById<TextView>(R.id.mouse_added_date)

        textView.text = deviceName

        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        orientationGroup.setOnCheckedChangeListener { _, checkedId ->
            val orientationValue = when (checkedId) {
                R.id.left_right -> "LEFT_RIGHT"
                R.id.right_left -> "RIGHT_LEFT"
                else -> null
            }
            if (orientationValue != null) {
                lifecycleScope.launch {
                    try {
                        val request = UpdateDeviceReq(orientation = orientationValue)
                        DevicesClient.instance.updateDevices(deviceId, request)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

        var isOn = false

        lifecycleScope.launch {
            try {
                val response = DevicesClient.instance.getDevices("")
                val device = response.body()?.find { it.id == deviceId }
                isOn = device?.active ?: false

                mouseNameCard.text = device?.name ?: deviceName ?: "Mouse"
                mouseAddedDate.text = "MOUSE · Added ${device?.createdAt?.substring(0, 10) ?: "—"}"
                deviceIdView.text = device?.id?.toString() ?: "—"
                deviceTypeView.text = device?.type ?: "—"
                lastUpdatedView.text = device?.updatedAt?.substring(0, 10) ?: "—"

                when (device?.orientation) {
                    "RIGHT_LEFT" -> orientationGroup.check(R.id.right_left)
                    else -> orientationGroup.check(R.id.left_right)
                }

                if (isOn) {
                    image.setImageResource(R.drawable.green)
                    deviceStatusView.text = "Enabled"
                    statusBadge.text = "● Active"
                } else {
                    image.setImageResource(R.drawable.red)
                    deviceStatusView.text = "Disabled"
                    statusBadge.text = "● Inactive"
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        toggleMouseButton.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Mouse Control")
                .setMessage("Do you want to toggle this mouse?")
                .setPositiveButton("Yes") { _, _ ->
                    lifecycleScope.launch {
                        try {
                            val request = ActiveRequest(!isOn)
                            val response = DevicesClient.instance.setActive(deviceId, request)

                            if (response.isSuccessful) {
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
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
                .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }.show()
        }
    }
}