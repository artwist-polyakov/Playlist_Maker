package com.example.playlistmaker.ui.mainscreen

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.example.playlistmaker.ui.settings.SettingsActivity
import com.example.playlistmaker.ui.media.MediaActivity
import com.example.playlistmaker.ui.search.SearchActivity

class MainActivity : AppCompatActivity() {

    companion object {
        const val PREFS = "my_prefs"
        const val THEME_PREF = "isDarkTheme"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sharedPref = getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isDarkTheme = sharedPref.getBoolean(THEME_PREF, currentNightMode == Configuration.UI_MODE_NIGHT_YES)
        (applicationContext as App).switchTheme(isDarkTheme)
        val searchButton = findViewById<Button>(R.id.search)
        val mediaButton = findViewById<Button>(R.id.media)
        val settingsButton = findViewById<Button>(R.id.settings)

        //через лямбду
        searchButton.setOnClickListener {
//            Toast.makeText(this@MainActivity, "Нажали на кнопку ПОИСК 🔎 ", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@MainActivity, SearchActivity::class.java)
            startActivity(intent)
        }

        mediaButton.setOnClickListener {
//            Toast.makeText(this@MainActivity, "Нажали на кнопку ПОИСК 🔎 ", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@MainActivity, MediaActivity::class.java)
            startActivity(intent)
        }

        //через анонимный класс
        val settingsButtonClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
//                Toast.makeText(this@MainActivity, "Нажали на кнопку МЕДИА 🎶", Toast.LENGTH_SHORT)
//                    .show()
                val intent = Intent(this@MainActivity, SettingsActivity::class.java)
                startActivity(intent)
            }
        }
        settingsButton.setOnClickListener(settingsButtonClickListener)


        //через лямбду
//        settingsButton.setOnClickListener {
////            Toast.makeText(this@MainActivity, "Нажали на кнопку НАСТРОЙКИ ⚙️ ", Toast.LENGTH_SHORT).show()
//            val intent = Intent(this@MainActivity, SettingsActivity::class.java)
//            startActivity(intent)
//        }
    }
}