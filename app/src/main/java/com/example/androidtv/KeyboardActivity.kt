package com.example.androidtv

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.coroutines.launch

class KeyboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.keyboard_activity)

        val deviceName = intent.getStringExtra("device_name")
        val deviceId = intent.getIntExtra("device_id", -1)

        val backButton = findViewById<Button>(R.id.back_button)
        val textView = findViewById<TextView>(R.id.keyboard_name)
        val toggleKeyboardButton = findViewById<Button>(R.id.toggle_keyboard_button)
        val image = findViewById<ImageView>(R.id.active)
        val capsLockSwitch = findViewById<SwitchMaterial>(R.id.caps_lock_switch)

        val deviceIdView = findViewById<TextView>(R.id.device_id)
        val deviceTypeView = findViewById<TextView>(R.id.device_type)
        val lastUpdatedView = findViewById<TextView>(R.id.last_updated)
        val deviceStatusView = findViewById<TextView>(R.id.device_status)
        val statusBadge = findViewById<TextView>(R.id.status_badge)
        val keyboardNameCard = findViewById<TextView>(R.id.keyboard_name_card)
        val keyboardAddedDate = findViewById<TextView>(R.id.keyboard_added_date)

        textView.text = deviceName

        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        capsLockSwitch.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                try {
                    val request = UpdateDeviceReq(capsLock = isChecked)
                    DevicesClient.instance.updateDevices(deviceId, request)
                } catch (e: Exception) {
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

                keyboardNameCard.text = device?.name ?: deviceName ?: "Keyboard"
                keyboardAddedDate.text = "KEYBOARD · Added ${device?.createdAt?.substring(0, 10) ?: "—"}"
                deviceIdView.text = device?.id?.toString() ?: "—"
                deviceTypeView.text = device?.type ?: "—"
                lastUpdatedView.text = device?.updatedAt?.substring(0, 10) ?: "—"

                capsLockSwitch.isChecked = device?.capsLock ?: false

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

        toggleKeyboardButton.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Keyboard Control")
                .setMessage("Do you want to toggle this keyboard?")
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