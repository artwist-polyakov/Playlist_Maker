package com.example.playlistmaker.media.ui.activity

import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class MediaViewPagerAdapter (fragmentManager: FragmentManager, lifecycle: Lifecycle)
    : FragmentStateAdapter(fragmentManager, lifecycle) {

    val fragments = listOf(
        FavoritesFragment.newInstance(),
        PlaylistsFragment.newInstance()
    )

    override fun getItemCount() = fragments.size

    override fun createFragment(position: Int) = fragments[position]

}