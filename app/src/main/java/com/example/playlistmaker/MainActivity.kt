package com.example.playlistmaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchButton = findViewById<Button>(R.id.search)
        val mediaButton = findViewById<Button>(R.id.media)
        val settingsButton = findViewById<Button>(R.id.settings)

        //через лямбду
        searchButton.setOnClickListener {
            Toast.makeText(this@MainActivity, "Нажали на кнопку ПОИСК 🔎 ", Toast.LENGTH_SHORT).show()
        }

        //через анонимный класс
        val mediaButtonClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                Toast.makeText(this@MainActivity, "Нажали на кнопку МЕДИА 🎶", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        mediaButton.setOnClickListener(mediaButtonClickListener)


        //через лямбду
        settingsButton.setOnClickListener {
            Toast.makeText(this@MainActivity, "Нажали на кнопку НАСТРОЙКИ ⚙️ ", Toast.LENGTH_SHORT).show()
        }
    }
}