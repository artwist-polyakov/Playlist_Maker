package com.example.playlistmaker.media.ui.fragments

import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.playlistmaker.media.ui.fragments.playlists.PlaylistsFragment
import com.example.playlistmaker.media.ui.fragments.favorites.FavoritesFragment

class MediaViewPagerAdapter(
    supportFragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(supportFragmentManager, lifecycle) {

    val fragments = listOf(
        FavoritesFragment.newInstance(),
        PlaylistsFragment.newInstance()
    )

    override fun getItemCount() = 2

    override fun createFragment(position: Int) = when(position) {
        0 -> FavoritesFragment.newInstance()
        else -> PlaylistsFragment.newInstance()
    }
}