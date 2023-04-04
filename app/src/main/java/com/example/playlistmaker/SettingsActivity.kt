package com.example.playlistmaker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val backButton = findViewById<ImageView>(R.id.return_button)
        backButton.setOnClickListener {
            val intent = Intent(this@SettingsActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }

    }
