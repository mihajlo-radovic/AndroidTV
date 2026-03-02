package com.example.androidtv

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)

        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        val versionName = packageInfo.versionName ?: "Unknown"

        val versionTextView: TextView = findViewById(R.id.app_version)
        versionTextView.text = "Version: $versionName"

    }
}
