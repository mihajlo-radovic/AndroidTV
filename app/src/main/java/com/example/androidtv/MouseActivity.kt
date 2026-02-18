package com.example.androidtv

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MouseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mouse_activity)

        val deviceName = intent.getStringExtra("device_name")

        val textView = findViewById<TextView>(R.id.mouse_name)
        val toggleMouseButton = findViewById<Button>(R.id.toggle_mouse_button)
        val image = findViewById<ImageView>(R.id.active)
        textView.text = deviceName

        var isOn = false

        when(deviceName) {

            "Mouse","Mouse 2","Mouse 3" -> {
                toggleMouseButton.setOnClickListener {
                    AlertDialog.Builder(this)
                        .setTitle("Mouse Control")
                        .setMessage("Do you want to toggle this mouse?")
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