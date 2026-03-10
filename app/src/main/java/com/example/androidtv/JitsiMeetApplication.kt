package com.example.androidtv

import android.app.Application
import org.jitsi.meet.sdk.JitsiMeet
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import java.net.URL

class JitsiMeetApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        val serverURL = URL("https://meet.jit.si")

        val defaultOptions = JitsiMeetConferenceOptions.Builder()
            .setServerURL(serverURL)
            .setFeatureFlag("welcomepage.enabled", false)
            .build()

        JitsiMeet.setDefaultConferenceOptions(defaultOptions)
    }
}