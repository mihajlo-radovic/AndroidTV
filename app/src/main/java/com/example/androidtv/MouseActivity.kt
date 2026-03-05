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

        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val textView = findViewById<TextView>(R.id.mouse_name)
        val toggleMouseButton = findViewById<Button>(R.id.toggle_mouse_button)
        val image = findViewById<ImageView>(R.id.active)
        textView.text = deviceName

        val orientationGroup = findViewById<RadioGroup>(R.id.orientation_group)

        orientationGroup.setOnCheckedChangeListener {_, checkedId ->

            val orientationValue = when(checkedId){
                R.id.left_right -> "LEFT_RIGHT"
                R.id.right_left -> "RIGHT_LEFT"
                else -> null
            }
            if (orientationValue !=null){
                lifecycleScope.launch {
                    try {
                        val request = UpdateDeviceReq(orientation = orientationValue)
                        DevicesClient.instance.updateDevices(deviceId, request)
                    }catch (e: Exception){
                        e.printStackTrace()
                    }
                }
            }
        }

        var isOn = false

        lifecycleScope.launch {
            try {
                val response = DevicesClient.instance.getDevices()
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

        toggleMouseButton.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Mouse Control")
                .setMessage("Do you want to toggle this mouse?")
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