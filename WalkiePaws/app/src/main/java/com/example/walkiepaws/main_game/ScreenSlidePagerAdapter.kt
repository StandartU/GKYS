package com.example.walkiepaws.main_game

import android.annotation.SuppressLint
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter


class ScreenSlidePagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    private val fragmentIds = mutableListOf<Long>(0L, 1L, 2L)

    override fun getItemCount(): Int = fragmentIds.size

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> KitchenFragment()
            1 -> LivingRoomFragment()
            2 -> BedroomFragment()
            else -> LivingRoomFragment()
        }
    }

    override fun getItemId(position: Int): Long {
        return fragmentIds[position]
    }

    override fun containsItem(itemId: Long): Boolean {
        return fragmentIds.contains(itemId)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun regenerateFragments() {
        // Создаем новые ID, чтобы они отличались от предыдущих
        Log.d("ПЕРЕСОЗДАНИЕ", "ПЕРЕСОЗДАНИЕ")
        for (i in fragmentIds.indices) {
            fragmentIds[i] = System.nanoTime()
        }
        notifyDataSetChanged()
    }
}

