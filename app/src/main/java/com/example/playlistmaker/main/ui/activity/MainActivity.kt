package com.example.playlistmaker.main.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.databinding.ActivityMainBinding
import com.example.playlistmaker.main.ui.view_model.MainViewModel
import com.example.playlistmaker.media.ui.activity.MediaActivity
import com.example.playlistmaker.search.ui.activity.SearchActivity
import com.example.playlistmaker.settings.ui.activity.SettingsActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModel()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.search.setOnClickListener {
            viewModel.onSearchClicked()
        }
        binding.media.setOnClickListener {
            viewModel.onMediaClicked()
        }
        binding.settings.setOnClickListener {
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