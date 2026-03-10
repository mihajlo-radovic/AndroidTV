package com.example.androidtv

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import com.google.android.material.textfield.TextInputEditText
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions

class JoinMeeting : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_join_meeting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        val joinButton = view.findViewById<Button>(R.id.join_meeting_button)
        val textInput = view.findViewById<TextInputEditText>(R.id.join_text_input)

        //textInput.requestFocus()

        joinButton.setOnClickListener {
            val roomCode = textInput.text.toString().trim()

            if (roomCode.isEmpty()) {
                textInput.error = "Enter a room code"
                return@setOnClickListener
            }

            val options = JitsiMeetConferenceOptions.Builder()
                .setRoom(roomCode)
                .build()

            JitsiMeetActivity.launch(requireActivity(), options)
        }
    }
}