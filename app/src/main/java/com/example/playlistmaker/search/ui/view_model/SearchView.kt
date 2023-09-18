package com.example.playlistmaker.search.ui.view_model

import com.example.playlistmaker.search.ui.fragments.SearchState

interface SearchView {
    // Методы, меняющие внешний вид экрана
    fun render(state: SearchState)
}