package com.example.androidtv

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class CameraActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        lateinit var spinner: Spinner

        super.onCreate(savedInstanceState)
        setContentView(R.layout.camera_activity)

        val deviceName = intent.getStringExtra("device_name")
        val deviceId = intent.getIntExtra("device_id", -1)

        val backButton = findViewById<Button>(R.id.back_button)

        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        spinner = findViewById<Spinner>(R.id.camera_resolution)
        val list = listOf("720p 30fps", "720p 60fps", "1080p 30fps", "1080p 60fps")

        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, list)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = arrayAdapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = parent?.getItemAtPosition(position).toString()

                lifecycleScope.launch {
                    try {
                        val request = UpdateDeviceReq(resolution = selectedItem)
                        DevicesClient.instance.updateDevices(deviceId, request)
                    }catch (e: Exception){
                        e.printStackTrace()
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }


        val textView = findViewById<TextView>(R.id.camera_name)
        val toggleCameraButton = findViewById<Button>(R.id.toggle_camera_button)
        val image = findViewById<ImageView>(R.id.active)
        textView.text = deviceName

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

        toggleCameraButton.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Camera Control")
                .setMessage("Do you want to toggle this camera?")
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