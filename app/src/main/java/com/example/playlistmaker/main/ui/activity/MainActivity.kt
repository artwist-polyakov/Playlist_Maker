package com.example.playlistmaker.main.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.main.ui.view_model.MainViewModel
import com.example.playlistmaker.media.ui.activity.MediaActivity
import com.example.playlistmaker.search.ui.activity.SearchActivity
import com.example.playlistmaker.settings.ui.activity.SettingsActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.search).setOnClickListener {
            viewModel.onSearchClicked()
        }
        findViewById<Button>(R.id.media).setOnClickListener {
            viewModel.onMediaClicked()
        }
        findViewById<Button>(R.id.settings).setOnClickListener {
            viewModel.onSettingsClicked()
        }
        viewModel.navigationEvent.observe(this) { event ->
            val intent = when (event) {
                MainViewModel.NavigationEvent.SEARCH -> {
                    Intent(this, SearchActivity::class.java)
                }
                MainViewModel.NavigationEvent.MEDIA -> {
                    Intent(this, MediaActivity::class.java)
                }
                MainViewModel.NavigationEvent.SETTINGS -> {
                    Intent(this, SettingsActivity::class.java)
                }
            }
            startActivity(intent)
        }
    }
}