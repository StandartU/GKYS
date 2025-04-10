package com.example.walkiepaws.main_game

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter


class ScreenSlidePagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> KitchenFragment()
            1 -> LivingRoomFragment()
            2 -> BedroomFragment()
            else -> LivingRoomFragment()
        }
    }
}
