package com.example.playlistmaker.search.ui.view_model

interface SearchView {
    // Методы, меняющие внешний вид экрана
    fun render(state: TracksState)
}