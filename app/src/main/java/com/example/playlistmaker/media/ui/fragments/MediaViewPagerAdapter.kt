package com.example.playlistmaker.media.ui.fragments

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class MediaViewPagerAdapter (parentFragment: Fragment)
    : FragmentStateAdapter(parentFragment) {

    val fragments = listOf(
        FavoritesFragment.newInstance(),
        PlaylistsFragment.newInstance()
    )

    override fun getItemCount() = fragments.size

    override fun createFragment(position: Int) = fragments[position]

}