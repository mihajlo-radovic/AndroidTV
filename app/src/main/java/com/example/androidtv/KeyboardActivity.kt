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

class KeyboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.keyboard_activity)

        val deviceName = intent.getStringExtra("device_name")

        val textView = findViewById<TextView>(R.id.keyboard_name)
        val toggleKeyboardButton = findViewById<Button>(R.id.toggle_keyboard_button)
        val image = findViewById<ImageView>(R.id.active)
        textView.text = deviceName

        var isOn = false

        when(deviceName) {

            "Keyboard", "Keyboard 2", "Keyboard 3" -> {
                toggleKeyboardButton.setOnClickListener {
                    AlertDialog.Builder(this)
                        .setTitle("Keyboard Control")
                        .setMessage("Do you want to toggle this keyboard?")
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