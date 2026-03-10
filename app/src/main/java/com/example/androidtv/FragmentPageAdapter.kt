package com.example.androidtv

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class FragmentPageAdapter (fragmentManager: FragmentManager, lifecycle: Lifecycle): FragmentStateAdapter(fragmentManager, lifecycle){
    override fun createFragment(position: Int): Fragment {
        return if (position == 0)
            JoinMeeting()
        else
            CreateMeeting()
    }

    override fun getItemCount(): Int {
        return 2
    }
}